package com.majruszsdifficulty.entity;

import com.majruszsdifficulty.mixin.IMixinCreeper;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnExploded;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class CreeperlingEntity extends Creeper {
	public static EntityType< CreeperlingEntity > createEntityType() {
		return EntityType.Builder.of( CreeperlingEntity::new, MobCategory.MONSTER )
			.sized( 0.6f, 0.9f )
			.build( "creeperling" );
	}

	public static AttributeSupplier createChildAttributes() {
		return Creeper.createAttributes()
			.add( Attributes.MAX_HEALTH, 6.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.35 )
			.build();
	}

	public CreeperlingEntity( EntityType< ? extends Creeper > entityType, Level level ) {
		super( entityType, level );

		( ( IMixinCreeper )this ).setExplosionRadius( 2 );
	}

	@Override
	public boolean isPowered() {
		return false; // disables charged Creeperling
	}

	@Override
	public int getExperienceReward() {
		return 3;
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntityDimensions size ) {
		return 0.75f;
	}

	@AutoInstance
	public static class WeakExplosions {
		public WeakExplosions() {
			OnExploded.listen( this::weakenExplosion )
				.addCondition( data->data.entity instanceof CreeperlingEntity );
		}

		private void weakenExplosion( OnExploded data ) {
			data.skipBlockIf( blockPos->true );
			data.skipEntityIf( entity->!( entity instanceof LivingEntity ) );
		}
	}
}
