package com.majruszsdifficulty.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class OceanShieldModel extends Model {
	public ModelPart root;
	public ModelPart plate;
	public ModelPart handle;
	public ModelPart spike1, spike2, spike3, spike4, spike5, spike6, spike7, spike8;

	public OceanShieldModel( ModelPart modelPart ) {
		super( RenderType::entitySolid );

		this.root = modelPart;
		this.plate = this.root.getChild( "plate" );
		this.handle = this.root.getChild( "handle" );
		this.spike1 = this.root.getChild( "spike1" );
		this.spike2 = this.root.getChild( "spike2" );
		this.spike3 = this.root.getChild( "spike3" );
		this.spike4 = this.root.getChild( "spike4" );
		this.spike5 = this.root.getChild( "spike5" );
		this.spike6 = this.root.getChild( "spike6" );
		this.spike7 = this.root.getChild( "spike7" );
		this.spike8 = this.root.getChild( "spike8" );
	}

	@Override
	public void renderToBuffer( PoseStack poseStack, VertexConsumer consumer, int packedLight, int overlay, float red, float green, float blue,
		float alpha
	) {
		this.root.render( poseStack, consumer, packedLight, overlay, red, green, blue, alpha );
	}

	public ImmutableList< ModelPart > getModels() {
		return ImmutableList.of( this.plate, this.handle, this.spike1, this.spike2, this.spike3, this.spike4, this.spike5, this.spike6, this.spike7,
			this.spike8
		);
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild( "handle", CubeListBuilder.create().texOffs( 30, 0 ).addBox( -1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F ),
			PartPose.ZERO
		);

		partDefinition.addOrReplaceChild( "plate", CubeListBuilder.create()
			.texOffs( 0, 0 )
			.addBox( -7.0F, -11.0F, -2.0F, 14.0F, 12.0F, 1.0F )
			.texOffs( 0, 13 )
			.addBox( -6.0F, 1.0F, -2.0F, 12.0F, 3.0F, 1.0F )
			.texOffs( 0, 17 )
			.addBox( -5.0F, 4.0F, -2.0F, 10.0F, 3.0F, 1.0F )
			.texOffs( 0, 21 )
			.addBox( -4.0F, 7.0F, -2.0F, 8.0F, 2.0F, 1.0F )
			.texOffs( 0, 24 )
			.addBox( -3.0F, 9.0F, -2.0F, 6.0F, 1.0F, 1.0F ), PartPose.ZERO );

		partDefinition.addOrReplaceChild( "spike1", CubeListBuilder.create().texOffs( 46, 0 ).addBox( -4.0F, -8.0F, -4.0F, 1.0F, 1.0F, 2.0F ),
			PartPose.offsetAndRotation( 0.0f, 0.0f, 0.0f, 0.0F, 0.17453292519943295F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "spike2", CubeListBuilder.create().texOffs( 46, 0 ).addBox( 3.0F, -8.0F, -4.0F, 1.0F, 1.0F, 2.0F ),
			PartPose.offsetAndRotation( 0.0f, 0.0f, 0.0f, 0.0F, -0.17453292519943295F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "spike3", CubeListBuilder.create().texOffs( 46, 0 ).addBox( 2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F ),
			PartPose.offsetAndRotation( 0.0f, 0.0f, 0.0f, 0.0F, -0.17453292519943295F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "spike4", CubeListBuilder.create().texOffs( 46, 0 ).addBox( -3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F ),
			PartPose.offsetAndRotation( 0.0f, 0.0f, 0.0f, 0.0F, 0.17453292519943295F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "spike5", CubeListBuilder.create().texOffs( 46, 0 ).addBox( -0.5F, 5.0F, -4.0F, 1.0F, 1.0F, 2.0F ),
			PartPose.offsetAndRotation( 0.0f, 0.0f, 0.0f, 0.12217304763960307F, 0.0F, 0.0F )
		);

		partDefinition.addOrReplaceChild( "spike6", CubeListBuilder.create().texOffs( 47, 1 ).addBox( -2.5F, -4.5F, -3.0F, 1.0F, 1.0F, 1.0F ),
			PartPose.ZERO
		);

		partDefinition.addOrReplaceChild( "spike7", CubeListBuilder.create().texOffs( 47, 1 ).addBox( 1.5F, -4.5F, -3.0F, 1.0F, 1.0F, 1.0F ),
			PartPose.ZERO
		);

		partDefinition.addOrReplaceChild( "spike8", CubeListBuilder.create().texOffs( 47, 1 ).addBox( -0.5F, 2.0F, -3.0F, 1.0F, 1.0F, 1.0F ),
			PartPose.ZERO
		);

		return LayerDefinition.create( meshDefinition, 64, 64 );
	}
}
