package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.mlib.TimeConverter;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** New late game leggings. */
@Mod.EventBusSubscriber
public class EndLeggingsItem extends EndArmorItem {
	private static final String ARMOR_TAG = "EndLeggingsArmorTag";
	private static final String ARMOR_BONUS_TAG = "EndLeggingsArmorBonus";
	private static final String ARMOR_COUNTER_TAG = "EndLeggingsCounter";
	protected static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "4290dea8-ef26-40bc-8f7f-28912d31329a", "EndLeggingsArmorBonus",
		Attributes.ARMOR, AttributeModifier.Operation.ADDITION
	);
	protected final ConfigGroup configGroup;
	protected final DoubleConfig armorBonus;

	public EndLeggingsItem() {
		super( EquipmentSlot.LEGS );

		this.configGroup = new ConfigGroup( "EndLeggings", "Config for End Leggings ability." );
		FEATURES_GROUP.addGroup( this.configGroup );

		String comment = "Armor bonus for every enchantment level.";
		this.armorBonus = new DoubleConfig( "armor_bonus", comment, false, 0.2, 0.0, 10.0 );
		this.configGroup.addConfig( this.armorBonus );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		super.appendHoverText( itemStack, world, tooltip, flag );

		CompoundTag tag = itemStack.getOrCreateTagElement( ARMOR_TAG );
		double bonus = tag.getDouble( ARMOR_BONUS_TAG );

		if( bonus > 0.0 )
			tooltip.add( getArmorBonusTooltip( bonus ) );
	}

	@SubscribeEvent
	public static void applyArmorBonus( LivingEquipmentChangeEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		double armorBonus = calculateEnchantmentBonus( entity );
		ItemStack leggingsItemStack = entity.getItemBySlot( EquipmentSlot.LEGS );
		boolean hasEndLeggings = leggingsItemStack.getItem() instanceof EndLeggingsItem;

		updateLeggingsTag( entity.getItemBySlot( EquipmentSlot.LEGS ), armorBonus );
		ARMOR_ATTRIBUTE.setValueAndApply( entity, hasEndLeggings ? armorBonus : 0.0 );
	}

	@SubscribeEvent
	public static void updateArmorBonusOnAllItems( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		if( !( entity.level instanceof ServerLevel ) || !( entity instanceof Player ) )
			return;

		CompoundTag data = entity.getPersistentData();
		int counter = ( data.getInt( ARMOR_COUNTER_TAG ) + 1 ) % TimeConverter.secondsToTicks( 1.0 );
		data.putInt( ARMOR_COUNTER_TAG, counter );

		if( counter == 0 )
			for( ItemStack itemStack : ( ( Player )entity ).getInventory().items )
				updateLeggingsTag( itemStack, 0.0 );
	}

	/** Calculates current armor bonus depending on enchantment levels. */
	private static double calculateEnchantmentBonus( LivingEntity entity ) {
		double armorBonus = Instances.END_LEGGINGS_ITEM.armorBonus.get();
		int enchantmentLevelSum = 0;
		ItemStack leggings = entity.getItemBySlot( EquipmentSlot.LEGS );
		if( leggings.getItem() instanceof EndLeggingsItem ) {
			for( ItemStack armorPiece : entity.getArmorSlots() ) {
				if( armorPiece.isEmpty() )
					continue;

				Map< Enchantment, Integer > enchantments = EnchantmentHelper.getEnchantments( armorPiece );
				for( Map.Entry< Enchantment, Integer > enchantment : enchantments.entrySet() )
					enchantmentLevelSum += enchantment.getValue();
			}
		}

		return enchantmentLevelSum * armorBonus;
	}

	/** Updates armor bonus value stored in leggings data. */
	private static void updateLeggingsTag( ItemStack leggings, double armorBonus ) {
		if( leggings.isEmpty() || !( leggings.getItem() instanceof EndLeggingsItem ) )
			return;

		CompoundTag tag = leggings.getOrCreateTagElement( ARMOR_TAG );
		tag.putDouble( ARMOR_BONUS_TAG, armorBonus );
		leggings.addTagElement( ARMOR_TAG, tag );
	}

	/** Returns text with current armor bonus. */
	@OnlyIn( Dist.CLIENT )
	private MutableComponent getArmorBonusTooltip( double armorBonus ) {
		MutableComponent text = new TextComponent( "+" ).append( armorBonus + " " );
		return text.append( new TranslatableComponent( "item.majruszs_difficulty.end_leggings.item_tooltip" ) )
			.withStyle( ChatFormatting.GRAY );
	}
}
