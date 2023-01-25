package com.majruszsdifficulty.particles;

import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.particles.ConfigurableParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends ConfigurableParticle {
	private final SpriteSet spriteSet;

	public BloodParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		float randomRatio = Random.nextFloat();
		float colorVariation = Mth.lerp( randomRatio, 0.6f, 0.9f );

		this.spriteSet = spriteSet;
		this.xd = this.xd * 0.0025 + Random.nextDouble( -xSpeed, xSpeed );
		this.yd = this.yd * 0.0200 + Random.nextDouble( -ySpeed, ySpeed );
		this.zd = this.zd * 0.0025 + Random.nextDouble( -zSpeed, zSpeed );
		this.lifetime = Utility.secondsToTicks( 38.0 );
		this.age = ( int )( this.lifetime * Mth.lerp( randomRatio,  0.75f, 0.0f ) );
		this.scaleFormula = lifetime->1.5f;
		this.yOffset = Mth.lerp( randomRatio, 0.001f, 0.005f ); // random required to minimize z-fighting

		this.setSpriteFromAge( this.spriteSet );
		this.setColor( colorVariation, colorVariation, colorVariation );
		this.setRenderUpwardsWhenOnGround();
	}

	@Override
	public void tick() {
		super.tick();

		this.setSpriteFromAge( this.spriteSet );
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, BloodParticle::new );
		}
	}
}