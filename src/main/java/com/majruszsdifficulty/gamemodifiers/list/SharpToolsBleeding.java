package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import com.mlib.items.ItemHelper;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

import java.util.function.Supplier;

@AutoInstance
public class SharpToolsBleeding {
	static final String ATTRIBUTE_ID = "effect.majruszsdifficulty.bleeding.item_tooltip";
	static Supplier< Boolean > IS_ENABLED = ()->false;
	static Supplier< Float > GET_CHANCE = ()->0.0f;

	public SharpToolsBleeding() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "SharpToolsBleeding" )
			.comment( "All sharp items (tools, shears etc.) may inflict bleeding." );

		var chance = Condition.< OnBleedingCheck.Data > chanceCRD( 0.25, false );
		GET_CHANCE = ()->( ( DoubleConfig )chance.getConfigs().get( 0 ) ).asFloat(); // TODO: refactor
		var excludable = Condition.< OnBleedingCheck.Data > excludable();
		IS_ENABLED = ()->( ( BooleanConfig )excludable.getConfigs().get( 0 ) ).isEnabled() && BleedingEffect.isEnabled(); // TODO: refactor
		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.NORMAL ) )
			.addCondition( chance )
			.addCondition( excludable )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() == data.attacker ) )
			.insertTo( group );

		OnItemAttributeTooltip.listen( this::addTooltip )
			.addCondition( Condition.predicate( data->data.item instanceof TieredItem || data.item instanceof TridentItem || data.item instanceof ShearsItem ) )
			.addCondition( Condition.predicate( IS_ENABLED ) )
			.insertTo( group );
	}

	private void addTooltip( OnItemAttributeTooltip.Data data ) {
		String chance = TextHelper.percent( GET_CHANCE.get() );
		String amplifier = TextHelper.toRoman( BleedingEffect.getAmplifier() + 1 );
		data.add( EquipmentSlot.MAINHAND, Component.translatable( ATTRIBUTE_ID, chance, amplifier )
			.withStyle( ChatFormatting.DARK_GREEN ) );
	}
}
