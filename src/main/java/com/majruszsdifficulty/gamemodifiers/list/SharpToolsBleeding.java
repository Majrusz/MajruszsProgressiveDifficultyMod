package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.items.ItemHelper;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public class SharpToolsBleeding extends GameModifier {
	final BleedingConfig bleeding = new BleedingConfig();

	public SharpToolsBleeding() {
		super( Registries.Modifiers.DEFAULT, "SharpToolsBleeding", "All sharp items (tools, shears etc.) may inflict bleeding." );

		OnDamaged.Context onDamaged = new OnDamaged.Context( this.bleeding::apply );
		onDamaged.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.25, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( new Condition.ArmorDependentChance<>() )
			.addCondition( OnDamaged.DEALT_ANY_DAMAGE )
			.addCondition( data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.bleeding );

		this.addContext( onDamaged );
	}
}
