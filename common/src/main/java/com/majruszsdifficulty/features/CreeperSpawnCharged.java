package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntitySpawned;
import com.mlib.contexts.base.Condition;
import com.mlib.data.Serializables;
import com.mlib.math.Range;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperSpawnCharged {
	private GameStageValue< Boolean > isEnabled = GameStageValue.alwaysEnabled();
	private float chance = 0.125f;
	private boolean isScaledByCRD = true;

	public CreeperSpawnCharged() {
		OnEntitySpawned.listen( this::charge )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->this.chance, ()->this.isScaledByCRD ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->this.isEnabled.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.entity instanceof Creeper );

		Serializables.get( Config.Features.class )
			.define( "creeper_spawn_charged", subconfig->{
				subconfig.defineBooleanMap( "is_enabled", s->this.isEnabled.get(), ( s, v )->this.isEnabled.set( v ) );
				subconfig.defineFloat( "chance", s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
				subconfig.defineBoolean( "is_scaled_by_crd", s->this.isScaledByCRD, ( s, v )->this.isScaledByCRD = v );
			} );
	}

	private void charge( OnEntitySpawned data ) {
		Creeper creeper = ( Creeper )data.entity;
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.getServerLevel() );
		if( lightningBolt != null ) {
			creeper.thunderHit( data.getServerLevel(), lightningBolt );
			creeper.clearFire();
		}
	}
}
