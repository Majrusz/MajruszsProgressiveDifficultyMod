package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class UndeadBattleStandardItem extends Item {
	private final static String TOOLTIP_TRANSLATION_KEY_1 = "item.majruszs_difficulty.undead_battle_standard.item_tooltip1";
	private final static String TOOLTIP_TRANSLATION_KEY_2 = "item.majruszs_difficulty.undead_battle_standard.item_tooltip2";
	private final static String TOOLTIP_TRANSLATION_KEY_3 = "item.majruszs_difficulty.undead_battle_standard.item_tooltip3";
	public UndeadBattleStandardItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		if( !world.isRemote && RegistryHandler.UNDEAD_ARMY_MANAGER.spawn( player ) ) {
			if( !player.abilities.isCreativeMode )
				itemStack.shrink( 1 );
			player.addStat( Stats.ITEM_USED.get( this ) );
		}

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		tooltip.add( new TranslationTextComponent( TOOLTIP_TRANSLATION_KEY_1 ) );
		MajruszsHelper.addAdvancedTooltips( tooltip, flag, TOOLTIP_TRANSLATION_KEY_2, TOOLTIP_TRANSLATION_KEY_3 );
	}
}
