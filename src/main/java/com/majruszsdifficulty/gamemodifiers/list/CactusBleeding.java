package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraft.world.damagesource.DamageSource;

public class CactusBleeding extends GameModifier {
	public CactusBleeding() {
		super( Registries.Modifiers.DEFAULT, "CactusBleeding", "Cactus damage may inflict bleeding." );

		OnBleedingCheck.Context onCheck = new OnBleedingCheck.Context( OnBleedingCheck.Data::trigger );
		onCheck.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.5, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( data->data.source.equals( DamageSource.CACTUS ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker );

		this.addContext( onCheck );
	}
}
