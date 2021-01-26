package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/** Boots that increases movement speed. */
@Mod.EventBusSubscriber
public class HermesBootsItem extends ArmorItem {
	protected static final AttributeHandler MOVEMENT_BONUS_ATTRIBUTE = new AttributeHandler( "dbe472cb-df52-44ab-ab38-01b00e24f649",
		"HermesBootsMovementSpeedBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE
	);
	protected final DoubleConfig movementSpeedBonus;
	protected final ConfigGroup configGroup;

	public HermesBootsItem() {
		super( CustomArmorMaterial.HERMES, EquipmentSlotType.FEET, ( new Properties() ).group( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );

		String comment = "Movement speed extra multiplier.";
		this.movementSpeedBonus = new DoubleConfig( "movement_speed_bonus", comment, false, 0.2, 0.0, 1.0 );

		this.configGroup = new ConfigGroup( "HermesBoots", "Hermes Boots item configuration." );
		MajruszsDifficulty.FEATURES_GROUP.addGroup( this.configGroup );
		this.configGroup.addConfig( this.movementSpeedBonus );
	}

	/** Adding tooltip with information for what bandage is used. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		if( !flag.isAdvanced() )
			return;

		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.hermes_boots.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		MOVEMENT_BONUS_ATTRIBUTE.setValue( Instances.HERMES_BOOTS_ITEM.getMovementSpeedBonus( entity ) ).apply( entity );
	}

	public double getMovementSpeedBonus( LivingEntity entity ) {
		ItemStack boots = entity.getItemStackFromSlot( EquipmentSlotType.FEET );
		return ( boots.getItem() instanceof HermesBootsItem ? 1.0 : 0.0 ) * this.movementSpeedBonus.get();
	}
}
