package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.majruszsdifficulty.renderers.CursedArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn( Dist.CLIENT )
@Mixin( LivingEntityRenderer.class )
public abstract class MixinLivingEntityRenderer< Type extends LivingEntity > {
	@ModifyVariable( method = "render", at = @At( "STORE" ), ordinal = 0 )
	public VertexConsumer render( VertexConsumer consumer, Type entity, float p_114209_, float partialTicks, PoseStack poseStack,
		MultiBufferSource bufferSource, int packedLight
	) {
		if( !( entity instanceof CursedArmorEntity ) )
			return consumer;

		return VertexMultiConsumer.create( bufferSource.getBuffer( RenderType.entityNoOutline( CursedArmorRenderer.TEXTURE ) ) );
	}
}
