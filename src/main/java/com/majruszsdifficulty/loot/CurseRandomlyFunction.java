package com.majruszsdifficulty.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurseRandomlyFunction extends LootItemConditionalFunction {
	final List< ResourceLocation > excludedEnchantments;

	public static LootItemFunctionType newType() {
		return new LootItemFunctionType( new CurseRandomlyFunction.Serializer() );
	}

	public CurseRandomlyFunction( LootItemCondition[] itemConditions, List< ResourceLocation > excludedEnchantments ) {
		super( itemConditions );
		this.excludedEnchantments = excludedEnchantments;
	}

	@Override
	public LootItemFunctionType getType() {
		return Registries.CURSE_RANDOMLY.get();
	}

	@Override
	public ItemStack run( ItemStack itemStack, LootContext context ) {
		java.util.Random randomSource = context.getRandom();
		List< Enchantment > randomEnchantments = new ArrayList<>();
		List< Enchantment > enchantments = this.buildValidEnchantmentList( itemStack );
		if( !enchantments.isEmpty() ) {
			randomEnchantments.add( enchantments.get( randomSource.nextInt( enchantments.size() ) ) );
		}
		List< Enchantment > curses = this.buildValidCurseList( itemStack );
		if( !curses.isEmpty() ) {
			randomEnchantments.add( curses.get( randomSource.nextInt( curses.size() ) ) );
		}

		return !randomEnchantments.isEmpty() ? enchantItem( itemStack, randomSource, randomEnchantments ) : itemStack;
	}

	private List< Enchantment > buildValidEnchantmentList( ItemStack itemStack ) {
		return ForgeRegistries.ENCHANTMENTS.getEntries()
			.stream()
			.filter( enchantment->!this.excludedEnchantments.contains( enchantment.getKey().location() ) )
			.map( Map.Entry::getValue )
			.filter( Enchantment::isDiscoverable )
			.filter( enchantment->!enchantment.isCurse() )
			.filter( enchantment->enchantment.canEnchant( itemStack ) )
			.toList();
	}

	private List< Enchantment > buildValidCurseList( ItemStack itemStack ) {
		return ForgeRegistries.ENCHANTMENTS.getEntries()
			.stream()
			.filter( enchantment->!this.excludedEnchantments.contains( enchantment.getKey().location() ) )
			.map( Map.Entry::getValue )
			.filter( Enchantment::isDiscoverable )
			.filter( Enchantment::isCurse )
			.filter( enchantment->enchantment.canEnchant( itemStack ) )
			.toList();
	}

	private static ItemStack enchantItem( ItemStack itemStack, java.util.Random randomSource,
		List< Enchantment > enchantments
	) {
		if( itemStack.is( Items.BOOK ) ) {
			itemStack = new ItemStack( Items.ENCHANTED_BOOK );
		}

		for( Enchantment enchantment : enchantments ) {
			int level = Mth.nextInt( randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel() );
			if( itemStack.is( Items.ENCHANTED_BOOK ) ) {
				EnchantedBookItem.addEnchantment( itemStack, new EnchantmentInstance( enchantment, level ) );
			} else {
				itemStack.enchant( enchantment, level );
			}
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