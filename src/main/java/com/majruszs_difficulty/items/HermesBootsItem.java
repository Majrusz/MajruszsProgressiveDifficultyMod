package com.majruszs_difficulty.items;

import com.google.common.collect.ImmutableMultimap;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.UUID;

/** Boots that increases movement speed. */
@Mod.EventBusSubscriber
public class HermesBootsItem extends AttributeArmorItem {
	protected final DoubleConfig movementSpeedBonus;
	protected final ConfigGroup configGroup;

	public HermesBootsItem() {
		super( CustomArmorMaterial.HERMES, EquipmentSlot.FEET, ( new Properties() ).tab( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );

		String comment = "Movement speed extra multiplier. (requires game/world restart!)";
		this.movementSpeedBonus = new DoubleConfig( "movement_speed_bonus", comment, true, 0.25, 0.0, 5.0 );

		this.configGroup = new ConfigGroup( "HermesBoots", "Hermes Boots item configuration." );
		MajruszsDifficulty.FEATURES_GROUP.addGroup( this.configGroup );
		this.configGroup.addConfig( this.movementSpeedBonus );
	}

	/** Returns path to Hermes Boots texture. */
	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		ResourceLocation textureLocation = MajruszsDifficulty.getLocation( "textures/models/armor/hermes_layer.png" );
		return textureLocation.toString();
	}

	/** Returns model used to render armor. */
	/*@OnlyIn( Dist.CLIENT )
	@Nullable
	@Override
	public < ArmorModel extends BipedModel< ? > > ArmorModel getArmorModel( LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot,
		ArmorModel defaultModel
	) {
		HermesArmorModel model = new HermesArmorModel();

		model.bipedHead.showModel = armorSlot == EquipmentSlot.HEAD;
		model.bipedHeadwear.showModel = false;
		model.bipedBody.showModel = armorSlot == EquipmentSlot.CHEST;
		model.bipedLeftArm.showModel = false;
		model.bipedRightArm.showModel = false;
		model.bipedRightLeg.showModel = armorSlot == EquipmentSlot.FEET;
		model.bipedLeftLeg.showModel = armorSlot == EquipmentSlot.FEET;

		model.isChild = defaultModel.isChild;
		model.isSitting = defaultModel.isSitting;
		model.isSneak = defaultModel.isSneak;
		model.rightArmPose = defaultModel.rightArmPose;
		model.leftArmPose = defaultModel.leftArmPose;

		return ( ArmorModel )model;
	}*/

	/** Called whenever attributes should be updated. */
	@Override
	protected void updateAttributes( ImmutableMultimap.Builder< Attribute, AttributeModifier > builder ) {
		builder.put( Attributes.MOVEMENT_SPEED,
			new AttributeModifier( UUID.fromString( "dbe472cb-df52-44ab-ab38-01b00e24f649" ), "HermesBootsMovementSpeedBonus",
				this.movementSpeedBonus.get(), AttributeModifier.Operation.MULTIPLY_BASE
			)
		);
	}
}
