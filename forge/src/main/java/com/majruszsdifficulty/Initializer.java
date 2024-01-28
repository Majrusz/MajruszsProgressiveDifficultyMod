package com.majruszsdifficulty;

import com.majruszsdifficulty.items.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod( MajruszsDifficulty.MOD_ID )
public class Initializer {
	public Initializer() {
		MajruszsDifficulty.HELPER.register();
		MinecraftForge.EVENT_BUS.register( this );

		FMLJavaModLoadingContext.get().getModEventBus().addListener( Initializer::registerTabs );
	}

	private static void registerTabs( CreativeModeTabEvent.Register event ) {
		event.registerCreativeModeTab( MajruszsDifficulty.HELPER.getLocation( "primary" ), builder->{
			builder.title( CreativeModeTabs.PRIMARY )
				.icon( ()->new ItemStack( MajruszsDifficulty.UNDEAD_BATTLE_STANDARD_ITEM.get() ) )
				.displayItems( ( features, entries )->CreativeModeTabs.definePrimaryItems( entries::accept ) );
		} );
	}
}
