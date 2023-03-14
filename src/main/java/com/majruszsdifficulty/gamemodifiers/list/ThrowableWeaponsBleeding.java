package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;

@AutoInstance
public class ThrowableWeaponsBleeding {
	public ThrowableWeaponsBleeding() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "ThrowableWeaponsBleeding" )
			.comment( "All throwable sharp items (arrows, trident etc.) may inflict bleeding." );

		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.4, false ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() instanceof Arrow || data.source.getDirectEntity() instanceof ThrownTrident ) )
			.insertTo( group );
	}
}
