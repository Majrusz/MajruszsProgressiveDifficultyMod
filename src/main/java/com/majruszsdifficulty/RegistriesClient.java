package com.majruszsdifficulty;

import com.majruszsdifficulty.items.EndShardLocatorItem;
import com.majruszsdifficulty.items.SoulJarItem;
import com.majruszsdifficulty.models.*;
import com.majruszsdifficulty.particles.BloodParticle;
import com.majruszsdifficulty.renderers.*;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Deprecated( forRemoval = true )
@OnlyIn( Dist.CLIENT )
@Mod.EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT )
public class RegistriesClient {
	public static void setup() {
		ForgeHooksClient.registerLayerDefinition( CreeperlingRenderer.LAYER, ()->CreeperlingModel.createBodyLayer( CubeDeformation.NONE ) );
		ForgeHooksClient.registerLayerDefinition( TankRenderer.LAYER, ()->TankModel.createBodyLayer( CubeDeformation.NONE ) );
		ForgeHooksClient.registerLayerDefinition( CursedArmorRenderer.INNER_ARMOR_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( LayerDefinitions.INNER_ARMOR_DEFORMATION, 0.0f ), 64, 32 ) );
		ForgeHooksClient.registerLayerDefinition( CursedArmorRenderer.OUTER_ARMOR_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.0f ), 64, 32 ) );
		ForgeHooksClient.registerLayerDefinition( CursedArmorRenderer.MAIN_LAYER, ()->LayerDefinition.create( CursedArmorModel.createMesh( CubeDeformation.NONE, 0.0f ), 64, 64 ) );
		ForgeHooksClient.registerLayerDefinition( CerberusRenderer.LAYER, CerberusModel::createBodyLayer );
		ForgeHooksClient.registerLayerDefinition( GiantRenderer.LAYER, ()->LayerDefinition.create( GiantModel.createMesh( CubeDeformation.NONE, 0.0f ), 64, 64 ) );

		EntityRenderers.register( Registries.CREEPERLING.get(), CreeperlingRenderer::new );
		EntityRenderers.register( Registries.TANK.get(), TankRenderer::new );
		EntityRenderers.register( Registries.CURSED_ARMOR.get(), CursedArmorRenderer::new );
		EntityRenderers.register( Registries.CERBERUS.get(), CerberusRenderer::new );
		EntityRenderers.register( Registries.GIANT.get(), GiantRenderer::new );

		ItemProperties.register( Registries.ENDERIUM_SHARD_LOCATOR.get(), new ResourceLocation( "shard_distance" ), EndShardLocatorItem::calculateDistanceToEndShard );
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void registerParticles( RegisterParticleProvidersEvent event ) {
		event.register( Registries.BLOOD.get(), BloodParticle.Factory::new );
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void registerItemColors( RegisterColorHandlersEvent.Item event ) {
		event.register( new SoulJarItem.ItemColor(), Registries.SOUL_JAR.get() );
	}
}
