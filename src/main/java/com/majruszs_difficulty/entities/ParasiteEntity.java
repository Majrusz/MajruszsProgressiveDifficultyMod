package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class ParasiteEntity extends Spider {
	public static final EntityType< ParasiteEntity > type;

	static {
		type = EntityType.Builder.of( ParasiteEntity::new, MobCategory.MONSTER )
			.sized( 0.7f, 0.5f )
			.build( MajruszsDifficulty.getLocation( "parasite" )
				.toString() );
	}

	public ParasiteEntity( EntityType< ? extends Spider > type, Level world ) {
		super( type, world );

		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Animal.class, true ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Villager.class, true ) );
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn( ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType type,
		@Nullable SpawnGroupData data, @Nullable CompoundTag tag
	) {
		return data;
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntityDimensions size ) {
		return 0.45f;
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 14.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.4 )
			.add( Attributes.ATTACK_DAMAGE, 2.0 )
			.build();
	}

	@Override
	public boolean isSensitiveToWater() {
		return true;
	}

	/** Spawns teleport particles and plays sounds at given block position. */
	public static void spawnEffects( ServerLevel world, BlockPos position ) {
		world.sendParticles( ParticleTypes.PORTAL, position.getX(), position.getY(), position.getZ(), 32, 0.5, 0.5, 0.5, 0.25 );
		world.playSound( null, position.getX(), position.getY(), position.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.4f, 0.9f );
	}
}
