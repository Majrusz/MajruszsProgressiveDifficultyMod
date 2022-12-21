package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.effects.BleedingEffect;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import com.mlib.items.ItemHelper;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

@AutoInstance
public class SharpToolsBleeding extends GameModifier {
	static final String ATTRIBUTE_ID = "effect.majruszsdifficulty.bleeding.item_tooltip";
	final Condition.Ref< CustomConditions.CRDChance< OnDamaged.Data > > chance = new Condition.Ref<>();
	final Condition.Ref< Condition.Excludable< OnDamaged.Data > > excludable = new Condition.Ref<>();

	public SharpToolsBleeding() {
		super( Registries.Modifiers.DEFAULT, "SharpToolsBleeding", "All sharp items (tools, shears etc.) may inflict bleeding." );

		OnBleedingCheck.Context onCheck = new OnBleedingCheck.Context( OnBleedingCheck.Data::trigger );
		onCheck.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance< OnBleedingCheck.Data >( 0.25, false ).save( this.chance ) )
			.addCondition( new Condition.Excludable< OnBleedingCheck.Data >().save( this.excludable ) )
			.addCondition( new Condition.IsLivingBeing<>() )
			.addCondition( data->ItemHelper.hasInMainHand( data.attacker, TieredItem.class, TridentItem.class, ShearsItem.class ) )
			.addCondition( data->data.source.getDirectEntity() == data.attacker );

		OnItemAttributeTooltip.Context onTooltip = new OnItemAttributeTooltip.Context( this::addTooltip );
		onTooltip.addCondition( data->data.item instanceof TieredItem || data.item instanceof TridentItem || data.item instanceof ShearsItem )
			.addCondition( data->this.excludable.get().getConfig().isEnabled() )
			.addCondition( data->BleedingEffect.isEnabled() );

		this.addContexts( onCheck, onTooltip );
	}

	private void addTooltip( OnItemAttributeTooltip.Data data ) {
		String chance = TextHelper.percent( this.chance.get().getConfig().asFloat() );
		String amplifier = TextHelper.toRoman( BleedingEffect.getAmplifier() + 1 );
		data.add( EquipmentSlot.MAINHAND, Component.translatable( ATTRIBUTE_ID, chance, amplifier )
			.withStyle( ChatFormatting.DARK_GREEN ) );
	}
}
