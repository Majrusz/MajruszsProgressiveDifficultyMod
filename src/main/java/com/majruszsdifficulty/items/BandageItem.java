package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnPlayerInteract;
import com.mlib.items.ItemHelper;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BandageItem extends Item {
	static final String TOOLTIP_ID = "item.majruszsdifficulty.bandage.effect";

	public BandageItem( Rarity rarity ) {
		super( new Properties().stacksTo( 16 ).rarity( rarity ) );
	}

	public BandageItem() {
		this( Rarity.COMMON );
	}

	@Override
	public void appendHoverText( ItemStack itemStack, @Nullable Level level, List< Component > components, TooltipFlag flag ) {
		if( this instanceof GoldenBandageItem ) {
			components.add( this.buildComponent( Effects.INSTANCE.goldenRegeneration ).withStyle( ChatFormatting.BLUE ) );
			components.add( this.buildComponent( Effects.INSTANCE.goldenImmunity ).withStyle( ChatFormatting.BLUE ) );
		} else {
			components.add( this.buildComponent( Effects.INSTANCE.regeneration ).withStyle( ChatFormatting.BLUE ) );
		}

		components.add( CommonComponents.EMPTY );
		components.add( Component.translatable( "potion.whenDrank" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		components.add( Component.translatable( TOOLTIP_ID ).withStyle( ChatFormatting.BLUE ) );
	}

	private MutableComponent buildComponent( EffectConfig config ) {
		Component effectName = config.getEffect().getDisplayName();
		Component fullName = config.getAmplifier() > 0 ? Component.translatable( "potion.withAmplifier", effectName.getString(), TextHelper.toRoman( config.getAmplifier() + 1 ) ) : Component.literal( effectName.getString() );

		return Component.translatable( "potion.withDuration", fullName.getString(), StringUtil.formatTickDuration( config.getDuration() ) );
	}

	@AutoInstance
	public static class Effects extends GameModifier {
		static Effects INSTANCE = null;
		final EffectConfig regeneration = new EffectConfig( "Regeneration", ()->MobEffects.REGENERATION, 0, 4.0 );
		final EffectConfig goldenRegeneration = new EffectConfig( "Regeneration", ()->MobEffects.REGENERATION, 1, 4.0 );
		final EffectConfig goldenImmunity = new EffectConfig( "Immunity", Registries.BLEEDING_IMMUNITY::get, 0, 60.0 );

		public Effects() {
			super( Registries.Modifiers.DEFAULT, "Bandages", "" );

			OnPlayerInteract.Context onInteraction = new OnPlayerInteract.Context( this::useBandage );
			onInteraction.addCondition( data->data.itemStack.getItem() instanceof BandageItem )
				.addCondition( data->data.target != null )
				.addCondition( data->!( data.event instanceof PlayerInteractEvent.RightClickBlock ) )
				.addCondition( Effects::canUse )
				.addConfig( new ConfigGroup( "Bandage", "Config for a Bandage item.", this.regeneration ) )
				.addConfig( new ConfigGroup( "GoldenBandage", "Config for a Golden Bandage item.", this.goldenRegeneration, this.goldenImmunity ) );

			INSTANCE = this;

			this.addContext( onInteraction );
		}

		private void useBandage( OnPlayerInteract.Data data ) {
			Player player = data.player;
			LivingEntity target = data.target;
			ItemStack itemStack = data.itemStack;

			assert target != null;
			player.swing( data.event.getHand(), true );
			setCooldown( player );
			removeBleeding( itemStack, player, target );
			if( target instanceof Villager villager && villager.hasEffect( Registries.BLEEDING.get() ) ) {
				increaseReputation( villager, player );
			}
			applyEffects( itemStack, target );
			ItemHelper.consumeItemOnUse( itemStack, player );
			playSfx( target );
			data.event.setCancellationResult( InteractionResult.SUCCESS );
		}

		private void applyEffects( ItemStack itemStack, LivingEntity target ) {
			if( itemStack.getItem() instanceof GoldenBandageItem ) {
				this.goldenRegeneration.apply( target );
				this.goldenImmunity.apply( target );
			} else {
				this.regeneration.apply( target );
			}
		}

		private static void setCooldown( Player player ) {
			int duration = Utility.secondsToTicks( 0.7 );

			ItemHelper.addCooldown( player, duration, Registries.BANDAGE.get(), Registries.GOLDEN_BANDAGE.get() );
		}

		private static boolean canUse( OnPlayerInteract.Data data ) {
			return !ItemHelper.isOnCooldown( data.player, Registries.BANDAGE.get(), Registries.GOLDEN_BANDAGE.get() );
		}

		private static void increaseReputation( Villager villager, Player player ) {
			villager.getGossips().add( player.getUUID(), GossipType.MINOR_POSITIVE, 5 );
		}

		private static void removeBleeding( ItemStack itemStack, Player player, LivingEntity target ) {
			BleedingEffect bleeding = Registries.BLEEDING.get();
			if( target.hasEffect( bleeding ) && player instanceof ServerPlayer serverPlayer ) {
				Registries.BANDAGE_TRIGGER.trigger( serverPlayer, ( BandageItem )itemStack.getItem(), target.equals( serverPlayer ) );
			}
			target.removeEffect( bleeding );
		}

		private static void playSfx( LivingEntity target ) {
			target.level.playSound( null, target.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 1.0f, 1.0f );
		}
	}
}
