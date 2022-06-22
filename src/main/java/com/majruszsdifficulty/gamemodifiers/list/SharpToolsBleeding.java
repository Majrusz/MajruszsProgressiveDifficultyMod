package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.GameModifierHelper;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.items.ItemHelper;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public class SharpToolsBleeding extends GameModifier {
	static final Config.Bleeding BLEEDING = new Config.Bleeding();
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.25, false ) );
		ON_DAMAGED.addCondition( new ICondition.MayBleed() );
		ON_DAMAGED.addCondition( new ICondition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) ) );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public SharpToolsBleeding() {
		super( "SharpToolsBleeding", "All sharp items (tools, shears etc.) may inflict bleeding.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			GameModifierHelper.applyBleeding( damagedData.target, damagedData.attacker, BLEEDING );
		}
	}
}
