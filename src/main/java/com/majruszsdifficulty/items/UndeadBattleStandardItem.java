package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.mlib.items.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class UndeadBattleStandardItem extends Item {
	private final static String TOOLTIP_TRANSLATION_KEY_1 = "item.majruszsdifficulty.undead_battle_standard.item_tooltip1";
	private final static String TOOLTIP_TRANSLATION_KEY_2 = "item.majruszsdifficulty.undead_battle_standard.item_tooltip2";
	private final static String TOOLTIP_TRANSLATION_KEY_3 = "item.majruszsdifficulty.undead_battle_standard.item_tooltip3";

	public UndeadBattleStandardItem() {
		super( new Properties().stacksTo( 1 ).tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );

		if( !world.isClientSide && Registries.UNDEAD_ARMY_MANAGER != null && Registries.UNDEAD_ARMY_MANAGER.tryToSpawn( player ) ) {
			ItemHelper.consumeItemOnUse( itemStack, player );
		}

		return InteractionResultHolder.sidedSuccess( itemStack, world.isClientSide() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		tooltip.add( new TranslatableComponent( TOOLTIP_TRANSLATION_KEY_1 ) );
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY_2, TOOLTIP_TRANSLATION_KEY_3 );
	}

	@Override
	public void fillItemCategory( CreativeModeTab itemGroup, NonNullList< ItemStack > itemStacks ) {
		if( !this.allowdedIn( itemGroup ) )
			return;

		itemStacks.add( new ItemStack( this ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.HELMET_ID ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.CHESTPLATE_ID ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.LEGGINGS_ID ) );
		itemStacks.add( UndeadArmorItem.constructItem( UndeadArmorItem.BOOTS_ID ) );
	}
}
