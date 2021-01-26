package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class UndeadBattleStandardItem extends Item {
	public UndeadBattleStandardItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		if( !world.isRemote && RegistryHandler.UNDEAD_ARMY_MANAGER.spawn( player, ( ServerWorld )world ) ) {
			if( !player.abilities.isCreativeMode )
				itemStack.shrink( 1 );
			player.addStat( Stats.ITEM_USED.get( this ) );
		}

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		if( !flag.isAdvanced() )
			return;

		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.undead_battle_standard.item_tooltip1" ) );
		toolTip.add(
			new TranslationTextComponent( "item.majruszs_difficulty.undead_battle_standard.item_tooltip2" ).mergeStyle( TextFormatting.GRAY ) );
		toolTip.add(
			new TranslationTextComponent( "item.majruszs_difficulty.undead_battle_standard.item_tooltip3" ).mergeStyle( TextFormatting.GRAY ) );
	}
}
