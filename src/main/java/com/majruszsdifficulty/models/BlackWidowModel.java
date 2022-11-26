package com.majruszsdifficulty.models;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BlackWidowModel< Type extends Entity > extends SpiderModel< Type > {
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, -2.0F));

		partdefinition.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(16, 11).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

		partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 20.5F, -1.5F));

		partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.5F, -1.5F));

		partdefinition.addOrReplaceChild("right_middle_front_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 20.5F, -0.5F));

		partdefinition.addOrReplaceChild("left_middle_front_leg", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.5F, -0.5F));

		partdefinition.addOrReplaceChild("right_middle_hind_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 20.5F, 0.5F));

		partdefinition.addOrReplaceChild("left_middle_hind_leg", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.5F, 0.5F));

		partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 20.5F, 1.5F));

		partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -0.5F, -1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 20.5F, 1.5F));

		return LayerDefinition.create( meshdefinition, 32, 32 );
	}

	public BlackWidowModel( ModelPart p_170984_ ) {
		super( p_170984_ );
	}
}