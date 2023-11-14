package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.data.EffectDef;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntitySpawned;
import com.mlib.contexts.base.Condition;
import com.mlib.data.Serializables;
import com.mlib.math.Random;
import com.mlib.math.Range;
import com.mlib.time.TimeHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

import java.util.List;

@AutoInstance
public class CreeperSpawnDebuffed {
	private GameStageValue< Boolean > isEnabled = GameStageValue.alwaysEnabled();
	private float chance = 0.375f;
	private boolean isScaledByCRD = true;
	private List< EffectDef > effects = List.of(
		new EffectDef( ()->MobEffects.WEAKNESS, 0, 60.0f ),
		new EffectDef( ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 60.0f ),
		new EffectDef( ()->MobEffects.DIG_SLOWDOWN, 0, 60.0f ),
		new EffectDef( ()->MobEffects.SATURATION, 0, 60.0f )
	);

	public CreeperSpawnDebuffed() {
		OnEntitySpawned.listen( this::applyRandomEffect )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->this.chance, ()->this.isScaledByCRD ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->this.isEnabled.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.entity instanceof Creeper );

		Serializables.get( Config.Features.class )
			.define( "creeper_spawn_debuffed", subconfig->{
				subconfig.defineBooleanMap( "is_enabled", s->this.isEnabled.get(), ( s, v )->this.isEnabled.set( v ) );
				subconfig.defineFloat( "chance", s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
				subconfig.defineBoolean( "is_scaled_by_crd", s->this.isScaledByCRD, ( s, v )->this.isScaledByCRD = v );
				subconfig.defineCustomList( "effects", s->this.effects, ( s, v )->this.effects = v, EffectDef::new );
			} );
	}

	private void applyRandomEffect( OnEntitySpawned data ) {
		Creeper creeper = ( Creeper )data.entity;
		EffectDef effectDef = Random.next( this.effects );

		creeper.addEffect( new MobEffectInstance( effectDef.effect.get(), TimeHelper.toTicks( effectDef.duration ), effectDef.amplifier ) );
	}
}
