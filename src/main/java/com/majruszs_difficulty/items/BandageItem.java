package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.effects.BleedingEffect;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Simple bandage item that removes bleeding effect and gives regeneration for few seconds. */
@Mod.EventBusSubscriber
public class BandageItem extends Item {
	private static final String TOOLTIP_TRANSLATION_KEY_1 = "item.majruszs_difficulty.bandage.item_tooltip1";
	private static final String TOOLTIP_TRANSLATION_KEY_2 = "item.majruszs_difficulty.bandage.item_tooltip2";
	protected final ConfigGroup configGroup;
	protected final AvailabilityConfig isAlwaysUsable;
	protected final DurationConfig effectDuration;
	protected final IntegerConfig effectAmplifier;

	public BandageItem() {
		this( "Bandage", 0, Rarity.COMMON );
	}

	public BandageItem( String name, int defaultAmplifier, Rarity rarity ) {
		super( ( new Properties() ).maxStackSize( 16 )
			.group( Instances.ITEM_GROUP ).rarity( rarity ) );

		this.configGroup = new ConfigGroup( name, "Configuration for " + name + " item." );
		FEATURES_GROUP.addGroup( this.configGroup );

		String usableComment = "Is " + name + " always usable? If not player can only use " + name + " when it is bleeding.";
		String durationComment = "Duration in seconds of Regeneration effect.";
		String amplifierComment = "Level/amplifier of Regeneration effect.";
		this.isAlwaysUsable = new AvailabilityConfig( "is_always_usable", usableComment, false, true );
		this.effectDuration = new DurationConfig( "regeneration_duration", durationComment, false, 4.0, 1.0, 120.0 );
		this.effectAmplifier = new IntegerConfig( "regeneration_amplifier", amplifierComment, false, defaultAmplifier, 0, 10 );
		this.configGroup.addConfigs( this.isAlwaysUsable, this.effectDuration, this.effectAmplifier );
	}

	/** Using bandage on right click. (self healing) */
	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );
		useIfPossible( itemStack, player, player );

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	/** Adding tooltip with information for what bandage is used. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addAdvancedTooltips( tooltip, flag, TOOLTIP_TRANSLATION_KEY_1, TOOLTIP_TRANSLATION_KEY_2 );
	}

	/** Using bandage on right click. (other entity healing) */
	@SubscribeEvent
	public static void onRightClick( PlayerInteractEvent.EntityInteract event ) {
		if( !( event.getTarget() instanceof LivingEntity ) )
			return;

		if( Instances.BANDAGE_ITEM.useIfPossible( event.getItemStack(), event.getPlayer(), ( LivingEntity )event.getTarget() ) )
			event.setCancellationResult( ActionResultType.SUCCESS );
	}

	/** Checks whether Bandage is always usable. (player can use it even if it does not have a Bleeding effect) */
	public boolean isAlwaysUsable() {
		return this.isAlwaysUsable.isEnabled();
	}

	/** Returns duration in ticks of Regeneration effect. */
	public int getRegenerationDuration() {
		return this.effectDuration.getDuration();
	}

	/** Returns amplifier of Regeneration effect. */
	public int getRegenerationAmplifier() {
		return this.effectAmplifier.get();
	}

	/** Applies Regeneration on target depending on current mod settings. */
	protected void applyEffects( LivingEntity target ) {
		EffectHelper.applyEffectIfPossible( target, Effects.REGENERATION, getRegenerationDuration(), getRegenerationAmplifier() );
	}

	/**
	 Removes bleeding from the target or applies regeneration if it is possible.

	 @param bandage Bandage item.
	 @param player  Player that is right clicking.
	 @param target  Target that will be healed. (may be the same player)

	 @return Returns information (boolean) if bleeding was removed or regeneration was applied.
	 */
	private boolean useIfPossible( ItemStack bandage, PlayerEntity player, LivingEntity target ) {
		if( !couldBeUsedOn( target, bandage ) )
			return false;

		if( !player.abilities.isCreativeMode )
			bandage.shrink( 1 );

		player.addStat( Stats.ITEM_USED.get( bandage.getItem() ) );
		removeBleeding( target );
		applyEffects( target );
		target.world.playSound( null, target.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f );

		return true;
	}

	/** Checks whether item could be used on target. */
	private boolean couldBeUsedOn( LivingEntity target, ItemStack bandage ) {
		boolean isBandage = bandage.getItem() instanceof BandageItem;
		boolean targetHasRegeneration = target.isPotionActive( Effects.REGENERATION );

		return isBandage && ( ( isAlwaysUsable() && !targetHasRegeneration ) || target.isPotionActive( Instances.BLEEDING ) );
	}

	/** Removes Bleeding effect from the target. */
	private void removeBleeding( LivingEntity target ) {
		BleedingEffect bleeding = Instances.BLEEDING;

		target.removePotionEffect( bleeding );
		target.removeActivePotionEffect( bleeding );
	}
}
