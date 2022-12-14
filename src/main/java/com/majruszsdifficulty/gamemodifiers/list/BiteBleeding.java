package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import javax.annotation.Nullable;

public class BiteBleeding extends GameModifier {
	final BleedingConfig bleeding = new BleedingConfig();

	public BiteBleeding() {
		super( Registries.Modifiers.DEFAULT, "BiteBleeding", "Animals (wolfs and from other mods), zombies and spiders may inflict bleeding." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this.bleeding::apply );
		onDamaged.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.5, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( new Condition.ArmorDependentChance<>() )
			.addCondition( OnDamaged.DEALT_ANY_DAMAGE )
			.addCondition( data->canBite( data.attacker ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.bleeding );

		this.addContext( onDamaged );
	}

	private static boolean canBite( @Nullable LivingEntity attacker ) {
		return ( attacker instanceof Animal || attacker instanceof Zombie || attacker instanceof Spider ) && !( attacker instanceof Llama );
	}
}
