package com.majruszsdifficulty.entity;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn( Dist.CLIENT )
public class TankRenderer extends MobRenderer< Tank, TankModel< Tank > > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "tank" );
	public static final ModelLayerLocation ARMOR_LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "tank", "armor" );
	public static final ResourceLocation TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/tank.png" );

	public TankRenderer( EntityRendererProvider.Context context ) {
		super( context, new TankModel<>( context.bakeLayer( LAYER ) ), 0.5f );

		this.addLayer( new UndeadArmyArmorLayer<>( this, new TankModel<>( context.bakeLayer( ARMOR_LAYER ) ), "textures/entity/tank_undead_army_armor.png" ) );
	}

	@Override
	public ResourceLocation getTextureLocation( Tank tank ) {
		return TEXTURE;
	}

	@Override
	public void render( Tank tank, float p_114209_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight ) {
		this.model.prepareMobModel( tank, 0.0f, 0.0f, partialTicks );

		super.render( tank, p_114209_, partialTicks, poseStack, bufferSource, packedLight );
	}
}
