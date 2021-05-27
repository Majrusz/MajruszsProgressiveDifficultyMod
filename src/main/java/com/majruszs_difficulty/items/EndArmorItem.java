package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New late game armor. */
public class EndArmorItem extends ArmorItem {
	private static final String ARMOR_TICK_TAG = "EndArmorTickCounter";
	public EndArmorItem( EquipmentSlotType slot ) {
		super( CustomArmorMaterial.END, slot, ( new Item.Properties() ).group( Instances.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).isImmuneToFire() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
	}

	/** Returns path to End Armor texture. */
	@Nullable
	@Override
	public String getArmorTexture( ItemStack stack, Entity entity, EquipmentSlotType slot, String type ) {
		CompoundNBT data = entity.getPersistentData();
		data.putInt( ARMOR_TICK_TAG, ( data.getInt( ARMOR_TICK_TAG )+1 )%( 80*4 ) );
		String register = "textures/models/armor/end_layer_";
		register += ( slot == EquipmentSlotType.LEGS ? "2" : "1" ) + "_";
		register += ( "" + ( 1 + data.getInt( ARMOR_TICK_TAG )/80 ) ) + ".png";

		ResourceLocation textureLocation = MajruszsDifficulty.getLocation( register );
		return textureLocation.toString();
	}
}
