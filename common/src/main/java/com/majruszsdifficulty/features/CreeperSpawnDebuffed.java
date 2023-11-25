package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EffectDef;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

import java.util.List;

public class CreeperSpawnDebuffed {
	private static boolean IS_ENABLED = true;
	private static GameStage REQUIRED_GAME_STAGE = GameStageHelper.find( GameStage.NORMAL_ID );
	private static float CHANCE = 0.375f;
	private static boolean IS_SCALED_BY_CRD = true;
	private static List< EffectDef > EFFECTS = List.of(
		new EffectDef( ()->MobEffects.WEAKNESS, 0, 60.0f ),
		new EffectDef( ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 60.0f ),
		new EffectDef( ()->MobEffects.DIG_SLOWDOWN, 0, 60.0f ),
		new EffectDef( ()->MobEffects.SATURATION, 0, 60.0f )
	);

	static {
		OnEntitySpawned.listen( CreeperSpawnDebuffed::applyRandomEffect )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( data->IS_ENABLED )
			.addCondition( CustomCondition.check( REQUIRED_GAME_STAGE ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof Creeper );

		Serializables.getStatic( Config.Features.class )
			.define( "creeper_spawn_debuffed", CreeperSpawnDebuffed.class );

		Serializables.getStatic( CreeperSpawnDebuffed.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "required_game_stage", Reader.string(), ()->REQUIRED_GAME_STAGE.getId(), v->REQUIRED_GAME_STAGE = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v )
			.define( "effects", Reader.list( Reader.custom( EffectDef::new ) ), ()->EFFECTS, v->EFFECTS = v );
	}

	private static void applyRandomEffect( OnEntitySpawned data ) {
		( ( Creeper )data.entity ).addEffect( Random.next( EFFECTS ).toEffectInstance() );
	}
}
