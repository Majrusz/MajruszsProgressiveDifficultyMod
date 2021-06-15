package com.majruszs_difficulty.renderers;

import com.google.common.collect.ImmutableList;
import com.majruszs_difficulty.RegistryHandlerClient;
import com.majruszs_difficulty.models.OceanShieldModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class OceanShieldRenderer extends ItemStackTileEntityRenderer {
	private final OceanShieldModel oceanShield = new OceanShieldModel();

	@Override
	public void func_239207_a_( ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer,
		int combinedLight, int combinedOverlay
	) {
		matrixStack.push();
		matrixStack.scale( 1, -1, -1 );
		boolean flag = stack.getChildTag( "BlockEntityTag" ) != null;
		RenderMaterial renderMaterial = RegistryHandlerClient.OCEAN_SHIELD_MATERIAL;


		IVertexBuilder ivertexbuilder = renderMaterial.getSprite()
			.wrapBuffer( ItemRenderer.getEntityGlintVertexBuilder( buffer, this.oceanShield.getRenderType( renderMaterial.getAtlasLocation() ), true,
				stack.hasEffect()
			) );
		this.oceanShield.handle.render( matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F );
		if( !flag ) {
			ImmutableList.of( this.oceanShield.plate, this.oceanShield.spike1, this.oceanShield.spike2, this.oceanShield.spike3,
				this.oceanShield.spike4, this.oceanShield.spike5, this.oceanShield.spike6, this.oceanShield.spike7, this.oceanShield.spike8
			)
				.forEach( ( part )->part.render( matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F ) );
		}
		matrixStack.pop();
	}
}
