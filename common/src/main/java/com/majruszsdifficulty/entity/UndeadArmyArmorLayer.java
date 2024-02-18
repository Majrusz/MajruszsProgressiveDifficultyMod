package com.majruszsdifficulty.entity;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@OnlyIn( Dist.CLIENT )
public class UndeadArmyArmorLayer< EntityType extends LivingEntity, ModelType extends EntityModel< EntityType > > extends RenderLayer< EntityType, ModelType > {
	private final ModelType model;
	private final ResourceLocation id;

	public UndeadArmyArmorLayer( RenderLayerParent< EntityType, ModelType > parent, ModelType model, String id ) {
		super( parent );

		this.model = model;
		this.id = MajruszsDifficulty.HELPER.getLocation( id );
	}

	@Override
	public void render( PoseStack poseStack, MultiBufferSource bufferSource, int $$2, EntityType entity, float $$4, float $$5, float $$6, float $$7, float $$8,
		float $$9
	) {
		if( !UndeadArmyHelper.isPartOfUndeadArmy( entity ) ) {
			return;
		}

		this.getParentModel().copyPropertiesTo( this.model );
		this.model.renderToBuffer( poseStack, bufferSource.getBuffer( RenderType.armorCutoutNoCull( this.id ) ), $$2, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0F );
	}
}
