package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.mlib.MajruszLibrary;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
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
	protected final AvailabilityConfig effectiveness;
	protected final DoubleConfig minimumEffectiveness;
	protected final DoubleConfig maximumEffectiveness;
	private final String translationKey;
	private static final String INVENTORY_TOOLTIP_TRANSLATION_KEY = "majruszs_difficulty.items.inventory_item";
	private static final String EFFECTIVENESS_TAG = "Effectiveness";
	private static final String EFFECTIVENESS_VALUE_TAG = "Value";

	public InventoryItem( String configName, String translationKeyID ) {
		super( ( new Properties() ).maxStackSize( 1 )
			.rarity( Rarity.UNCOMMON )
			.group( Instances.ITEM_GROUP ) );

		String groupComment = "Functionality of " + configName + ".";
		String availabilityComment = "Is the effectiveness mechanic enabled?";
		String minComment = "Minimum effectiveness bonus.";
		String maxComment = "Maximum effectiveness bonus.";
		this.group = FEATURES_GROUP.addGroup( new ConfigGroup( configName.replace( " ", "" ), groupComment ) );
		this.effectiveness = new AvailabilityConfig( "effectiveness", availabilityComment, false, true );
		this.minimumEffectiveness = new DoubleConfig( "minimum_effectiveness", minComment, false, -0.5, -1.0, 10.0 );
		this.maximumEffectiveness = new DoubleConfig( "maximum_effectiveness", maxComment, false, 0.5, -1.0, 10.0 );
		this.group.addConfigs( this.effectiveness, this.minimumEffectiveness, this.maximumEffectiveness );

		this.translationKey = "item.majruszs_difficulty." + translationKeyID + ".item_tooltip";
	}

	/** Adding tooltip for what this inventory item does. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		tooltip.add( new TranslationTextComponent( this.translationKey ).mergeStyle( TextFormatting.GOLD ) );

		MajruszsHelper.addAdvancedTooltips( tooltip, flag, " ", INVENTORY_TOOLTIP_TRANSLATION_KEY );
	}

	/** Returns whether effectiveness mechanic is enabled and is valid. */
	public boolean isEffectivenessEnabled() {
		return this.effectiveness.isEnabled() && this.minimumEffectiveness.get() <= this.maximumEffectiveness.get();
	}

	/** Returns current effectiveness from item stack. */
	public double getEffectiveness( ItemStack itemStack ) {
		if( !( itemStack.getItem() instanceof InventoryItem ) )
			return 0.0;

		CompoundNBT data = itemStack.getOrCreateChildTag( EFFECTIVENESS_TAG );
		return data.getDouble( EFFECTIVENESS_VALUE_TAG );
	}

	/** Sets random effectiveness in given item stack. */
	public void setRandomEffectiveness( ItemStack itemStack ) {
		if( !isEffectivenessEnabled() )
			return;

		int bound = ( int )( this.maximumEffectiveness.get()-this.minimumEffectiveness.get() )*100+1;
		double value = MajruszLibrary.RANDOM.nextInt( bound )/100.0+this.minimumEffectiveness.get();

		CompoundNBT data = itemStack.getOrCreateChildTag( EFFECTIVENESS_TAG );
		data.putDouble( EFFECTIVENESS_VALUE_TAG, value );
	}

	/** Checks whether player have this item in inventory. */
	protected boolean hasAny( PlayerEntity player, InventoryItem item ) {
		Set< Item > items = new HashSet<>();
		items.add( item );

		return player.inventory.hasAny( items );
	}
}
