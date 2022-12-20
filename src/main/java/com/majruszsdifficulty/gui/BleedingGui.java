package com.majruszsdifficulty.gui;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.gamemodifiers.contexts.OnClientTick;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber( value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD )
public class BleedingGui {
	static final Particles PARTICLES = new Particles();

	public static void addBloodOnScreen( int count ) {
		List< Integer > x = IntStream.iterate( 0, i->i + 1 ).limit( Particle.GRID_WIDTH ).boxed().collect( Collectors.toList() );
		List< Integer > y = IntStream.iterate( 0, i->i + 1 ).limit( Particle.GRID_HEIGHT ).boxed().collect( Collectors.toList() );

		Collections.shuffle( x );
		Collections.shuffle( y );

		IntStream.iterate( 0, i->i + 1 )
			.limit( count )
			.forEach( idx->PARTICLES.get().get( x.get( idx ) * Particle.GRID_HEIGHT + y.get( idx ) ).makeVisible() );
	}

	@SubscribeEvent
	public static void registerGui( RegisterGuiOverlaysEvent event ) {
		event.registerBelowAll( "bleeding", new Overlay() );
	}

	static class Overlay implements IGuiOverlay {
		@Override
		public void render( ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight ) {
			RenderSystem.setShader( GameRenderer::getPositionTexShader );
			RenderSystem.enableBlend();
			for( Particle particle : PARTICLES.get() ) {
				if( particle.hasFinished() )
					continue;

				Particle.RenderData renderData = particle.buildRenderData( screenWidth, screenHeight );
				RenderSystem.setShaderColor( 1.0f, 1.0f, 1.0f, particle.getAlpha() );
				RenderSystem.setShaderTexture( 0, renderData.resource );
				GuiComponent.blit( poseStack, renderData.x, renderData.y, 0, 0, renderData.size, renderData.size, renderData.size, renderData.size );
			}
			RenderSystem.disableBlend();
		}
	}

	static class Particles {
		final List< Particle > particles;

		public Particles() {
			this.particles = new ArrayList<>();
			for( int x = 0; x < Particle.GRID_WIDTH; ++x ) {
				for( int y = 0; y < Particle.GRID_HEIGHT; ++y ) {
					this.particles.add( new Particle( x, y ) );
				}
			}

			new OnClientTick.Context( this::updateParticles );
		}

		public List< Particle > get() {
			return this.particles;
		}

		private void updateParticles( OnClientTick.Data data ) {
			this.particles.forEach( Particle::tick );
		}
	}

	static class Particle {
		static final int ASSETS_COUNT = 5;
		static final int GRID_WIDTH = 6, GRID_HEIGHT = 4;
		static final int LIFETIME = 180;
		static final List< ResourceLocation > ASSETS = new ArrayList<>();

		static {
			for( int i = 0; i < ASSETS_COUNT; ++i ) {
				ASSETS.add( Registries.getLocation( String.format( "textures/particle/blood_%d.png", i + 1 ) ) );
			}
		}

		final int x;
		final int y;
		int ticks = LIFETIME;
		int phase = 0;

		public Particle( int x, int y ) {
			this.x = x;
			this.y = y;
		}

		public void makeVisible() {
			if( !this.hasFinished() )
				return;

			this.ticks = Random.nextInt( 0, 40 );
			this.phase = Random.nextInt( 0, ASSETS_COUNT - 1 );
		}

		public RenderData buildRenderData( int width, int height ) {
			float size = height / ( GRID_HEIGHT * 1.5f );
			float x = this.x * size + ( this.x >= GRID_WIDTH / 2 ? width - GRID_WIDTH * size : 0 );
			float y = ( 1.5f * this.y + ( this.x % 2 == 0 ? 0.0f : 0.5f ) ) * size;

			return new RenderData( ( int )x, ( int )y, ( int )size, ASSETS.get( this.phase ) );
		}

		public boolean hasFinished() {
			return this.ticks >= LIFETIME;
		}

		public float getAlpha() {
			return ( float )( 0.7f * Math.sqrt( Mth.clamp( 1.0f - 1.0f * this.ticks / LIFETIME, 0.0f, 1.0f ) ) );
		}

		public void tick() {
			if( Minecraft.getInstance().isPaused() )
				return;

			++this.ticks;
		}

		public record RenderData( int x, int y, int size, ResourceLocation resource ) {}
	}
}
