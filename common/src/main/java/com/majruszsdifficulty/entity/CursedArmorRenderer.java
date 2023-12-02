package com.majruszsdifficulty.entity;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn( Dist.CLIENT )
public class CursedArmorRenderer extends HumanoidMobRenderer< CursedArmor, CursedArmorModel< CursedArmor > > {
	public static final ModelLayerLocation LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cursed_armor" );
	public static final ModelLayerLocation INNER_ARMOR_LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cursed_armor", "inner_armor" );
	public static final ModelLayerLocation OUTER_ARMOR_LAYER = MajruszsDifficulty.HELPER.getLayerLocation( "cursed_armor", "outer_armor" );
	public static final ResourceLocation TEXTURE = MajruszsDifficulty.HELPER.getLocation( "textures/entity/cursed_armor.png" );

	public CursedArmorRenderer( EntityRendererProvider.Context context ) {
		super( context, new CursedArmorModel<>( context.bakeLayer( LAYER ) ), 0.0f );

		this.addLayer( new HumanoidArmorLayer<>( this, new CursedArmorModel<>( context.bakeLayer( INNER_ARMOR_LAYER ) ), new CursedArmorModel<>( context.bakeLayer( OUTER_ARMOR_LAYER ) ), context.getModelManager() ) );
	}

	@Override
	public ResourceLocation getTextureLocation( CursedArmor cursedArmor ) {
		return TEXTURE;
	}
}
