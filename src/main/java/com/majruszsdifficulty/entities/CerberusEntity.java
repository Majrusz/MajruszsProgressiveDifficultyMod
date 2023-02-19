package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.PacketHandler;
import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.entities.CustomSkills;
import com.mlib.entities.EntityHelper;
import com.mlib.entities.ICustomSkillProvider;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnEffectApplicable;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.goals.CustomMeleeGoal;
import com.mlib.math.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public class CerberusEntity extends Monster implements ICustomSkillProvider< CerberusEntity.Skills > {
	static final String GROUP_ID = "Cerberus";

	static {
		GameModifier.addNewGroup( Registries.Modifiers.MOBS, GROUP_ID ).name( "Cerberus" );
	}

	public final Skills skills = new Skills( this );
	public boolean hasTarget = false;

	public static Supplier< EntityType< CerberusEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CerberusEntity::new, MobCategory.MONSTER ).sized( 1.0f, 1.99f ).build( "cerberus" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 240.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 10.0 )
			.add( Attributes.FOLLOW_RANGE, 30.0 )
			.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 )
			.add( ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0 )
			.build();
	}

	public CerberusEntity( EntityType< ? extends CerberusEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public Skills getCustomSkills() {
		return this.skills;
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 60, 90 );
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( this.isSilent() ) {
			return;
		}
		boolean isCustomSound = sound == Registries.CERBERUS_GROWL.get() || sound == Registries.CERBERUS_BITE.get();
		float randomizedVolume = SoundHandler.randomized( volume * 1.25f ).get();
		float randomizedPitch = SoundHandler.randomized( isCustomSound ? pitch : pitch * 0.75f ).get();

		this.level.playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), randomizedVolume, randomizedPitch );
	}

	@Override
	public void tick() {
		super.tick();

		this.skills.tick();
		boolean hasTarget = this.getTarget() != null;
		if( hasTarget != this.hasTarget && !this.level.isClientSide ) {
			this.hasTarget = hasTarget;
			PacketHandler.CHANNEL.send( PacketDistributor.DIMENSION.with( ()->this.level.dimension() ), new TargetMessage( this ) );
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal( 1, new CustomMeleeGoal<>( this, 1.5, false ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomStrollGoal( this, 1.0 ) );
		this.goalSelector.addGoal( 8, new LookAtPlayerGoal( this, Player.class, 8.0f, 1.0f ) );
		this.goalSelector.addGoal( 8, new RandomLookAroundGoal( this ) );

		this.targetSelector.addGoal( 1, new HurtByTargetGoal( this ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, Mob.class, 2, true, false, CerberusEntity::isValidTarget ) );
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return Registries.CERBERUS_GROWL.get();
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 1.8f;
	}

	@Override
	protected SoundEvent getHurtSound( DamageSource damageSource ) {
		return SoundEvents.WITHER_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.WITHER_SKELETON_DEATH;
	}

	@Override
	protected void playStepSound( BlockPos blockPos, BlockState blockState ) {
		this.playSound( SoundEvents.WITHER_SKELETON_STEP, 0.15f, 1.0f );
	}

	private static boolean isValidTarget( LivingEntity entity ) {
		return !Registries.UNDEAD_ARMY_MANAGER.isPartOfUndeadArmy( entity )
			&& !( entity instanceof CerberusEntity );
	}

	public static class Skills extends CustomSkills< SkillType > {
		public Skills( PathfinderMob mob ) {
			super( mob, PacketHandler.CHANNEL, SkillMessage::new );
		}

		@Override
		public boolean tryToStart( LivingEntity entity, double distanceSquared ) {
			if( Math.sqrt( distanceSquared ) >= 3.5 || !( this.mob.level instanceof ServerLevel level ) ) {
				return false;
			}

			Vec3 position = this.getAttackPosition( this.mob.position(), entity.position() );
			this.pushMobTowards( entity );
			this.mob.playSound( Registries.CERBERUS_BITE.get(), 1.0f, 1.0f );
			this.start( SkillType.BITE, Utility.secondsToTicks( 0.7 ) )
				.onRatio( 0.55f, ()->this.hurtAllEntitiesInRange( level, position ) );

			return true;
		}

		private void pushMobTowards( LivingEntity entity ) {
			Vec3 direction = VectorHelper.multiply( VectorHelper.normalize( VectorHelper.subtract( entity.position(), this.mob.position() ) ), 0.9 );
			this.mob.push( direction.x, direction.y + 0.1, direction.z );
		}

		private void hurtAllEntitiesInRange( ServerLevel level, Vec3 position ) {
			List< LivingEntity > entities = EntityHelper.getEntitiesInSphere( LivingEntity.class, level, position, 2.5, entity->!entity.is( this.mob ) );
			for( LivingEntity entity : entities ) {
				this.mob.doHurtTarget( entity );
				if( entity instanceof ServerPlayer player && player.isBlocking() ) {
					player.disableShield( true );
				}
			}
		}

		private Vec3 getAttackPosition( Vec3 pos1, Vec3 pos2 ) {
			return VectorHelper.add( pos1, VectorHelper.multiply( VectorHelper.normalize( VectorHelper.subtract( pos2, pos1 ) ), 1.75 ) );
		}
	}

	public static class SkillMessage extends CustomSkills.Message< SkillType > {
		public SkillMessage( Entity entity, int ticks, SkillType skillType ) {
			super( entity, ticks, skillType, SkillType::values );
		}

		public SkillMessage() {
			super( SkillType::values );
		}
	}

	public static class TargetMessage extends SerializableStructure {
		int entityId;
		boolean hasTarget = false;

		public TargetMessage( CerberusEntity cerberus ) {
			this();

			this.entityId = cerberus.getId();
			this.hasTarget = cerberus.hasTarget;
		}

		public TargetMessage() {
			this.define( null, ()->this.entityId, x->this.entityId = x );
			this.define( null, ()->this.hasTarget, x->this.hasTarget = x );
		}

		@OnlyIn( Dist.CLIENT )
		public void onClient( NetworkEvent.Context context ) {
			Level level = Minecraft.getInstance().level;
			if( level != null && level.getEntity( this.entityId ) instanceof CerberusEntity cerberus ) {
				cerberus.hasTarget = this.hasTarget;
			}
		}
	}

	@AutoInstance
	public static class WitherAttack extends GameModifier {
		final EffectConfig wither = new EffectConfig( MobEffects.WITHER, 1, 10.0 );

		public WitherAttack() {
			super( GROUP_ID );

			new OnDamaged.Context( this::applyWither )
				.addCondition( data->data.attacker instanceof CerberusEntity )
				.addConfig( this.wither.name( "Wither" ) )
				.insertTo( this );

			new OnEntityTick.Context( this::spawnParticle )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.Cooldown< OnEntityTick.Data >( 4, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( data->data.entity instanceof CerberusEntity )
				.insertTo( this );
		}

		private void applyWither( OnDamaged.Data data ) {
			this.wither.apply( data.target );
		}

		private void spawnParticle( OnEntityTick.Data data ) {
			ParticleHandler.SMOKE.spawn( data.level, data.entity.position().add( 0.0, 0.75, 0.0 ), 3, ()->new Vec3( 0.25, 0.5, 0.25 ) );
		}
	}

	public enum SkillType {
		BITE
	}
}
