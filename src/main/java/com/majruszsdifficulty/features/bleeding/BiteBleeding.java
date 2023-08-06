package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.OnBleedingCheck;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

@AutoInstance
public class BiteBleeding {
	public BiteBleeding() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.BLEEDING )
			.name( "Bite" )
			.comment( "Wolves, zombies, spiders, and animals from other mods may inflict bleeding." );

		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( Condition.chanceCRD( 0.5, false ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( OnBleedingCheck.Data::isDirect ) )
			.addCondition( BiteBleeding.canBite() )
			.insertTo( group );
	}

	private static Condition< OnBleedingCheck.Data > canBite() {
		return Condition.predicate( data->{
			return ( data.attacker instanceof Animal || data.attacker instanceof Zombie || data.attacker instanceof Spider )
				&& !( data.attacker instanceof Llama );
		} );
	}
}
