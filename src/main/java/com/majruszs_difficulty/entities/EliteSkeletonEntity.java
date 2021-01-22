package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.MajruszLibrary;
import com.mlib.WorldHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

/** Entity that is more powerful version of Skeleton. */
public class EliteSkeletonEntity extends SkeletonEntity {
	public static final Potion[] arrowPotions = new Potion[]{ Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS };
	public static final EntityType< EliteSkeletonEntity > type;

	static {
		type = EntityType.Builder.create( EliteSkeletonEntity::new, EntityClassification.MONSTER )
			.size( 0.6f, 2.0f )
			.build( MajruszsDifficulty.getLocation( "elite_skeleton" )
				.toString() );
	}

	public final RangedBowAttackGoal< AbstractSkeletonEntity > rangedAttackGoal;

	public EliteSkeletonEntity( EntityType< ? extends SkeletonEntity > type, World world ) {
		super( type, world );
		this.rangedAttackGoal = new RangedBowAttackGoal<>( this, 5.0 / 6.0, 15, 20.0f );
		overwriteRangedAttackGoal();
	}

	/** Overriding basic Skeleton attack method with changes to inaccuracy, velocity and ammo this entity uses. */
	@Override
	public void attackEntityWithRangedAttack( LivingEntity target, float distanceFactor ) {
		ItemStack heldItemStack = getHeldItemMainhand();
		AbstractArrowEntity arrowEntity = getArrowEntity( distanceFactor );

		if( heldItemStack.getItem() instanceof BowItem )
			arrowEntity = ( ( BowItem )heldItemStack.getItem() ).customArrow( arrowEntity );

		double d0 = target.getPosX() - this.getPosX();
		double d1 = target.getPosYHeight( 1.0 / 3.0 ) - arrowEntity.getPosY();
		double d2 = target.getPosZ() - this.getPosZ();
		double d3 = MathHelper.sqrt( d0 * d0 + d2 * d2 );

		arrowEntity.shoot( d0, d1 + d3 * 0.2, d2, 2.0f, 0 ); // here is the change to velocity and inaccuracy
		playSound( SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / ( MajruszLibrary.RANDOM.nextFloat() * 0.4f + 0.8f ) );
		this.world.addEntity( arrowEntity );
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 20.0 )
			.createMutableAttribute( Attributes.MOVEMENT_SPEED, 0.3 )
			.createMutableAttribute( Attributes.ATTACK_DAMAGE, 2.5 )
			.create();
	}

	/**
	 Generating ammunition the skeleton will use. Skeleton have a chance to use tipped arrow instead of standard one.

	 @param distanceFactor Distance between Skeleton and its target.
	 */
	protected AbstractArrowEntity getArrowEntity( float distanceFactor ) {
		ItemStack ammunition = findAmmo( getHeldItem( ProjectileHelper.getHandWith( this, Items.BOW ) ) );

		double finalChance = Instances.ENTITIES_CONFIG.eliteSkeleton.tippedArrowChance.get();
		if( isServerWorld() )
			finalChance *= WorldHelper.getClampedRegionalDifficulty( this );

		if( finalChance >= MajruszLibrary.RANDOM.nextDouble() && ammunition.getItem() instanceof ArrowItem ) {
			ammunition = new ItemStack( Items.TIPPED_ARROW );
			PotionUtils.addPotionToItemStack( ammunition, arrowPotions[ MajruszLibrary.RANDOM.nextInt( arrowPotions.length ) ] );
		}

		return fireArrow( ammunition, distanceFactor );
	}

	/** Overwrites basic ranged attack goal with new one which gives all the bonuses to Skeleton. */
	protected void overwriteRangedAttackGoal() {
		ItemStack itemstack = getHeldItem( ProjectileHelper.getHandWith( this, Items.BOW ) );

		if( itemstack.getItem() instanceof BowItem ) {
			int attackCooldown = ( this.world.getDifficulty() != Difficulty.HARD ) ? 30 : 15;

			this.rangedAttackGoal.setAttackCooldown( attackCooldown );
			this.goalSelector.addGoal( 4, this.rangedAttackGoal );
		}
	}
}
