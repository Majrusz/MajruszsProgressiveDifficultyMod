package com.majruszs_difficulty.loot_modifiers;

import com.google.gson.JsonObject;
import com.majruszs_difficulty.events.treasure_bag.TreasureBagManager;
import com.majruszs_difficulty.items.TreasureBagItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
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

public class AddTreasureBagsToLoot extends LootModifier {
	public AddTreasureBagsToLoot( ILootCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		Entity entity = context.get( LootParameters.THIS_ENTITY );
		DamageSource damageSource = context.get( LootParameters.DAMAGE_SOURCE );

		if( entity == null || damageSource == null )
			return generatedLoot;

		TreasureBagItem treasureBag = TreasureBagManager.getTreasureBag( entity.getType() );
		if( treasureBag == null || !( entity instanceof LivingEntity ) || !treasureBag.isAvailable() )
			return generatedLoot;

		if( TreasureBagManager.rewardAllPlayers( ( LivingEntity )entity ) )
			if( TreasureBagManager.shouldReplaceLoot( entity.getType() ) )
				generatedLoot.clear();

		return generatedLoot;
	}

	public static class Serializer extends GlobalLootModifierSerializer< AddTreasureBagsToLoot > {
		@Override
		public AddTreasureBagsToLoot read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
			return new AddTreasureBagsToLoot( conditions );
		}

		@Override
		public JsonObject write( AddTreasureBagsToLoot instance ) {
			return null;
		}
	}
}