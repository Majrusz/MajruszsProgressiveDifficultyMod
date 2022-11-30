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
	public static final ModelLayerLocation MAIN_LAYER = new ModelLayerLocation( Registries.getLocation( "cursed_armor" ), "main" );
	public static final ModelLayerLocation INNER_ARMOR = new ModelLayerLocation( Registries.getLocation( "cursed_armor" ), "inner_armor" );
	public static final ModelLayerLocation OUTER_ARMOR = new ModelLayerLocation( Registries.getLocation( "cursed_armor" ), "outer_armor" );
	static final ResourceLocation TEXTURE_LOCATION = Registries.getLocation( "textures/entity/cursed_armor.png" );

	public CursedArmorRenderer( EntityRendererProvider.Context context ) {
		super( context, new CursedArmorModel<>( context.bakeLayer( MAIN_LAYER ) ), 0.5f );
		this.addLayer( new HumanoidArmorLayer<>( this, new CursedArmorModel<>( context.bakeLayer( INNER_ARMOR ) ), new CursedArmorModel<>( context.bakeLayer( OUTER_ARMOR ) ) ) );
	}

	@Override
	public ResourceLocation getTextureLocation( CursedArmorEntity cursedArmor ) {
		return TEXTURE_LOCATION;
	}
}