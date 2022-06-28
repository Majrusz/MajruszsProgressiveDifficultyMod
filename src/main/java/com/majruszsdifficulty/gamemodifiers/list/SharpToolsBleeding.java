package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.items.ItemHelper;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public class SharpToolsBleeding extends GameModifier {
	static final BleedingConfig BLEEDING = new BleedingConfig();
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( SharpToolsBleeding::applyBleeding );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 0.25 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new Condition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public SharpToolsBleeding() {
		super( GameModifier.DEFAULT, "SharpToolsBleeding", "All sharp items (tools, shears etc.) may inflict bleeding.", ON_DAMAGED );
	}

	private static void applyBleeding( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		BLEEDING.apply( data );
	}
}
