package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.modhelper.Resource;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

@OnlyIn( Dist.CLIENT )
public class CerberusModel< Type extends Cerberus > extends HierarchicalModel< Type > {
	public static Resource< ModelDef > MODEL = MajruszsDifficulty.HELPER.loadClient( "cerberus_model", ModelDef.class );
	public final ModelParts modelParts;
	public final ModelPart body, necks, neck1, neck3, jawUpper1, jawUpper2, jawUpper3, jawLower1, jawLower2, jawLower3, frontLeg1, frontLeg2, hindLeg1, hindLeg2;

	public CerberusModel( ModelPart modelPart ) {
		this.modelParts = new ModelParts( modelPart, MODEL.get() );
		this.body = this.modelParts.get( "body" );
		this.necks = this.modelParts.get( "necks" );
		this.neck1 = this.modelParts.get( "neck1" );
		this.neck3 = this.modelParts.get( "neck3" );
		this.jawUpper1 = this.modelParts.get( "jawUpper1" );
		this.jawUpper2 = this.modelParts.get( "jawUpper2" );
		this.jawUpper3 = this.modelParts.get( "jawUpper3" );
		this.jawLower1 = this.modelParts.get( "jawLower1" );
		this.jawLower2 = this.modelParts.get( "jawLower2" );
		this.jawLower3 = this.modelParts.get( "jawLower3" );
		this.frontLeg1 = this.modelParts.get( "frontLeg1" );
		this.frontLeg2 = this.modelParts.get( "frontLeg2" );
		this.hindLeg1 = this.modelParts.get( "hindLeg1" );
		this.hindLeg2 = this.modelParts.get( "hindLeg2" );
	}

	@Override
	public void setupAnim( Type cerberus, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		this.modelParts.reset();

		// head rotation when looking around
		this.necks.yRot += Math.toRadians( netHeadYaw );
		this.necks.xRot += Math.toRadians( headPitch ) + 0.0873f;

		// head rotation for side heads to follow the main one
		this.neck1.yRot += Math.toRadians( netHeadYaw * ( netHeadYaw < 0.0f ? 0.255f : -0.075f ) + 2.0f * Math.sin( 0.1f * ageInTicks ) );
		this.neck3.yRot += Math.toRadians( netHeadYaw * ( netHeadYaw > 0.0f ? 0.225f : -0.075f ) - 2.0f * Math.cos( 0.1f * ageInTicks ) );

		// jaw rotation dependent on players distance
		float jawRotation = ( float )Math.toRadians( -1.0f * Math.sin( ageInTicks / 10.0f ) - 15.0f * Mth.clamp( 1.20f - this.getPlayerDistance( cerberus ) / 5.0f, 0.0f, 1.0f ) );
		this.jawUpper1.xRot = this.jawUpper2.xRot = this.jawUpper3.xRot = jawRotation;
		this.jawLower1.xRot = this.jawLower2.xRot = this.jawLower3.xRot = jawRotation;

		// movement anims
		float limbFactor1 = 0.5f * limbSwing, limbFactor2 = 0.6f * limbSwingAmount;
		this.frontLeg1.xRot = this.hindLeg1.xRot = ( float )( Math.cos( limbFactor1 ) * limbFactor2 );
		this.frontLeg2.xRot = this.hindLeg2.xRot = ( float )( Math.cos( limbFactor1 + Math.PI ) * limbFactor2 );
		this.body.y += Math.abs( ( float )Math.cos( limbFactor1 ) * limbSwingAmount );

		cerberus.getAnimations().forEach( animation->animation.apply( this.modelParts, ageInTicks ) );
	}

	@Override
	public ModelPart root() {
		return this.modelParts.getRoot();
	}

	private float getPlayerDistance( Type cerberus ) {
		LocalPlayer player = Side.getMinecraft().player;

		return player != null && !player.isSpectator() ? AnyPos.from( player.position() ).dist( cerberus.position() ).floatValue() : Float.MAX_VALUE;
	}
}

