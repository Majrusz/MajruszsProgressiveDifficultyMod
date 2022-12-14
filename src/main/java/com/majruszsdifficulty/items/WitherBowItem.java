package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnProjectileShot;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class WitherBowItem extends BowItem {
	final static String TOOLTIP_TRANSLATION_KEY = "item.majruszsdifficulty.wither_sword.tooltip";

	public WitherBowItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		if( !flag.isAdvanced() )
			return;

		String amplifier = TextHelper.toRoman( Effect.WITHER.getAmplifier() + 1 );
		String duration = TextHelper.minPrecision( Utility.ticksToSeconds( Effect.WITHER.getDuration() ) );
		tooltip.add( Component.translatable( TOOLTIP_TRANSLATION_KEY, amplifier, duration ).withStyle( ChatFormatting.GRAY ) );
	}

	@OnlyIn( Dist.CLIENT )
	public static float calculatePull( ItemStack itemStack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entity,
		int seed
	) {
		return entity != null && entity.getUseItem() == itemStack ? ( float )( itemStack.getUseDuration() - entity.getUseItemRemainingTicks() ) / 20.0f : 0.0f;
	}

	@OnlyIn( Dist.CLIENT )
	public static float calculatePulling( ItemStack itemStack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entity,
		int seed
	) {
		return entity != null && entity.isUsingItem() && entity.getUseItem() == itemStack ? 1.0f : 0.0f;
	}

	@AutoInstance
	public static class Effect extends GameModifier {
		static final EffectConfig WITHER = new EffectConfig( "", ()->MobEffects.WITHER, 0, 16.0 );

		public Effect() {
			super( Registries.Modifiers.DEFAULT, "WitherBowEffect", "Wither Bow inflicts wither effect." );

			OnProjectileShot.Context onShot = new OnProjectileShot.Context( this::applyWither );
			onShot.addCondition( data->data.weapon != null && data.weapon.getItem() instanceof WitherBowItem )
				.addCondition( data->data.projectile instanceof Arrow )
				.addConfig( WITHER );

			this.addContext( onShot );
		}

		private void applyWither( OnProjectileShot.Data data ) {
			Arrow arrow = ( Arrow )data.projectile;
			arrow.addEffect( new MobEffectInstance( MobEffects.WITHER, WITHER.getDuration(), WITHER.getAmplifier() ) );
		}
	}

}
