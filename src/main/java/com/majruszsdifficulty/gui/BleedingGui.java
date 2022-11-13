package com.majruszsdifficulty.gui;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.gamemodifiers.contexts.OnClientTick;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber( value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD )
public class BleedingGui {

	public static void addBlood() {

	}

	@SubscribeEvent
	public static void registerGui( RegisterGuiOverlaysEvent event ) {
		event.registerAboveAll( "bleeding", new Overlay() );
	}

	static class Overlay implements IGuiOverlay {
		private static final ResourceLocation FILLED_THIRST = Registries.getLocation( "textures/particle/blood_1.png" );
		final Particles particles = new Particles();

		@Override
		public void render( ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight ) {
			RenderSystem.setShader( GameRenderer::getPositionColorTexShader );
			RenderSystem.setShaderColor( 1.0f, 1.0f, 1.0f, 0.2f );
			for( Particle particle : this.particles.get() ) {
				Particle.RenderData renderData = particle.buildRenderData( screenWidth, screenHeight );
				RenderSystem.setShaderTexture( 0, FILLED_THIRST );
				GuiComponent.blit( poseStack, renderData.x, renderData.y, 0, 0, renderData.size, renderData.size, renderData.size, renderData.size );
			}
		}
	}

	static class Particles {
		final List< Particle > particles;

		public Particles() {
			this.particles = new ArrayList<>();
			for( int i = 0; i < Particle.GRID_WIDTH * Particle.GRID_HEIGHT; ++i ) {
				this.particles.add( new Particle( i % Particle.GRID_WIDTH, i / Particle.GRID_WIDTH ) );
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
		static final int PHASE_COUNT = 7;
		static final int GRID_WIDTH = 6, GRID_HEIGHT = 5;
		final int x;
		final int y;
		int ticks = 0;
		int phase = PHASE_COUNT + 1;

		public Particle( int x, int y ) {
			this.x = x;
			this.y = y;
		}

		public void spawn() {
			this.ticks = Random.nextInt( 1, 10 );
			this.phase = Random.nextInt( 1, 3 );
		}

		public RenderData buildRenderData( int width, int height ) {
			float size = 1.0f * height / GRID_HEIGHT;
			float x = this.x * 1.0f * size + ( this.x >= GRID_WIDTH / 2 ? width - GRID_WIDTH * size : 0 );
			float y = this.y * 1.0f * size;

			return new RenderData( ( int )x, ( int )y, ( int )size );
		}

		public boolean hasFinished() {
			return this.phase > PHASE_COUNT;
		}

		public void tick() {
			++this.ticks;
			if( this.ticks >= 20 ) {
				++this.phase;
				this.ticks = 0;
			}
		}

		public record RenderData( int x, int y, int size ) {}
	}
}
