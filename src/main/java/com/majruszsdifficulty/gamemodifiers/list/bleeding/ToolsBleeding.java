package com.majruszsdifficulty.gamemodifiers.list.bleeding;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingTooltip;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.data.JsonListener;
import com.mlib.data.SerializableStructure;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.items.ItemHelper;
import com.mlib.text.RegexString;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@AutoInstance
public class ToolsBleeding {
	final BooleanConfig availability;
	final JsonListener.Holder< ToolsDef > toolsDef;

	public ToolsBleeding() {
		this.availability = Condition.DefaultConfigs.excludable( true );
		this.toolsDef = JsonListener.add( "custom", Registries.getLocation( "bleeding_tools" ), ToolsDef.class, ToolsDef::new )
			.syncWithClients( Registries.HELPER );

		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.BLEEDING )
			.name( "Tools" )
			.comment( "Various items may inflict bleeding (configurable via data pack)." );

		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( Condition.excludable( this.availability ) )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( OnBleedingCheck.Data::isDirect ) )
			.addCondition( ToolsBleeding.tryItemChance( this.toolsDef ) )
			.insertTo( group );

		OnBleedingTooltip.listen( this::addTooltip )
			.addCondition( Condition.predicate( data->this.availability.isEnabled() ) )
			.addCondition( Condition.predicate( data->this.toolsDef.get().find( data.itemStack ).isPresent() ) )
			.insertTo( group );
	}

	private void addTooltip( OnBleedingTooltip.Data data ) {
		data.addItem( this.toolsDef.get().find( data.itemStack ).orElseThrow().getChance( data.itemStack ) );
	}

	public static class ToolsDef extends SerializableStructure {
		public List< ToolDef > toolDefs = new ArrayList<>();

		public ToolsDef() {
			this.define( null, ()->this.toolDefs, x->this.toolDefs = x, ToolDef::new );
		}

		public Optional< ToolDef > find( ItemStack itemStack ) {
			return this.toolDefs.stream()
				.filter( toolDef->toolDef.matches( itemStack ) )
				.findFirst();
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void onClient( NetworkEvent.Context context ) {
			Registries.HELPER.findInstance( ToolsBleeding.class ).ifPresent( instance->instance.toolsDef.onSync( this ) );
		}
	}

	public static class ToolDef extends SerializableStructure {
		public RegexString id = new RegexString();
		public float chance;
		public List< EnchantmentDef > enchantmentDefs = new ArrayList<>();

		public ToolDef() {
			this.define( "id", this.id::get, this.id::set );
			this.define( "chance", ()->this.chance, x->this.chance = x );
			this.define( "enchantments", ()->this.enchantmentDefs, x->this.enchantmentDefs = x, EnchantmentDef::new );
		}

		public float getChance( ItemStack itemStack ) {
			return this.chance + ItemHelper.getEnchantmentsInfo( itemStack ).stream().map( this::getExtraChance ).reduce( 0.0f, Float::sum );
		}

		public boolean matches( ItemStack itemStack ) {
			return this.id.matches( Utility.getRegistryString( itemStack.getItem() ) );
		}

		private float getExtraChance( ItemHelper.EnchantmentInfo enchantmentInfo ) {
			for( EnchantmentDef enchantmentDef : this.enchantmentDefs ) {
				if( enchantmentDef.id.matches( enchantmentInfo.id ) ) {
					return enchantmentInfo.level * enchantmentDef.chance;
				}
			}

			return 0.0f;
		}
	}

	public static class EnchantmentDef extends SerializableStructure {
		public RegexString id = new RegexString();
		public float chance;

		public EnchantmentDef() {
			this.define( "id", this.id::get, this.id::set );
			this.define( "extra_chance", ()->this.chance, x->this.chance = x );
		}
	}

	private static Condition< OnBleedingCheck.Data > tryItemChance( Supplier< ToolsDef > toolsDef ) {
		return Condition.predicate( data->{
			if( data.attacker == null ) {
				return false;
			}

			ItemStack itemStack = data.attacker.getMainHandItem();
			Optional< ToolDef > toolDef = toolsDef.get().find( itemStack );
			return toolDef.isPresent() && Random.tryChance( toolDef.get().getChance( itemStack ) );
		} );
	}
}
