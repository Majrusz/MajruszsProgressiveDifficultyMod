package com.majruszsdifficulty.gamemodifiers.list.bleeding;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import net.minecraft.world.damagesource.DamageTypes;

@AutoInstance
public class CactusBleeding {
	public CactusBleeding() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.BLEEDING )
			.name( "Cactus" )
			.comment( "Touching cactus may inflict bleeding." );

		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( Condition.chanceCRD( 0.5, false ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( data->data.source.is( DamageTypes.CACTUS ) ) )
			.addCondition( Condition.predicate( OnBleedingCheck.Data::isDirect ) )
			.insertTo( group );
	}
}
