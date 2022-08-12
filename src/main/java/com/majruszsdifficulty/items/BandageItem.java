package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.mlib.config.ConfigGroup;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnPlayerInteractContext;
import com.mlib.gamemodifiers.data.OnPlayerInteractData;
import com.mlib.items.ItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/** A bandage item that removes the bleeding and gives regeneration for a few seconds. */
@Mod.EventBusSubscriber
public class BandageItem extends Item {
	private static final String TOOLTIP_TRANSLATION_KEY_1 = "item.majruszsdifficulty.bandage.item_tooltip1";
	private static final String TOOLTIP_TRANSLATION_KEY_2 = "item.majruszsdifficulty.bandage.item_tooltip2";

	public BandageItem( Rarity rarity ) {
		super( new Properties().stacksTo( 16 ).tab( Registries.ITEM_GROUP ).rarity( rarity ) );
	}

	public BandageItem() {
		this( Rarity.COMMON );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag
	) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY_1, TOOLTIP_TRANSLATION_KEY_2 );
	}

	public static class BandageUse extends GameModifier {
		final EffectConfig regeneration = new EffectConfig( "Regeneration", ()->MobEffects.REGENERATION, 0, 4.0 );
		final EffectConfig goldenRegeneration = new EffectConfig( "Regeneration", ()->MobEffects.REGENERATION, 1, 4.0 );
		final EffectConfig goldenImmunity = new EffectConfig( "Immunity", Registries.BLEEDING_IMMUNITY::get, 0, 60.0 );

		public BandageUse() {
			super( Registries.Modifiers.DEFAULT, "Bandages", "" );

			OnPlayerInteractContext onInteraction = new OnPlayerInteractContext( this::useBandage );
			onInteraction.addCondition( data->data.itemStack.getItem() instanceof BandageItem )
				.addCondition( data->data.target != null )
				.addCondition( data->!data.player.swinging )
				.addCondition( data->!( data.event instanceof PlayerInteractEvent.RightClickBlock ) )
				.addConfig( new ConfigGroup( "Bandage", "Config for a Bandage item.", this.regeneration ) )
				.addConfig( new ConfigGroup( "GoldenBandage", "Config for a Golden Bandage item.", this.goldenRegeneration, this.goldenImmunity ) );

			this.addContext( onInteraction );
		}

		private void useBandage( OnPlayerInteractData data ) {
			Player player = data.player;
			LivingEntity target = data.target;
			ItemStack itemStack = data.itemStack;

			assert target != null;
			ItemHelper.consumeItemOnUse( itemStack, player );
			player.swing( data.event.getHand(), true );
			removeBleeding( itemStack, player, target );
			if( target instanceof Villager villager && villager.hasEffect( Registries.BLEEDING.get() ) ) {
				increaseReputation( villager, player );
			}
			applyEffects( itemStack, target );
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
