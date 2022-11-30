package com.majruszsdifficulty.renderers;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.majruszsdifficulty.models.CursedArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class CursedArmorRenderer extends HumanoidMobRenderer< CursedArmorEntity, CursedArmorModel< CursedArmorEntity > > {
	public static final ModelLayerLocation MAIN_LAYER = Registries.getModelLayer( "cursed_armor" );
	public static final ModelLayerLocation INNER_ARMOR_LAYER = Registries.getModelLayer( "cursed_armor", "inner_armor" );
	public static final ModelLayerLocation OUTER_ARMOR_LAYER = Registries.getModelLayer( "cursed_armor", "outer_armor" );
	static final ResourceLocation TEXTURE = Registries.getLocation( "textures/entity/cursed_armor.png" );

	public CursedArmorRenderer( EntityRendererProvider.Context context ) {
		super( context, new CursedArmorModel<>( context.bakeLayer( MAIN_LAYER ) ), 0.5f );
		this.addLayer( new HumanoidArmorLayer<>( this, new CursedArmorModel<>( context.bakeLayer( INNER_ARMOR_LAYER ) ), new CursedArmorModel<>( context.bakeLayer( OUTER_ARMOR_LAYER ) ) ) );
	}

	@Override
	public ResourceLocation getTextureLocation( CursedArmorEntity cursedArmor ) {
		return TEXTURE;
	}
}