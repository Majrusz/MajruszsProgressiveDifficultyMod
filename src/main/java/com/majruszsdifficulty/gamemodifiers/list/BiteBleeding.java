package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import javax.annotation.Nullable;

public class BiteBleeding extends GameModifier {
	final BleedingConfig bleeding = new BleedingConfig();

	public BiteBleeding() {
		super( GameModifier.DEFAULT, "BiteBleeding", "Animals (wolfs and from other mods), zombies and spiders may inflict bleeding." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::applyBleeding );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new Condition.Chance( 0.5 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.IsLivingBeing() )
			.addCondition( new Condition.ArmorDependentChance() )
			.addCondition( data->canBite( data.attacker ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.bleeding );

		this.addContext( onDamaged );
	}

	private void applyBleeding( OnDamagedData data ) {
		this.bleeding.apply( data );
	}

	private static boolean canBite( @Nullable LivingEntity attacker ) {
		return ( attacker instanceof Animal || attacker instanceof Zombie || attacker instanceof Spider ) && !( attacker instanceof Llama );
	}
}
