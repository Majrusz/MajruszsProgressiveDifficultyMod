package com.majruszsdifficulty.items;

import com.mlib.annotations.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.data.SerializableStructure;
import com.mlib.gamemodifiers.contexts.OnBreakSpeed;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import com.mlib.gamemodifiers.contexts.OnItemEquipped;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SoulJarItem extends Item {
	static final float DAMAGE_BONUS = 2.5f;
	static final float MOVEMENT_BONUS = 0.15f;
	static final float RANGE_BONUS = 0.5f;
	static final int ARMOR_BONUS = 2;
	static final float MINING_BONUS = 0.15f;
	static final int LUCK_BONUS = 1;
	static final AttributeHandler MOVEMENT_ATTRIBUTE = new AttributeHandler( "51e7e4fb-e8b4-4c90-ab8a-e8c334e206be", "SoulJarMovementBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
	static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "7d2d7767-51da-46cc-8081-80fda32d4126", "SoulJarArmorBonus", Attributes.ARMOR, AttributeModifier.Operation.ADDITION );
	static final AttributeHandler REACH_ATTRIBUTE = new AttributeHandler( "23868877-961b-44c9-89c3-376e5c06dbd1", "SoulJarReachBonus", ForgeMod.REACH_DISTANCE.get(), AttributeModifier.Operation.ADDITION );
	static final AttributeHandler RANGE_ATTRIBUTE = new AttributeHandler( "a45d6f34-5b78-4d7c-b60a-03fe6400f8cd", "SoulJarRangeBonus", ForgeMod.ATTACK_RANGE.get(), AttributeModifier.Operation.ADDITION );
	static final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler( "a2a496f4-3799-46eb-856c-1ba992f67912", "SoulJarLuckBonus", Attributes.LUCK, AttributeModifier.Operation.ADDITION );

	public SoulJarItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}

	@AutoInstance
	public static class Handler {
		public Handler() {
			new OnItemEquipped.Context( this::updateAttributes )
				.addCondition( data->data.entity instanceof LivingEntity );

			new OnPreDamaged.Context( this::increaseDamage )
				.addCondition( data->data.target instanceof Mob mob && mob.getMobType() == MobType.UNDEAD )
				.addCondition( data->data.attacker != null )
				.addCondition( data->hasBonus( data.attacker, BonusType.DAMAGE ) );

			new OnBreakSpeed.Context( this::increaseSpeed )
				.addCondition( data->hasBonus( data.player, BonusType.MINING ) );

			new OnItemAttributeTooltip.Context( this::addTooltip )
				.addCondition( data->data.itemStack.getItem() instanceof SoulJarItem );
		}

		private static boolean hasBonus( @Nullable Entity entity, BonusType bonusType ) {
			ItemStack itemStack = entity instanceof LivingEntity livingEntity ? livingEntity.getItemBySlot( EquipmentSlot.OFFHAND ) : ItemStack.EMPTY;
			if( !( itemStack.getItem() instanceof SoulJarItem ) )
				return false;

			return new BonusInfo( itemStack.getOrCreateTag() )
				.getBonusTypes()
				.contains( bonusType );
		}

		private void updateAttributes( OnItemEquipped.Data data ) {
			LivingEntity entity = ( LivingEntity )data.entity;
			float speedBonus = hasBonus( data.entity, BonusType.MOVEMENT ) ? MOVEMENT_BONUS : 0.0f;
			float armorBonus = hasBonus( data.entity, BonusType.ARMOR ) ? ARMOR_BONUS : 0.0f;
			float rangeBonus = hasBonus( data.entity, BonusType.RANGE ) ? RANGE_BONUS : 0.0f;
			float luckBonus = hasBonus( data.entity, BonusType.LUCK ) ? LUCK_BONUS : 0.0f;

			MOVEMENT_ATTRIBUTE.setValueAndApply( entity, speedBonus );
			ARMOR_ATTRIBUTE.setValueAndApply( entity, armorBonus );
			REACH_ATTRIBUTE.setValueAndApply( entity, rangeBonus );
			RANGE_ATTRIBUTE.setValueAndApply( entity, rangeBonus );
			LUCK_ATTRIBUTE.setValueAndApply( entity, luckBonus );
		}

		private void increaseDamage( OnPreDamaged.Data data ) {
			data.extraDamage += DAMAGE_BONUS * 2.5f;
			data.spawnMagicParticles = true;
		}

		private void increaseSpeed( OnBreakSpeed.Data data ) {
			data.event.setNewSpeed( data.event.getNewSpeed() + MINING_BONUS * data.event.getOriginalSpeed() );
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			BonusInfo bonusInfo = new BonusInfo( data.itemStack.getOrCreateTag() );
			for( BonusType bonusType : bonusInfo.getBonusTypes() ) {
				data.add( EquipmentSlot.OFFHAND, bonusType.getComponent( 1.0f ) );
			}

			bonusInfo.write( data.itemStack.getOrCreateTag() );
		}
	}

	public static class BonusInfo extends SerializableStructure {
		public int bonusMask = 0b0;

		public BonusInfo( CompoundTag tag ) {
			super( "SoulJar" );

			this.define( "BonusMask", ()->this.bonusMask, x->this.bonusMask = x );

			this.read( tag );
		}

		public List< BonusType > getBonusTypes() {
			return Arrays.stream( BonusType.values() )
				.filter( bonusType->( bonusType.bit & this.bonusMask ) != 0 )
				.toList();
		}
	}

	public enum BonusType {
		DAMAGE( 1 << 0, "item.majruszsdifficulty.soul_jar.smite", multiplier->TextHelper.signed( DAMAGE_BONUS * multiplier ) ),
		MOVEMENT( 1 << 1, "item.majruszsdifficulty.soul_jar.movement", multiplier->TextHelper.signedPercent( MOVEMENT_BONUS * multiplier ) ),
		RANGE( 1 << 2, "item.majruszsdifficulty.soul_jar.range", multiplier->TextHelper.signed( RANGE_BONUS * multiplier ) ),
		ARMOR( 1 << 3, "item.majruszsdifficulty.soul_jar.armor", multiplier->TextHelper.signed( ( int )( ARMOR_BONUS * multiplier ) ) ),
		MINING( 1 << 4, "item.majruszsdifficulty.soul_jar.mining", multiplier->TextHelper.signedPercent( MINING_BONUS * multiplier ) ),
		LUCK( 1 << 5, "item.majruszsdifficulty.soul_jar.luck", multiplier->TextHelper.signed( ( int )( LUCK_BONUS * multiplier ) ) );

		final int bit;
		final String id;
		final Function< Float, String > valueProvider;

		BonusType( int bit, String id, Function< Float, String > valueProvider ) {
			this.bit = bit;
			this.id = id;
			this.valueProvider = valueProvider;
		}

		Component getComponent( float multiplier ) {
			return Component.translatable( this.id, this.valueProvider.apply( multiplier ) ).withStyle( ChatFormatting.BLUE );
		}
	}
}
