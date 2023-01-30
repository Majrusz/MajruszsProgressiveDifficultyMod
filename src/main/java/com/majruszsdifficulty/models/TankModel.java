package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.TankEntity;
import com.mlib.animations.Animation;
import com.mlib.animations.Frame;
import com.mlib.animations.InterpolationType;
import com.mlib.math.VectorHelper;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.math.Vector3f;

@OnlyIn( Dist.CLIENT )
public class TankModel< Type extends TankEntity > extends HierarchicalModel< Type > {
	static final Animation< Float > SPECIAL_ATTACK_BODY_X = new Animation<>( TankEntity.SPECIAL_ATTACK_DURATION );
	static final Animation< Float > SPECIAL_ATTACK_ARMS_X = new Animation<>( TankEntity.SPECIAL_ATTACK_DURATION );
	static final Animation< Float > NORMAL_ATTACK_BODY_Y = new Animation<>( TankEntity.NORMAL_ATTACK_DURATION );
	static final Animation< Vector3f > NORMAL_ATTACK_ARM = new Animation<>( TankEntity.NORMAL_ATTACK_DURATION );

	static {
		SPECIAL_ATTACK_BODY_X.add( 0.00f, new Frame.Degrees( 10.0f ) )
			.add( 0.30f, new Frame.Degrees( -30.0f, InterpolationType.SQUARE ) )
			.add( 0.40f, new Frame.Degrees( -30.0f ) )
			.add( 0.60f, new Frame.Degrees( 45.0f, InterpolationType.SQUARE ) )
			.add( 0.70f, new Frame.Degrees( 35.0f ) )
			.add( 0.85f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 10.0f ) );

		SPECIAL_ATTACK_ARMS_X.add( 0.00f, new Frame.Degrees( 0.0f ) )
			.add( 0.40f, new Frame.Degrees( -160.0f, InterpolationType.SQUARE ) )
			.add( 0.60f, new Frame.Degrees( -40.0f, InterpolationType.SQUARE ) )
			.add( 0.70f, new Frame.Degrees( -30.0f ) )
			.add( 0.85f, new Frame.Degrees( 10.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) );

		NORMAL_ATTACK_BODY_Y.add( 0.00f, new Frame.Degrees( 0.0f ) )
			.add( 0.15f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.25f, new Frame.Degrees( 30.0f ) )
			.add( 0.45f, new Frame.Degrees( -60.0f, InterpolationType.SQUARE ) )
			.add( 0.55f, new Frame.Degrees( -50.0f ) )
			.add( 1.00f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) );

		NORMAL_ATTACK_ARM.add( 0.00f, new Frame.Vector( 0.0f, 5.0f, 12.5f ) )
			.add( 0.20f, new Frame.Vector( 45.0f, 0.0f, 45.0f, InterpolationType.SQUARE ) )
			.add( 0.30f, new Frame.Vector( 45.0f, 0.0f, 45.0f ) )
			.add( 0.50f, new Frame.Vector( -90.0f, -30.0f, 60.0f, InterpolationType.SQUARE ) )
			.add( 0.60f, new Frame.Vector( -80.0f, -25.0f, 50.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Vector( 0.0f, 5.0f, 12.5f, InterpolationType.SQUARE ) );
	}

	public ModelPart root, body, head, arms, leftArm, leftForearm, rightArm, rightForearm, leftLeg, rightLeg;
	protected float normalAttackDurationRatioLeft = 0.0f;
	protected float specialAttackDurationRatioLeft = 0.0f;
	protected boolean isLeftHandAttack = false;

	public TankModel( ModelPart modelPart ) {
		this.root = modelPart;
		this.body = this.root.getChild( "body" );
		this.head = this.body.getChild( "head" );
		this.arms = this.body.getChild( "arms" );
		this.leftArm = this.arms.getChild( "leftArm" );
		this.leftForearm = this.leftArm.getChild( "leftForearm" );
		this.rightArm = this.arms.getChild( "rightArm" );
		this.rightForearm = this.rightArm.getChild( "rightForearm" );
		this.leftLeg = this.root.getChild( "leftLeg" );
		this.rightLeg = this.root.getChild( "rightLeg" );
	}

	@Override
	public void setupAnim( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
		float headPitch
	) {
		float limbFactor1 = limbSwing * 0.3333f, limbFactor2 = 0.9f * limbSwingAmount, bodyFactor = 0.2f * limbSwingAmount;

		// head rotation when looking around
		this.head.yRot = netHeadYaw * ( ( float )Math.PI / 180f );
		this.head.xRot = headPitch * ( ( float )Math.PI / 180f ) + 0.0873F;

		// leg and body rotation while moving
		this.leftLeg.xRot = Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightLeg.xRot = Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.body.zRot = Mth.cos( limbFactor1 ) * bodyFactor;

		// body animations when the Tank is using standard attack
		float handMultiplier = this.isLeftHandAttack ? -1.0f : 1.0f;
		this.body.yRot = this.head.yRot * 0.4f + handMultiplier * NORMAL_ATTACK_BODY_Y.apply( this.normalAttackDurationRatioLeft, ageInTicks );

		// arms animations when the Tank is using standard attack (we need to update both hands because model parts share reference between all instances)
		float extraLeftArmRotationX = ( float )( Math.cos( limbFactor1 ) * limbSwingAmount ) * 20.0f;
		float extraRightArmRotationX = ( float )( Math.cos( limbFactor1 + ( float )Math.PI ) * limbSwingAmount ) * 20.0f;
		if( this.isLeftHandAttack ) {
			rotateArm( this.leftArm, ageInTicks, this.normalAttackDurationRatioLeft, extraLeftArmRotationX );
			rotateArm( this.rightArm, ageInTicks, 1.0f, extraRightArmRotationX );
		} else {
			rotateArm( this.leftArm, ageInTicks, 1.0f, extraLeftArmRotationX );
			rotateArm( this.rightArm, ageInTicks, this.normalAttackDurationRatioLeft, extraRightArmRotationX );
		}

		// body and arms animations when Tank is using special attack
		this.body.xRot = SPECIAL_ATTACK_BODY_X.apply( this.specialAttackDurationRatioLeft, ageInTicks );
		this.arms.xRot = SPECIAL_ATTACK_ARMS_X.apply( this.specialAttackDurationRatioLeft, ageInTicks );
	}

	@Override
	public void prepareMobModel( Type tank, float p_102862_, float p_102863_, float packedLight ) {
		this.normalAttackDurationRatioLeft = tank.isAttacking( TankEntity.AttackType.NORMAL ) ? tank.calculateAttackDurationRatioLeft() : 1.0f;
		this.specialAttackDurationRatioLeft = tank.isAttacking( TankEntity.AttackType.SPECIAL ) ? tank.calculateAttackDurationRatioLeft() : 1.0f;
		this.isLeftHandAttack = tank.isLeftHandAttack;

		super.prepareMobModel( tank, p_102862_, p_102863_, packedLight );
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	public static LayerDefinition createBodyLayer( CubeDeformation cubeDeformation ) {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		PartDefinition body = partDefinition.addOrReplaceChild( "body", CubeListBuilder.create()
			.texOffs( 0, 20 )
			.addBox( -8.0F, -22.0F, -5.0F, 16.0F, 12.0F, 8.0F, new CubeDeformation( 0.0F ) )
			.texOffs( 36, 40 )
			.addBox( -1.0F, -23.0F, 1.0F, 2.0F, 22.0F, 2.0F, new CubeDeformation( -0.1F ) )
			.texOffs( 0, 40 )
			.addBox( -6.0F, -4.0F, -3.0F, 12.0F, 4.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 12.0F, 0.0F, 0.1745F, 0.0F, 0.0F ) );

		PartDefinition head = body.addOrReplaceChild( "head", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -22.0F, -1.0F, 0.0873F, 0.0F, 0.0F ) );

		PartDefinition arms = body.addOrReplaceChild( "arms", CubeListBuilder.create(), PartPose.offset( 0.0F, -22.0F, 0.0F ) );

		PartDefinition rightArm = arms.addOrReplaceChild( "rightArm", CubeListBuilder.create()
			.texOffs( 40, 0 )
			.addBox( -2.5F, 0.0F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation( 0.25F ) ), PartPose.offsetAndRotation( -8.5F, 0.0F, -0.5F, 0.0436F, 0.0873F, 0.2182F ) );

		PartDefinition rightForearm = rightArm.addOrReplaceChild( "rightForearm", CubeListBuilder.create()
			.texOffs( 44, 37 )
			.addBox( -2.5F, -0.5F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 15.5F, 0.0F, -0.8727F, 0.0F, 0.0F ) );

		PartDefinition leftArm = arms.addOrReplaceChild( "leftArm", CubeListBuilder.create()
			.texOffs( 40, 0 )
			.addBox( -2.5F, 0.0F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation( 0.25F ) ), PartPose.offsetAndRotation( 8.5F, 0.0F, -0.5F, 0.0F, -0.0873F, -0.2182F ) );

		PartDefinition leftForearm = leftArm.addOrReplaceChild( "leftForearm", CubeListBuilder.create()
			.texOffs( 44, 37 )
			.addBox( -2.5F, -2.0F, -3.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 17.0F, -1.0F, -0.8727F, 0.0F, 0.0F ) );

		PartDefinition leftLeg = partDefinition.addOrReplaceChild( "leftLeg", CubeListBuilder.create()
			.texOffs( 48, 21 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 3.0F, 12.0F, 0.0F ) );

		PartDefinition rightLeg = partDefinition.addOrReplaceChild( "rightLeg", CubeListBuilder.create()
			.texOffs( 48, 21 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( -3.0F, 12.0F, 0.0F ) );

		return LayerDefinition.create( meshDefinition, 64, 64 );
	}

	private void rotateArm( ModelPart arm, float ageInTicks, float duration, float extraRotationX ) {
		float multiplier = arm == this.leftArm ? -1.0f : 1.0f;
		Vector3f vectorHandMultiplier = new Vector3f( 1.0f, multiplier, multiplier );
		Vector3f rotation = VectorHelper.add( NORMAL_ATTACK_ARM.apply( duration, ageInTicks ), new Vector3f( extraRotationX, 0.0f, 0.0f ) );
		Animation.applyRotationInDegrees( VectorHelper.multiply( rotation, vectorHandMultiplier ), arm );
	}
}
