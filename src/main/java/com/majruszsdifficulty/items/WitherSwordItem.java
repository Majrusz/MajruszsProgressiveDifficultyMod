package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.items.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class WitherSwordItem extends SwordItem {
	final static String TOOLTIP_TRANSLATION_KEY = "item.majruszsdifficulty.wither_sword.tooltip";

	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 3, -2.4f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		if( !flag.isAdvanced() )
			return;

		String amplifier = Utility.toRoman( Effect.WITHER.getAmplifier() + 1 );
		String duration = Utility.minPrecision( Utility.ticksToSeconds( Effect.WITHER.getDuration() ) );
		tooltip.add( Component.translatable( TOOLTIP_TRANSLATION_KEY, amplifier, duration ).withStyle( ChatFormatting.GRAY ) );
	}

	public static class Effect extends GameModifier {
		static final EffectConfig WITHER = new EffectConfig( "", ()->MobEffects.WITHER, 1, 6.0 );
		static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( Effect::applyWither );

		static {
			ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->ItemHelper.hasInMainHand( data.attacker, WitherSwordItem.class ) ) );
			ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
			ON_DAMAGED.addConfig( WITHER );
		}

		public Effect() {
			super( GameModifier.DEFAULT, "WitherSwordEffect", "Wither Sword inflicts wither effect.", ON_DAMAGED );
		}

		private static void applyWither( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
			EffectHelper.applyEffectIfPossible( data.target, MobEffects.WITHER, WITHER.getDuration(), WITHER.getAmplifier() );
		}
	}

}
