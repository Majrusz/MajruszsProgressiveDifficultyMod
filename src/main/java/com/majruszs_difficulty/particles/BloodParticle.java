package com.majruszs_difficulty.particles;

import com.mlib.MajruszLibrary;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends TextureSheetParticle {
	public BloodParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );
		this.xd = this.xd * 0.0025 + xSpeed;
		this.yd = this.yd * 0.02 + ySpeed;
		this.zd = this.zd * 0.0025 + zSpeed;
		this.quadSize *= 0.25 + MajruszLibrary.RANDOM.nextDouble() * 0.25;

		this.lifetime = ( 20 * 10 + MajruszLibrary.RANDOM.nextInt( 60 ) );
	}

	@Override
	public float getQuadSize( float scaleFactor ) {
		float factor = ( ( float )this.age + scaleFactor ) / ( float )this.lifetime;
		return this.quadSize * ( 1.0F - factor * 0.5F );
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if( this.age++ >= this.lifetime )
			this.remove();

		else {
			this.move( this.xd, this.yd, this.zd );
			this.xd *= 0.95;
			this.yd -= 0.0375f;
			this.zd *= 0.95;
			if( this.onGround ) {
				this.xd *= 0.5;
				this.zd *= 0.5;
			}
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory implements ParticleProvider< SimpleParticleType > {
		private final SpriteSet spriteSet;

		public Factory( SpriteSet sprite ) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle( SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed,
			double zSpeed
		) {
			BloodParticle particle = new BloodParticle( world, x, y, z, xSpeed, ySpeed, zSpeed );
			particle.pickSprite( this.spriteSet );
			particle.setColor( 1.0f, 0.2f, 0.2f );

			return particle;
		}
	}
}