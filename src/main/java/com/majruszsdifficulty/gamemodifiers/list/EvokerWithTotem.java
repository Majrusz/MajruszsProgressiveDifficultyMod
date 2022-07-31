package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EvokerWithTotem extends DifficultyModifier {
	public EvokerWithTotem() {
		super( DifficultyModifier.DEFAULT, "EvokerWithTotem", "Evoker may spawn with a Totem of Undying." );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::giveTotemOfUndying );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 1.0 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.target instanceof Evoker );

		this.addContext( onSpawned );
	}

	private void giveTotemOfUndying( OnSpawnedData data ) {
		Evoker evoker = ( Evoker )data.target;
		evoker.setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.TOTEM_OF_UNDYING ) );
	}
}
