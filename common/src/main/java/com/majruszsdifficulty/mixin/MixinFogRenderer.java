package com.majruszsdifficulty.mixin;

import com.majruszsdifficulty.events.bloodmoon.BloodMoonHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( FogRenderer.class )
public abstract class MixinFogRenderer {
	private static @Shadow float fogRed;
	private static @Shadow float fogGreen;
	private static @Shadow float fogBlue;

	@Inject(
		at = @At(
			by = -3,
			shift = At.Shift.BY,
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor (FFFF)V",
			value = "INVOKE"
		),
		method = "setupColor (Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)V"
	)
	private static void setupColor( Camera $$0, float $$1, ClientLevel $$2, int $$3, float $$4, CallbackInfo callback ) {
		float ratio = Mth.lerp( BloodMoonHelper.getColorRatio(), 1.0f, 0.25f );

		fogRed *= 1.0f;
		fogGreen *= ratio;
		fogBlue *= ratio;
	}
}
