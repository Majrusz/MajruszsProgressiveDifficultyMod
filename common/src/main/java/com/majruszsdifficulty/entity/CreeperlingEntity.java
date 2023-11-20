package com.majruszsdifficulty.entity;

import com.majruszlibrary.contexts.OnExploded;
import com.majruszsdifficulty.mixin.IMixinCreeper;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class CreeperlingEntity extends Creeper {
	static {
		OnExploded.listen( CreeperlingEntity::weakenExplosion )
			.addCondition( data->data.entity instanceof CreeperlingEntity );
	}

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

	private static void weakenExplosion( OnExploded data ) {
		data.skipBlockIf( blockPos->true );
		data.skipEntityIf( entity->!( entity instanceof LivingEntity ) );
	}
}
