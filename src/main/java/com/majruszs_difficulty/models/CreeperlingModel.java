package com.majruszs_difficulty.models;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Model for new Creeperling enemy. */
@OnlyIn( Dist.CLIENT )
public class CreeperlingModel< Type extends Entity > extends SegmentedModel< Type > {
	public ModelRenderer body, leftBackFoot, leftFrontFoot, rightFrontFoot, rightBackFoot, head;

	public CreeperlingModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;

		this.leftBackFoot = new ModelRenderer( this, 0, 14 );
		this.leftBackFoot.setRotationPoint( 1.0F, 21.0F, 2.0F );
		this.leftBackFoot.addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F );

		this.rightBackFoot = new ModelRenderer( this, 0, 14 );
		this.rightBackFoot.setRotationPoint( -1.0F, 21.0F, 2.0F );
		this.rightBackFoot.addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F );

		this.rightFrontFoot = new ModelRenderer( this, 0, 14 );
		this.rightFrontFoot.setRotationPoint( -1.0F, 21.0F, -2.0F );
		this.rightFrontFoot.addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F );

		this.leftFrontFoot = new ModelRenderer( this, 0, 14 );
		this.leftFrontFoot.setRotationPoint( 1.0F, 21.0F, -2.0F );
		this.leftFrontFoot.addBox( -1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F );

		this.body = new ModelRenderer( this, 8, 14 );
		this.body.setRotationPoint( 0.0F, 15.0F, 0.0F );
		this.body.addBox( -2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F );

		this.head = new ModelRenderer( this, 0, 0 );
		this.head.setRotationPoint( 0.0F, 15.0F, 0.0F );
		this.head.addBox( -3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, 0.0F, 0.0F, 0.0F );
	}

	public Iterable< ModelRenderer > getParts() {
		return ImmutableList.of( this.leftBackFoot, this.head, this.rightBackFoot, this.rightFrontFoot, this.leftFrontFoot, this.body );
	}

	@Override
	public void setRotationAngles( Type entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.head.rotateAngleY = netHeadYaw * ( ( float )Math.PI / 180f );
		this.head.rotateAngleX = headPitch * ( ( float )Math.PI / 180f );

		float limbFactor1 = 2.5f * limbSwing * 0.6662f, limbFactor2 = 1.4f * limbSwingAmount;
		this.leftBackFoot.rotateAngleX = MathHelper.cos( limbFactor1 ) * limbFactor2;
		this.rightBackFoot.rotateAngleX = MathHelper.cos( limbFactor1+ ( float )Math.PI ) * limbFactor2;
		this.rightFrontFoot.rotateAngleX = MathHelper.cos( limbFactor1 + ( float )Math.PI ) * limbFactor2;
		this.leftFrontFoot.rotateAngleX = MathHelper.cos( limbFactor1 ) * limbFactor2;
	}

}
