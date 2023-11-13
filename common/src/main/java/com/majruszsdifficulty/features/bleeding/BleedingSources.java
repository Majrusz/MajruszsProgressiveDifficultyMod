package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.contexts.OnBleedingCheck;
import com.majruszsdifficulty.contexts.OnBleedingTooltip;
import com.majruszsdifficulty.data.Config;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.base.Condition;
import com.mlib.data.Serializables;
import com.mlib.item.EnchantmentHelper;
import com.mlib.math.Random;
import com.mlib.math.Range;
import com.mlib.registry.Registries;
import com.mlib.text.RegexString;
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
	@AutoInstance
	public static class Arrows {
		private boolean isEnabled = true;
		private float chance = 0.333f;

		public Arrows() {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( Condition.chance( this.chance ) )
				.addCondition( data->this.isEnabled )
				.addCondition( data->data.source.getDirectEntity() instanceof Arrow );

			OnBleedingTooltip.listen( this::addTooltip )
				.addCondition( data->this.isEnabled )
				.addCondition( data->data.itemStack.getItem() instanceof ProjectileWeaponItem );

			Serializables.get( Config.Bleeding.Sources.class )
				.define( "arrows", subconfig->{
					subconfig.defineBoolean( "is_enabled", s->this.isEnabled, ( s, v )->this.isEnabled = v );
					subconfig.defineFloat( "chance", s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
				} );
		}

		private void addTooltip( OnBleedingTooltip data ) {
			data.addItem( this.chance );
		}
	}

	@AutoInstance
	public static class Bite {
		private boolean isEnabled = true;
		private float chance = 0.5f;
		private List< EntityType< ? > > blacklistedAnimals = List.of( EntityType.LLAMA );

		public Bite() {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( Condition.chance( this.chance ) )
				.addCondition( data->this.isEnabled )
				.addCondition( data->!data.source.isIndirect() )
				.addCondition( data->data.attacker instanceof Animal || data.attacker instanceof Zombie || data.attacker instanceof Spider )
				.addCondition( data->!this.blacklistedAnimals.contains( data.attacker.getType() ) );

			Serializables.get( Config.Bleeding.Sources.class )
				.define( "bite", subconfig->{
					subconfig.defineBoolean( "is_enabled", s->this.isEnabled, ( s, v )->this.isEnabled = v );
					subconfig.defineFloat( "chance", s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
					subconfig.defineEntityTypeList( "blacklisted_animals", s->this.blacklistedAnimals, ( s, v )->this.blacklistedAnimals = v );
				} );
		}
	}

	@AutoInstance
	public static class Cactus {
		private boolean isEnabled = true;
		private float chance = 0.5f;

		public Cactus() {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( Condition.chance( this.chance ) )
				.addCondition( data->this.isEnabled )
				.addCondition( data->data.source.is( DamageTypes.CACTUS ) );

			Serializables.get( Config.Bleeding.Sources.class )
				.define( "cactus", subconfig->{
					subconfig.defineBoolean( "is_enabled", s->this.isEnabled, ( s, v )->this.isEnabled = v );
					subconfig.defineFloat( "chance", s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
				} );
		}
	}

	@AutoInstance
	public static class Tools {
		private boolean isEnabled = true;
		private List< ToolDef > toolDefs = List.of(
			new ToolDef( "minecraft:trident", 0.25f ),
			new ToolDef( "{regex}.*_sword", 0.25f, List.of(
				new EnchantmentDef( "minecraft:sharpness", 0.015f ),
				new EnchantmentDef( "{regex}(minecraft:smite)|(minecraft:bane_of_arthropods)|(majruszsenchantments:misanthropy)", 0.01f )
			) ),
			new ToolDef( "{regex}.*_axe", 0.225f, List.of(
				new EnchantmentDef( "minecraft:sharpness", 0.015f ),
				new EnchantmentDef( "{regex}(minecraft:smite)|(minecraft:bane_of_arthropods)|(majruszsenchantments:misanthropy)", 0.01f )
			) ),
			new ToolDef( "{regex}(.*_pickaxe)|minecraft:shears", 0.2f ),
			new ToolDef( "{regex}.*_shovel", 0.175f ),
			new ToolDef( "{regex}.*_hoe", 0.15f )
		);

		public Tools() {
			OnBleedingCheck.listen( OnBleedingCheck::trigger )
				.addCondition( data->this.isEnabled )
				.addCondition( this::toolBasedChance );

			OnBleedingTooltip.listen( this::addTooltip )
				.addCondition( data->this.isEnabled );

			Serializables.get( Config.Bleeding.Sources.class )
				.define( "tools", subconfig->{
					subconfig.defineBoolean( "is_enabled", s->this.isEnabled, ( s, v )->this.isEnabled = v );
					subconfig.defineCustomList( "list", s->this.toolDefs, ( s, v )->this.toolDefs = v, ToolDef::new );
				} );
		}

		private void addTooltip( OnBleedingTooltip data ) {
			this.find( data.itemStack ).ifPresent( def->data.addItem( def.getChance( data.itemStack ) ) );
		}

		private Optional< ToolDef > find( ItemStack itemStack ) {
			return this.toolDefs.stream()
				.filter( toolDef->toolDef.matches( itemStack ) )
				.findFirst();
		}

		private boolean toolBasedChance( OnBleedingCheck data ) {
			if( data.attacker == null ) {
				return false;
			}

			ItemStack itemStack = data.attacker.getMainHandItem();
			return this.find( itemStack )
				.map( def->Random.check( def.getChance( itemStack ) ) )
				.orElse( false );
		}

		public static class ToolDef {
			public RegexString id;
			public float chance;
			public List< EnchantmentDef > enchantmentDefs;

			static {
				Serializables.get( ToolDef.class )
					.defineString( "id", s->s.id.get(), ( s, v )->s.id.set( v ) )
					.defineFloat( "chance", s->s.chance, ( s, v )->s.chance = Range.CHANCE.clamp( v ) )
					.defineCustomList( "enchantments", s->s.enchantmentDefs, ( s, v )->s.enchantmentDefs = v, EnchantmentDef::new );
			}

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

			static {
				Serializables.get( EnchantmentDef.class )
					.defineString( "id", s->s.id.get(), ( s, v )->s.id.set( v ) )
					.defineFloat( "extra_chance_per_level", s->s.chance, ( s, v )->s.chance = Range.CHANCE.clamp( v ) );
			}

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
