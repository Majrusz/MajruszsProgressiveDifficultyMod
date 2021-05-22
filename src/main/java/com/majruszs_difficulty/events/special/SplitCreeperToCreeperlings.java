package com.majruszs_difficulty.events.special;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.majruszs_difficulty.entities.CreeperlingEntity;
import com.majruszs_difficulty.events.ChanceFeatureBase;
import com.mlib.MajruszLibrary;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, GameState.State.NORMAL, false );

		String creeperlingComment = "Maximum amount of Creeperlings.";
		this.creeperlingsAmount = new GameStateIntegerConfig( "maximum_creeperlings", creeperlingComment, 1, 3, 5, 0, 10 );
		this.featureGroup.addConfig( this.creeperlingsAmount );
	}

	@SubscribeEvent
	public static void onExplosion( ExplosionEvent.Detonate event ) {
		if( !isValid( event.getExplosion(), event.getWorld() ) )
			return;

		SplitCreeperToCreeperlings splitCreeperToCreeperlings = Instances.SPLIT_CREEPER_TO_CREEPERLINGS;
		Explosion explosion = event.getExplosion();
		CreeperEntity creeper = ( CreeperEntity )explosion.getExploder();
		ServerWorld world = ( ServerWorld )event.getWorld();
		int amountOfCreeperlings = splitCreeperToCreeperlings.getRandomAmountOfCreeperlings();
		if( creeper == null )
			return;

		for( int i = 0; i < amountOfCreeperlings; ++i ) {
			BlockPos position = getNearbyPosition( creeper );

			CreeperlingEntity creeperling = CreeperlingEntity.type.spawn( world, null, null, null, position, SpawnReason.SPAWNER, true, true );
			if( creeperling != null )
				creeperling.setAttackTarget( creeper.getAttackTarget() );
		}
	}

	/** Returns position nearby given Creeper. */
	private static BlockPos getNearbyPosition( CreeperEntity creeper ) {
		BlockPos position = creeper.getPosition();
		double x = position.getX() + MajruszLibrary.RANDOM.nextDouble() - 0.5, y = position.getY() + MajruszLibrary.RANDOM.nextDouble() - 0.5, z = position.getZ() + MajruszLibrary.RANDOM.nextDouble() - 0.5;

		return new BlockPos( x, y, z );
	}

	/** Checks whether feature should be called. */
	private static boolean isValid( Explosion explosion, World world ) {
		SplitCreeperToCreeperlings splitCreeperToCreeperlings = Instances.SPLIT_CREEPER_TO_CREEPERLINGS;
		boolean isCausedByCreeper = explosion.getExploder() instanceof CreeperEntity && !( explosion.getExploder() instanceof CreeperlingEntity );
		boolean isServerWorld = world instanceof ServerWorld;

		return isCausedByCreeper && isServerWorld && splitCreeperToCreeperlings.isEnabled() && splitCreeperToCreeperlings.tryChance( null );
	}

	/** Returns random amount of Creeperlings from range [0; maximum]. */
	protected int getRandomAmountOfCreeperlings() {
		return MajruszLibrary.RANDOM.nextInt( this.creeperlingsAmount.getCurrentGameStateValue() + 1 );
	}
}
