package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.PacketHandler;
import com.majruszs_difficulty.goals.TankAttackGoal;
import com.mlib.CommonHelper;
import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import com.mlib.network.message.EntityFloatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import org.spongepowered.asm.mixin.injection.At;

/** New undead huge skeleton. */
@Mod.EventBusSubscriber
public class TankEntity extends Monster {
	public static final EntityType< TankEntity > type;
	protected static final int SPECIAL_ATTACK_DURATION = TimeConverter.secondsToTicks( 1.75 );
	protected static final int NORMAL_ATTACK_DURATION = TimeConverter.secondsToTicks( 1.0 );

	static {
		type = EntityType.Builder.of( TankEntity::new, MobCategory.MONSTER )
			.sized( 1.1f, 2.7f )
			.build( MajruszsDifficulty.getLocation( "tank" ).toString() );
	}

	private int specialAttackTicksLeft;
	private int normalAttackTicksLeft;

	public TankEntity( EntityType< ? extends TankEntity > type, Level world ) {
		super( type, world );
		this.xpReward = 17;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 2, new TankAttackGoal( this ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
	}

	@Override
	protected int getExperienceReward( Player player ) {
		return super.getExperienceReward( player ) + MajruszLibrary.RANDOM.nextInt( 17 );
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 2.35f;
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 100.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 8.0 )
			.add( Attributes.FOLLOW_RANGE, 40.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 2.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 )
			.build();
	}

	@SubscribeEvent
	public static void onTick( LivingEvent.LivingUpdateEvent event ) {
		TankEntity tank = CommonHelper.castIfPossible( TankEntity.class, event.getEntityLiving() );
		if( tank != null ) {
			tank.specialAttackTicksLeft = Math.max( tank.specialAttackTicksLeft - 1, 0 );
			tank.normalAttackTicksLeft = Math.max( tank.normalAttackTicksLeft - 1, 0 );
		}
	}

	public void useAttack( AttackType attackType ) {
		switch( attackType ) {
			case NORMAL -> this.normalAttackTicksLeft = NORMAL_ATTACK_DURATION;
			case SPECIAL -> this.specialAttackTicksLeft = SPECIAL_ATTACK_DURATION;
		}

		if( this.level instanceof ServerLevel )
			PacketHandler.CHANNEL.send( PacketDistributor.DIMENSION.with( ()->this.level.dimension() ),
				new SpecialAttackMessage( this, this.specialAttackTicksLeft, attackType )
			);
	}

	public boolean isAttacking() {
		return this.specialAttackTicksLeft > 0 || this.normalAttackTicksLeft > 0;
	}

	public boolean isAttackLastTick() {
		return this.specialAttackTicksLeft == 1 || this.normalAttackTicksLeft == 1;
	}

	/*@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume * 1.25f, pitch * 0.75f );
	}*/

	public float getSpecialAttackDurationRatio() {
		return 1.0f - Mth.clamp( ( float )this.specialAttackTicksLeft / SPECIAL_ATTACK_DURATION, 0.0f, 1.0f );
	}

	public float getNormalAttackDurationRatio() {
		return 1.0f - Mth.clamp( ( float )this.normalAttackTicksLeft / NORMAL_ATTACK_DURATION, 0.0f, 1.0f );
	}

	public static class SpecialAttackMessage extends EntityFloatMessage {
		protected final AttackType attackType;

		public SpecialAttackMessage( Entity entity, float value, AttackType attackType ) {
			super( entity, value );
			this.attackType = attackType;
		}

		public SpecialAttackMessage( FriendlyByteBuf buffer ) {
			super( buffer );
			this.attackType = buffer.readEnum( AttackType.class );
		}

		@Override
		public void encode( FriendlyByteBuf buffer ) {
			super.encode( buffer );
			buffer.writeEnum( this.attackType );
		}

		@Override
		public void receiveMessage( NetworkEvent.Context context ) {
			Level level = Minecraft.getInstance().level;
			if( level != null ) {
				TankEntity tank = CommonHelper.castIfPossible( TankEntity.class, level.getEntity( this.id ) );
				if( tank != null )
					tank.useAttack( this.attackType );
			}
		}
	}

	public enum AttackType {
		NORMAL, SPECIAL
	}
}
