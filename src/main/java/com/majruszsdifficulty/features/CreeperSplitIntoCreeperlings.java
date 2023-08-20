package com.majruszsdifficulty.features;

import com.majruszsdifficulty.config.GameStageConfig;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.mlib.Random;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDeath;
import com.mlib.contexts.OnExplosionDetonate;
import com.mlib.math.Range;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@AutoInstance
public class CreeperSplitIntoCreeperlings {
	final GameStageConfig< Integer > creeperlingsAmount = GameStageConfig.create( 2, 4, 6, new Range<>( 1, 10 ) );

	public CreeperSplitIntoCreeperlings() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "CreeperSplitIntoCreeperlings" )
			.comment( "When the Creeper explode it may spawn a few Creeperlings." );

		OnExplosionDetonate.listen( this::spawnCreeperlings )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.666, true ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.explosion.getExploder() instanceof Creeper && !( data.explosion.getExploder() instanceof CreeperlingEntity ) ) )
			.addConfig( this.creeperlingsAmount.name( "MaxCreeperlings" ).comment( "Maximum amount of Creeperlings to spawn." ) )
			.insertTo( group );

		OnExplosionDetonate.listen( this::giveAdvancement )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.predicate( data->data.explosion.getExploder() instanceof CreeperlingEntity ) )
			.insertTo( group );

		OnDeath.listen( this::giveAdvancement )
			.addCondition( Condition.predicate( data->data.attacker instanceof ServerPlayer ) )
			.addCondition( Condition.predicate( data->data.target instanceof CreeperlingEntity ) )
			.insertTo( group );
	}

	private void spawnCreeperlings( OnExplosionDetonate.Data data ) {
		Creeper creeper = ( Creeper )data.explosion.getExploder();
		ServerLevel level = data.getServerLevel();
		int creeperlingsAmount = Random.nextInt( 1, this.creeperlingsAmount.getCurrentGameStageValue() + 1 );

		assert creeper != null && level != null;
		for( int i = 0; i < creeperlingsAmount; ++i ) {
			BlockPos position = creeper.blockPosition().offset( Random.nextVector( -2, 2, -1, 1, -2, 2 ).vec3i() );
			CreeperlingEntity creeperling = Registries.CREEPERLING.get()
				.spawn( level, ( CompoundTag )null, null, position, MobSpawnType.SPAWNER, true, true );
			if( creeperling != null )
				creeperling.setTarget( creeper.getTarget() );
		}
	}

	private void giveAdvancement( OnExplosionDetonate.Data data ) {
		ServerLevel level = data.getServerLevel();
		Vec3 position = data.event.getExplosion().getPosition();
		Vec3 offset = new Vec3( 10.0, 6.0, 10.0 );
		List< ServerPlayer > nearbyPlayers = level.getEntitiesOfClass( ServerPlayer.class, new AABB( position.subtract( offset ), position.add( offset ) ) );
		nearbyPlayers.forEach( this::giveAdvancement );
	}

	private void giveAdvancement( OnDeath.Data data ) {
		giveAdvancement( ( ServerPlayer )data.attacker );
	}

	private void giveAdvancement( ServerPlayer player ) {
		Registries.HELPER.triggerAchievement( player, "encountered_creeperling" );
	}
}
