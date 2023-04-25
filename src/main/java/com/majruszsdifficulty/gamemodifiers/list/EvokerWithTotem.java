package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@AutoInstance
public class EvokerWithTotem {
	public EvokerWithTotem() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "EvokerWithTotem" )
			.comment( "Evoker may spawn with a Totem of Undying." );

		OnSpawned.listenSafe( this::giveTotemOfUndying )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( Condition.chanceCRD( 1.0, true ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( Condition.predicate( data->data.target instanceof Evoker ) )
			.insertTo( group );
	}

	private void giveTotemOfUndying( OnSpawned.Data data ) {
		Evoker evoker = ( Evoker )data.target;
		evoker.setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.TOTEM_OF_UNDYING ) );
	}
}
