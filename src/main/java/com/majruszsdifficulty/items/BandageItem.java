package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.Utility;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.items.ItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import static com.majruszsdifficulty.MajruszsDifficulty.GAME_MODIFIERS_GROUP;

/** A bandage item that removes the bleeding and gives regeneration for a few seconds. */
@Mod.EventBusSubscriber
public class BandageItem extends Item {
	private static final String TOOLTIP_TRANSLATION_KEY_1 = "item.majruszsdifficulty.bandage.item_tooltip1";
	private static final String TOOLTIP_TRANSLATION_KEY_2 = "item.majruszsdifficulty.bandage.item_tooltip2";
	protected final ConfigGroup configGroup;
	protected final BooleanConfig isAlwaysUsable;
	protected final DoubleConfig effectDuration;
	protected final IntegerConfig effectAmplifier;

	public BandageItem() {
		this( "Bandage", 0, Rarity.COMMON );
	}

	public BandageItem( String name, int defaultAmplifier, Rarity rarity ) {
		super( new Properties().stacksTo( 16 ).tab( Registries.ITEM_GROUP ).rarity( rarity ) );

		this.isAlwaysUsable = new BooleanConfig( "is_always_usable", "Is " + name + " always usable? If not player can only use " + name + " when it is bleeding.", false, true );
		this.effectDuration = new DoubleConfig( "regeneration_duration", "Duration in seconds of Regeneration effect.", false, 4.0, 1.0, 120.0 );
		this.effectAmplifier = new IntegerConfig( "regeneration_amplifier", "Level/amplifier of Regeneration effect.", false, defaultAmplifier, 0, 10 );
		this.configGroup = GAME_MODIFIERS_GROUP.addGroup( new ConfigGroup( name, "Configuration for " + name + " item.", this.isAlwaysUsable, this.effectDuration, this.effectAmplifier ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );
		useIfPossible( itemStack, player, player ); // self-healing

		return InteractionResultHolder.sidedSuccess( itemStack, world.isClientSide() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY_1, TOOLTIP_TRANSLATION_KEY_2 );
	}

	@SubscribeEvent
	public static void onRightClick( PlayerInteractEvent.EntityInteract event ) {
		if( !( event.getTarget() instanceof LivingEntity target ) )
			return;

		if( !( event.getItemStack().getItem() instanceof BandageItem bandage ) )
			return;

		if( !bandage.useIfPossible( event.getItemStack(), event.getPlayer(), target ) ) // healing other entities
			return;

		Villager villager = Utility.castIfPossible( Villager.class, event.getTarget() );
		if( villager != null )
			increaseReputation( event.getPlayer(), villager );

		event.setCancellationResult( InteractionResult.SUCCESS );
	}

	public static void increaseReputation( Player player, Villager villager ) {
		villager.getGossips().add( player.getUUID(), GossipType.MINOR_POSITIVE, 5 );
	}

	public boolean isAlwaysUsable() {
		return this.isAlwaysUsable.isEnabled();
	}

	public int getRegenerationDuration() {
		return this.effectDuration.asTicks();
	}

	public int getRegenerationAmplifier() {
		return this.effectAmplifier.get();
	}

	protected void applyEffects( LivingEntity target ) {
		EffectHelper.applyEffectIfPossible( target, MobEffects.REGENERATION, getRegenerationDuration(), getRegenerationAmplifier() );
	}

	/** Removes the bleeding from the target or applies regeneration if it is possible. */
	private boolean useIfPossible( ItemStack bandage, Player player, LivingEntity target ) {
		if( !couldBeUsedOn( target, bandage ) )
			return false;

		ItemHelper.consumeItemOnUse( bandage, player );
		removeBleeding( target, player );
		applyEffects( target );
		target.level.playSound( null, target.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 1.0f, 1.0f );

		return true;
	}

	private boolean couldBeUsedOn( LivingEntity target, ItemStack bandage ) {
		boolean isBandage = bandage.getItem() instanceof BandageItem;
		boolean targetHasRegeneration = target.hasEffect( MobEffects.REGENERATION );

		return isBandage && ( ( isAlwaysUsable() && !targetHasRegeneration ) || target.hasEffect( Registries.BLEEDING.get() ) );
	}

	private void removeBleeding( LivingEntity target, Player causer ) {
		BleedingEffect bleeding = Registries.BLEEDING.get();

		if( target.hasEffect( bleeding ) && causer instanceof ServerPlayer serverCauser )
			Registries.BANDAGE_TRIGGER.trigger( serverCauser, this, target.equals( serverCauser ) );

		target.removeEffect( bleeding );
		target.removeEffectNoUpdate( bleeding );
	}
}
