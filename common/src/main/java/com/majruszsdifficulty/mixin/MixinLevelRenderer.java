package com.majruszsdifficulty.mixin;

import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.events.bloodmoon.BloodMoonHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( LevelRenderer.class )
public abstract class MixinLevelRenderer {
	@Inject(
		at = @At(
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;getMoonPhase ()I",
			value = "INVOKE"
		),
		method = "renderSky (Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V"
	)
	private void changeMoonColor( PoseStack $$0, Matrix4f $$1, float $$2, Camera $$3, boolean $$4, Runnable $$5, CallbackInfo callback ) {
		if( BloodMoonHelper.getColorRatio() > 0.0f ) {
			RenderSystem.setShaderTexture( 0, MajruszsDifficulty.HELPER.getLocation( "textures/environment/blood_moon_phases.png" ) );
		}
	}

	@Inject(
		at = @At(
			target = "Lnet/minecraft/client/renderer/FogRenderer;setupNoFog ()V",
			value = "INVOKE"
		),
		method = "renderSky (Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V"
	)
	private void changeStarsColor( PoseStack $$0, Matrix4f $$1, float $$2, Camera $$3, boolean $$4, Runnable $$5, CallbackInfo callback ) {
		float[] starsColor = RenderSystem.getShaderColor();
		float ratio = Mth.lerp( BloodMoonHelper.getColorRatio(), 1.0f, 0.5f );
		RenderSystem.setShaderColor( starsColor[ 0 ], ratio * starsColor[ 1 ], ratio * starsColor[ 2 ], starsColor[ 3 ] );
	}

	@ModifyVariable(
		at = @At( "STORE" ),
		method = "renderSky (Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
		ordinal = 0
	)
	private Vec3 modifySkyColor( Vec3 color ) {
		float ratio = Mth.lerp( BloodMoonHelper.getColorRatio(), 1.0f, 0.5f );

		return AnyPos.from( color ).mul( 1.0f, ratio, ratio ).vec3();
	}
}
