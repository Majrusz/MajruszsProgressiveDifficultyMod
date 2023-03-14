package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import net.minecraft.world.damagesource.DamageSource;

@AutoInstance
public class CactusBleeding extends GameModifier {
	public CactusBleeding() {
		super( Registries.Modifiers.DEFAULT );

		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 0.5, false ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( data->data.source.equals( DamageSource.CACTUS ) ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() == data.attacker ) )
			.insertTo( this );

		this.name( "CactusBleeding" ).comment( "Cactus damage may inflict bleeding." );
	}
}
