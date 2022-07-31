package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.majruszsdifficulty.gamemodifiers.configs.BleedingConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.mlib.items.ItemHelper;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

public class SharpToolsBleeding extends DifficultyModifier {
	final BleedingConfig bleeding = new BleedingConfig();

	public SharpToolsBleeding() {
		super( DifficultyModifier.DEFAULT, "SharpToolsBleeding", "All sharp items (tools, shears etc.) may inflict bleeding." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::applyBleeding );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 0.25, false ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.IsLivingBeing() )
			.addCondition( new Condition.ArmorDependentChance() )
			.addCondition( data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.addConfig( this.bleeding );

		this.addContext( onDamaged );
	}

	private void applyBleeding( OnDamagedData data ) {
		this.bleeding.apply( data );
	}
}
