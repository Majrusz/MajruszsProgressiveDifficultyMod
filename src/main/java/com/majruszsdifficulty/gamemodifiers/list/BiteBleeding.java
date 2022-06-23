package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.GameModifierHelper;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;

import javax.annotation.Nullable;

public class BiteBleeding extends GameModifier {
	static final Config.Bleeding BLEEDING = new Config.Bleeding();
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.5, false ) );
		ON_DAMAGED.addCondition( new ICondition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new ICondition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->canBite( data.attacker ) ) );
		ON_DAMAGED.addCondition( new DamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public BiteBleeding() {
		super( "BiteBleeding", "Animals (wolfs and from other mods), zombies and spiders may inflict bleeding.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			GameModifierHelper.applyBleeding( damagedData, BLEEDING );
		}
	}

	private static boolean canBite( @Nullable LivingEntity attacker ) {
		return ( attacker instanceof Animal || attacker instanceof Zombie || attacker instanceof Spider ) && !( attacker instanceof Llama );
	}
}
