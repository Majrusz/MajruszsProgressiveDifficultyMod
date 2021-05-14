package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.WorldHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.events.ExplosionSizeEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszs_difficulty.Instances.STRONGER_EXPLOSIONS;

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
		LivingEntity causer = event.explosion.getExploder() instanceof LivingEntity ? ( LivingEntity )event.explosion.getExploder() : event.explosion.getExplosivePlacedBy();

		event.size *= STRONGER_EXPLOSIONS.biggerSize.getRadius( causer );
		MajruszLibrary.LOGGER.info( STRONGER_EXPLOSIONS.biggerSize.getRadius( causer ) );
		if( Random.tryChance( STRONGER_EXPLOSIONS.causingFire.calculateChance( causer ) ) )
			event.causesFire = true;
	}

	public static class BiggerSize extends FeatureBase {
		private static final String CONFIG_NAME = "BiggerExplosionSize";
		private static final String CONFIG_COMMENT = "Increases size of all explosions. (creeper, tnt, ghast ball etc.)";
		protected final DoubleConfig maximumMultiplier;

		public BiggerSize() {
			super( CONFIG_NAME, CONFIG_COMMENT, GameState.State.NORMAL );

			String multiplierComment = "Explosion radius multiplier. (this value is scaled by Clamped Regional Difficulty)";
			this.maximumMultiplier = new DoubleConfig( "maximum_multiplier", multiplierComment, false, 2.0, 1.0, 10.0 );

			this.featureGroup.addConfig( this.maximumMultiplier );
		}

		/** Returns current radius multiplier depending on Clamped Regional Difficulty. */
		public float getRadius( LivingEntity entity ) {
			double factor = this.maximumMultiplier.get() - 1.0;
			double difficultyFactor = entity != null ? WorldHelper.getClampedRegionalDifficulty( entity ) : 0.5;

			return ( float )( 1.0 + factor * difficultyFactor );
		}
	}

	public static class CausingFire extends ChanceFeatureBase {
		private static final String CONFIG_NAME = "ExplosionCausingFire";
		private static final String CONFIG_COMMENT = "Makes all explosions to leave fire.";

		public CausingFire() {
			super( CONFIG_NAME, CONFIG_COMMENT, 1.0, GameState.State.EXPERT, true );
		}
	}
}
