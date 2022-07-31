package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import net.minecraft.world.damagesource.DamageSource;

public class CactusBleeding extends DifficultyModifier {
	final BleedingConfig bleeding = new BleedingConfig();

	public CactusBleeding() {
		super( DifficultyModifier.DEFAULT, "CactusBleeding", "Cactus damage may inflict bleeding." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::applyBleeding );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 0.5, false ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.IsLivingBeing() )
			.addCondition( new Condition.ArmorDependentChance() )
			.addCondition( data->data.source.equals( DamageSource.CACTUS ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.bleeding );

		this.addContext( onDamaged );
	}

	private void applyBleeding( OnDamagedData data ) {
		this.bleeding.apply( data );
	}
}
