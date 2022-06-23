package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.Random;
import com.mlib.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperDebuffsSpawn extends GameModifier {
	static final MobEffect[] EFFECTS = new MobEffect[]{
		MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN, MobEffects.SATURATION
	};
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();
	static final Config.Effect EFFECT = new Config.Effect( "Effect", 0, 60.0 );

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.375 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.Context<>( OnSpawnedContext.Data.class, data->data.target instanceof Creeper ) );
		ON_SPAWNED.addConfig( EFFECT );
	}

	public CreeperDebuffsSpawn() {
		super( GameModifier.DEFAULT, "CreeperDebuffsSpawn", "Creeper may spawn with negative effects applied.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data damagedData ) {
			damagedData.target.addEffect( new MobEffectInstance( Random.nextRandom( EFFECTS ), 1, 1 ) );
		}
	}
}
