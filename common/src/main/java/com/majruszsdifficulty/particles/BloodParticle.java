package com.majruszsdifficulty.particles;

import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.client.CustomParticle;
import com.mlib.math.Random;
import com.mlib.time.TimeHelper;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends CustomParticle {
	private final SpriteSet spriteSet;
	private final float yOffset;
	private final Quaternionf onGroundQuaternion;
	private float color;

	public BloodParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		float randomRatio = Random.nextFloat();

		this.spriteSet = spriteSet;
		this.xd = xSpeed + Random.nextDouble( -0.05f, 0.05f );
		this.yd = ySpeed * 0.5f;
		this.zd = zSpeed + Random.nextDouble( -0.05f, 0.05f );
		this.lifetime = ( int )( TimeHelper.toTicks( 40.0 ) * Mth.lerp( randomRatio, 0.8f, 1.0f ) );
		this.age = ( int )( this.lifetime * Mth.lerp( randomRatio, 0.0f, 0.5f ) );
		this.scaleFormula = lifetime->1.5f;
		this.yOffset = Mth.lerp( randomRatio, 0.001f, 0.005f ); // random required to minimize z-fighting
		this.onGroundQuaternion = Axis.XP.rotation( Mth.HALF_PI ).rotateZ( ( int )( randomRatio * 4.0f ) * Mth.HALF_PI );
		this.color = Mth.lerp( randomRatio, 0.8f, 1.0f );

		this.setSpriteFromAge( this.spriteSet );
		this.updateColor();
	}

	@Override
	public void tick() {
		super.tick();

		this.setSpriteFromAge( this.spriteSet );
		this.updateColor();
	}

	@Override
	public float getY( float y ) {
		return y + this.yOffset;
	}

	@Override
	public Quaternionf getQuaternion( Quaternionf quaternion ) {
		return this.onGround ? this.onGroundQuaternion : quaternion;
	}

	private void updateColor() {
		float color = this.color * ( 0.4f + 0.6f * ( 1.0f - ( float )this.age / this.lifetime ) );

		this.setColor( color, color, color );
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, BloodParticle::new );
		}
	}
}