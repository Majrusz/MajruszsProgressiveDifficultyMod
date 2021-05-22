package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.models.HermesArmorModel;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	private static final String TOOLTIP_TRANSLATION_KEY = "item.majruszs_difficulty.hermes_boots.item_tooltip";

	public HermesBootsItem() {
		super( CustomArmorMaterial.HERMES, EquipmentSlotType.FEET, ( new Properties() ).group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );

		String comment = "Movement speed extra multiplier.";
		this.movementSpeedBonus = new DoubleConfig( "movement_speed_bonus", comment, false, 0.25, 0.0, 1.0 );

		this.configGroup = new ConfigGroup( "HermesBoots", "Hermes Boots item configuration." );
		MajruszsDifficulty.FEATURES_GROUP.addGroup( this.configGroup );
		this.configGroup.addConfig( this.movementSpeedBonus );
	}

	/** Adds tooltip about Hermes Boots movement speed bonus. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, TOOLTIP_TRANSLATION_KEY );
	}

	/** Returns path to Hermes Boots texture. */
	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlotType slot, String type ) {
		ResourceLocation textureLocation = MajruszsDifficulty.getLocation( "textures/models/armor/hermes_layer.png" );
		return textureLocation.toString();
	}

	/** Returns model used to render armor. */
	@OnlyIn( Dist.CLIENT )
	@Nullable
	@Override
	public < ArmorModel extends BipedModel< ? > > ArmorModel getArmorModel( LivingEntity entity, ItemStack itemStack, EquipmentSlotType armorSlot,
		ArmorModel defaultModel
	) {
		HermesArmorModel model = new HermesArmorModel();

		model.bipedHead.showModel = armorSlot == EquipmentSlotType.HEAD;
		model.bipedHeadwear.showModel = false;
		model.bipedBody.showModel = armorSlot == EquipmentSlotType.CHEST;
		model.bipedLeftArm.showModel = false;
		model.bipedRightArm.showModel = false;
		model.bipedRightLeg.showModel = armorSlot == EquipmentSlotType.FEET;
		model.bipedLeftLeg.showModel = armorSlot == EquipmentSlotType.FEET;

		model.isChild = defaultModel.isChild;
		model.isSitting = defaultModel.isSitting;
		model.isSneak = defaultModel.isSneak;
		model.rightArmPose = defaultModel.rightArmPose;
		model.leftArmPose = defaultModel.leftArmPose;

		return ( ArmorModel )model;
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		MOVEMENT_BONUS_ATTRIBUTE.setValue( Instances.HERMES_BOOTS_ITEM.getMovementSpeedBonus( entity ) )
			.apply( entity );
	}

	/** Returns movement speed bonus if entity has Hermes Boots equipped. */
	public double getMovementSpeedBonus( LivingEntity entity ) {
		ItemStack boots = entity.getItemStackFromSlot( EquipmentSlotType.FEET );
		return ( boots.getItem() instanceof HermesBootsItem ? 1.0 : 0.0 ) * this.movementSpeedBonus.get();
	}
}
