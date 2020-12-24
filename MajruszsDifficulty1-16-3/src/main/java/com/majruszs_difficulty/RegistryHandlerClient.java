package com.majruszs_difficulty;

import com.majruszs_difficulty.entities.GiantEntity;
import com.majruszs_difficulty.entities.PillagerWolfEntity;
import com.majruszs_difficulty.renderers.GiantRenderer;
import com.majruszs_difficulty.renderers.PillagerWolfRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler( GiantEntity.type, GiantRenderer::new );
		RenderingRegistry.registerEntityRenderingHandler( PillagerWolfEntity.type, PillagerWolfRenderer::new );
	}
}
