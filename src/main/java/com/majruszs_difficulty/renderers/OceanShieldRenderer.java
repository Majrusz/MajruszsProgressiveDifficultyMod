package com.majruszs_difficulty.renderers;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.models.OceanShieldModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn( Dist.CLIENT )
public class OceanShieldRenderer implements ResourceManagerReloadListener {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation( MajruszsDifficulty.getLocation( "ocean_shield" ), "shield" );
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
	private final EntityModelSet entityModelSet;
	private OceanShieldModel oceanShield;

	public OceanShieldRenderer( BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet ) {
		this.blockEntityRenderDispatcher = renderDispatcher;
		this.entityModelSet = modelSet;
	}

	@Override
	public void onResourceManagerReload( ResourceManager p_172555_ ) {
		this.oceanShield = new OceanShieldModel( this.entityModelSet.bakeLayer( LAYER_LOCATION ) );
	}

	public void renderByItem( ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource,
		int combinedLight, int combinedOverlay
	) {
		boolean flag = itemStack.getTagElement( "BlockEntityTag" ) != null;
		poseStack.pushPose();
		poseStack.scale( 1.0F, -1.0F, -1.0F );
		Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
		VertexConsumer vertexconsumer = material.sprite()
			.wrap( ItemRenderer.getFoilBufferDirect( bufferSource, this.oceanShield.renderType( material.atlasLocation() ), true,
				itemStack.hasFoil()
			) );
		this.oceanShield.handle.render( poseStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F );
		if( flag ) {
			List< Pair< BannerPattern, DyeColor > > list = BannerBlockEntity.createPatterns( ShieldItem.getColor( itemStack ),
				BannerBlockEntity.getItemPatterns( itemStack )
			);
			BannerRenderer.renderPatterns( poseStack, bufferSource, combinedLight, combinedOverlay, this.oceanShield.plate, material, false, list,
				itemStack.hasFoil()
			);
		} else {
			this.oceanShield.plate.render( poseStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F );
		}

		poseStack.popPose();
	}
}
