package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.mlib.config.ConfigGroup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Class with shared code for all inventory items. (Fisherman Emblem, Giant Seed etc.) */
public class InventoryItem extends Item {
	protected final ConfigGroup group;
	private final String translationKey;

	public InventoryItem( String configName, String translationKeyID ) {
		super( ( new Properties() ).maxStackSize( 1 )
			.rarity( Rarity.UNCOMMON )
			.group( Instances.ITEM_GROUP ) );

		String groupComment = "Functionality of " + configName + ".";
		this.group = FEATURES_GROUP.addGroup( new ConfigGroup( configName.replace( " ", "" ), groupComment ) );
		this.translationKey = "item.majruszs_difficulty." + translationKeyID + ".item_tooltip";
	}

	/** Adding tooltip for what this inventory item does. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( this.translationKey ).mergeStyle( TextFormatting.GOLD ) );

		if( !flag.isAdvanced() )
			return;

		toolTip.add( new StringTextComponent( " " ) );
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.items.inventory_item" ).mergeStyle( TextFormatting.GRAY ) );
	}

	/** Checks whether player have this item in inventory. */
	protected boolean hasAny( PlayerEntity player, InventoryItem item ) {
		Set< Item > items = new HashSet<>();
		items.add( item );

		return player.inventory.hasAny( items );
	}
}
