package com.majruszs_difficulty.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class OceanShieldModel extends Model {
	public ModelRenderer plate;
	public ModelRenderer handle;
	public ModelRenderer spike1, spike2, spike3, spike4, spike5, spike6, spike7, spike8;

	public OceanShieldModel() {
		super( RenderType::getEntitySolid );

		this.textureWidth = 64;
		this.textureHeight = 64;

		this.handle = new ModelRenderer(this, 0, 0);
		this.handle.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.handle.setTextureOffset(30, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);

		this.plate = new ModelRenderer(this, 0, 0);
		this.plate.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.plate.addBox(-7.0F, -11.0F, -2.0F, 14.0F, 12.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.plate.setTextureOffset(0, 13).addBox(-6.0F, 1.0F, -2.0F, 12.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.plate.setTextureOffset(0, 17).addBox(-5.0F, 4.0F, -2.0F, 10.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.plate.setTextureOffset(0, 21).addBox(-4.0F, 7.0F, -2.0F, 8.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.plate.setTextureOffset(0, 24).addBox(-3.0F, 9.0F, -2.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);

		this.spike1 = new ModelRenderer(this, 0, 0);
		this.spike1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike1.setTextureOffset(46, 0).addBox(-4.0F, -8.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.setRotateAngle(spike1, 0.0F, 0.17453292519943295F, 0.0F);

		this.spike2 = new ModelRenderer(this, 0, 0);
		this.spike2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike2.setTextureOffset(46, 0).addBox(3.0F, -8.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.setRotateAngle(spike2, 0.0F, -0.17453292519943295F, 0.0F);

		this.spike3 = new ModelRenderer(this, 0, 0);
		this.spike3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike3.setTextureOffset(46, 0).addBox(2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.setRotateAngle(spike3, 0.0F, -0.17453292519943295F, 0.0F);

		this.spike4 = new ModelRenderer(this, 0, 0);
		this.spike4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike4.setTextureOffset(46, 0).addBox(-3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.setRotateAngle(spike4, 0.0F, 0.17453292519943295F, 0.0F);

		this.spike5 = new ModelRenderer(this, 0, 0);
		this.spike5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike5.setTextureOffset(46, 0).addBox(-0.5F, 5.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.setRotateAngle(spike5, 0.12217304763960307F, 0.0F, 0.0F);

		this.spike6 = new ModelRenderer(this, 0, 0);
		this.spike6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike6.setTextureOffset(47, 1).addBox(-2.5F, -4.5F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);

		this.spike7 = new ModelRenderer(this, 0, 0);
		this.spike7.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike7.setTextureOffset(47, 1).addBox(1.5F, -4.5F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);

		this.spike8 = new ModelRenderer(this, 0, 0);
		this.spike8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike8.setTextureOffset(47, 1).addBox(-0.5F, 2.0F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render( MatrixStack matrix, IVertexBuilder buffer, int packedLight, int overlay, float red, float green, float blue, float alpha ) {
		ImmutableList.of( this.plate, this.handle, this.spike1, this.spike2, this.spike3, this.spike4, this.spike5 )
			.forEach( ( modelRenderer )->modelRenderer.render( matrix, buffer, packedLight, overlay, red, green, blue, alpha ) );
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
