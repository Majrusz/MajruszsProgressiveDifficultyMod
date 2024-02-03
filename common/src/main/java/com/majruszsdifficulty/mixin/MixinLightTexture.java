package com.majruszsdifficulty.mixin;

import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.bloodmoon.BloodMoonHelper;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin( LightTexture.class )
public abstract class MixinLightTexture {
	@ModifyVariable(
		at = @At( "STORE" ),
		method = "updateLightTexture (F)V",
		ordinal = 0
	)
	private Vector3f modifyLight( Vector3f light ) {
		float ratio = Mth.lerp( BloodMoonHelper.getColorRatio(), 1.0f, 0.2f );

		return AnyPos.from( light ).mul( 1.0f, ratio, ratio ).vec3f();
	}
}