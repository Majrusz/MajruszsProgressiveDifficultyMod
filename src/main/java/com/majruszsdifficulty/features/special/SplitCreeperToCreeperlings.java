package com.majruszsdifficulty.features.special;

import com.majruszsdifficulty.GameState;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStateIntegerConfig;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.features.ChanceFeatureBase;
import com.mlib.MajruszLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Whenever Creeper dies there is a chance it will spawn few Creeperlings. */
@Mod.EventBusSubscriber
public class SplitCreeperToCreeperlings extends ChanceFeatureBase {
	private static final String CONFIG_NAME = "SplitCreeper";
	private static final String CONFIG_COMMENT = "Creepers after the explosion have a chance to spawn Creeperlings.";
	protected final GameStateIntegerConfig creeperlingsAmount;

	public SplitCreeperToCreeperlings() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.666, GameState.State.NORMAL, false );

		String creeperlingComment = "Maximum amount of Creeperlings.";
		this.creeperlingsAmount = new GameStateIntegerConfig( "maximum_creeperlings", creeperlingComment, 1, 3, 5, 0, 10 );
		this.featureGroup.addConfig( this.creeperlingsAmount );
	}

	@SubscribeEvent
	public static void onExplosion( ExplosionEvent.Detonate event ) {
		if( !isValid( event.getExplosion(), event.getWorld() ) )
			return;

		SplitCreeperToCreeperlings splitCreeperToCreeperlings = Registries.SPLIT_CREEPER_TO_CREEPERLINGS;
		Explosion explosion = event.getExplosion();
		Creeper creeper = ( Creeper )explosion.getExploder();
		ServerLevel world = ( ServerLevel )event.getWorld();
		int amountOfCreeperlings = splitCreeperToCreeperlings.getRandomAmountOfCreeperlings();
		if( creeper == null )
			return;

		for( int i = 0; i < amountOfCreeperlings; ++i ) {
			BlockPos position = getNearbyPosition( creeper );

			CreeperlingEntity creeperling = Registries.CREEPERLING.get().spawn( world, null, null, null, position, MobSpawnType.SPAWNER, true, true );
			if( creeperling != null )
				creeperling.setTarget( creeper.getTarget() );
		}
	}

	/** Returns position nearby given Creeper. */
	private static BlockPos getNearbyPosition( Creeper creeper ) {
		BlockPos position = creeper.blockPosition();
		double x = position.getX() + MajruszLibrary.RANDOM.nextDouble() - 0.5, y = position.getY() + MajruszLibrary.RANDOM.nextDouble() - 0.5, z = position.getZ() + MajruszLibrary.RANDOM.nextDouble() - 0.5;

		return new BlockPos( x, y, z );
	}

	/** Checks whether feature should be called. */
	private static boolean isValid( Explosion explosion, Level world ) {
		SplitCreeperToCreeperlings splitCreeperToCreeperlings = Registries.SPLIT_CREEPER_TO_CREEPERLINGS;
		boolean isCausedByCreeper = explosion.getExploder() instanceof Creeper && !( explosion.getExploder() instanceof CreeperlingEntity );
		boolean isServerLevel = world instanceof ServerLevel;

		return isCausedByCreeper && isServerLevel && splitCreeperToCreeperlings.isEnabled() && splitCreeperToCreeperlings.tryChance( null );
	}

	/** Returns random amount of Creeperlings from range [0; maximum]. */
	protected int getRandomAmountOfCreeperlings() {
		return MajruszLibrary.RANDOM.nextInt( this.creeperlingsAmount.getCurrentGameStateValue() + 1 );
	}
}
