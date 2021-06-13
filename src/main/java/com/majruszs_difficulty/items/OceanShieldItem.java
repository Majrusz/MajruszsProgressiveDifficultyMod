package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.renderers.OceanShieldRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.ShieldItem;

/** Shield that reflects part of the damage back to the attacker. */
public class OceanShieldItem extends ShieldItem {
	public OceanShieldItem() {
		super( ( new Properties() ).maxDamage( 499 )
			.rarity( Rarity.UNCOMMON )
			.group( Instances.ITEM_GROUP )
			.setISTER( ()->OceanShieldRenderer::new ) );
	}

	@Override
	public boolean getIsRepairable( ItemStack toRepair, ItemStack repair ) {
		return Items.PRISMARINE_CRYSTALS.equals( repair.getItem() ) || super.getIsRepairable( toRepair, repair );
	}

	@Override
	public boolean isShield( ItemStack itemStack, LivingEntity entity ) {
		return true;
	}

	/*@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, Instances.END_SHARD_ORE.isEnabled() );
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, HasteOnDestroyingBlocks.getTooltipTranslationKey() );
	}*/
}
