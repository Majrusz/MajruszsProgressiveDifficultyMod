package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import javax.annotation.Nullable;

public class BiteBleeding extends GameModifier {
	static final BleedingConfig BLEEDING = new BleedingConfig();
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( BiteBleeding::applyBleeding );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 0.5 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new Condition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->canBite( data.attacker ) ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public BiteBleeding() {
		super( GameModifier.DEFAULT, "BiteBleeding", "Animals (wolfs and from other mods), zombies and spiders may inflict bleeding.", ON_DAMAGED );
	}

	private static void applyBleeding( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		BLEEDING.apply( data );
	}

	private static boolean canBite( @Nullable LivingEntity attacker ) {
		return ( attacker instanceof Animal || attacker instanceof Zombie || attacker instanceof Spider ) && !( attacker instanceof Llama );
	}
}
