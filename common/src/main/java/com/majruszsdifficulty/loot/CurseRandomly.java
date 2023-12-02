package com.majruszsdifficulty.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.majruszlibrary.registry.Registries;
import com.majruszsdifficulty.MajruszsDifficulty;
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

import java.util.List;
import java.util.stream.StreamSupport;

public class CurseRandomly extends LootItemConditionalFunction {
	public static LootItemFunctionType create() {
		return new LootItemFunctionType( new LootItemConditionalFunction.Serializer<>() {
			@Override
			public CurseRandomly deserialize( JsonObject jsonObject, JsonDeserializationContext context, LootItemCondition[] itemConditions ) {
				return new CurseRandomly( itemConditions );
			}
		} );
	}

	public CurseRandomly( LootItemCondition[] itemConditions ) {
		super( itemConditions );
	}

	@Override
	public LootItemFunctionType getType() {
		return MajruszsDifficulty.CURSE_RANDOMLY_LOOT_FUNCTION.get();
	}

	@Override
	public ItemStack run( ItemStack itemStack, LootContext context ) {
		List< Enchantment > curses = this.generateCurses( itemStack );
		if( !curses.isEmpty() ) {
			itemStack = CurseRandomly.enchantItem( itemStack, context.getRandom(), curses );
		}
		List< Enchantment > enchantments = this.generateEnchantments( itemStack );
		if( !enchantments.isEmpty() ) {
			itemStack = CurseRandomly.enchantItem( itemStack, context.getRandom(), enchantments );
		}

		return itemStack;
	}

	private List< Enchantment > generateCurses( ItemStack itemStack ) {
		return StreamSupport.stream( Registries.ENCHANTMENTS.spliterator(), false )
			.filter( Enchantment::isDiscoverable )
			.filter( Enchantment::isCurse )
			.filter( enchantment->enchantment.canEnchant( itemStack ) )
			.toList();
	}

	private List< Enchantment > generateEnchantments( ItemStack itemStack ) {
		return StreamSupport.stream( Registries.ENCHANTMENTS.spliterator(), false )
			.filter( Enchantment::isDiscoverable )
			.filter( enchantment->!enchantment.isCurse() )
			.filter( enchantment->enchantment.canEnchant( itemStack ) )
			.toList();
	}

	private static ItemStack enchantItem( ItemStack itemStack, RandomSource randomSource, List< Enchantment > enchantments ) {
		Enchantment enchantment = enchantments.get( randomSource.nextInt( enchantments.size() ) );
		int level = Mth.nextInt( randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel() );
		if( itemStack.is( Items.BOOK ) ) {
			itemStack = new ItemStack( Items.ENCHANTED_BOOK );
			EnchantedBookItem.addEnchantment( itemStack, new EnchantmentInstance( enchantment, level ) );
		} else {
			itemStack.enchant( enchantment, level );
		}

		return itemStack;
	}
}