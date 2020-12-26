package com.majruszs_difficulty;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.RegistryObject;

public class NewSpawnEggs {
	public static void addDispenseBehaviorToAllRegisteredEggs() {
		DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior() {
			public ItemStack dispenseStack( IBlockSource source, ItemStack stack ) {
				Direction direction = source.getBlockState()
					.get( DispenserBlock.FACING );
				EntityType< ? > entityType = ( ( SpawnEggItem )stack.getItem() ).getType( stack.getTag() );
				entityType.spawn( source.getWorld(), stack, null, source.getBlockPos()
					.offset( direction ), SpawnReason.DISPENSER, direction != Direction.UP, false );
				stack.shrink( 1 );
				return stack;
			}
		};

		for( RegistryObject< Item > registry : RegistryHandler.ITEMS.getEntries() )
			if( registry.get() instanceof SpawnEggItem )
				DispenserBlock.registerDispenseBehavior( registry.get(), defaultBehavior );
	}

	public static void registerSpawnEgg( String name, EntityType< ? > entityType, int primaryColor, int secondaryColor ) {
		RegistryHandler.ITEMS.register( name,
			()->new SpawnEggItem( entityType, primaryColor, secondaryColor, new Item.Properties().group( ItemGroup.MISC ) )
		);
	}
}
