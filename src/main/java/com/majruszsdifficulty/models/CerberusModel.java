package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.CerberusEntity;
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

@OnlyIn( Dist.CLIENT )
public class CerberusModel< Type extends CerberusEntity > extends HierarchicalModel< Type > {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart spine;
	private final ModelPart necks;
	private final ModelPart neck1, head1, jawUpper1, jawLower1;
	private final ModelPart neck2, head2, jawUpper2, jawLower2;
	private final ModelPart neck3, head3, jawUpper3, jawLower3;

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
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild( "body", CubeListBuilder.create(), PartPose.offset( 0.0F, 25.0F, 0.0F ) );

		PartDefinition spine = body.addOrReplaceChild( "spine", CubeListBuilder.create()
			.texOffs( 28, 23 )
			.addBox( -2.0F, -2.0F, -12.0F, 4.0F, 4.0F, 25.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -25.0F, 2.0F, 0.0436F, 0.0F, 0.0F ) );

		PartDefinition necks = spine.addOrReplaceChild( "necks", CubeListBuilder.create(), PartPose.offset( 0.0F, -2.0F, -10.0F ) );

		PartDefinition neck1 = necks.addOrReplaceChild( "neck1", CubeListBuilder.create()
			.texOffs( 0, 45 )
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( -4.0F, 2.0F, -1.0F, 0.0F, 0.6109F, 0.0F ) );

		PartDefinition head1 = neck1.addOrReplaceChild( "head1", CubeListBuilder.create()
			.texOffs( 0, 31 )
			.addBox( -4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.5F, -6.0F, 0.0F, -0.1745F, 0.0F ) );

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
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 0.0F, 2.0F, -1.0F, -0.5236F, 0.0F, 0.0F ) );

		PartDefinition head2 = neck2.addOrReplaceChild( "head2", CubeListBuilder.create()
			.texOffs( 0, 31 )
			.addBox( -4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -3.5F, -7.0F, 0.5236F, 0.0F, 0.0F ) );

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
			.addBox( -2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation( -0.1F ) ), PartPose.offsetAndRotation( 4.0F, 2.0F, -1.0F, 0.0F, -0.6109F, 0.0F ) );

		PartDefinition head3 = neck3.addOrReplaceChild( "head3", CubeListBuilder.create()
			.texOffs( 0, 31 )
			.addBox( -4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.5F, -6.0F, 0.0F, 0.1745F, 0.0F ) );

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
			.addBox( -1.5F, 0.0F, -1.5F, 3.0F, 21.0F, 3.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -1.0F, 13.5F, 0.5236F, 0.0F, 0.0F ) );

		PartDefinition hindThigh1 = spine.addOrReplaceChild( "hindThigh1", CubeListBuilder.create()
			.texOffs( 80, 19 )
			.addBox( 0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 2.0F, 1.0F, 10.0F, -0.3491F, 0.0F, 0.0F ) );

		PartDefinition hindCalf1 = hindThigh1.addOrReplaceChild( "hindCalf1", CubeListBuilder.create()
			.texOffs( 80, 0 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 2.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F ) );

		PartDefinition hindThigh2 = spine.addOrReplaceChild( "hindThigh2", CubeListBuilder.create()
			.texOffs( 80, 19 )
			.addBox( -4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -2.0F, 1.0F, 10.0F, -0.3491F, 0.0F, 0.0F ) );

		PartDefinition hindCalf2 = hindThigh2.addOrReplaceChild( "hindCalf2", CubeListBuilder.create()
			.texOffs( 80, 0 )
			.addBox( -2.0F, 0.0F, -2.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -2.0F, 10.0F, 0.0F, 0.5236F, 0.0F, 0.0F ) );

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
		float limbFactor1 = limbSwing * 0.3333f, limbFactor2 = 0.9f * limbSwingAmount, bodyFactor = 0.2f * limbSwingAmount;

		// head rotation when looking around
		this.necks.yRot = ( float )Math.toRadians( netHeadYaw );
		this.necks.xRot = ( float )Math.toRadians( headPitch ) + 0.0873f;

		// jaw rotation dependent on players distance
		float jawRotation = ( float )Math.toRadians( -20.0f * Mth.clamp( 1.25f - this.getPlayerDistance( cerberus ) / 4.0f, 0.0f, 1.0f ) );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = jawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = jawRotation;
	}

	private float getPlayerDistance( Type cerberus ) {
		LocalPlayer player = Minecraft.getInstance().player;

		return player != null && !player.isSpectator() ? ( float )VectorHelper.distance( player.position(), cerberus.position() ) : Float.MAX_VALUE;
	}
}
