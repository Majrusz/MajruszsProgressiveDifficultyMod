package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.items.ItemHelper;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public class SharpToolsBleeding extends GameModifier {
	static final CustomConfigs.Bleeding BLEEDING = new CustomConfigs.Bleeding();
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

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

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			BLEEDING.apply( damagedData );
		}
	}
}
