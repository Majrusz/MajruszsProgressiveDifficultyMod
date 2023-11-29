package com.majruszsdifficulty.recipes;

import com.majruszlibrary.data.Serializables;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.SoulJar;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class SoulJarShieldRecipe extends CustomRecipe {
	public static Supplier< RecipeSerializer< ? > > create() {
		return ()->new SimpleCraftingRecipeSerializer<>( SoulJarShieldRecipe::new );
	}

	public SoulJarShieldRecipe( ResourceLocation id, CraftingBookCategory category ) {
		super( id, category );
	}

	@Override
	public boolean matches( CraftingContainer container, Level level ) {
		RecipeData recipeData = SoulJarShieldRecipe.convert( container );

		return !recipeData.shield.isEmpty() && !recipeData.soulJar.isEmpty();
	}

	@Override
	public ItemStack assemble( CraftingContainer container, RegistryAccess registryAccess ) {
		RecipeData recipeData = SoulJarShieldRecipe.convert( container );
		ItemStack shield = recipeData.shield.copy();
		Serializables.write( SoulJar.BonusInfo.read( recipeData.soulJar ), shield.getOrCreateTag() );

		return shield;
	}

	@Override
	public boolean canCraftInDimensions( int width, int height ) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer< ? > getSerializer() {
		return MajruszsDifficulty.Recipes.SOUL_JAR_SHIELD.get();
	}

	private static RecipeData convert( CraftingContainer container ) {
		ItemStack soulJar = ItemStack.EMPTY;
		ItemStack shield = ItemStack.EMPTY;
		for( int i = 0; i < container.getContainerSize(); ++i ) {
			ItemStack itemStack = container.getItem( i );
			if( itemStack.isEmpty() ) {
				continue;
			}

			if( itemStack.getItem() instanceof SoulJar && soulJar.isEmpty() ) {
				soulJar = itemStack;
			} else if( itemStack.getItem() instanceof ShieldItem && shield.isEmpty() ) {
				shield = itemStack;
			} else {
				return new RecipeData( ItemStack.EMPTY, ItemStack.EMPTY );
			}
		}

		return new RecipeData( soulJar, shield );
	}

	record RecipeData( ItemStack soulJar, ItemStack shield ) {}
}
