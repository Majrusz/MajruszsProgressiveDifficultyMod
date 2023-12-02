package com.majruszsdifficulty.items;

import net.minecraft.world.item.*;

public class EnderiumTool {
	public static class Axe extends AxeItem {
		public Axe() {
			super( CustomItemTier.ENDERIUM, 6.0f, -3.1f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}

	public static class Hoe extends HoeItem {
		public Hoe() {
			super( CustomItemTier.ENDERIUM, -5, 0.0f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
		}

		// TODO: till 3x3 area
	}

	public static class Pickaxe extends PickaxeItem {
		public Pickaxe() {
			super( CustomItemTier.ENDERIUM, 1, -2.8f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}

	public static class Shovel extends ShovelItem {
		public Shovel() {
			super( CustomItemTier.ENDERIUM, 1.5f, -3.0f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}

	public static class Sword extends SwordItem {
		public Sword() {
			super( CustomItemTier.ENDERIUM, 4, -2.6f, new Properties().rarity( Rarity.UNCOMMON ).fireResistant() );
		}
	}
}
