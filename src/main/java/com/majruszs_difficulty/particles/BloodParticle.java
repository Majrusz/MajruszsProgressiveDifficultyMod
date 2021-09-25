package com.majruszs_difficulty.particles;

import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import com.mlib.particles.SimpleParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends SimpleParticle {
	private final SpriteSet spriteSet;

	public BloodParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, 0.025 );
		this.spriteSet = spriteSet;
		this.xd = this.xd * 0.0025 + xSpeed;
		this.yd = this.yd * 0.0200 + ySpeed;
		this.zd = this.zd * 0.0025 + zSpeed;

		this.lifetime = TimeConverter.secondsToTicks( 28.0 );
		this.age = MajruszLibrary.RANDOM.nextInt( ( int )( this.lifetime * 0.75 ) );
		setSpriteFromAge( this.spriteSet );

		float colorVariation = MajruszLibrary.RANDOM.nextFloat() * 0.3f + 0.6f;
		setColor( colorVariation, colorVariation, colorVariation );
	}

	@Override
	public void tick() {
		super.tick();
		setSpriteFromAge( this.spriteSet );
	}

	@Override
	public float getQuadSize( float sizeFactor ) {
		return this.quadSize;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, BloodParticle::new );
		}
	}
}