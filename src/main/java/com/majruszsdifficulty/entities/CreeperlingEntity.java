package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.Registries;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnExplosionDetonate;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class CreeperlingEntity extends Creeper {
	public static Supplier< EntityType< CreeperlingEntity > > createSupplier() {
		return ()->EntityType.Builder.of( CreeperlingEntity::new, MobCategory.MONSTER )
			.sized( 0.6f, 0.9f )
			.build( "creeperling" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Monster.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 6.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.35 )
			.build();
	}

	public CreeperlingEntity( EntityType< ? extends CreeperlingEntity > type, Level level ) {
		super( type, level );

		this.explosionRadius = 2;
		this.xpReward = 3;
	}

	@Override
	public boolean isPowered() {
		return false; // creeperling will not be charged
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntityDimensions sizeIn ) {
		return 0.75f;
	}

	@AutoInstance
	public static class WeakExplosions {
		public WeakExplosions() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
				.name( "CreeperlingWeakExplosion" )
				.comment( "Makes Creeperling explosions not destroy blocks and items." );

			OnExplosionDetonate.listen( this::weakenExplosion )
				.addCondition( Condition.excludable() )
				.addCondition( Condition.predicate( data->data.explosion.getExploder() instanceof CreeperlingEntity ) )
				.insertTo( group );
		}

		private void weakenExplosion( OnExplosionDetonate.Data data ) {
			data.explosion.clearToBlow();
			data.event.getAffectedEntities().removeIf( entity->!( entity instanceof LivingEntity ) );
		}
	}
}
