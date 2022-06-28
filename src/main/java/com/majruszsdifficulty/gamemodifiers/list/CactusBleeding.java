package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.damagesource.DamageSource;

public class CactusBleeding extends GameModifier {
	static final BleedingConfig BLEEDING = new BleedingConfig();
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( CactusBleeding::applyBleeding );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 0.5 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new Condition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.equals( DamageSource.CACTUS ) ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public CactusBleeding() {
		super( GameModifier.DEFAULT, "CactusBleeding", "Cactus damage may inflict bleeding.", ON_DAMAGED );
	}

	private static void applyBleeding( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		BLEEDING.apply( data );
	}
}
