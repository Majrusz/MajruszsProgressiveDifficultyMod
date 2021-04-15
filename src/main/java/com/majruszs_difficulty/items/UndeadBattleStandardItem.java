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
		super( ( new Item.Properties() ).stacksTo( 1 )
			.tab( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public ActionResult< ItemStack > use( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );

		if( !world.isClientSide() && RegistryHandler.UNDEAD_ARMY_MANAGER.spawn( player ) ) {
			if( !player.abilities.instabuild )
				itemStack.shrink( 1 );
			player.awardStat( Stats.ITEM_USED.get( this ) );
		}

		return ActionResult.sidedSuccess( itemStack, world.isClientSide() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.undead_battle_standard.item_tooltip1" ) );

		if( !flag.isAdvanced() )
			return;

		toolTip.add(
			new TranslationTextComponent( "item.majruszs_difficulty.undead_battle_standard.item_tooltip2" ).withStyle( TextFormatting.GRAY ) );
		toolTip.add(
			new TranslationTextComponent( "item.majruszs_difficulty.undead_battle_standard.item_tooltip3" ).withStyle( TextFormatting.GRAY ) );
	}
}
