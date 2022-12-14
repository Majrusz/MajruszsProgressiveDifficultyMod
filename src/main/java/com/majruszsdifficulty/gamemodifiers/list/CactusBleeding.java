package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.damagesource.DamageSource;

public class CactusBleeding extends GameModifier {
	final BleedingConfig bleeding = new BleedingConfig();

	public CactusBleeding() {
		super( Registries.Modifiers.DEFAULT, "CactusBleeding", "Cactus damage may inflict bleeding." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this.bleeding::apply );
		onDamaged.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.5, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( new Condition.ArmorDependentChance<>() )
			.addCondition( OnDamaged.DEALT_ANY_DAMAGE )
			.addCondition( data->data.source.equals( DamageSource.CACTUS ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.bleeding );

		this.addContext( onDamaged );
	}
}
