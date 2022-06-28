package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Random;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.world.ExplosionEvent;

public class CreeperSplitIntoCreeperlings extends GameModifier {
	static final OnExplosionContext ON_EXPLOSION = new OnExplosionContext( CreeperSplitIntoCreeperlings::spawnCreeperlings );
	static final GameStageIntegerConfig CREEPERLINGS_AMOUNT = new GameStageIntegerConfig( "MaxCreeperlings", "Maximum amount of Creeperlings to spawn.", 2, 4, 6, 1, 10 );

	static {
		ON_EXPLOSION.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_EXPLOSION.addCondition( new Condition.Chance( 0.666 ) );
		ON_EXPLOSION.addCondition( new Condition.Excludable() );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.explosion.getExploder() instanceof Creeper && !( data.explosion.getExploder() instanceof CreeperlingEntity ) ) );
		ON_EXPLOSION.addCondition( new Condition.ContextOnExplosion( data->data.event instanceof ExplosionEvent.Detonate ) );
		ON_EXPLOSION.addConfig( CREEPERLINGS_AMOUNT );
	}

	public CreeperSplitIntoCreeperlings() {
		super( GameModifier.DEFAULT, "CreeperSplitIntoCreeperlings", "When the Creeper explode it may spawn a few Creeperlings.", ON_EXPLOSION );
	}

	private static void spawnCreeperlings( com.mlib.gamemodifiers.GameModifier gameModifier, OnExplosionContext.Data data ) {
		Creeper creeper = ( Creeper )data.explosion.getExploder();
		ServerLevel level = data.level;
		int creeperlingsAmount = Random.nextInt( 1, CREEPERLINGS_AMOUNT.getCurrentGameStageValue() + 1 );

		assert creeper != null && level != null;
		for( int i = 0; i < creeperlingsAmount; ++i ) {
			BlockPos position = creeper.blockPosition().offset( Random.getRandomVector3i( -2, 2, -1, 1, -2, 2 ) );
			CreeperlingEntity creeperling = Registries.CREEPERLING.get().spawn( level, null, null, null, position, MobSpawnType.SPAWNER, true, true );
			if( creeperling != null )
				creeperling.setTarget( creeper.getTarget() );
		}
	}
}
