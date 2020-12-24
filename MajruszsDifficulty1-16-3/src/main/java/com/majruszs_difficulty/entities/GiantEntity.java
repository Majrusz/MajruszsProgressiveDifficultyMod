package com.majruszs_difficulty.entities;

import com.majruszs_difficulty.AttributeHelper;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.goals.GiantAttackGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class GiantEntity extends ZombieEntity {
	public static final float scale = 5.0f;
	public static final EntityType< GiantEntity > type;

	static {
		type = EntityType.Builder.create( GiantEntity::new, EntityClassification.MONSTER )
			.size( 1.0f * scale, 2.0f * scale )
			.build( new ResourceLocation( MajruszsDifficulty.MOD_ID, "giant" ).toString() );
	}

	public GiantEntity( EntityType< ? extends net.minecraft.entity.monster.ZombieEntity > type, World world ) {
		super( type, world );
		this.experienceValue = 15;
	}

	public static AttributeModifierMap.MutableAttribute setAttributes() {
		return MobEntity.func_233666_p_()
			.func_233815_a_( AttributeHelper.Attributes.MAX_HEALTH, 120.0D )
			.func_233815_a_( AttributeHelper.Attributes.MOVEMENT_SPEED, 0.25D )
			.func_233815_a_( AttributeHelper.Attributes.ATTACK_DAMAGE, 10.0D )
			.func_233815_a_( AttributeHelper.Attributes.FOLLOW_RANGE, 40.0D )
			.func_233815_a_( AttributeHelper.Attributes.ATTACK_KNOCKBACK, 2.0D )
			.func_233815_a_( AttributeHelper.Attributes.KNOCKBACK_RESISTANCE, 0.5D )
			.func_233815_a_( AttributeHelper.Attributes.ZOMBIE_REINFORCEMENT_CHANCE, 0.0D );
	}

	@Override
	protected void applyAttributeBonuses( float difficulty ) {}

	@Override
	public boolean attackEntityFrom( DamageSource source, float amount ) {
		return super.attackEntityFrom( source, amount );
	}

	@Override
	protected float getStandingEyeHeight( Pose poseIn, EntitySize sizeIn ) {
		return 10.440001F;
	}

	public static EntityType< GiantEntity > buildEntity() {
		return type;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal( 8, new LookAtGoal( this, PlayerEntity.class, 8.0F ) );
		this.goalSelector.addGoal( 8, new LookRandomlyGoal( this ) );
		this.applyEntityAI();
	}

	protected void applyEntityAI() {
		this.goalSelector.addGoal( 2, new GiantAttackGoal( this, 1.0D, false ) );
		this.goalSelector.addGoal( 7, new WaterAvoidingRandomWalkingGoal( this, 1.0D ) );
		this.targetSelector.addGoal( 1, ( new HurtByTargetGoal( this ) ).setCallsForHelp( ZombifiedPiglinEntity.class ) );
		this.targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, PlayerEntity.class, true ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, AbstractVillagerEntity.class, false ) );
		this.targetSelector.addGoal( 3, new NearestAttackableTargetGoal<>( this, IronGolemEntity.class, true ) );
		this.targetSelector.addGoal( 5,
			new NearestAttackableTargetGoal<>( this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY )
		);
	}

	@Override
	protected int getExperiencePoints( PlayerEntity player ) {
		this.experienceValue += MajruszsDifficulty.RANDOM.nextInt( 15 );

		return super.getExperiencePoints( player );
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( !this.isSilent() )
			this.world.playSound( null, this.getPosX(), this.getPosY(), this.getPosZ(), sound, this.getSoundCategory(), volume * 1.25f, pitch * 0.75f );
	}
}
