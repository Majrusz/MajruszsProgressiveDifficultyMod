package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;

@AutoInstance
public class ThrowableWeaponsBleeding extends GameModifier {
	public ThrowableWeaponsBleeding() {
		super( Registries.Modifiers.DEFAULT );

		new OnBleedingCheck.Context( OnBleedingCheck.Data::trigger )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.4, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( data->data.source.getDirectEntity() instanceof Arrow || data.source.getDirectEntity() instanceof ThrownTrident )
			.insertTo( this );

		this.name( "ThrowableWeaponsBleeding" ).comment( "All throwable sharp items (arrows, trident etc.) may inflict bleeding." );
	}
}
