package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class ParasiteEntity extends SpiderEntity {
	public static final EntityType< ParasiteEntity > type;

	static {
		type = EntityType.Builder.create( ParasiteEntity::new, EntityClassification.MONSTER )
			.size( 0.7f, 0.5f )
			.build( MajruszsDifficulty.getLocation( "parasite" )
				.toString() );
	}

	public ParasiteEntity( EntityType< ? extends SpiderEntity > type, World world ) {
		super( type, world );
	}

	@Override
	@Nullable
	public ILivingEntityData onInitialSpawn( IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData entityData, @Nullable CompoundNBT nbt ) {
		return entityData;
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntitySize size ) {
		return 0.45f;
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 14.0 )
			.createMutableAttribute( Attributes.MOVEMENT_SPEED, 0.4 )
			.createMutableAttribute( Attributes.ATTACK_DAMAGE, 4.0 )
			.create();
	}

	/** Spawns teleport particles and plays sounds at given block position. */
	public static void spawnEffects( ServerWorld world, BlockPos position ) {
		world.spawnParticle( ParticleTypes.PORTAL, position.getX(), position.getY(), position.getZ(), 32, 0.5, 0.5, 0.5, 0.25 );
		world.playSound( null, position.getX(), position.getY(), position.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 0.4f, 0.9f );
	}
}
