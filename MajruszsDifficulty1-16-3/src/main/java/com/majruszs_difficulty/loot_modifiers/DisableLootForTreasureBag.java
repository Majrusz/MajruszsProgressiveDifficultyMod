package com.majruszs_difficulty.loot_modifiers;

import com.google.gson.JsonObject;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class DisableLootForTreasureBag extends LootModifier {
	public DisableLootForTreasureBag( ILootCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		Entity entity = context.get( LootParameters.THIS_ENTITY );
		DamageSource damageSource = context.get( LootParameters.DAMAGE_SOURCE );

		if( entity == null || damageSource == null )
			return generatedLoot;

		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer< DisableLootForTreasureBag > {
		@Override
		public DisableLootForTreasureBag read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
			return new DisableLootForTreasureBag( conditions );
		}

		@Override
		public JsonObject write( DisableLootForTreasureBag instance ) {
			return null;
		}
	}
}