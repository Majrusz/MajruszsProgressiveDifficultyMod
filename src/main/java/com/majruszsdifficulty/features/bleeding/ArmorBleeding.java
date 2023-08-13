package com.majruszsdifficulty.features.bleeding;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.OnBleedingCheck;
import com.majruszsdifficulty.contexts.OnBleedingTooltip;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.data.JsonListener;
import com.mlib.data.SerializableList;
import com.mlib.data.SerializableStructure;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.base.Priority;
import com.mlib.text.RegexString;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@AutoInstance
public class ArmorBleeding {
	final BooleanConfig availability;
	final JsonListener.Holder< ArmorsDef > armorsDef;

	public ArmorBleeding() {
		this.availability = Condition.DefaultConfigs.excludable( true );
		this.armorsDef = JsonListener.add( "custom", Registries.getLocation( "bleeding_armor" ), ArmorsDef.class, ArmorsDef::new )
			.syncWithClients( Registries.HELPER );

		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.BLEEDING )
			.name( "Armor" )
			.comment( "Reduces Bleeding chance for each armor piece equipped (configurable via data pack)." );

		OnBleedingCheck.listen( OnBleedingCheck.Data::cancel )
			.priority( Priority.LOWEST )
			.addCondition( Condition.excludable( this.availability ) )
			.addCondition( Condition.predicate( OnBleedingCheck.Data::isEffectTriggered ) )
			.addCondition( ArmorBleeding.tryItemChance( this.armorsDef ) )
			.insertTo( group );

		OnBleedingTooltip.listen( this::addTooltip )
			.addCondition( Condition.predicate( data->this.availability.isEnabled() ) )
			.addCondition( Condition.predicate( data->this.armorsDef.get().find( data.itemStack ).isPresent() ) )
			.insertTo( group );
	}

	private void addTooltip( OnBleedingTooltip.Data data ) {
		data.addArmor( LivingEntity.getEquipmentSlotForItem( data.itemStack ), this.armorsDef.get().find( data.itemStack ).orElseThrow().chanceMultiplier );
	}

	public static class ArmorsDef extends SerializableList {
		public List< ArmorDef > armorDefs = new ArrayList<>();

		public ArmorsDef() {
			this.defineCustom( ()->this.armorDefs, x->this.armorDefs = x, ArmorDef::new );
		}

		public Optional< ArmorDef > find( ItemStack itemStack ) {
			return this.armorDefs.stream()
				.filter( armorDef->armorDef.matches( itemStack ) )
				.findFirst();
		}

		@Override
		@OnlyIn( Dist.CLIENT )
		public void onClient( NetworkEvent.Context context ) {
			Registries.HELPER.findInstance( ArmorBleeding.class ).ifPresent( instance->instance.armorsDef.onSync( this ) );
		}
	}

	public static class ArmorDef extends SerializableStructure {
		public RegexString id = new RegexString();
		public float chanceMultiplier;

		public ArmorDef() {
			this.defineString( "id", this.id::get, this.id::set );
			this.defineFloat( "chance_multiplier", ()->this.chanceMultiplier, x->this.chanceMultiplier = x );
		}

		public boolean matches( ItemStack itemStack ) {
			return this.id.matches( Utility.getRegistryString( itemStack.getItem() ) );
		}
	}

	private static Condition< OnBleedingCheck.Data > tryItemChance( Supplier< ArmorsDef > toolsDef ) {
		return Condition.predicate( data->{
			double chanceMultiplier = 1.0;
			for( EquipmentSlot slot : EquipmentSlots.ARMOR ) {
				Optional< ArmorDef > armorDef = toolsDef.get().find( data.target.getItemBySlot( slot ) );
				if( armorDef.isPresent() ) {
					chanceMultiplier *= armorDef.get().chanceMultiplier;
				}
			}

			return Random.tryChance( 1.0 - chanceMultiplier );
		} );
	}
}
