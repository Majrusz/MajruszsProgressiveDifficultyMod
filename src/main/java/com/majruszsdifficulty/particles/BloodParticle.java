package com.majruszsdifficulty.particles;

import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.particles.SimpleParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends SimpleParticle {
	private final SpriteSet spriteSet;

	public BloodParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		float randomRatio = Random.nextFloat();
		float colorVariation = Mth.lerp( randomRatio, 0.6f, 0.9f );

		this.spriteSet = spriteSet;
		this.xd = this.xd * 0.0025 + xSpeed;
		this.yd = this.yd * 0.0200 + ySpeed;
		this.zd = this.zd * 0.0025 + zSpeed;
		this.lifetime = Utility.secondsToTicks( 38.0 );
		this.age = Random.nextInt( ( int )( this.lifetime * 0.75 ) );
		this.yOffset = Mth.lerp( randomRatio, 0.01, 0.02 ); // random required to minimize z-fighting
		this.renderUpwardsWhenOnGround = true;
		this.setSpriteFromAge( this.spriteSet );
		this.setColor( colorVariation, colorVariation, colorVariation );
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge( this.spriteSet );
	}

	@Override
	public float getQuadSize( float sizeFactor ) {
		return this.quadSize * 1.25f;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, BloodParticle::new );
		}
	}
}