package com.majruszs_difficulty.items;

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

public class UndeadBattleStandard extends Item {
	public UndeadBattleStandard() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( RegistryHandler.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		if( !world.isRemote && RegistryHandler.undeadArmyManager.spawn( player, ( ServerWorld )world ) ) {
			if( !player.abilities.isCreativeMode )
				itemStack.shrink( 1 );
			player.addStat( Stats.ITEM_USED.get( this ) );
		}

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.undead_army.item_tooltip1" ) );
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.undead_army.item_tooltip2" ).func_240699_a_( TextFormatting.GRAY ) );
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.undead_army.item_tooltip3" ).func_240699_a_( TextFormatting.GRAY ) );
	}
}
