package com.majruszsdifficulty.items;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EffectDef;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.OnItemAttributeTooltip;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.data.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;

public class WitherSword extends SwordItem {
	private static EffectDef EFFECT = new EffectDef( ()->MobEffects.WITHER, 1, 6.0f );

	static {
		OnEntityDamaged.listen( WitherSword::apply )
			.addCondition( data->!data.source.isIndirect() )
			.addCondition( data->data.attacker != null )
			.addCondition( data->data.attacker.getMainHandItem().getItem() instanceof WitherSword );

		OnItemAttributeTooltip.listen( WitherSword::addTooltip )
			.addCondition( data->data.itemStack.getItem() instanceof WitherSword );

		Serializables.getStatic( Config.Items.class )
			.define( "wither_sword", WitherSword.class );

		Serializables.getStatic( WitherSword.class )
			.define( "effect", Reader.custom( EffectDef::new ), ()->EFFECT, v->EFFECT = v );
	}

	public WitherSword() {
		super( CustomItemTier.WITHER, 3, -2.4f, new Properties().rarity( Rarity.UNCOMMON ) );
	}

	private static void apply( OnEntityDamaged data ) {
		data.target.addEffect( EFFECT.toEffectInstance() );
	}

	private static void addTooltip( OnItemAttributeTooltip data ) {
		data.add( EquipmentSlot.MAINHAND, TextHelper.translatable( "item.majruszsdifficulty.wither_sword.effect", TextHelper.percent( 1.0f ), TextHelper.toRoman( EFFECT.amplifier + 1 ) )
			.withStyle( ChatFormatting.DARK_GREEN )
		);
	}
}
