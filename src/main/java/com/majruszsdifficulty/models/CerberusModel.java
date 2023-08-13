package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.CerberusEntity;
import com.mlib.animations.Animation;
import com.mlib.animations.Frame;
import com.mlib.animations.InterpolationType;
import com.mlib.math.AnyPos;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class CerberusModel< Type extends CerberusEntity > extends HierarchicalModel< Type > {
	static final Animation< Float > BITE_JAW_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Float > BREATH_JAW_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Vector3f > BITE_SIDE_NECK_ROTATION = new Animation<>( 1.0f );

	static {
		BITE_JAW_ROTATION_X.add( 0.00f, new Frame.Degrees( 0.0f ) )
			.add( 0.30f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.70f, new Frame.Degrees( -30.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) );

		BREATH_JAW_ROTATION_X.add( 0.00f, new Frame.Degrees( 0.0f ) )
			.add( 0.15f, new Frame.Degrees( 0.0f ) )
			.add( 0.25f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.40f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.65f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.90f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 0.0f ) );

		BITE_SIDE_NECK_ROTATION.add( 0.00f, new Frame.Vector( 0.0f, 0.0f, 0.0f ) )
			.add( 0.30f, new Frame.Vector( -15.0f, 0.0f, -30.0f, InterpolationType.SQUARE ) )
			.add( 0.70f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Vector( 0.0f, 0.0f, 0.0f ) );
	}

	final ModelPart root;
	final ModelPart body;
	final ModelPart spine;
	final ModelPart necks;
	final ModelPart neck1, head1, jawUpper1, jawLower1;
	final ModelPart neck2, head2, jawUpper2, jawLower2;
	final ModelPart neck3, head3, jawUpper3, jawLower3;
	final ModelPart frontLeg1, frontLeg2;
	final ModelPart hindLeg1, hindLeg2;

	public CerberusModel( ModelPart root ) {
		this.root = root;
		this.body = root.getChild( "body" );
		this.spine = this.body.getChild( "spine" );
		this.necks = this.spine.getChild( "necks" );

		this.neck1 = this.necks.getChild( "neck1" );
		this.head1 = this.neck1.getChild( "head1" );
		this.jawUpper1 = this.head1.getChild( "jawUpper1" );
		this.jawLower1 = this.head1.getChild( "jawLower1" );

		this.neck2 = this.necks.getChild( "neck2" );
		this.head2 = this.neck2.getChild( "head2" );
		this.jawUpper2 = this.head2.getChild( "jawUpper2" );
		this.jawLower2 = this.head2.getChild( "jawLower2" );

		this.neck3 = this.necks.getChild( "neck3" );
		this.head3 = this.neck3.getChild( "head3" );
		this.jawUpper3 = this.head3.getChild( "jawUpper3" );
		this.jawLower3 = this.head3.getChild( "jawLower3" );

		this.frontLeg1 = this.spine.getChild( "frontLeg1" );
		this.frontLeg2 = this.spine.getChild( "frontLeg2" );
		this.hindLeg1 = this.spine.getChild( "hindLeg1" );
		this.hindLeg2 = this.spine.getChild( "hindLeg2" );
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild( "body", CubeListBuilder.create(), PartPose.offset( 0.0F, 27.0F, -1.0F ) );

		PartDefinition spine = body.addOrReplaceChild( "spine", CubeListBuilder.create()
			.texOffs( 31, 38 )
			.addBox( -2.0F, -2.0F, -12.0F, 4.0F, 4.0F, 22.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, -25.0F, 2.0F ) );

		PartDefinition necks = spine.addOrReplaceChild( "necks", CubeListBuilder.create(), PartPose.offset( 0.0F, -3.0F, -10.0F ) );

		PartDefinition neck1 = necks.addOrReplaceChild( "neck1", CubeListBuilder.create()
			.texOffs( 0, 45 )
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( -5.0F, 4.0F, 2.0F, 0.0F, 0.6109F, 0.0F ) );

		PartDefinition head1 = neck1.addOrReplaceChild( "head1", CubeListBuilder.create()
			.texOffs( 0, 31 )
			.addBox( -4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.5F, -6.0F, 0.1745F, -0.1745F, 0.0F ) );

		PartDefinition leftEar1 = head1.addOrReplaceChild( "leftEar1", CubeListBuilder.create()
			.texOffs( 22, 0 )
			.addBox( -0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 3.5F, -4.5F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition rightEar1 = head1.addOrReplaceChild( "rightEar1", CubeListBuilder.create()
			.texOffs( 22, 0 )
			.addBox( -0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( -3.5F, -4.5F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition jawUpper1 = head1.addOrReplaceChild( "jawUpper1", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -2.5F, -1.5F, -6.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 0.5F, -6.0F ) );

		PartDefinition teethUpper1 = jawUpper1.addOrReplaceChild( "teethUpper1", CubeListBuilder.create()
			.texOffs( 0, 15 )
			.addBox( -2.5F, -1.5F, -6.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition jawLower1 = head1.addOrReplaceChild( "jawLower1", CubeListBuilder.create()
			.texOffs( 0, 8 )
			.addBox( -2.5F, -0.4F, -5.9F, 5.0F, 1.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 3.5F, -6.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition teethLower1 = jawLower1.addOrReplaceChild( "teethLower1", CubeListBuilder.create()
			.texOffs( 0, 23 )
			.addBox( -2.5F, -2.4F, -5.9F, 5.0F, 2.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition neck2 = necks.addOrReplaceChild( "neck2", CubeListBuilder.create()
			.texOffs( 0, 45 )
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 3.0F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition head2 = neck2.addOrReplaceChild( "head2", CubeListBuilder.create()
			.texOffs( 0, 31 )
			.addBox( -4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -2.5F, -7.0F, 0.6981F, 0.0F, 0.0F ) );

		PartDefinition leftEar2 = head2.addOrReplaceChild( "leftEar2", CubeListBuilder.create()
			.texOffs( 22, 0 )
			.addBox( -0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 3.5F, -4.5F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition rightEar2 = head2.addOrReplaceChild( "rightEar2", CubeListBuilder.create()
			.texOffs( 22, 0 )
			.addBox( -0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( -3.5F, -4.5F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition jawUpper2 = head2.addOrReplaceChild( "jawUpper2", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -2.5F, -1.5F, -6.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 0.5F, -6.0F ) );

		PartDefinition teethUpper2 = jawUpper2.addOrReplaceChild( "teethUpper2", CubeListBuilder.create()
			.texOffs( 0, 15 )
			.addBox( -2.5F, -1.5F, -6.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition jawLower2 = head2.addOrReplaceChild( "jawLower2", CubeListBuilder.create()
			.texOffs( 0, 8 )
			.addBox( -2.5F, -0.4F, -5.9F, 5.0F, 1.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 3.5F, -6.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition teethLower2 = jawLower2.addOrReplaceChild( "teethLower2", CubeListBuilder.create()
			.texOffs( 0, 23 )
			.addBox( -2.5F, -2.4F, -5.9F, 5.0F, 2.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition neck3 = necks.addOrReplaceChild( "neck3", CubeListBuilder.create()
			.texOffs( 0, 45 )
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 5.0F, 4.0F, 2.0F, 0.0F, -0.6109F, 0.0F ) );

		PartDefinition head3 = neck3.addOrReplaceChild( "head3", CubeListBuilder.create()
			.texOffs( 0, 31 )
			.addBox( -4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.5F, -6.0F, 0.1745F, 0.1745F, 0.0F ) );

		PartDefinition leftEar3 = head3.addOrReplaceChild( "leftEar3", CubeListBuilder.create()
			.texOffs( 22, 0 )
			.addBox( -0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 3.5F, -4.5F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition rightEar3 = head3.addOrReplaceChild( "rightEar3", CubeListBuilder.create()
			.texOffs( 22, 0 )
			.addBox( -0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( -3.5F, -4.5F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition jawUpper3 = head3.addOrReplaceChild( "jawUpper3", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -2.5F, -1.5F, -6.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 0.5F, -6.0F ) );

		PartDefinition teethUpper3 = jawUpper3.addOrReplaceChild( "teethUpper3", CubeListBuilder.create()
			.texOffs( 0, 15 )
			.addBox( -2.5F, -1.5F, -6.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition jawLower3 = head3.addOrReplaceChild( "jawLower3", CubeListBuilder.create()
			.texOffs( 0, 8 )
			.addBox( -2.5F, -0.4F, -5.9F, 5.0F, 1.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 3.5F, -6.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition teethLower3 = jawLower3.addOrReplaceChild( "teethLower3", CubeListBuilder.create()
			.texOffs( 0, 23 )
			.addBox( -2.5F, -2.4F, -5.9F, 5.0F, 2.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F ) );

		PartDefinition chest = spine.addOrReplaceChild( "chest", CubeListBuilder.create()
			.texOffs( 28, 0 )
			.addBox( -7.0F, -6.5F, -5.0F, 14.0F, 8.0F, 12.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 4.5F, -5.0F, 0.0F, 3.1416F, 0.0F ) );

		PartDefinition frontLeg1 = spine.addOrReplaceChild( "frontLeg1", CubeListBuilder.create()
			.texOffs( 28, 22 )
			.addBox( -2.0F, -1.0F, -2.0F, 4.0F, 20.0F, 4.0F, new CubeDeformation( 0.1F ) ), PartPose.offset( 5.0F, 3.0F, -9.0F ) );

		PartDefinition frontLeg2 = spine.addOrReplaceChild( "frontLeg2", CubeListBuilder.create()
			.texOffs( 28, 22 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 20.0F, 4.0F, new CubeDeformation( 0.1F ) ), PartPose.offset( -5.0F, 2.0F, -9.0F ) );

		PartDefinition pelvis = spine.addOrReplaceChild( "pelvis", CubeListBuilder.create()
			.texOffs( 53, 22 )
			.addBox( -6.0F, -4.5F, -16.0F, 12.0F, 4.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 2.5F, -6.0F, 0.0F, 3.1416F, 0.0F ) );

		PartDefinition hindLeg1 = spine.addOrReplaceChild( "hindLeg1", CubeListBuilder.create()
			.texOffs( 28, 22 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 20.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 4.0F, 2.0F, 7.0F ) );

		PartDefinition hindLeg2 = spine.addOrReplaceChild( "hindLeg2", CubeListBuilder.create()
			.texOffs( 28, 22 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 20.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( -4.0F, 2.0F, 7.0F ) );

		PartDefinition tail = spine.addOrReplaceChild( "tail", CubeListBuilder.create()
			.texOffs( 44, 22 )
			.addBox( -1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.0F, 10.0F, 0.3927F, 0.0F, 0.0F ) );

		return LayerDefinition.create( meshdefinition, 96, 64 );
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim( Type cerberus, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
		float headPitch
	) {
		CerberusEntity.Skills skills = cerberus.getCustomSkills();
		this.spine.xRot = ( float )Math.toRadians( 0.0f );

		// head rotation when looking around
		this.necks.yRot = ( float )Math.toRadians( netHeadYaw );
		this.necks.xRot = ( float )Math.toRadians( headPitch ) + 0.0873f;

		// head rotation for side heads to follow the main one
		this.neck1.yRot = ( float )( Math.toRadians( 35.0f + netHeadYaw * ( netHeadYaw < 0.0f ? 0.255f : -0.075f ) + 2.0f * Math.sin( 0.1f * ageInTicks ) ) );
		this.neck3.yRot = ( float )( Math.toRadians( -35.0f + netHeadYaw * ( netHeadYaw > 0.0f ? 0.225f : -0.075f ) - 2.0f * Math.cos( 0.1f * ageInTicks ) ) );

		// jaw rotation dependent on players distance
		float jawRotation = ( float )Math.toRadians( -1.0f * Math.sin( ageInTicks / 10.0f ) - 15.0f * Mth.clamp( 1.20f - this.getPlayerDistance( cerberus ) / 5.0f, 0.0f, 1.0f ) );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = jawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = jawRotation;

		// movement anims
		float limbFactor1 = 0.5f * limbSwing, limbFactor2 = 0.6f * limbSwingAmount;
		this.frontLeg1.xRot = this.hindLeg1.xRot = ( float )( Math.cos( limbFactor1 ) * limbFactor2 );
		this.frontLeg2.xRot = this.hindLeg2.xRot = ( float )( Math.cos( limbFactor1 + Math.PI ) * limbFactor2 );
		this.body.y = 27.0f + 1.0f * Math.abs( ( float )Math.cos( limbFactor1 ) * limbSwingAmount );

		// bite anims (jaw)
		float biteRatio = skills.getRatio( CerberusEntity.SkillType.BITE );
		float biteJawRotation = ( float )Mth.clamp( jawRotation - BITE_JAW_ROTATION_X.apply( biteRatio, ageInTicks ), Math.toRadians( -30.0 ), 0.0 );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = biteJawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = biteJawRotation;

		// bite anims (fire breath)
		float breathRatio = skills.getRatio( CerberusEntity.SkillType.FIRE_BREATH );
		float breathJawRotation = ( float )Mth.clamp( biteJawRotation - BREATH_JAW_ROTATION_X.apply( breathRatio, ageInTicks ), Math.toRadians( -30.0 ), 0.0 );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = breathJawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = breathJawRotation;

		// bite anims (side necks)
		Vector3f neckRotation = BITE_SIDE_NECK_ROTATION.apply( biteRatio, ageInTicks );
		neckRotation.mul( ( float )Math.PI / 180.0f );
		this.neck1.xRot = neckRotation.x();
		this.neck1.zRot = neckRotation.z();
		this.neck3.xRot = neckRotation.x();
		this.neck3.zRot = -neckRotation.z();
	}

	private float getPlayerDistance( Type cerberus ) {
		LocalPlayer player = Minecraft.getInstance().player;

		return player != null && !player.isSpectator() ? AnyPos.from( player.position() ).dist( cerberus.position() ).floatValue() : Float.MAX_VALUE;
	}
}
