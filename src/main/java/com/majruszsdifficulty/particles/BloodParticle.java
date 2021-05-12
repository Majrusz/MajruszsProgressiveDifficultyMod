package com.majruszsdifficulty.particles;

import com.mlib.MajruszLibrary;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends SpriteTexturedParticle {
	public BloodParticle( ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( world, x, y, z, xSpeed, ySpeed, zSpeed );
		this.motionX = this.motionX * 0.0025 + xSpeed;
		this.motionY = this.motionY * 0.02 + ySpeed;
		this.motionZ = this.motionZ * 0.0025 + zSpeed;
		this.particleScale *= 0.25 + MajruszLibrary.RANDOM.nextDouble() * 0.25;

		this.maxAge = ( 20 * 10 + MajruszLibrary.RANDOM.nextInt( 60 ) );
	}

	@Override
	public float getScale( float scaleFactor ) {
		float factor = ( ( float )this.age + scaleFactor ) / ( float )this.maxAge;
		return this.particleScale * ( 1.0F - factor * 0.5F );
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if( this.age++ >= this.maxAge )
			this.setExpired();

		else {
			this.move( this.motionX, this.motionY, this.motionZ );
			this.motionX *= 0.95;
			this.motionY -= 0.0375f;
			this.motionZ *= 0.95;
			if( this.onGround ) {
				this.motionX *= 0.5;
				this.motionZ *= 0.5;
			}
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory implements IParticleFactory< BasicParticleType > {
		private final IAnimatedSprite spriteSet;

		public Factory( IAnimatedSprite sprite ) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle makeParticle( BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed,
			double zSpeed
		) {
			BloodParticle particle = new BloodParticle( world, x, y, z, xSpeed, ySpeed, zSpeed );
			particle.selectSpriteRandomly( this.spriteSet );
			particle.setColor( 1.0f, 0.2f, 0.2f );

			return particle;
		}
	}
}