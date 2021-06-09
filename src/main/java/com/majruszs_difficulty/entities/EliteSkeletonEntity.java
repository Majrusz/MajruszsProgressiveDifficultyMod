package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/** Entity that is more powerful version of Skeleton. */
public class EliteSkeletonEntity extends SkeletonEntity {
	public static final Potion[] arrowPotions = new Potion[]{ Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS };
	public static final EntityType< EliteSkeletonEntity > type;
	private static final float ARROW_VELOCITY = 2.0f, ARROW_INACCURACY = 0.0f;

	static {
		type = EntityType.Builder.create( EliteSkeletonEntity::new, EntityClassification.MONSTER )
			.size( 0.6f, 2.0f )
			.build( MajruszsDifficulty.getLocation( "elite_skeleton" )
				.toString() );
	}

	private int quickShotsLeft = 0;

	public EliteSkeletonEntity( EntityType< ? extends SkeletonEntity > type, World world ) {
		super( type, world );
		this.aiArrowAttack = new RangedBowAttackGoal<>( this, 5.0 / 6.0, getAttackCooldown(), 20.0f );
	}

	/** Overriding basic Skeleton attack method with changes to inaccuracy, velocity and ammo this entity uses. */
	@Override
	public void attackEntityWithRangedAttack( LivingEntity target, float distanceFactor ) {
		EntitiesConfig.EliteSkeletonConfig config = Instances.ENTITIES_CONFIG.eliteSkeleton;
		double clampedRegionalDifficulty = GameState.getRegionalDifficulty( this );

		handleQuickShot( config.quickShotChance.get() * clampedRegionalDifficulty );
		handleExtraArrows( config.multiShotChance.get() * clampedRegionalDifficulty, target, distanceFactor );

		spawnArrow( 0.0, target, distanceFactor );
		playSound( SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / ( MajruszLibrary.RANDOM.nextFloat() * 0.4f + 0.8f ) );
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( Attributes.MAX_HEALTH, 20.0 )
			.createMutableAttribute( Attributes.MOVEMENT_SPEED, 0.3 )
			.createMutableAttribute( Attributes.ATTACK_DAMAGE, 2.5 )
			.create();
	}

	/** Spawns the arrow directed to target position. */
	protected void spawnArrow( double yFactor, LivingEntity target, float distanceFactor ) {
		ItemStack heldItemStack = getHeldItemMainhand();
		AbstractArrowEntity arrowEntity = getArrowEntity( distanceFactor );
		if( heldItemStack.getItem() instanceof BowItem )
			arrowEntity = ( ( BowItem )heldItemStack.getItem() ).customArrow( arrowEntity );

		double d0 = target.getPosX() - this.getPosX();
		double d1 = target.getPosYHeight( 1.0 / 5.0 ) - arrowEntity.getPosY();
		double d2 = target.getPosZ() - this.getPosZ();
		double d3 = MathHelper.sqrt( d0 * d0 + d2 * d2 );

		arrowEntity.shoot( d0, d1 + d3 * 0.2 + yFactor, d2, ARROW_VELOCITY, ARROW_INACCURACY );
		this.world.addEntity( arrowEntity );
	}

	/**
	 Generating ammunition the skeleton will use. Skeleton have a chance to use tipped arrow instead of standard one.

	 @param distanceFactor Distance between Skeleton and its target.
	 */
	protected AbstractArrowEntity getArrowEntity( float distanceFactor ) {
		ItemStack ammunition = findAmmo( getHeldItem( ProjectileHelper.getHandWith( this, Items.BOW ) ) );

		double tippedArrowChance = Instances.ENTITIES_CONFIG.eliteSkeleton.tippedArrowChance.get();
		if( this.world instanceof ServerWorld )
			tippedArrowChance *= GameState.getRegionalDifficulty( this );

		if( Random.tryChance( tippedArrowChance ) && ammunition.getItem() instanceof ArrowItem ) {
			ammunition = new ItemStack( Items.TIPPED_ARROW );
			PotionUtils.addPotionToItemStack( ammunition, arrowPotions[ MajruszLibrary.RANDOM.nextInt( arrowPotions.length ) ] );
		}

		return fireArrow( ammunition, distanceFactor );
	}

	/** Returns base cooldown between Elite Skeleton's attacks. */
	protected int getAttackCooldown() {
		return this.world.getDifficulty() != Difficulty.HARD ? 40 : 20;
	}

	/** Handles the Skeleton's shooting without any cooldown. (only the time it takes to draw the bowstring) */
	protected void handleQuickShot( double quickShotChance ) {
		if( this.quickShotsLeft == 0 && Random.tryChance( quickShotChance ) && this.world instanceof ServerWorld ) {
			ServerWorld serverWorld = ( ServerWorld )this.world;
			serverWorld.spawnParticle( ParticleTypes.ANGRY_VILLAGER, getPosX(), getPosYHeight( 0.5 ), getPosZ(), 6, 0.625, 0.25, 0.625, 0.125 );
			this.quickShotsLeft = 3;
			this.aiArrowAttack.setAttackCooldown( 1 );
		} else if( this.quickShotsLeft > 0 && --this.quickShotsLeft == 0 ) {
			this.aiArrowAttack.setAttackCooldown( getAttackCooldown() );
		}
	}

	/** Handles the Skeleton's shooting with 3 arrows instead of 1. */
	protected void handleExtraArrows( double multiShotChance, LivingEntity target, float distanceFactor ) {
		if( this.quickShotsLeft == 0 && Random.tryChance( multiShotChance ) ) {
			spawnArrow( 0.75, target, distanceFactor );
			spawnArrow( -0.75, target, distanceFactor );
		}
	}
}
