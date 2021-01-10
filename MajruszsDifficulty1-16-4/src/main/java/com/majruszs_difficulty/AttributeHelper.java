package com.majruszs_difficulty;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.UUID;

/** Handling attributes in more user-friendly way. */
public class AttributeHelper {
	private final UUID uuid;
	private final String name;
	private final Attribute attribute;
	private final AttributeModifier.Operation operation;
	private double value = 1.0D;

	public AttributeHelper( String uuid, String name, Attribute attribute, AttributeModifier.Operation operation ) {
		this.uuid = UUID.fromString( uuid );
		this.name = name;
		this.attribute = attribute;
		this.operation = operation;
	}

	/** Setting current attribute value. */
	public AttributeHelper setValue( double value ) {
		this.value = value;

		return this;
	}

	/**
	 Applying current effect to the target.

	 @param livingEntity Entity to apply effects.
	 */
	public AttributeHelper apply( LivingEntity livingEntity ) {
		ModifiableAttributeInstance attributeInstance = livingEntity.getAttribute( this.attribute );

		if( attributeInstance != null ) {
			attributeInstance.removeModifier( this.uuid );
			AttributeModifier modifier = new AttributeModifier( this.uuid, this.name, this.value, this.operation );
			attributeInstance.applyPersistentModifier( modifier );
		}

		return this;
	}

	// This is done because the attribute names change between versions and are sometimes fields of type 'field_523832968'
	public static class Attributes {
		public static final Attribute MAX_HEALTH = net.minecraft.entity.ai.attributes.Attributes.MAX_HEALTH;
		public static final Attribute ATTACK_DAMAGE = net.minecraft.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
		public static final Attribute ATTACK_KNOCKBACK = net.minecraft.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK;
		public static final Attribute FOLLOW_RANGE = net.minecraft.entity.ai.attributes.Attributes.FOLLOW_RANGE;
		public static final Attribute MOVEMENT_SPEED = net.minecraft.entity.ai.attributes.Attributes.MOVEMENT_SPEED;
		public static final Attribute KNOCKBACK_RESISTANCE = net.minecraft.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE;
		public static final Attribute ARMOR = net.minecraft.entity.ai.attributes.Attributes.ARMOR;
		public static final Attribute ZOMBIE_REINFORCEMENT_CHANCE = net.minecraft.entity.ai.attributes.Attributes.ZOMBIE_SPAWN_REINFORCEMENTS;
	}
}
