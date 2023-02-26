package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.entities.CreeperlingEntity;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnExplosion;
import com.mlib.math.Range;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

@AutoInstance
public class CreeperSplitIntoCreeperlings extends GameModifier {
	final GameStageIntegerConfig creeperlingsAmount = new GameStageIntegerConfig( 2, 4, 6, new Range<>( 1, 10 ) );

	public CreeperSplitIntoCreeperlings() {
		super( Registries.Modifiers.DEFAULT );

		new OnExplosion.Context( this::spawnCreeperlings )
			.addCondition( new CustomConditions.GameStage<>( GameStage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.666, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( data->data.explosion.getExploder() instanceof Creeper && !( data.explosion.getExploder() instanceof CreeperlingEntity ) )
			.addCondition( data->data.event instanceof ExplosionEvent.Detonate )
			.addConfig( this.creeperlingsAmount.name( "MaxCreeperlings" ).comment( "Maximum amount of Creeperlings to spawn." ) )
			.insertTo( this );

		new OnExplosion.Context( this::giveAdvancement )
			.addCondition( data->data.explosion.getExploder() instanceof CreeperlingEntity && data.level != null )
			.insertTo( this );

		new OnDeath.Context( this::giveAdvancement )
			.addCondition( data->data.attacker instanceof ServerPlayer )
			.addCondition( data->data.target instanceof CreeperlingEntity )
			.insertTo( this );

		this.name( "CreeperSplitIntoCreeperlings" ).comment( "When the Creeper explode it may spawn a few Creeperlings." );
	}

	private void spawnCreeperlings( OnExplosion.Data data ) {
		Creeper creeper = ( Creeper )data.explosion.getExploder();
		ServerLevel level = data.level;
		int creeperlingsAmount = Random.nextInt( 1, this.creeperlingsAmount.getCurrentGameStageValue() + 1 );

		assert creeper != null && level != null;
		for( int i = 0; i < creeperlingsAmount; ++i ) {
			BlockPos position = creeper.blockPosition().offset( Random.getRandomVector3i( -2, 2, -1, 1, -2, 2 ) );
			CreeperlingEntity creeperling = Registries.CREEPERLING.get()
				.spawn( level, ( CompoundTag )null, null, null, position, MobSpawnType.SPAWNER, true, true );
			if( creeperling != null )
				creeperling.setTarget( creeper.getTarget() );
		}
	}

	private void giveAdvancement( OnExplosion.Data data ) {
		assert data.level != null;
		Vec3 position = data.event.getExplosion().getPosition();
		Vec3 offset = new Vec3( 10.0, 6.0, 10.0 );
		List< ServerPlayer > nearbyPlayers = data.level.getEntitiesOfClass( ServerPlayer.class, new AABB( position.subtract( offset ), position.add( offset ) ) );
		nearbyPlayers.forEach( this::giveAdvancement );
	}

	private void giveAdvancement( OnDeath.Data data ) {
		giveAdvancement( ( ServerPlayer )data.attacker );
	}

	private void giveAdvancement( ServerPlayer player ) {
		Registries.BASIC_TRIGGER.trigger( player, "encountered_creeperling" );
	}
}
