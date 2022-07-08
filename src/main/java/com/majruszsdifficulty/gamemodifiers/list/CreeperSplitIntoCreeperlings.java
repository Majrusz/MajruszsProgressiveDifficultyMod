package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDeathContext;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import com.mlib.gamemodifiers.data.OnDeathData;
import com.mlib.gamemodifiers.data.OnExplosionData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.List;

public class CreeperSplitIntoCreeperlings extends GameModifier {
	final GameStageIntegerConfig creeperlingsAmount = new GameStageIntegerConfig( "MaxCreeperlings", "Maximum amount of Creeperlings to spawn.", 2, 4, 6, 1, 10 );

	public CreeperSplitIntoCreeperlings() {
		super( GameModifier.DEFAULT, "CreeperSplitIntoCreeperlings", "When the Creeper explode it may spawn a few Creeperlings." );

		OnExplosionContext onExplosion = new OnExplosionContext( this::spawnCreeperlings );
		onExplosion.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new Condition.Chance( 0.666 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.explosion.getExploder() instanceof Creeper && !( data.explosion.getExploder() instanceof CreeperlingEntity ) )
			.addCondition( data->data.event instanceof ExplosionEvent.Detonate )
			.addConfig( this.creeperlingsAmount );

		OnExplosionContext onExplosion2 = new OnExplosionContext( this::giveAdvancement );
		onExplosion2.addCondition( data->data.explosion.getExploder() instanceof CreeperlingEntity && data.level != null );

		OnDeathContext onDeath = new OnDeathContext( this::giveAdvancement );
		onDeath.addCondition( data->data.attacker instanceof ServerPlayer );

		this.addContexts( onExplosion, onExplosion2, onDeath );
	}

	private void spawnCreeperlings( OnExplosionData data ) {
		Creeper creeper = ( Creeper )data.explosion.getExploder();
		ServerLevel level = data.level;
		int creeperlingsAmount = Random.nextInt( 1, this.creeperlingsAmount.getCurrentGameStageValue() + 1 );

		assert creeper != null && level != null;
		for( int i = 0; i < creeperlingsAmount; ++i ) {
			BlockPos position = creeper.blockPosition().offset( Random.getRandomVector3i( -2, 2, -1, 1, -2, 2 ) );
			CreeperlingEntity creeperling = Registries.CREEPERLING.get().spawn( level, null, null, null, position, MobSpawnType.SPAWNER, true, true );
			if( creeperling != null )
				creeperling.setTarget( creeper.getTarget() );
		}
	}

	private void giveAdvancement( OnExplosionData data ) {
		assert data.level != null;
		Vec3 position = data.event.getExplosion().getPosition();
		Vec3 offset = new Vec3( 10.0, 6.0, 10.0 );
		List< ServerPlayer > nearbyPlayers = data.level.getEntitiesOfClass( ServerPlayer.class, new AABB( position.subtract( offset ), position.add( offset ) ) );
		nearbyPlayers.forEach( this::giveAdvancement );
	}

	private void giveAdvancement( OnDeathData data ) {
		giveAdvancement( ( ServerPlayer )data.attacker );
	}

	private void giveAdvancement( ServerPlayer player ) {
		Registries.BASIC_TRIGGER.trigger( player, "encountered_creeperling" );
	}
}
