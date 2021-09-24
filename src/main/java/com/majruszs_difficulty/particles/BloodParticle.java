package com.majruszs_difficulty.particles;

import com.mlib.MajruszLibrary;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class BloodParticle extends TextureSheetParticle {
	private static final double Y_OFFSET = 0.025;

	public BloodParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );
		this.xd = this.xd * 0.0025 + xSpeed;
		this.yd = this.yd * 0.02 + ySpeed;
		this.zd = this.zd * 0.0025 + zSpeed;
		this.quadSize *= 0.25 + MajruszLibrary.RANDOM.nextDouble() * 0.25;

		this.lifetime = ( 20 * 10 + MajruszLibrary.RANDOM.nextInt( 60 ) );
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

	@Override
	public void render( VertexConsumer vertexConsumer, Camera camera, float scaleFactor ) {
		Vec3 vec3 = camera.getPosition();
		float f = ( float )( Mth.lerp( ( double )scaleFactor, this.xo, this.x ) - vec3.x() );
		float f1 = ( float )( Mth.lerp( ( double )scaleFactor, this.yo + Y_OFFSET, this.y + Y_OFFSET ) - vec3.y() );
		float f2 = ( float )( Mth.lerp( ( double )scaleFactor, this.zo, this.z ) - vec3.z() );
		Quaternion quaternion;
		if( this.roll == 0.0F ) {
			quaternion = camera.rotation();
		} else {
			quaternion = new Quaternion( camera.rotation() );
			float f3 = Mth.lerp( scaleFactor, this.oRoll, this.roll );
			quaternion.mul( Vector3f.ZP.rotation( f3 ) );
		}

		Vector3f vector3f1 = new Vector3f( -1.0F, -1.0F, 0.0F );
		vector3f1.transform( quaternion );
		Vector3f[] avector3f = new Vector3f[]{ new Vector3f( -1.0F, -1.0F, 0.0F ), new Vector3f( -1.0F, 1.0F, 0.0F ),
			new Vector3f( 1.0F, 1.0F, 0.0F ), new Vector3f( 1.0F, -1.0F, 0.0F )
		};
		float f4 = this.getQuadSize( scaleFactor );

		for( int i = 0; i < 4; ++i ) {
			Vector3f vector3f = avector3f[ i ];
			vector3f.transform( quaternion );
			vector3f.mul( f4 );
			vector3f.add( f, f1, f2 );
		}

		float f7 = this.getU0();
		float f8 = this.getU1();
		float f5 = this.getV0();
		float f6 = this.getV1();
		int j = this.getLightColor( scaleFactor );
		vertexConsumer.vertex( ( double )avector3f[ 0 ].x(), ( double )avector3f[ 0 ].y(), ( double )avector3f[ 0 ].z() )
			.uv( f8, f6 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
		vertexConsumer.vertex( ( double )avector3f[ 1 ].x(), ( double )avector3f[ 1 ].y(), ( double )avector3f[ 1 ].z() )
			.uv( f8, f5 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
		vertexConsumer.vertex( ( double )avector3f[ 2 ].x(), ( double )avector3f[ 2 ].y(), ( double )avector3f[ 2 ].z() )
			.uv( f7, f5 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
		vertexConsumer.vertex( ( double )avector3f[ 3 ].x(), ( double )avector3f[ 3 ].y(), ( double )avector3f[ 3 ].z() )
			.uv( f7, f6 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
	}

	@Override
	public float getQuadSize( float scaleFactor ) {
		float factor = ( ( float )this.age + scaleFactor ) / ( float )this.lifetime;
		return this.quadSize * ( 1.0F - factor * 0.5F );
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
			float colorFactor = MajruszLibrary.RANDOM.nextFloat() * 0.2f + 0.05f;
			particle.setColor( 1.0f, colorFactor, colorFactor );

			return particle;
		}
	}
}