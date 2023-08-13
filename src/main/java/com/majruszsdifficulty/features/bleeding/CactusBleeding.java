package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.OnBleedingCheck;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import net.minecraft.world.damagesource.DamageSource;

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
			.addCondition( Condition.predicate( data->data.source.equals( DamageSource.CACTUS ) ) )
			.addCondition( Condition.predicate( OnBleedingCheck.Data::isDirect ) )
			.insertTo( group );
	}
}
