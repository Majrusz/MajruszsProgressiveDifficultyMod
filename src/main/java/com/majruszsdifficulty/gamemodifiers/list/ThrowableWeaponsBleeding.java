package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class ThrowableWeaponsBleeding extends GameModifier {
	public ThrowableWeaponsBleeding() {
		super( Registries.Modifiers.DEFAULT, "ThrowableWeaponsBleeding", "All throwable sharp items (arrows, trident etc.) may inflict bleeding." );

		OnBleedingCheck.Context onCheck = new OnBleedingCheck.Context( OnBleedingCheck.Data::trigger );
		onCheck.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.4, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( data->data.source.getDirectEntity() instanceof Arrow || data.source.getDirectEntity() instanceof ThrownTrident );

		this.addContext( onCheck );
	}
}
