package com.majruszs_difficulty;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class MajruszsHelper {
	/**
	 Returns player from given damage source.
	 Returns null if casting was impossible.
	 */
	@Nullable
	public static PlayerEntity getPlayerFromDamageSource( DamageSource damageSource ) {
		return damageSource.getTrueSource() instanceof PlayerEntity ? ( PlayerEntity )damageSource.getTrueSource() : null;
	}

	/**
	 Checking if given entity is human.

	 @param entity Entity to test.
	 */
	public static boolean isHuman( @Nullable Entity entity ) {
		return entity instanceof PlayerEntity || entity instanceof VillagerEntity || entity instanceof PillagerEntity || entity instanceof WitchEntity;
	}

	/**
	 Checking if given entity is animal.

	 @param entity Entity to test.
	 */
	public static boolean isAnimal( @Nullable Entity entity ) {
		return entity instanceof AnimalEntity;
	}

	/** Adds tooltip to list if advanced tooltips are enabled. */
	public static void addAdvancedTooltip( List< ITextComponent > tooltip, ITooltipFlag flag, String translationKey ) {
		if( flag.isAdvanced() )
			tooltip.add( new TranslationTextComponent( translationKey ).mergeStyle( TextFormatting.GRAY ) );
	}

	/** Adds multiple tooltips to list if advanced tooltips are enabled. */
	public static void addAdvancedTooltips( List< ITextComponent > tooltip, ITooltipFlag flag, String ...translationKeys ) {
		if( flag.isAdvanced() )
			for( String translationKey : translationKeys )
				addAdvancedTooltip( tooltip, flag, translationKey );
	}

	/** Returns formatted text with information that item is disabled. */
	public static IFormattableTextComponent getDisabledItemTooltip() {
		return new TranslationTextComponent( "majruszs_difficulty.items.disabled_tooltip" ).mergeStyle( TextFormatting.RED, TextFormatting.BOLD );
	}

	/** Adds information that item is disabled if certain conditions are met. */
	public static void addExtraTooltipIfDisabled( List< ITextComponent > toolTip, boolean isEnabled ) {
		if( !isEnabled )
			toolTip.add( getDisabledItemTooltip() );
	}
}
