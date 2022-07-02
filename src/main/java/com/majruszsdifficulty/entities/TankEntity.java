package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.PacketHandler;
import com.majruszsdifficulty.goals.TankAttackGoal;
import com.majruszsdifficulty.undeadarmy.UndeadArmyManager;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.network.message.EntityMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/** A new tough undead similar to the Iron Golem. */
@Mod.EventBusSubscriber
public class TankEntity extends Monster {
	public static final int SPECIAL_ATTACK_DURATION = Utility.secondsToTicks( 0.9 );
	public static final int NORMAL_ATTACK_DURATION = Utility.secondsToTicks( 0.6 );

	public static Supplier< EntityType< TankEntity > > createSupplier() {
		return ()->EntityType.Builder.of( TankEntity::new, MobCategory.MONSTER ).sized( 0.99f, 2.7f ).build( "tank" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 140.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.25 )
			.add( Attributes.ATTACK_DAMAGE, 8.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.ATTACK_KNOCKBACK, 3.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.75 )
			.build();
	}

	public boolean isLeftHandAttack;
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

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolem.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Warden.class, true ) );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 17 );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SKELETON_AMBIENT;
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 2.35f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource damageSource ) {
		return SoundEvents.SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SKELETON_DEATH;
	}

	@Override
	protected void playStepSound( BlockPos blockPos, BlockState blockState ) {
		this.playSound( SoundEvents.SKELETON_STEP, 0.15f, 1.0f );
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume * 1.25f, pitch * 0.75f );
	}

	@Override
	public void aiStep() {
		if( this.isSunBurnTick() && !UndeadArmyManager.isUndeadArmy( this ) )
			this.setSecondsOnFire( 8 );

		super.aiStep();
	}

	public void tick() {
		super.tick();
		this.specialAttackTicksLeft = Math.max( this.specialAttackTicksLeft - 1, 0 );
		this.normalAttackTicksLeft = Math.max( this.normalAttackTicksLeft - 1, 0 );
	}

	public void useAttack( AttackType attackType ) {
		switch( attackType ) {
			case NORMAL -> this.normalAttackTicksLeft = NORMAL_ATTACK_DURATION;
			case SPECIAL -> this.specialAttackTicksLeft = SPECIAL_ATTACK_DURATION;
		}

		this.isLeftHandAttack = Random.nextBoolean();
		if( this.level instanceof ServerLevel )
			PacketHandler.CHANNEL.send( PacketDistributor.DIMENSION.with( ()->this.level.dimension() ), new TankAttackMessage( this, attackType ) );
	}

	public boolean isAttacking() {
		return ( this.normalAttackTicksLeft + this.specialAttackTicksLeft ) > 0;
	}

	public boolean isAttacking( AttackType attackType ) {
		return switch( attackType ) {
			case NORMAL -> this.normalAttackTicksLeft > 0;
			case SPECIAL -> this.specialAttackTicksLeft > 0;
		};
	}

	public boolean isAttackLastTick() {
		return this.specialAttackTicksLeft == 1 || this.normalAttackTicksLeft == 1;
	}

	public float calculateAttackDurationRatioLeft() {
		float ratio;
		if( this.specialAttackTicksLeft > this.normalAttackTicksLeft ) {
			ratio = ( float )this.specialAttackTicksLeft / SPECIAL_ATTACK_DURATION;
		} else {
			ratio = ( float )this.normalAttackTicksLeft / NORMAL_ATTACK_DURATION;
		}

		return 1.0f - Mth.clamp( ratio, 0.0f, 1.0f );
	}

	public enum AttackType {
		NORMAL, SPECIAL
	}

	public static class TankAttackMessage extends EntityMessage {
		protected final AttackType attackType;

		public TankAttackMessage( Entity entity, AttackType attackType ) {
			super( entity );
			this.attackType = attackType;
		}

		public TankAttackMessage( FriendlyByteBuf buffer ) {
			super( buffer );
			this.attackType = buffer.readEnum( AttackType.class );
		}

		@Override
		public void encode( FriendlyByteBuf buffer ) {
			super.encode( buffer );
			buffer.writeEnum( this.attackType );
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void receiveMessage( NetworkEvent.Context context ) {
			Level level = Minecraft.getInstance().level;
			if( level == null )
				return;

			TankEntity tank = Utility.castIfPossible( TankEntity.class, level.getEntity( this.id ) );
			if( tank != null )
				tank.useAttack( this.attackType );
		}
	}
}
