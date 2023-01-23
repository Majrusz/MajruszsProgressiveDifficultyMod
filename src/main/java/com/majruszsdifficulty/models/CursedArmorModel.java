package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.mlib.animations.Animation;
import com.mlib.animations.Frame;
import com.mlib.animations.InterpolationType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.math.Vector3f;

@OnlyIn( Dist.CLIENT )
public class CursedArmorModel< Type extends CursedArmorEntity > extends HumanoidModel< Type > {
	static final Animation< Vector3f > RIGHT_LEG = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Vector3f > LEFT_LEG = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Vector3f > RIGHT_ARM = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Vector3f > LEFT_ARM = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Vector3f > BODY = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Vector3f > HEAD = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Float > SCALE_1 = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Float > SCALE_2 = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation< Float > SCALE_3 = new Animation<>( CursedArmorEntity.ASSEMBLE_DURATION );

	static {
		RIGHT_LEG.add( 0.00f, new Frame.Vector( 0.0f, 15.0f, 0.0f ) )
			.add( 2.00f, new Frame.Vector( 0.0f, 15.0f, 0.0f ) )
			.add( 3.50f, new Frame.Vector( -4.0f, -3.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 3.75f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.SQUARE_ROOT ) )
			.add( 4.50f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) );

		LEFT_LEG.add( 0.00f, new Frame.Vector( 0.0f, 15.0f, 0.0f ) )
			.add( 2.00f, new Frame.Vector( 0.0f, 15.0f, 0.0f ) )
			.add( 3.50f, new Frame.Vector( 4.0f, -3.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 3.75f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 4.50f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) );

		RIGHT_ARM.add( 0.00f, new Frame.Vector( 4.0f, 27.0f, 0.0f ) )
			.add( 1.50f, new Frame.Vector( 4.0f, 27.0f, 0.0f ) )
			.add( 3.50f, new Frame.Vector( -5.0f, -8.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 3.75f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 4.50f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) );

		LEFT_ARM.add( 0.00f, new Frame.Vector( -4.0f, 27.0f, 0.0f ) )
			.add( 1.50f, new Frame.Vector( -4.0f, 27.0f, 0.0f ) )
			.add( 3.50f, new Frame.Vector( 5.0f, -8.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 3.75f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 4.50f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) );

		BODY.add( 0.00f, new Frame.Vector( 0.0f, 27.0f, 0.0f ) )
			.add( 1.50f, new Frame.Vector( 0.0f, 27.0f, 0.0f ) )
			.add( 3.50f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 3.75f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 4.50f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) );

		HEAD.add( 0.00f, new Frame.Vector( 0.0f, 35.0f, 0.0f ) )
			.add( 1.00f, new Frame.Vector( 0.0f, 35.0f, 0.0f ) )
			.add( 3.50f, new Frame.Vector( 0.0f, -10.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 3.75f, new Frame.Vector( 0.0f, -6.0f, 0.0f, InterpolationType.CUBE_ROOT ) )
			.add( 4.50f, new Frame.Vector( 0.0f, 0.0f, 0.0f, InterpolationType.SQUARE ) );

		SCALE_1.add( 0.00f, new Frame.Value( 0.01f ) )
			.add( 1.00f, new Frame.Value( 0.01f ) )
			.add( 2.00f, new Frame.Value( 1.0f ) )
			.add( 3.50f, new Frame.Value( 1.05f ) )
			.add( 3.75f, new Frame.Value( 1.0f ) )
			.add( 4.50f, new Frame.Value( 1.0f ) );

		SCALE_2.add( 0.00f, new Frame.Value( 0.01f ) )
			.add( 1.50f, new Frame.Value( 0.01f ) )
			.add( 2.50f, new Frame.Value( 1.0f ) )
			.add( 3.50f, new Frame.Value( 1.05f ) )
			.add( 3.75f, new Frame.Value( 1.0f ) )
			.add( 4.50f, new Frame.Value( 1.0f ) );

		SCALE_3.add( 0.00f, new Frame.Value( 0.01f ) )
			.add( 2.00f, new Frame.Value( 0.01f ) )
			.add( 3.00f, new Frame.Value( 1.0f ) )
			.add( 3.50f, new Frame.Value( 1.05f ) )
			.add( 3.75f, new Frame.Value( 1.0f ) )
			.add( 4.50f, new Frame.Value( 1.0f ) );
	}

	public CursedArmorModel( ModelPart root ) {
		super( root );
	}

	@Override
	public void setupAnim( Type cursedArmor, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
		float headPitch
	) {
		Animation.applyPosition( new Vector3f( -1.9f, 0.0f, 0.0f ), this.rightLeg ); // initial values from HumanoidModel because x is not ever updated
		Animation.applyPosition( new Vector3f( 1.9f, 0.0f, 0.0f ), this.leftLeg );
		super.setupAnim( cursedArmor, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch );

		Animation.Applier.setup( cursedArmor.getAssembleTime(), ageInTicks )
			.apply( RIGHT_LEG, Animation::addPosition, this.rightLeg )
			.apply( LEFT_LEG, Animation::addPosition, this.leftLeg )
			.apply( RIGHT_ARM, Animation::addPosition, this.rightArm )
			.apply( LEFT_ARM, Animation::addPosition, this.leftArm )
			.apply( BODY, Animation::addPosition, this.body )
			.apply( HEAD, Animation::addPosition, this.head )
			.apply( SCALE_3, Animation::applyScale, this.rightLeg, this.leftLeg )
			.apply( SCALE_2, Animation::applyScale, this.rightArm, this.leftArm )
			.apply( SCALE_1, Animation::applyScale, this.body, this.head );
	}
}
