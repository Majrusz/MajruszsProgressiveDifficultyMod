package com.majruszsdifficulty.features.special;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.features.ChanceFeatureBase;
import com.majruszsdifficulty.features.FeatureBase;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.events.ExplosionSizeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static com.majruszsdifficulty.Instances.STRONGER_EXPLOSIONS;

/** Increases explosion size and makes the explosion always spawn fire after reaching certain game state. */
@Mod.EventBusSubscriber
public class StrongerExplosions {
	private final BiggerSize biggerSize;
	private final CausingFire causingFire;

	public StrongerExplosions() {
		this.biggerSize = new BiggerSize();
		this.causingFire = new CausingFire();
	}

	@SubscribeEvent
	public static void onExplosion( ExplosionSizeEvent event ) {
		LivingEntity causer = event.explosion.getExploder() instanceof LivingEntity ? ( LivingEntity )event.explosion.getExploder() : event.explosion.getSourceMob();
		if( causer == null )
			causer = getNearestEntity( event.explosion, event.level );

		if( STRONGER_EXPLOSIONS.biggerSize.isEnabled() )
			event.size *= STRONGER_EXPLOSIONS.biggerSize.getRadius( causer );

		if( !( causer instanceof CreeperlingEntity ) && STRONGER_EXPLOSIONS.causingFire.isEnabled() && Random.tryChance(
			STRONGER_EXPLOSIONS.causingFire.calculateChance( causer ) ) )
			event.causesFire = true;
	}

	/** Returns nearest entity to given explosion. (required to calculating regional difficulty) */
	private static @Nullable
	LivingEntity getNearestEntity( Explosion explosion, Level world ) {
		Vec3 position = explosion.getPosition();
		double offset = 50.0;
		AABB axisAlignedBB = new AABB( position.x - offset, position.y - offset, position.z - offset, position.x + offset, position.y + offset,
			position.z + offset
		);
		for( LivingEntity entity : world.getEntitiesOfClass( LivingEntity.class, axisAlignedBB ) )
			return entity;

		return null;
	}

	public static class BiggerSize extends FeatureBase {
		private static final String CONFIG_NAME = "BiggerExplosionSize";
		private static final String CONFIG_COMMENT = "Increases size of all explosions. (creeper, tnt, ghast ball etc.)";
		protected final DoubleConfig maximumMultiplier;

		public BiggerSize() {
			super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.NORMAL );

			String multiplierComment = "Explosion radius multiplier. (this value is scaled by Clamped Regional Difficulty)";
			this.maximumMultiplier = new DoubleConfig( "maximum_multiplier", multiplierComment, false, 1.2599, 1.0, 10.0 );

			this.featureGroup.addConfig( this.maximumMultiplier );
		}

		/** Returns current radius multiplier depending on Clamped Regional Difficulty. */
		public float getRadius( LivingEntity entity ) {
			double factor = this.maximumMultiplier.get() - 1.0;
			double difficultyFactor = entity != null ? GameState.getRegionalDifficulty( entity ) : 0.5;

			return ( float )( 1.0 + factor * difficultyFactor );
		}
	}

	public static class CausingFire extends ChanceFeatureBase {
		private static final String CONFIG_NAME = "ExplosionCausingFire";
		private static final String CONFIG_COMMENT = "Makes all explosions leave fire.";

		public CausingFire() {
			super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.EXPERT, true );
		}
	}
}
