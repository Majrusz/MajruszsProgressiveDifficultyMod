package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.OnEntitySpawned;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.events.base.CustomCondition;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EvokerWithTotem {
	private static final GameStageValue< Boolean > IS_ENABLED = GameStageValue.alwaysEnabled();
	private static float CHANCE = 1.0f;
	private static boolean IS_SCALED_BY_CRD = true;

	static {
		OnEntitySpawned.listen( EvokerWithTotem::giveTotem )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.chanceCRD( ()->CHANCE, ()->IS_SCALED_BY_CRD ) )
			.addCondition( CustomCondition.isEnabled( IS_ENABLED ) )
			.addCondition( data->!data.isLoadedFromDisk )
			.addCondition( data->data.entity instanceof Evoker );

		Serializables.getStatic( Config.Features.class )
			.define( "evoker_with_totem", EvokerWithTotem.class );

		Serializables.getStatic( EvokerWithTotem.class )
			.define( "is_enabled", Reader.map( Reader.bool() ), ()->IS_ENABLED.get(), v->IS_ENABLED.set( v ) )
			.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), ()->IS_SCALED_BY_CRD, v->IS_SCALED_BY_CRD = v );
	}

	private static void giveTotem( OnEntitySpawned data ) {
		data.entity.setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.TOTEM_OF_UNDYING ) );
	}
}
