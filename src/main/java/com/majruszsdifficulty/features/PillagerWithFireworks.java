package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.contexts.OnSpawned;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import com.mlib.math.Range;
import com.mlib.modhelper.AutoInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

@AutoInstance
public class PillagerWithFireworks {
	final DoubleConfig crossbowMultishotChance = new DoubleConfig( 0.25, Range.CHANCE );
	final DoubleConfig fireworkDropChance = new DoubleConfig( 0.25, Range.CHANCE );
	final IntegerConfig fireworkCount = new IntegerConfig( 8, new Range<>( 1, 64 ) );

	public PillagerWithFireworks() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "PillagerWithFireworks" )
			.comment( "Pillager may spawn with crossbow with multishot and fireworks in offhand." );

		ConfigGroup fireworkGroup = new ConfigGroup()
			.addConfig( this.fireworkDropChance.name( "drop_chance" ).comment( "Chance for fireworks to drop." ) )
			.addConfig( this.fireworkCount.name( "count" ).comment( "Determines count of fireworks for pillagers." ) );

		ConfigGroup crossbowGroup = new ConfigGroup()
			.addConfig( this.crossbowMultishotChance.name( "multishot_chance" ).comment( "Chance for crossbow to have Multishot enchantment." ) );

		OnSpawned.listenSafe( this::giveExtraItems )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.MASTER ) )
			.addCondition( Condition.chanceCRD( 0.15, true ) )
			.addCondition( Condition.isServer() )
			.addCondition( Condition.excludable() )
			.addCondition( OnSpawned.isNotLoadedFromDisk() )
			.addCondition( Condition.predicate( data->data.target.getType().equals( EntityType.PILLAGER ) ) )
			.addConfig( fireworkGroup.name( "Firework" ) )
			.addConfig( crossbowGroup.name( "Crossbow" ) )
			.insertTo( group );
	}

	private void giveExtraItems( OnSpawned.Data data ) {
		ItemStack itemStack = new ItemStack( Items.FIREWORK_ROCKET, this.fireworkCount.get() );
		SerializableHelper.write( FireworksDef::new, itemStack.getOrCreateTag() );

		Pillager pillager = ( Pillager )data.target;
		pillager.setItemSlot( EquipmentSlot.OFFHAND, itemStack );
		pillager.setDropChance( EquipmentSlot.OFFHAND, this.fireworkDropChance.asFloat() );

		if( Random.tryChance( this.crossbowMultishotChance ) && pillager.getMainHandItem().is( Items.CROSSBOW ) ) {
			pillager.getMainHandItem().enchant( Enchantments.MULTISHOT, 1 );
		}
	}

	private static class FireworksDef extends SerializableStructure {
		public List< ExplosionsDef > explosions = List.of( new ExplosionsDef() );

		public FireworksDef() {
			super( "Fireworks" );

			this.defineCustom( "Explosions", ()->this.explosions, x->this.explosions = x, ExplosionsDef::new );
		}

		private static class ExplosionsDef extends SerializableStructure {
			public ExplosionsDef() {
				this.defineInteger( "Type", ()->0, x->{} );
			}

			@Override
			public void write( Tag tag ) {
				super.write( tag );

				// at the moment the library stores integers in different way which is not read by fireworks properly
				( ( CompoundTag )tag ).putIntArray( "Colors", new int[]{ 0x4a2a30, 0x666666 } );
			}
		}
	}
}
