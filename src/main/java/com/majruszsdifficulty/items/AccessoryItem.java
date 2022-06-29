package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.accessories.AccessoryBonus;
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

public class AccessoryItem extends Item {
	final AccessoryBonus[] bonuses;

	public AccessoryItem( Rarity rarity, AccessoryBonus... bonuses ) {
		super( new Properties().stacksTo( 1 ).tab( Registries.ITEM_GROUP ).rarity( rarity ) );
		this.bonuses = bonuses;
	}

	public AccessoryItem( AccessoryBonus... bonuses ) {
		this( Rarity.RARE, bonuses );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		for( AccessoryBonus bonus : this.bonuses ) {
			bonus.addTooltip( itemStack, tooltip );
		}
	}
}
