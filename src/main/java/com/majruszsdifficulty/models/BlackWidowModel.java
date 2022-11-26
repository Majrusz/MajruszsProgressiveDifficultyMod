package com.majruszsdifficulty.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BlackWidowModel< Type extends Entity > extends HierarchicalModel< Type > {
	final ModelPart root, head, neck, body, leg1, leg2, leg3, leg4, leg5, leg6, leg7, leg8;

	public BlackWidowModel( ModelPart root ) {
		this.root = root;
		this.head = root.getChild( "head" );
		this.neck = root.getChild( "neck" );
		this.body = root.getChild( "body" );
		this.leg1 = root.getChild( "leg1" );
		this.leg2 = root.getChild( "leg2" );
		this.leg3 = root.getChild( "leg3" );
		this.leg4 = root.getChild( "leg4" );
		this.leg5 = root.getChild( "leg5" );
		this.leg6 = root.getChild( "leg6" );
		this.leg7 = root.getChild( "leg7" );
		this.leg8 = root.getChild( "leg8" );
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild( "head", CubeListBuilder.create()
			.texOffs( 0, 11 )
			.addBox( -2.0F, -1.0F, -3.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 21.0F, -2.0F ) );

		PartDefinition neck = partdefinition.addOrReplaceChild( "neck", CubeListBuilder.create()
			.texOffs( 14, 11 )
			.addBox( -1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 21.0F, 0.0F ) );

		PartDefinition body = partdefinition.addOrReplaceChild( "body", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -3.0F, -2.0F, 0.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 21.0F, 0.0F ) );

		PartDefinition leg1 = partdefinition.addOrReplaceChild( "leg1", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( -8.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( -1.0F, 21.5F, -4.5F ) );

		PartDefinition leg2 = partdefinition.addOrReplaceChild( "leg2", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( 0.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 1.0F, 21.5F, -4.5F ) );

		PartDefinition leg3 = partdefinition.addOrReplaceChild( "leg3", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( -8.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( -1.0F, 21.5F, -2.5F ) );

		PartDefinition leg4 = partdefinition.addOrReplaceChild( "leg4", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( 0.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 1.0F, 21.5F, -2.5F ) );

		PartDefinition leg5 = partdefinition.addOrReplaceChild( "leg5", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( -8.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( -1.0F, 21.5F, -0.5F ) );

		PartDefinition leg6 = partdefinition.addOrReplaceChild( "leg6", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( 0.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 1.0F, 21.5F, -0.5F ) );

		PartDefinition leg7 = partdefinition.addOrReplaceChild( "leg7", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( -8.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( -1.0F, 21.5F, 1.5F ) );

		PartDefinition leg8 = partdefinition.addOrReplaceChild( "leg8", CubeListBuilder.create()
			.texOffs( 0, 17 )
			.addBox( 0.0F, 0.5F, 1.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offset( 1.0F, 21.5F, 1.5F ) );

		return LayerDefinition.create( meshdefinition, 32, 32 );
	}

	@Override
	public void setupAnim( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
		float headPitch
	) {

	}

	@Override
	public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red,
		float green, float blue, float alpha
	) {
		head.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		neck.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		body.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg1.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg2.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg3.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg4.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg5.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg6.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg7.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
		leg8.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}