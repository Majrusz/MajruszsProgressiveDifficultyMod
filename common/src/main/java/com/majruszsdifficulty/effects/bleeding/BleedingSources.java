package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.registry.Registries;
import com.majruszlibrary.text.RegexString;
import com.majruszsdifficulty.effects.Bleeding;
import com.majruszsdifficulty.events.OnBleedingCheck;
import com.majruszsdifficulty.events.OnBleedingTooltip;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.List;
import java.util.Optional;

public class BleedingSources {
	public static class Arrows {
		private static boolean IS_ENABLED = true;
		private static float CHANCE = 0.333f;

		static {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( Condition.chance( ()->CHANCE ) )
				.addCondition( data->IS_ENABLED )
				.addCondition( data->data.source.getDirectEntity() instanceof Arrow );

			OnBleedingTooltip.listen( Arrows::addTooltip )
				.addCondition( data->IS_ENABLED )
				.addCondition( data->data.itemStack.getItem() instanceof ProjectileWeaponItem );

			Serializables.getStatic( Bleeding.Config.Sources.class )
				.define( "arrows", Arrows.class );

			Serializables.getStatic( Arrows.class )
				.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
				.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) );
		}

		private static void addTooltip( OnBleedingTooltip data ) {
			data.addItem( CHANCE );
		}
	}

	public static class Bite {
		private static boolean IS_ENABLED = true;
		private static float CHANCE = 0.5f;
		private static List< EntityType< ? > > BLACKLISTED_ANIMALS = List.of( EntityType.LLAMA );

		static {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( Condition.chance( ()->CHANCE ) )
				.addCondition( data->IS_ENABLED )
				.addCondition( data->!data.source.isIndirect() )
				.addCondition( data->data.attacker instanceof Animal || data.attacker instanceof Zombie || data.attacker instanceof Spider )
				.addCondition( data->!BLACKLISTED_ANIMALS.contains( data.attacker.getType() ) );

			Serializables.getStatic( Bleeding.Config.Sources.class )
				.define( "bite", Bite.class );

			Serializables.getStatic( Bite.class )
				.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
				.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) )
				.define( "blacklisted_animals", Reader.list( Reader.entityType() ), ()->BLACKLISTED_ANIMALS, v->BLACKLISTED_ANIMALS = v );
		}
	}

	public static class Cactus {
		private static boolean IS_ENABLED = true;
		private static float CHANCE = 0.5f;

		static {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( Condition.chance( ()->CHANCE ) )
				.addCondition( data->IS_ENABLED )
				.addCondition( data->data.source.is( DamageTypes.CACTUS ) );

			Serializables.getStatic( Bleeding.Config.Sources.class )
				.define( "cactus", Cactus.class );

			Serializables.getStatic( Cactus.class )
				.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
				.define( "chance", Reader.number(), ()->CHANCE, v->CHANCE = Range.CHANCE.clamp( v ) );
		}
	}

	public static class Tools {
		private static boolean IS_ENABLED = true;
		private static List< ToolDef > TOOL_DEFS = List.of(
			new ToolDef( "minecraft:trident", 0.3f ),
			new ToolDef( "{regex}.*_sword", 0.3f, List.of(
				new EnchantmentDef( "minecraft:sharpness", 0.02f ),
				new EnchantmentDef( "{regex}(minecraft:smite)|(minecraft:bane_of_arthropods)|(majruszsenchantments:misanthropy)", 0.01f )
			) ),
			new ToolDef( "{regex}.*_axe", 0.26f, List.of(
				new EnchantmentDef( "minecraft:sharpness", 0.02f ),
				new EnchantmentDef( "{regex}(minecraft:smite)|(minecraft:bane_of_arthropods)|(majruszsenchantments:misanthropy)", 0.01f )
			) ),
			new ToolDef( "{regex}(.*_pickaxe)|minecraft:shears", 0.22f ),
			new ToolDef( "{regex}.*_shovel", 0.18f ),
			new ToolDef( "{regex}.*_hoe", 0.14f )
		);

		static {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( data->IS_ENABLED )
				.addCondition( Tools::toolBasedChance );

			OnBleedingTooltip.listen( Tools::addTooltip )
				.addCondition( data->IS_ENABLED );

			Serializables.getStatic( Bleeding.Config.Sources.class )
				.define( "tools", Tools.class );

			Serializables.getStatic( Tools.class )
				.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
				.define( "list", Reader.list( Reader.custom( ToolDef::new ) ), ()->TOOL_DEFS, v->TOOL_DEFS = v );

			Serializables.get( ToolDef.class )
				.define( "id", Reader.string(), s->s.id.get(), ( s, v )->s.id.set( v ) )
				.define( "chance", Reader.number(), s->s.chance, ( s, v )->s.chance = Range.CHANCE.clamp( v ) )
				.define( "enchantments", Reader.list( Reader.custom( EnchantmentDef::new ) ), s->s.enchantmentDefs, ( s, v )->s.enchantmentDefs = v );

			Serializables.get( EnchantmentDef.class )
				.define( "id", Reader.string(), s->s.id.get(), ( s, v )->s.id.set( v ) )
				.define( "extra_chance_per_level", Reader.number(), s->s.chance, ( s, v )->s.chance = Range.CHANCE.clamp( v ) );
		}

		private static void addTooltip( OnBleedingTooltip data ) {
			Tools.find( data.itemStack ).ifPresent( def->data.addItem( def.getChance( data.itemStack ) ) );
		}

		private static Optional< ToolDef > find( ItemStack itemStack ) {
			return TOOL_DEFS.stream()
				.filter( toolDef->toolDef.matches( itemStack ) )
				.findFirst();
		}

		private static boolean toolBasedChance( OnBleedingCheck data ) {
			if( data.attacker == null ) {
				return false;
			}

			ItemStack itemStack = data.attacker.getMainHandItem();
			return Tools.find( itemStack )
				.map( def->Random.check( def.getChance( itemStack ) ) )
				.orElse( false );
		}

		public static class ToolDef {
			public RegexString id;
			public float chance;
			public List< EnchantmentDef > enchantmentDefs;

			public ToolDef( String id, float chance, List< EnchantmentDef > enchantmentDefs ) {
				this.id = new RegexString( id );
				this.chance = chance;
				this.enchantmentDefs = enchantmentDefs;
			}

			public ToolDef( String id, float chance ) {
				this( id, chance, List.of() );
			}

			public ToolDef() {
				this( "", 0.0f );
			}

			public float getChance( ItemStack itemStack ) {
				return this.chance + EnchantmentHelper.read( itemStack ).enchantments.stream().map( this::getExtraChance ).reduce( 0.0f, Float::sum );
			}

			public boolean matches( ItemStack itemStack ) {
				return this.id.matches( Registries.get( itemStack.getItem() ).toString() );
			}

			private float getExtraChance( EnchantmentHelper.EnchantmentDef itemEnchantment ) {
				for( EnchantmentDef enchantmentDef : this.enchantmentDefs ) {
					if( enchantmentDef.id.matches( itemEnchantment.id.toString() ) ) {
						return itemEnchantment.level * enchantmentDef.chance;
					}
				}

				return 0.0f;
			}
		}

		public static class EnchantmentDef {
			public RegexString id;
			public float chance;

			public EnchantmentDef( String id, float chance ) {
				this.id = new RegexString( id );
				this.chance = chance;
			}

			public EnchantmentDef() {
				this( "", 0.0f );
			}
		}
	}
}
