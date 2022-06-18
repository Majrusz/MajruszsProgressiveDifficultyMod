package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.RegistryHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
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
		super( ( new Item.Properties() ).stacksTo( 1 ).tab( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );

		if( !world.isClientSide && RegistryHandler.UNDEAD_ARMY_MANAGER != null && RegistryHandler.UNDEAD_ARMY_MANAGER.tryToSpawn( player ) ) {
			if( !player.getAbilities().instabuild )
				itemStack.shrink( 1 );
			player.awardStat( Stats.ITEM_USED.get( this ) );
		}

		return InteractionResultHolder.sidedSuccess( itemStack, world.isClientSide() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		tooltip.add( Component.translatable( TOOLTIP_TRANSLATION_KEY_1 ) );
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY_2, TOOLTIP_TRANSLATION_KEY_3 );
	}
}
