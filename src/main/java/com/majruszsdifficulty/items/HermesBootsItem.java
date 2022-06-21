package com.majruszsdifficulty.items;

import com.google.common.collect.ImmutableMultimap;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.Registries;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.events.ConfigsLoadedEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.UUID;

/** Boots that increases movement speed. */
@Mod.EventBusSubscriber
public class HermesBootsItem extends ArmorItem {
	protected final DoubleConfig movementSpeedBonus;
	protected final ConfigGroup configGroup;

	public HermesBootsItem() {
		super( CustomArmorMaterial.HERMES, EquipmentSlot.FEET, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );

		this.movementSpeedBonus = new DoubleConfig( "movement_speed_bonus", "Extra movement speed multiplier. (requires game/world restart!)", true, 0.25, 0.0, 5.0 );
		this.configGroup = MajruszsDifficulty.GAME_MODIFIERS_GROUP.addGroup( new ConfigGroup( "HermesBoots", "Hermes Boots item configuration.", this.movementSpeedBonus ) );
	}

	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlot slot, String type ) {
		return Registries.getLocationString( "textures/models/armor/hermes_layer.png" );
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
	@SubscribeEvent
	public static void applySpeedModifier( ConfigsLoadedEvent event ) {
		if( event.configHandler != MajruszsDifficulty.CONFIG_HANDLER )
			return;

		HermesBootsItem item = Registries.HERMES_BOOTS.get();
		ImmutableMultimap.Builder< Attribute, AttributeModifier > builder = ImmutableMultimap.builder();
		item.defaultModifiers = builder.putAll( item.defaultModifiers )
			.put( Attributes.MOVEMENT_SPEED, new AttributeModifier( UUID.fromString( "dbe472cb-df52-44ab-ab38-01b00e24f649" ), "HermesBootsMovementSpeedBonus", item.movementSpeedBonus.get(), AttributeModifier.Operation.MULTIPLY_BASE ) )
			.build();
	}
}
