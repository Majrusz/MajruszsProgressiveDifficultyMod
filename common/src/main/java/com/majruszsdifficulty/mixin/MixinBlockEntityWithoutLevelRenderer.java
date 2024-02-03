package com.majruszsdifficulty.mixin;

import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.SoulJar;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin( BlockEntityWithoutLevelRenderer.class )
public abstract class MixinBlockEntityWithoutLevelRenderer {
	private @Shadow ShieldModel shieldModel;

	@Inject(
		at = @At(
			ordinal = 0,
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose ()V"
		),
		method = "renderByItem (Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
		require = 0 // compatibility with Optifine
	)
	private void renderByItem( ItemStack itemStack, ItemTransforms.TransformType context, PoseStack poseStack, MultiBufferSource multiBufferSource, int x, int y,
		CallbackInfo callback
	) {
		SoulJar.BonusInfo bonusInfo = SoulJar.BonusInfo.read( itemStack );
		if( !bonusInfo.hasBonuses() ) {
			return;
		}

		List< SoulJar.BonusType > bonusTypes = bonusInfo.getBonusTypes();
		for( int idx = 0; idx < bonusTypes.size(); ++idx ) {
			SoulJar.BonusType bonusType = bonusTypes.get( idx );
			ResourceLocation texture = MajruszsDifficulty.HELPER.getLocation( "textures/entity/shield_soul_jar_overlay_%d.png".formatted( idx + 1 ) );
			VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect( multiBufferSource, RenderType.entityTranslucent( texture ), false, itemStack.hasFoil() );
			float red = ( bonusType.getColor() >> 16 ) & 0xff;
			float green = ( bonusType.getColor() >> 8 ) & 0xff;
			float blue = bonusType.getColor() & 0xff;
			float alpha = ( float )Math.max( Math.cos( Math.PI * ( TimeHelper.getClientTime() - 0.6667f * idx ) ), 0.0f );

			this.shieldModel.renderToBuffer( poseStack, vertexConsumer, x, y, red / 255.0f, green / 255.0f, blue / 255.0f, alpha );
		}
	}
}
