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
		this.xd = xSpeed;
		this.yd = ySpeed * 0.5f;
		this.zd = zSpeed;
		this.lifetime = TimeHelper.toTicks( 38.0 );
		this.age = ( int )( this.lifetime * Mth.lerp( randomRatio, 0.75f, 0.0f ) );
		this.scaleFormula = lifetime->1.5f;
		this.yOffset = Mth.lerp( randomRatio, 0.001f, 0.005f ); // random required to minimize z-fighting
		this.onGroundQuaternion = Axis.XP.rotation( Mth.HALF_PI ).rotateZ( ( int )( randomRatio * 4.0f ) * Mth.HALF_PI );
		this.color = Mth.lerp( randomRatio, 0.6f, 0.9f );

		this.setSpriteFromAge( this.spriteSet );
		this.setColor( this.color, this.color, this.color );
	}

	@Override
	public void tick() {
		super.tick();

		float color = this.color * ( 0.7f + 0.3f * ( 1.0f - ( float )this.age / this.lifetime ) );

		this.setSpriteFromAge( this.spriteSet );
		this.setColor( color, color, color );
	}

	@Override
	public float getY( float y ) {
		return y + this.yOffset;
	}

	@Override
	public Quaternionf getQuaternion( Quaternionf quaternion ) {
		return this.onGround ? this.onGroundQuaternion : quaternion;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, BloodParticle::new );
		}
	}
}