package com.majruszsdifficulty.entity;

import com.majruszlibrary.events.OnExploded;
import com.majruszsdifficulty.mixin.IMixinCreeper;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class Creeperling extends Creeper {
	static {
		OnExploded.listen( Creeperling::weakenExplosion )
			.addCondition( data->data.entity instanceof Creeperling );
	}

	public static EntityType< Creeperling > createEntityType() {
		return EntityType.Builder.of( Creeperling::new, MobCategory.MONSTER )
			.sized( 0.6f, 0.9f )
			.build( "creeperling" );
	}

	public static AttributeSupplier createChildAttributes() {
		return Creeper.createAttributes()
			.add( Attributes.MAX_HEALTH, 6.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.35 )
			.build();
	}

	public Creeperling( EntityType< ? extends Creeper > entityType, Level level ) {
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
