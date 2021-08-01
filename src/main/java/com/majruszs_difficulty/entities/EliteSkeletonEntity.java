package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

/** Entity that is more powerful version of Skeleton. */
public class EliteSkeletonEntity extends Skeleton {
	public static final Potion[] arrowPotions = new Potion[]{ Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS };
	public static final EntityType< EliteSkeletonEntity > type;
	private static final float ARROW_VELOCITY = 2.0f, ARROW_INACCURACY = 0.0f;

	static {
		type = EntityType.Builder.of( EliteSkeletonEntity::new, MobCategory.MONSTER )
			.sized( 0.6f, 2.0f )
			.build( MajruszsDifficulty.getLocation( "elite_skeleton" )
				.toString() );
	}

	private int quickShotsLeft = 0;

	public EliteSkeletonEntity( EntityType< ? extends Skeleton > type, Level world ) {
		super( type, world );
		this.bowGoal = new RangedBowAttackGoal<>( this, 5.0 / 6.0, getAttackCooldown(), 20.0f );
	}

	/** Overriding basic Skeleton attack method with changes to inaccuracy, velocity and ammo this entity uses. */
	@Override
	public void performRangedAttack( LivingEntity target, float distanceFactor ) {
		EntitiesConfig.EliteSkeletonConfig config = Instances.ENTITIES_CONFIG.eliteSkeleton;
		double clampedRegionalDifficulty = GameState.getRegionalDifficulty( this );

		handleQuickShot( config.quickShotChance.get() * clampedRegionalDifficulty );
		handleExtraArrows( config.multiShotChance.get() * clampedRegionalDifficulty, target, distanceFactor );

		spawnArrow( 0.0, target, distanceFactor );
		playSound( SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / ( MajruszLibrary.RANDOM.nextFloat() * 0.4f + 0.8f ) );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 20.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 2.5 )
			.build();
	}

	/** Spawns the arrow directed to target position. */
	protected void spawnArrow( double yFactor, LivingEntity target, float distanceFactor ) {
		ItemStack heldItemStack = getMainHandItem();
		AbstractArrow arrowEntity = getArrowEntity( distanceFactor );
		if( heldItemStack.getItem() instanceof BowItem )
			arrowEntity = ( ( BowItem )heldItemStack.getItem() ).customArrow( arrowEntity );

		double d0 = target.getX() - this.getX();
		double d1 = target.getY( 1.0 / 5.0 ) - arrowEntity.getY();
		double d2 = target.getZ() - this.getZ();
		double d3 = Mth.sqrt( ( float )( d0 * d0 + d2 * d2 ) );

		arrowEntity.shoot( d0, d1 + d3 * 0.2 + yFactor, d2, ARROW_VELOCITY, ARROW_INACCURACY );
		this.level.addFreshEntity( arrowEntity );
	}

	/**
	 Generating ammunition the skeleton will use. Skeleton have a chance to use tipped arrow instead of standard one.

	 @param distanceFactor Distance between Skeleton and its target.
	 */
	protected AbstractArrow getArrowEntity( float distanceFactor ) {
		ItemStack ammunition = new ItemStack( Items.ARROW );

		double tippedArrowChance = Instances.ENTITIES_CONFIG.eliteSkeleton.tippedArrowChance.get() * GameState.getRegionalDifficulty( this );
		if( Random.tryChance( tippedArrowChance ) && ammunition.getItem() instanceof ArrowItem ) {
			ammunition = new ItemStack( Items.TIPPED_ARROW );
			PotionUtils.setPotion( ammunition, arrowPotions[ MajruszLibrary.RANDOM.nextInt( arrowPotions.length ) ] );
		}

		return ProjectileUtil.getMobArrow( this, ammunition, distanceFactor );
	}

	/** Returns base cooldown between Elite Skeleton's attacks. */
	protected int getAttackCooldown() {
		return this.level.getDifficulty() != Difficulty.HARD ? 40 : 20;
	}

	/** Handles the Skeleton's shooting without any cooldown. (only the time it takes to draw the bowstring) */
	protected void handleQuickShot( double quickShotChance ) {
		if( this.quickShotsLeft == 0 && Random.tryChance( quickShotChance ) && this.level instanceof ServerLevel ) {
			ServerLevel serverLevel = ( ServerLevel )this.level;
			serverLevel.sendParticles( ParticleTypes.ANGRY_VILLAGER, getX(), getY( 0.5 ), getZ(), 6, 0.5, 0.25, 0.5, 0.125 );
			this.quickShotsLeft = 3;
			this.bowGoal.setMinAttackInterval( 1 );
		} else if( this.quickShotsLeft > 0 && --this.quickShotsLeft == 0 ) {
			this.bowGoal.setMinAttackInterval( getAttackCooldown() );
		}
	}

	/** Handles the Skeleton's shooting with 3 arrows instead of 1. */
	protected void handleExtraArrows( double multiShotChance, LivingEntity target, float distanceFactor ) {
		if( this.quickShotsLeft == 0 && Random.tryChance( multiShotChance ) ) {
			spawnArrow( 1.25, target, distanceFactor );
			spawnArrow( -1.25, target, distanceFactor );
		}
	}
}
