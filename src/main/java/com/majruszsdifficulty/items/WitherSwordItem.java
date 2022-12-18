package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import com.mlib.items.ItemHelper;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;

public class WitherSwordItem extends SwordItem {
	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 3, -2.4f, new Properties().rarity( Rarity.UNCOMMON ) );
	}

	@AutoInstance
	public static class Effect extends GameModifier {
		static final String ATTRIBUTE_ID = "item.majruszsdifficulty.wither_sword.effect";
		static final EffectConfig WITHER = new EffectConfig( "", ()->MobEffects.WITHER, 1, 6.0 );

		public Effect() {
			super( Registries.Modifiers.DEFAULT, "WitherSwordEffect", "Wither Sword inflicts wither effect." );

			OnDamaged.Context onDamaged = new OnDamaged.Context( this::applyWither );
			onDamaged.addCondition( data->ItemHelper.hasInMainHand( data.attacker, WitherSwordItem.class ) )
				.addCondition( data->data.source.getDirectEntity() == data.attacker )
				.addConfig( WITHER );

			new OnItemAttributeTooltip.Context( this::addTooltip )
				.addCondition( data->data.item instanceof WitherSwordItem );

			this.addContext( onDamaged );
		}

		private void applyWither( OnDamaged.Data data ) {
			MobEffectHelper.tryToApply( data.target, MobEffects.WITHER, WITHER.getDuration(), WITHER.getAmplifier() );
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			String chance = TextHelper.percent( 1.0f );
			String amplifier = TextHelper.toRoman( Effect.WITHER.getAmplifier() + 1 );
			data.add( EquipmentSlot.MAINHAND, Component.translatable( ATTRIBUTE_ID, chance, amplifier )
				.withStyle( ChatFormatting.DARK_GREEN ) );
		}
	}

}
