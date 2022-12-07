package com.majruszsdifficulty.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class CurseRandomlyFunction extends LootItemConditionalFunction {
	final List< ResourceLocation > excludedCurses;

	public static LootItemFunctionType newType() {
		return new LootItemFunctionType( new CurseRandomlyFunction.Serializer() );
	}

	public CurseRandomlyFunction( LootItemCondition[] itemConditions, List< ResourceLocation > excludedCurses ) {
		super( itemConditions );
		this.excludedCurses = excludedCurses;
	}

	@Override
	public LootItemFunctionType getType() {
		return Registries.CURSE_RANDOMLY.get();
	}

	@Override
	public ItemStack run( ItemStack itemStack, LootContext context ) {
		List< Enchantment > list = Registry.ENCHANTMENT.holders()
			.filter( enchantment->!this.excludedCurses.contains( enchantment.key().location() ) )
			.map( Holder::get )
			.filter( Enchantment::isDiscoverable )
			.filter( Enchantment::isCurse )
			.filter( enchantment->enchantment.canEnchant( itemStack ) )
			.toList();
		if( list.isEmpty() )
			return itemStack;

		RandomSource randomSource = context.getRandom();
		return enchantItem( itemStack, list.get( randomSource.nextInt( list.size() ) ), randomSource );
	}

	private static ItemStack enchantItem( ItemStack itemStack, Enchantment enchantment, RandomSource randomSource ) {
		int level = Mth.nextInt( randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel() );
		if( itemStack.is( Items.BOOK ) ) {
			itemStack = new ItemStack( Items.ENCHANTED_BOOK );
			EnchantedBookItem.addEnchantment( itemStack, new EnchantmentInstance( enchantment, level ) );
		} else {
			itemStack.enchant( enchantment, level );
		}

		return itemStack;
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer< CurseRandomlyFunction > {
		@Override
		public CurseRandomlyFunction deserialize( JsonObject jsonObject, JsonDeserializationContext context,
			LootItemCondition[] itemConditions
		) {
			List< ResourceLocation > excludedEnchantments = new ArrayList<>();
			if( jsonObject.has( "excluding" ) ) {
				jsonObject.get( "excluding" )
					.getAsJsonArray()
					.forEach( enchantment->excludedEnchantments.add( new ResourceLocation( enchantment.getAsString() ) ) );
			}

			return new CurseRandomlyFunction( itemConditions, excludedEnchantments );
		}
	}
}