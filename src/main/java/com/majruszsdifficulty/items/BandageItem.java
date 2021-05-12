package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.effects.BleedingEffect;
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
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static com.majruszsdifficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Simple bandage item that removes bleeding effect and gives regeneration for few seconds. */
@Mod.EventBusSubscriber
public class BandageItem extends Item {
	protected final ConfigGroup configGroup;
	protected final AvailabilityConfig isAlwaysUsable;
	protected final DurationConfig effectDuration;
	protected final IntegerConfig effectAmplifier;

	public BandageItem() {
		super( ( new Properties() ).maxStackSize( 16 )
			.group( Instances.ITEM_GROUP ) );

		this.configGroup = new ConfigGroup( "Bandage", "Configuration for Bandage item." );
		FEATURES_GROUP.addGroup( this.configGroup );

		String usableComment = "Is Bandage always usable? If not player can only use Bandage when it is bleeding.";
		String durationComment = "Duration in seconds of Regeneration effect.";
		String amplifierComment = "Level/amplifier of Regeneration effect.";
		this.isAlwaysUsable = new AvailabilityConfig( "is_always_usable", usableComment, false, true );
		this.effectDuration = new DurationConfig( "duration", durationComment, false, 4.0, 1.0, 120.0 );
		this.effectAmplifier = new IntegerConfig( "amplifier", amplifierComment, false, 0, 0, 10 );
		this.configGroup.addConfigs( this.isAlwaysUsable, this.effectDuration, this.effectAmplifier );
	}

	/** Using bandage on right click. (self healing) */
	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );
		Instances.BANDAGE_ITEM.useIfPossible( itemStack, player, player );

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	/** Adding tooltip with information for what bandage is used. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		if( !flag.isAdvanced() )
			return;

		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.bandage.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
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
		applyRegeneration( target );
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

	/** Applies Regeneration on target depending on current mod settings. */
	private void applyRegeneration( LivingEntity target ) {
		EffectHelper.applyEffectIfPossible( target, Effects.REGENERATION, getRegenerationDuration(), getRegenerationAmplifier() );
	}
}
