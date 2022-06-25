package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.Config;
import com.mlib.gamemodifiers.contexts.OnPlayerInteractContext;
import com.mlib.items.ItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, TOOLTIP_TRANSLATION_KEY_1, TOOLTIP_TRANSLATION_KEY_2 );
	}

	public static class BandageUse extends GameModifier {
		static final Config.Effect REGENERATION = new Config.Effect( "Regeneration", ()->MobEffects.REGENERATION, 0, 4.0 );
		static final Config.Effect GOLDEN_REGENERATION = new Config.Effect( "GoldenBandageRegeneration", ()->MobEffects.REGENERATION, 1, 4.0 );
		static final Config.Effect GOLDEN_IMMUNITY = new Config.Effect( "GoldenBandageImmunity", Registries.BLEEDING_IMMUNITY::get, 0, 60.0 );
		static final OnPlayerInteractContext ON_INTERACTION = new OnPlayerInteractContext();

		static {
			ON_INTERACTION.addCondition( new Condition.ContextOnPlayerInteract( data->data.itemStack.getItem() instanceof BandageItem ) );
			ON_INTERACTION.addCondition( new Condition.ContextOnPlayerInteract( data->data.target != null ) );
			ON_INTERACTION.addCondition( new Condition.ContextOnPlayerInteract( data->!data.player.swinging ) );
			ON_INTERACTION.addCondition( new Condition.ContextOnPlayerInteract( data->!( data.event instanceof PlayerInteractEvent.RightClickBlock ) ) );
			ON_INTERACTION.addConfigs( REGENERATION, GOLDEN_REGENERATION, GOLDEN_IMMUNITY );
		}

		public BandageUse() {
			super( GameModifier.DEFAULT, "Bandage", "Config for bandages.", ON_INTERACTION );
		}

		@Override
		public void execute( Object data ) {
			if( data instanceof OnPlayerInteractContext.Data interactData ) {
				useBandage( interactData, interactData.event );
				interactData.event.setCancellationResult( InteractionResult.SUCCESS );
			}
		}

		public static void useBandage( OnPlayerInteractContext.Data data, PlayerInteractEvent event ) {
			Player player = data.player;
			LivingEntity target = data.target;
			ItemStack itemStack = event.getItemStack();

			assert target != null;
			ItemHelper.consumeItemOnUse( itemStack, player );
			player.swing( event.getHand(), true );
			removeBleeding( itemStack, player, target );
			if( target instanceof Villager villager && villager.hasEffect( Registries.BLEEDING.get() ) ) {
				increaseReputation( villager, player );
			}
			applyEffects( itemStack, target );
			playSfx( target );
		}

		private static void applyEffects( ItemStack itemStack, LivingEntity target ) {
			if( itemStack.getItem() instanceof GoldenBandageItem ) {
				GOLDEN_REGENERATION.apply( target );
				GOLDEN_IMMUNITY.apply( target );
			} else {
				REGENERATION.apply( target );
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
