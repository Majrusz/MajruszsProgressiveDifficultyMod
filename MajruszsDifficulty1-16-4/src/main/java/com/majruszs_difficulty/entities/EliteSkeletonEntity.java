package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EliteSkeletonEntity extends SkeletonEntity {
	public static final Potion[] arrowPotions = new Potion[]{ Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS };
	public static final EntityType< EliteSkeletonEntity > type;
	public final RangedBowAttackGoal< AbstractSkeletonEntity > rangedAttackGoal;

	static {
		type = EntityType.Builder.create( EliteSkeletonEntity::new, EntityClassification.MONSTER )
			.size( 0.6f, 2.0f )
			.build( new ResourceLocation( MajruszsDifficulty.MOD_ID, "elite_skeleton" ).toString() );
	}

	public EliteSkeletonEntity( EntityType< ? extends SkeletonEntity > type, World world ) {
		super( type, world );
		this.rangedAttackGoal = new RangedBowAttackGoal<>( this, 5.0/6.0, 15, 20.0f );
		overwriteRangedAttackGoal();
	}

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

		arrowEntity.shoot( d0, d1 + d3 * 0.2, d2, 2.0f, 0 );
		playSound( SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / ( MajruszsDifficulty.RANDOM.nextFloat() * 0.4f + 0.8f ) );
		this.world.addEntity( arrowEntity );
	}

	protected AbstractArrowEntity getArrowEntity( float distanceFactor ) {
		ItemStack ammunition = findAmmo( getHeldItem( ProjectileHelper.getHandWith( this, Items.BOW ) ) );

		double finalChance = Config.getChance( Config.Chances.ELITE_TIPPED_ARROW );
		if( isServerWorld() )
			finalChance *= MajruszsHelper.getClampedRegionalDifficulty( this, ( ServerWorld )this.world );

		if( finalChance >= MajruszsDifficulty.RANDOM.nextDouble() && ammunition.getItem() instanceof ArrowItem ) {
			ammunition = new ItemStack( Items.TIPPED_ARROW );
			PotionUtils.addPotionToItemStack( ammunition, arrowPotions[ MajruszsDifficulty.RANDOM.nextInt( arrowPotions.length ) ] );
		}

		return fireArrow( ammunition, distanceFactor );
	}

	protected void overwriteRangedAttackGoal() {
		ItemStack itemstack = getHeldItem( ProjectileHelper.getHandWith( this, Items.BOW ) );

		if( itemstack.getItem() instanceof BowItem ) {
			int attackCooldown = ( this.world.getDifficulty() != Difficulty.HARD ) ? 30 : 15;

			this.rangedAttackGoal.setAttackCooldown( attackCooldown );
			this.goalSelector.addGoal( 4, this.rangedAttackGoal );
		}
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.createMutableAttribute( AttributeHelper.Attributes.MAX_HEALTH, 20.0 )
			.createMutableAttribute( AttributeHelper.Attributes.MOVEMENT_SPEED, 0.3 )
			.createMutableAttribute( AttributeHelper.Attributes.ATTACK_DAMAGE, 2.5 )
			.create();
	}
}
