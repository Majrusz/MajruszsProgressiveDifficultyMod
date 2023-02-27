package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.CerberusEntity;
import com.mlib.animations.Animation;
import com.mlib.animations.Frame;
import com.mlib.animations.InterpolationType;
import com.mlib.math.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn( Dist.CLIENT )
public class CerberusModel< Type extends CerberusEntity > extends HierarchicalModel< Type > {
	static final Animation< Float > BITE_JAW_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Vector3f > BITE_SIDE_NECK_ROTATION = new Animation<>( 1.0f );
	static final Animation< Float > GALLOP_FRONT_LEG_1_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Float > GALLOP_FRONT_LEG_2_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Float > GALLOP_HIND_LEG_1_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Float > GALLOP_HIND_LEG_2_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Float > GALLOP_SPINE_ROTATION_X = new Animation<>( 1.0f );
	static final Animation< Float > GALLOP_BODY_POSITION_Y = new Animation<>( 1.0f );

	static {
		BITE_JAW_ROTATION_X.add( 0.00f, new Frame.Degrees( 0.0f ) )
			.add( 0.30f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.70f, new Frame.Degrees( -30.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) );

		BITE_SIDE_NECK_ROTATION.add( 0.00f, new Frame.Vector( 0.0f, 0.0f, 0.0f ) )
			.add( 0.30f, new Frame.Vector( -15.0f, 0.0f, -30.0f, InterpolationType.SQUARE ) )
			.add( 0.70f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Vector( 0.0f, 0.0f, 0.0f ) );

		GALLOP_FRONT_LEG_1_ROTATION_X.add( 0.00f, new Frame.Degrees( 45.0f ) )
			.add( 0.25f, new Frame.Degrees( 15.0f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Degrees( -15.0f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 45.00f, InterpolationType.SQUARE ) );

		GALLOP_FRONT_LEG_2_ROTATION_X.add( 0.00f, new Frame.Degrees( 15.0f ) )
			.add( 0.25f, new Frame.Degrees( 0.0f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Degrees( 30.0f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Degrees( 45.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 15.00f, InterpolationType.SQUARE ) );

		GALLOP_HIND_LEG_1_ROTATION_X.add( 0.00f, new Frame.Degrees( -15.0f ) )
			.add( 0.25f, new Frame.Degrees( 15.0f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Degrees( -15.0f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Degrees( -45.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( -15.00f, InterpolationType.SQUARE ) );

		GALLOP_HIND_LEG_2_ROTATION_X.add( 0.00f, new Frame.Degrees( -45.0f ) )
			.add( 0.25f, new Frame.Degrees( -15.0f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Degrees( 15.0f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Degrees( -15.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( -45.00f, InterpolationType.SQUARE ) );

		GALLOP_SPINE_ROTATION_X.add( 0.00f, new Frame.Degrees( 0.0f ) )
			.add( 0.25f, new Frame.Degrees( -5.00f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Degrees( 5.00f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Degrees( 10.00f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Degrees( 0.00f, InterpolationType.SQUARE ) );

		GALLOP_BODY_POSITION_Y.add( 0.00f, new Frame.Value( 0.0f ) )
			.add( 0.25f, new Frame.Value( 0.0f, InterpolationType.SQUARE ) )
			.add( 0.50f, new Frame.Value( 0.0f, InterpolationType.SQUARE ) )
			.add( 0.75f, new Frame.Value( -2.0f, InterpolationType.SQUARE ) )
			.add( 1.00f, new Frame.Value( 0.00f, InterpolationType.SQUARE ) );
	}

	final ModelPart root;
	final ModelPart body;
	final ModelPart spine;
	final ModelPart necks;
	final ModelPart neck1, head1, jawUpper1, jawLower1;
	final ModelPart neck2, head2, jawUpper2, jawLower2;
	final ModelPart neck3, head3, jawUpper3, jawLower3;
	final ModelPart frontThigh1, frontThigh2;
	final ModelPart hindThigh1, hindThigh2;

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

		this.frontThigh1 = this.spine.getChild( "frontThigh1" );
		this.frontThigh2 = this.spine.getChild( "frontThigh2" );
		this.hindThigh1 = this.spine.getChild( "hindThigh1" );
		this.hindThigh2 = this.spine.getChild( "hindThigh2" );
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild( "body", CubeListBuilder.create(), PartPose.offset( 0.0F, 25.0F, 0.0F ) );

		PartDefinition spine = body.addOrReplaceChild( "spine", CubeListBuilder.create()
			.texOffs( 28, 23 )
			.addBox( -2.0F, -2.0F, -12.0F, 4.0F, 4.0F, 25.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -25.0F, 2.0F, 0.0F, 0.0F, 0.0F ) );

		PartDefinition necks = spine.addOrReplaceChild( "necks", CubeListBuilder.create(), PartPose.offset( 0.0F, -2.0F, -10.0F ) );

		PartDefinition neck1 = necks.addOrReplaceChild( "neck1", CubeListBuilder.create()
			.texOffs( 0, 45 )
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( -4.0F, 2.0F, -1.0F, 0.0F, 0.6109F, 0.0F ) );

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
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 1.0F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

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
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 4.0F, 2.0F, -1.0F, 0.0F, -0.6109F, 0.0F ) );

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
			.addBox( -6.0F, -4.5F, -5.0F, 12.0F, 9.0F, 14.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 2.5F, -3.0F, 0.0F, 3.1416F, 0.0F ) );

		PartDefinition frontThigh1 = spine.addOrReplaceChild( "frontThigh1", CubeListBuilder.create()
			.texOffs( 80, 19 )
			.addBox( -4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -6.0F, 1.0F, -9.0F, 0.5236F, 0.0F, 0.0F ) );

		PartDefinition frontCalf1 = frontThigh1.addOrReplaceChild( "frontCalf1", CubeListBuilder.create()
			.texOffs( 80, 0 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -2.0F, 10.0F, 0.0F, -0.7854F, 0.0F, 0.0F ) );

		PartDefinition frontThigh2 = spine.addOrReplaceChild( "frontThigh2", CubeListBuilder.create()
			.texOffs( 80, 19 )
			.addBox( 0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 6.0F, 1.0F, -9.0F, 0.5236F, 0.0F, 0.0F ) );

		PartDefinition frontCalf2 = frontThigh2.addOrReplaceChild( "frontCalf2", CubeListBuilder.create()
			.texOffs( 80, 0 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 2.0F, 10.0F, 0.0F, -0.7854F, 0.0F, 0.0F ) );

		PartDefinition tail = spine.addOrReplaceChild( "tail", CubeListBuilder.create()
			.texOffs( 28, 23 )
			.addBox( -1.0F, 0.0F, -1.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.0F, 13.5F, 0.6109F, 0.0F, 0.0F ) );

		PartDefinition tail2 = tail.addOrReplaceChild( "tail2", CubeListBuilder.create()
			.texOffs( 28, 23 )
			.addBox( -1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 10.0F, -0.5F, -0.4363F, 0.0F, 0.0F ) );

		PartDefinition hindThigh1 = spine.addOrReplaceChild( "hindThigh1", CubeListBuilder.create()
			.texOffs( 80, 19 )
			.addBox( -4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -2.0F, 1.0F, 10.0F, -0.3491F, 0.0F, 0.0F ) );

		PartDefinition hindCalf1 = hindThigh1.addOrReplaceChild( "hindCalf1", CubeListBuilder.create()
			.texOffs( 80, 0 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -2.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F ) );

		PartDefinition hindThigh2 = spine.addOrReplaceChild( "hindThigh2", CubeListBuilder.create()
			.texOffs( 80, 19 )
			.addBox( 0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 2.0F, 1.0F, 10.0F, -0.3491F, 0.0F, 0.0F ) );

		PartDefinition hindCalf2 = hindThigh2.addOrReplaceChild( "hindCalf2", CubeListBuilder.create()
			.texOffs( 80, 0 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 2.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F ) );

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

		// jaw rotation dependent on players distance
		float jawRotation = ( float )Math.toRadians( -1.0f * Math.sin( ageInTicks / 10.0f ) - 15.0f * Mth.clamp( 1.20f - this.getPlayerDistance( cerberus ) / 5.0f, 0.0f, 1.0f ) );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = jawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = jawRotation;

		// movement anims
		if( cerberus.hasTarget ) {
			// gallop anims
			float swingRatio = ( limbSwing * 0.15f ) % 1.0f;
			this.frontThigh1.xRot = ( float )Math.toRadians( 30.0f ) - GALLOP_FRONT_LEG_1_ROTATION_X.apply( swingRatio, ageInTicks ) * limbSwingAmount;
			this.frontThigh2.xRot = ( float )Math.toRadians( 30.0f ) - GALLOP_FRONT_LEG_2_ROTATION_X.apply( swingRatio, ageInTicks ) * limbSwingAmount;
			this.hindThigh1.xRot = ( float )Math.toRadians( -20.0f ) - GALLOP_HIND_LEG_1_ROTATION_X.apply( swingRatio, ageInTicks ) * limbSwingAmount;
			this.hindThigh2.xRot = ( float )Math.toRadians( -20.0f ) - GALLOP_HIND_LEG_2_ROTATION_X.apply( swingRatio, ageInTicks ) * limbSwingAmount;
			this.spine.xRot -= GALLOP_SPINE_ROTATION_X.apply( swingRatio, ageInTicks ) * limbSwingAmount;
			this.body.y = 25.0f - GALLOP_BODY_POSITION_Y.apply( swingRatio, ageInTicks ) * limbSwingAmount;
		} else {
			// walk anims
			float swingRatio = ( float )( Math.cos( 0.4f * limbSwing ) * limbSwingAmount );
			this.frontThigh2.xRot = ( float )Math.toRadians( 30.0f + 30.0f * swingRatio );
			this.hindThigh2.xRot = ( float )Math.toRadians( -20.0f + 30.0f * swingRatio );
			this.frontThigh1.xRot = ( float )Math.toRadians( 30.0f - 30.0f * swingRatio );
			this.hindThigh1.xRot = ( float )Math.toRadians( -20.0f - 30.0f * swingRatio );
			this.body.y = 25.0f + 1.0f * Math.abs( swingRatio );
		}

		// bite anims (jaw)
		float biteRatio = skills.getRatio( CerberusEntity.SkillType.BITE );
		float biteJawRotation = ( float )Mth.clamp( jawRotation - BITE_JAW_ROTATION_X.apply( biteRatio, ageInTicks ), Math.toRadians( -30.0 ), 0.0 );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = biteJawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = biteJawRotation;

		// bite anims (side necks)
		Vector3f neckRotation = new Vector3f( BITE_SIDE_NECK_ROTATION.apply( biteRatio, ageInTicks ) ).mul( ( float )Math.PI / 180.0f );
		this.neck1.xRot = neckRotation.x;
		this.neck1.zRot = neckRotation.z;
		this.neck3.xRot = neckRotation.x;
		this.neck3.zRot = -neckRotation.z;

	}

	private float getPlayerDistance( Type cerberus ) {
		LocalPlayer player = Minecraft.getInstance().player;

		return player != null && !player.isSpectator() ? ( float )VectorHelper.distance( player.position(), cerberus.position() ) : Float.MAX_VALUE;
	}
}
