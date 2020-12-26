package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EliteSkeletonEntity extends SkeletonEntity {
	public static double tippedArrowChance = 1;
	public static final String[] effects = new String[] { "poison", "weakness", "slowness" };
	public static final EntityType< EliteSkeletonEntity > type;
	static {
		type = EntityType.Builder.create( EliteSkeletonEntity::new, EntityClassification.MONSTER )
			.size( 0.6f, 2.0f )
			.build( new ResourceLocation( MajruszsDifficulty.MOD_ID, "elite_skeleton" ).toString() );
	}

	public EliteSkeletonEntity( EntityType< ? extends SkeletonEntity > type, World world ) {
		super( type, world );
	}

	@Override
	public void attackEntityWithRangedAttack( LivingEntity target, float distanceFactor ) {
		ItemStack ammunition = getAmmunition();
		ItemStack heldItemStack = getHeldItemMainhand();
		AbstractArrowEntity arrowEntity = fireArrow( ammunition, distanceFactor );

		if( heldItemStack.getItem() instanceof BowItem )
			arrowEntity = ( ( BowItem )heldItemStack.getItem() ).customArrow( arrowEntity );

		double d0 = target.getPosX() - this.getPosX();
		double d1 = target.getPosYHeight( 0.3333333333333333D ) - arrowEntity.getPosY();
		double d2 = target.getPosZ() - this.getPosZ();
		double d3 = MathHelper.sqrt( d0 * d0 + d2 * d2 );

		arrowEntity.shoot( d0, d1 + d3 * ( double )0.2f, d2, 2.0f, 0 );
		this.playSound( SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / ( MajruszsDifficulty.RANDOM.nextFloat() * 0.4F + 0.8F ) );
		this.world.addEntity( arrowEntity );
	}

	protected ItemStack getAmmunition() {
		ItemStack ammunition = this.findAmmo( this.getHeldItem( ProjectileHelper.getHandWith( this, Items.BOW ) ) );

		if( tippedArrowChance >= MajruszsDifficulty.RANDOM.nextDouble() && ammunition.getItem() instanceof ArrowItem ) {
			ammunition = new ItemStack( Items.TIPPED_ARROW );

			CompoundNBT tag = ammunition.getOrCreateTag();
			tag.putString( "Potion", "minecraft:" + effects[ MajruszsDifficulty.RANDOM.nextInt( effects.length ) ] );
			ammunition.setTag( tag );
		}

		return ammunition;
	}

	public static AttributeModifierMap getAttributeMap() {
		return MobEntity.func_233666_p_()
			.func_233815_a_( AttributeHelper.Attributes.MAX_HEALTH, 20.0D )
			.func_233815_a_( AttributeHelper.Attributes.MOVEMENT_SPEED, 0.25D )
			.func_233815_a_( AttributeHelper.Attributes.ATTACK_DAMAGE, 2.5D ).func_233813_a_();
	}
}
