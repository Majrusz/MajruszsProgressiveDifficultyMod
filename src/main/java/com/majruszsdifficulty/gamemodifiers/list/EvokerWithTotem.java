package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EvokerWithTotem extends GameModifier {
	public EvokerWithTotem() {
		super( Registries.Modifiers.DEFAULT, "EvokerWithTotem", "Evoker may spawn with a Totem of Undying." );

		OnSpawned.Context onSpawned = new OnSpawned.Context( this::giveTotemOfUndying );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 1.0, true ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
			.addCondition( data->data.target instanceof Evoker );

		this.addContext( onSpawned );
	}

	private void giveTotemOfUndying( OnSpawned.Data data ) {
		Evoker evoker = ( Evoker )data.target;
		evoker.setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.TOTEM_OF_UNDYING ) );
	}
}
