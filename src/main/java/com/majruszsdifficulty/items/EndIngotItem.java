package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** New strong end-game ingot. */
public class EndIngotItem extends Item {
	public EndIngotItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}
}
