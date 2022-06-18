package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.TankEntity;
import com.mlib.animations.Animation;
import com.mlib.animations.Frame;
import com.mlib.animations.FrameDegrees;
import com.mlib.animations.FrameVec3;
import com.mlib.math.VectorHelper;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Model for new Tank enemy. */
@OnlyIn( Dist.CLIENT )
public class TankModel< Type extends TankEntity > extends HierarchicalModel< Type > {
	protected static final Animation< Float > SPECIAL_ATTACK_BODY_X;
	protected static final Animation< Float > SPECIAL_ATTACK_ARMS_X;
	protected static final Animation< Float > NORMAL_ATTACK_BODY_Y;
	protected static final Animation< Vec3 > NORMAL_ATTACK_ARM;

	static {
		SPECIAL_ATTACK_BODY_X = new Animation<>();
		SPECIAL_ATTACK_BODY_X.addFrame( new FrameDegrees( 0.0f, 10.0f ) )
			.addFrame( new FrameDegrees( 0.3f, -30.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 0.4f, -30.0f ) )
			.addFrame( new FrameDegrees( 0.6f, 45.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 0.7f, 35.0f ) )
			.addFrame( new FrameDegrees( 0.85f, 0.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 1.0f, 10.0f ) );

		SPECIAL_ATTACK_ARMS_X = new Animation<>();
		SPECIAL_ATTACK_ARMS_X.addFrame( new FrameDegrees( 0.0f, 0.0f ) )
			.addFrame( new FrameDegrees( 0.4f, -160.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 0.6f, -40.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 0.7f, -30.0f ) )
			.addFrame( new FrameDegrees( 0.85f, 10.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 1.0f, 0.0f, Frame.InterpolationType.SQUARE ) );

		NORMAL_ATTACK_BODY_Y = new Animation<>();
		NORMAL_ATTACK_BODY_Y.addFrame( new FrameDegrees( 0.0f, 0.0f ) )
			.addFrame( new FrameDegrees( 0.15f, 30.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 0.25f, 30.0f ) )
			.addFrame( new FrameDegrees( 0.45f, -60.0f, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameDegrees( 0.55f, -50.0f ) )
			.addFrame( new FrameDegrees( 1.0f, 0.0f, Frame.InterpolationType.SQUARE ) );

		NORMAL_ATTACK_ARM = new Animation<>();
		NORMAL_ATTACK_ARM.addFrame( new FrameVec3( 0.0f, 0.0, 0.0, 10.0 ) )
			.addFrame( new FrameVec3( 0.2f, 45.0, 0.0, 45.0, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameVec3( 0.3f, 45.0, 0.0, 45.0 ) )
			.addFrame( new FrameVec3( 0.5f, -90.0, -30.0, 60.0, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameVec3( 0.6f, -80.0, -25.0, 50.0, Frame.InterpolationType.SQUARE ) )
			.addFrame( new FrameVec3( 1.0f, 0.0, 0.0, 10.0, Frame.InterpolationType.SQUARE ) );
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
	public void setupAnim( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		float handMultiplier = this.isLeftHandAttack ? -1.0f : 1.0f;
		float limbFactor1 = limbSwing * 0.3333f, limbFactor2 = 0.9f * limbSwingAmount, bodyFactor = 0.2f * limbSwingAmount;
		Vec3 vectorHandMultiplier = new Vec3( 1.0f, handMultiplier, handMultiplier );

		// head rotation when looking around
		this.head.yRot = netHeadYaw * ( ( float )Math.PI / 180f );
		this.head.xRot = headPitch * ( ( float )Math.PI / 180f ) + 0.0873F;

		// leg and body rotation while moving
		this.leftLeg.xRot = Mth.cos( limbFactor1 ) * limbFactor2;
		this.rightLeg.xRot = Mth.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.body.zRot = Mth.cos( limbFactor1 ) * bodyFactor;

		// body and arms animations when Tank is using standard attack
		this.body.yRot = this.head.yRot * 0.4f + handMultiplier * NORMAL_ATTACK_BODY_Y.apply( this.normalAttackDurationRatioLeft );
		Vec3 armRotation = VectorHelper.multiply( NORMAL_ATTACK_ARM.apply( this.normalAttackDurationRatioLeft ), vectorHandMultiplier );
		Animation.applyRotationInDegrees( armRotation, this.isLeftHandAttack ? this.leftArm : this.rightArm );

		// body and arms animations when Tank is using special attack
		this.body.xRot = SPECIAL_ATTACK_BODY_X.apply( this.specialAttackDurationRatioLeft );
		this.arms.xRot = SPECIAL_ATTACK_ARMS_X.apply( this.specialAttackDurationRatioLeft );
	}

	@Override
	public void prepareMobModel( Type tank, float p_102862_, float p_102863_, float packedLight ) {
		this.normalAttackDurationRatioLeft = tank.isAttacking( TankEntity.AttackType.NORMAL ) ? tank.getAttackDurationRatioLeft() : 1.0f;
		this.specialAttackDurationRatioLeft = tank.isAttacking( TankEntity.AttackType.SPECIAL ) ? tank.getAttackDurationRatioLeft() : 1.0f;
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

		partDefinition.addOrReplaceChild( "body", CubeListBuilder.create()
				.texOffs( 0, 20 )
				.addBox( -8.0F, -22.0F, -5.0F, 16.0F, 12.0F, 8.0F, cubeDeformation )
				.texOffs( 36, 40 )
				.addBox( -1.0F, -23.0F, 1.0F, 2.0F, 22.0F, 2.0F, cubeDeformation.extend( -0.1F ) )
				.texOffs( 0, 40 )
				.addBox( -6.0F, -4.0F, -3.0F, 12.0F, 4.0F, 6.0F, cubeDeformation ),
			PartPose.offsetAndRotation( 0.0F, 12.0F, 0.0F, 0.1745F, 0.0F, 0.0F )
		);

		partDefinition.getChild( "body" )
			.addOrReplaceChild( "head", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation ),
				PartPose.offsetAndRotation( 0.0F, -22.0F, -1.0F, 0.0873F, 0.0F, 0.0F )
			);

		partDefinition.getChild( "body" ).addOrReplaceChild( "arms", CubeListBuilder.create(), PartPose.offset( 0.0F, -22.0F, 0.0F ) );

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.addOrReplaceChild( "rightArm",
				CubeListBuilder.create().texOffs( 40, 0 ).addBox( -2.5F, 0.0F, -2.5F, 5.0F, 16.0F, 5.0F, cubeDeformation ),
				PartPose.offsetAndRotation( -9.5F, 0.0F, -0.5F, 0.0F, 0.0F, 0.1745F )
			);

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.getChild( "rightArm" )
			.addOrReplaceChild( "rightForearm",
				CubeListBuilder.create().texOffs( 44, 37 ).addBox( -2.5F, -0.5F, -2.5F, 5.0F, 16.0F, 5.0F, cubeDeformation.extend( -0.2F ) ),
				PartPose.offsetAndRotation( 0.0F, 14.5F, 0.0F, -0.7854F, 0.0F, 0.0F )
			);

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.addOrReplaceChild( "leftArm", CubeListBuilder.create().texOffs( 40, 0 ).addBox( -2.5F, 0.0F, -2.5F, 5.0F, 16.0F, 5.0F, cubeDeformation ),
				PartPose.offsetAndRotation( 9.5F, 0.0F, -0.5F, 0.0F, 0.0F, -0.1745F )
			);

		partDefinition.getChild( "body" )
			.getChild( "arms" )
			.getChild( "leftArm" )
			.addOrReplaceChild( "leftForearm",
				CubeListBuilder.create().texOffs( 44, 37 ).addBox( -2.5F, -2.0F, -3.5F, 5.0F, 16.0F, 5.0F, cubeDeformation.extend( -0.2F ) ),
				PartPose.offsetAndRotation( 0.0F, 16.0F, 0.0F, -0.7854F, 0.0F, 0.0F )
			);

		partDefinition.addOrReplaceChild( "leftLeg",
			CubeListBuilder.create().texOffs( 48, 21 ).addBox( -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation ),
			PartPose.offset( 3.0F, 12.0F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "rightLeg",
			CubeListBuilder.create().texOffs( 48, 21 ).addBox( -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation ),
			PartPose.offset( -3.0F, 12.0F, 0.0F )
		);

		return LayerDefinition.create( meshDefinition, 64, 64 );
	}
}
