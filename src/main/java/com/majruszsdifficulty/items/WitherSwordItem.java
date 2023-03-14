package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.EffectConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
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
		final EffectConfig wither = new EffectConfig( MobEffects.WITHER, 1, 6.0 );

		public Effect() {
			super( Registries.Modifiers.DEFAULT );

			OnDamaged.listen( this::applyWither )
				.addCondition( Condition.predicate( data->ItemHelper.hasInMainHand( data.attacker, WitherSwordItem.class ) ) )
				.addCondition( Condition.predicate( data->data.source.getDirectEntity() == data.attacker ) )
				.addConfig( this.wither )
				.insertTo( this );

			OnItemAttributeTooltip.listen( this::addTooltip )
				.addCondition( Condition.predicate( data->data.item instanceof WitherSwordItem ) )
				.insertTo( this );

			this.name( "WitherSwordEffect" ).comment( "Wither Sword inflicts wither effect." );
		}

		private void applyWither( OnDamaged.Data data ) {
			MobEffectHelper.tryToApply( data.target, MobEffects.WITHER, this.wither.getDuration(), this.wither.getAmplifier() );
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			String chance = TextHelper.percent( 1.0f );
			String amplifier = TextHelper.toRoman( this.wither.getAmplifier() + 1 );
			data.add( EquipmentSlot.MAINHAND, Component.translatable( ATTRIBUTE_ID, chance, amplifier )
				.withStyle( ChatFormatting.DARK_GREEN ) );
		}
	}

}
