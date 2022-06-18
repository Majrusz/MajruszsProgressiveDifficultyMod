package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.Utility;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
	private static final String TOOLTIP_TRANSLATION_KEY_1 = "item.majruszsdifficulty.bandage.item_tooltip1";
	private static final String TOOLTIP_TRANSLATION_KEY_2 = "item.majruszsdifficulty.bandage.item_tooltip2";
	protected final ConfigGroup configGroup;
	protected final AvailabilityConfig isAlwaysUsable;
	protected final DurationConfig effectDuration;
	protected final IntegerConfig effectAmplifier;

	public BandageItem() {
		this( "Bandage", 0, Rarity.COMMON );
	}

	public BandageItem( String name, int defaultAmplifier, Rarity rarity ) {
		super( ( new Properties() ).stacksTo( 16 ).tab( Instances.ITEM_GROUP ).rarity( rarity ) );

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
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );
		useIfPossible( itemStack, player, player );

		return InteractionResultHolder.sidedSuccess( itemStack, world.isClientSide() );
	}

	/** Adding tooltip with information for what bandage is used. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY_1, TOOLTIP_TRANSLATION_KEY_2 );
	}

	/** Using bandage on right click. (other entity healing) */
	@SubscribeEvent
	public static void onRightClick( PlayerInteractEvent.EntityInteract event ) {
		if( !( event.getTarget() instanceof LivingEntity ) )
			return;

		ItemStack itemStack = event.getItemStack();
		if( !( itemStack.getItem() instanceof BandageItem ) )
			return;
		BandageItem bandage = ( BandageItem )itemStack.getItem();
		if( bandage.useIfPossible( event.getItemStack(), event.getPlayer(), ( LivingEntity )event.getTarget() ) ) {
			Villager villager = Utility.castIfPossible( Villager.class, event.getTarget() );
			if( villager != null )
				increaseReputation( event.getPlayer(), villager );

			event.setCancellationResult( InteractionResult.SUCCESS );
		}
	}

	/** Increases reputation with given villager. */
	public static void increaseReputation( Player player, Villager villager ) {
		villager.getGossips().add( player.getUUID(), GossipType.MINOR_POSITIVE, 5 );
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
		EffectHelper.applyEffectIfPossible( target, MobEffects.REGENERATION, getRegenerationDuration(), getRegenerationAmplifier() );
	}

	/**
	 Removes bleeding from the target or applies regeneration if it is possible.

	 @param bandage Bandage item.
	 @param player  Player that is right clicking.
	 @param target  Target that will be healed. (may be the same player)

	 @return Returns information (boolean) if bleeding was removed or regeneration was applied.
	 */
	private boolean useIfPossible( ItemStack bandage, Player player, LivingEntity target ) {
		if( !couldBeUsedOn( target, bandage ) )
			return false;

		if( !player.getAbilities().instabuild )
			bandage.shrink( 1 );

		player.awardStat( Stats.ITEM_USED.get( bandage.getItem() ) );
		removeBleeding( target, player );
		applyEffects( target );
		target.level.playSound( null, target.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 1.0f, 1.0f );

		return true;
	}

	/** Checks whether item could be used on target. */
	private boolean couldBeUsedOn( LivingEntity target, ItemStack bandage ) {
		boolean isBandage = bandage.getItem() instanceof BandageItem;
		boolean targetHasRegeneration = target.hasEffect( MobEffects.REGENERATION );

		return isBandage && ( ( isAlwaysUsable() && !targetHasRegeneration ) || target.hasEffect( Instances.BLEEDING ) );
	}

	/** Removes Bleeding effect from the target. */
	private void removeBleeding( LivingEntity target, Player causer ) {
		BleedingEffect bleeding = Instances.BLEEDING;

		if( target.hasEffect( bleeding ) && causer instanceof ServerPlayer )
			Instances.BANDAGE_TRIGGER.trigger( ( ServerPlayer )causer, this, target.equals( causer ) );

		target.removeEffect( bleeding );
		target.removeEffectNoUpdate( bleeding );
	}
}
