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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Class with shared code for all inventory items. (Fisherman Emblem, Giant Seed etc.) */
public class InventoryItem extends Item {
	private static final String INVENTORY_TOOLTIP_TRANSLATION_KEY = "majruszs_difficulty.items.inventory_item";
	private static final String EFFECTIVENESS_TRANSLATION_KEY = "majruszs_difficulty.items.effectiveness";
	private static final String EFFECTIVENESS_TAG = "Effectiveness";
	private static final String EFFECTIVENESS_VALUE_TAG = "Value";
	protected final ConfigGroup group;
	protected final AvailabilityConfig effectiveness;
	protected final DoubleConfig minimumEffectiveness;
	protected final DoubleConfig maximumEffectiveness;
	private final String translationKey;

	public InventoryItem( String configName, String translationKeyID ) {
		super( ( new Properties() ).maxStackSize( 1 )
			.rarity( Rarity.RARE )
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
		if( isEffectivenessEnabled() && getEffectiveness( itemStack ) != 0.0 ) {
			IFormattableTextComponent text = new StringTextComponent( getEffectiveness( itemStack ) > 0.0 ? "+" : "" );
			text.appendString( "" + ( int )( getEffectiveness( itemStack ) * 100 ) + "% " );
			text.append( new TranslationTextComponent( EFFECTIVENESS_TRANSLATION_KEY ) );
			text.mergeStyle( getEffectivenessColor( itemStack ) );

			tooltip.add( text );
		}

		MajruszsHelper.addAdvancedTooltips( tooltip, flag, " ", INVENTORY_TOOLTIP_TRANSLATION_KEY );
	}

	/** Returns effectiveness color depending on current value. */
	@OnlyIn( Dist.CLIENT )
	public TextFormatting getEffectivenessColor( ItemStack itemStack ) {
		double value = getEffectiveness( itemStack );
		if( value == this.maximumEffectiveness.get() )
			return TextFormatting.GOLD;
		else if( value < 0.0 )
			return TextFormatting.RED;
		else
			return TextFormatting.GREEN;
	}

	/** Returns whether effectiveness mechanic is enabled and is valid. */
	public boolean isEffectivenessEnabled() {
		return this.effectiveness.isEnabled() && this.minimumEffectiveness.get() <= this.maximumEffectiveness.get();
	}

	/** Checks whether item stack has effectiveness tag. */
	public boolean hasEffectivenessTag( ItemStack itemStack ) {
		return itemStack.getOrCreateChildTag( EFFECTIVENESS_TAG )
			.contains( EFFECTIVENESS_VALUE_TAG );
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

		double gaussianRandom = MathHelper.clamp( MajruszLibrary.RANDOM.nextGaussian()/3.0, -1.0, 1.0 ); // random value from range [-1.0; 1.0] with mean ~= 0.0 and standard deviation ~= 0.3333..
		double gaussianRandomShifted = ( gaussianRandom + 1.0 ) / 2.0; // random value from range [0.0; 1.0] with mean ~= 0.5 and standard deviation ~= 0.1666..
		double randomValue = gaussianRandomShifted*( this.maximumEffectiveness.get() - this.minimumEffectiveness.get() ) + this.minimumEffectiveness.get();
		double value = Math.round( randomValue * 100.0 )/100.0;
		CompoundNBT data = itemStack.getOrCreateChildTag( EFFECTIVENESS_TAG );
		data.putDouble( EFFECTIVENESS_VALUE_TAG, value );
	}

	/** Checks whether player have this item in inventory. */
	protected boolean hasAny( PlayerEntity player, InventoryItem item ) {
		Set< Item > items = new HashSet<>();
		items.add( item );

		return player.inventory.hasAny( items );
	}

	/** Returns highest inventory item effectiveness. */
	protected double getHighestEffectiveness( PlayerEntity player, InventoryItem item ) {
		double bonus = this.minimumEffectiveness.get();
		for( ItemStack itemStack : player.inventory.mainInventory )
			if( item.equals( itemStack.getItem() ) && item.getEffectiveness( itemStack ) > bonus )
				bonus = item.getEffectiveness( itemStack );

		return bonus;
	}

	/** Spawns special particles at given position. */
	protected void spawnParticles( Vector3d position, ServerWorld world, double offset ) {
		world.spawnParticle( ParticleTypes.HAPPY_VILLAGER, position.getX(), position.getY(), position.getZ(), 5, offset, offset, offset, 0.1 );
	}
}
