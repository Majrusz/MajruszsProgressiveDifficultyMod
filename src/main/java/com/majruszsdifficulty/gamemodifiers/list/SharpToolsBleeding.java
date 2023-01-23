package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import com.mlib.items.ItemHelper;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

import java.util.function.Supplier;

@AutoInstance
public class SharpToolsBleeding extends GameModifier {
	static final String ATTRIBUTE_ID = "effect.majruszsdifficulty.bleeding.item_tooltip";
	static Supplier< Boolean > IS_ENABLED = ()->false;
	static Supplier< Float > GET_CHANCE = ()->0.0f;

	public SharpToolsBleeding() {
		super( Registries.Modifiers.DEFAULT );

		new OnBleedingCheck.Context( OnBleedingCheck.Data::trigger )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new BleedingChance( 0.25, false ) )
			.addCondition( new ExcludableBleeding() )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker )
			.insertTo( this );

		new OnItemAttributeTooltip.Context( this::addTooltip )
			.addCondition( data->data.item instanceof TieredItem || data.item instanceof TridentItem || data.item instanceof ShearsItem )
			.addCondition( IS_ENABLED )
			.insertTo( this );

		this.name( "SharpToolsBleeding" ).comment( "All sharp items (tools, shears etc.) may inflict bleeding." );
	}

	private void addTooltip( OnItemAttributeTooltip.Data data ) {
		String chance = TextHelper.percent( GET_CHANCE.get() );
		String amplifier = TextHelper.toRoman( BleedingEffect.getAmplifier() + 1 );
		data.add( EquipmentSlot.MAINHAND, new TranslatableComponent( ATTRIBUTE_ID, chance, amplifier )
			.withStyle( ChatFormatting.DARK_GREEN ) );
	}

	private static class BleedingChance extends CustomConditions.CRDChance< OnBleedingCheck.Data > {
		BleedingChance( double chance, boolean scaledByCRD ) {
			super( chance, scaledByCRD );

			GET_CHANCE = this.chance::asFloat;
		}
	}

	private static class ExcludableBleeding extends Condition.Excludable< OnBleedingCheck.Data > {
		ExcludableBleeding() {
			super();

			IS_ENABLED = ()->this.availability.isEnabled() && BleedingEffect.isEnabled();
		}
	}
}
