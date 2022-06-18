package com.majruszsdifficulty.loot_modifiers;

import com.google.gson.JsonObject;
import com.majruszsdifficulty.features.treasure_bag.TreasureBagManager;
import com.majruszsdifficulty.items.TreasureBagItem;
import com.mlib.loot_modifiers.LootHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class AddTreasureBagsToLoot extends LootModifier {
	public AddTreasureBagsToLoot( LootItemCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public ObjectArrayList< ItemStack > doApply( ObjectArrayList< ItemStack > generatedLoot, LootContext context ) {
		Entity entity = LootHelper.getParameter( context, ( LootContextParams.THIS_ENTITY ) );
		DamageSource damageSource = LootHelper.getParameter( context, ( LootContextParams.DAMAGE_SOURCE ) );

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
		public AddTreasureBagsToLoot read( ResourceLocation name, JsonObject object, LootItemCondition[] conditions ) {
			return new AddTreasureBagsToLoot( conditions );
		}

		@Override
		public JsonObject write( AddTreasureBagsToLoot instance ) {
			return null;
		}
	}
}