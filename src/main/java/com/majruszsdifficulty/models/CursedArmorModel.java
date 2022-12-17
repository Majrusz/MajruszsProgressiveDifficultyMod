package com.majruszsdifficulty.models;

import com.majruszsdifficulty.entities.CursedArmorEntity;
import com.mlib.MajruszLibrary;
import com.mlib.Utility;
import com.mlib.animations.Animation;
import com.mlib.animations.Frame;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn( Dist.CLIENT )
public class CursedArmorModel< Type extends CursedArmorEntity > extends HumanoidModel< Type > {
	static final Animation.Vector RIGHT_LEG = new Animation.Vector( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Vector LEFT_LEG = new Animation.Vector( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Vector RIGHT_ARM = new Animation.Vector( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Vector LEFT_ARM = new Animation.Vector( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Vector BODY = new Animation.Vector( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Vector HEAD = new Animation.Vector( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Float SCALE_1 = new Animation.Float( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Float SCALE_2 = new Animation.Float( CursedArmorEntity.ASSEMBLE_DURATION );
	static final Animation.Float SCALE_3 = new Animation.Float( CursedArmorEntity.ASSEMBLE_DURATION );

	static {
		RIGHT_LEG.addNewVectorFrame( 0.0f, new Vector3f( 0.0f, 15.0f, 0.0f ) )
			.addNewVectorFrame( 2.0f, new Vector3f( 0.0f, 15.0f, 0.0f ) )
			.addNewVectorFrame( 3.5f, new Vector3f( -4.0f, -3.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 3.75f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 4.5f, new Vector3f( 0.0f, 0.0f, 0.0f ), Frame.InterpolationType.SQUARE );

		LEFT_LEG.addNewVectorFrame( 0.0f, new Vector3f( 0.0f, 15.0f, 0.0f ) )
			.addNewVectorFrame( 2.0f, new Vector3f( 0.0f, 15.0f, 0.0f ) )
			.addNewVectorFrame( 3.5f, new Vector3f( 4.0f, -3.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 3.75f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 4.5f, new Vector3f( 0.0f, 0.0f, 0.0f ), Frame.InterpolationType.SQUARE );

		RIGHT_ARM.addNewVectorFrame( 0.0f, new Vector3f( 4.0f, 27.0f, 0.0f ) )
			.addNewVectorFrame( 1.5f, new Vector3f( 4.0f, 27.0f, 0.0f ) )
			.addNewVectorFrame( 3.5f, new Vector3f( -6.0f, -8.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 3.75f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 4.5f, new Vector3f( 0.0f, 0.0f, 0.0f ), Frame.InterpolationType.SQUARE );

		LEFT_ARM.addNewVectorFrame( 0.0f, new Vector3f( -4.0f, 27.0f, 0.0f ) )
			.addNewVectorFrame( 1.5f, new Vector3f( -4.0f, 27.0f, 0.0f ) )
			.addNewVectorFrame( 3.5f, new Vector3f( 6.0f, -8.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 3.75f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 4.5f, new Vector3f( 0.0f, 0.0f, 0.0f ), Frame.InterpolationType.SQUARE );

		BODY.addNewVectorFrame( 0.0f, new Vector3f( 0.0f, 27.0f, 0.0f ) )
			.addNewVectorFrame( 1.5f, new Vector3f( 0.0f, 27.0f, 0.0f ) )
			.addNewVectorFrame( 3.5f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 3.75f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 4.5f, new Vector3f( 0.0f, 0.0f, 0.0f ), Frame.InterpolationType.SQUARE );

		HEAD.addNewVectorFrame( 0.0f, new Vector3f( 0.0f, 35.0f, 0.0f ) )
			.addNewVectorFrame( 1.0f, new Vector3f( 0.0f, 35.0f, 0.0f ) )
			.addNewVectorFrame( 3.5f, new Vector3f( 0.0f, -10.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 3.75f, new Vector3f( 0.0f, -6.0f, 0.0f ), Frame.InterpolationType.SQUARE_ROOT )
			.addNewVectorFrame( 4.5f, new Vector3f( 0.0f, 0.0f, 0.0f ), Frame.InterpolationType.SQUARE );

		SCALE_1.addNewFloatFrame( 0.0f, 0.01f )
			.addNewFloatFrame( 1.0f, 0.01f )
			.addNewFloatFrame( 2.0f, 1.0f )
			.addNewFloatFrame( 3.5f, 1.1f )
			.addNewFloatFrame( 3.75f, 1.0f )
			.addNewFloatFrame( 4.5f, 1.0f );

		SCALE_2.addNewFloatFrame( 0.0f, 0.01f )
			.addNewFloatFrame( 1.5f, 0.01f )
			.addNewFloatFrame( 2.5f, 1.0f )
			.addNewFloatFrame( 3.5f, 1.1f )
			.addNewFloatFrame( 3.75f, 1.0f )
			.addNewFloatFrame( 4.5f, 1.0f );

		SCALE_3.addNewFloatFrame( 0.0f, 0.01f )
			.addNewFloatFrame( 2.0f, 0.01f )
			.addNewFloatFrame( 3.0f, 1.0f )
			.addNewFloatFrame( 3.5f, 1.1f )
			.addNewFloatFrame( 3.75f, 1.0f )
			.addNewFloatFrame( 4.5f, 1.0f );
	}

	public CursedArmorModel( ModelPart root ) {
		super( root );
	}

	@Override
	public void setupAnim( Type cursedArmor, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
		float headPitch
	) {
		Animation.applyPosition( new Vector3f( -1.9f, 0.0f, 0.0f ), this.rightLeg ); // initial values from HumanoidModel
		Animation.applyPosition( new Vector3f( 1.9f, 0.0f, 0.0f ), this.leftLeg );
		super.setupAnim( cursedArmor, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch );

		float assembleTime = cursedArmor.getAssembleTime();
		Animation.addPosition( RIGHT_LEG.apply( assembleTime, ageInTicks ), this.rightLeg );
		Animation.addPosition( LEFT_LEG.apply( assembleTime, ageInTicks ), this.leftLeg );
		Animation.addPosition( RIGHT_ARM.apply( assembleTime, ageInTicks ), this.rightArm );
		Animation.addPosition( LEFT_ARM.apply( assembleTime, ageInTicks ), this.leftArm );
		Animation.addPosition( BODY.apply( assembleTime, ageInTicks ), this.body );
		Animation.addPosition( HEAD.apply( assembleTime, ageInTicks ), this.head );

		float scale3 = SCALE_3.apply( assembleTime, ageInTicks );
		Animation.applyScale( scale3, this.rightLeg );
		Animation.applyScale( scale3, this.leftLeg );

		float scale2 = SCALE_2.apply( assembleTime, ageInTicks );
		Animation.applyScale( scale2, this.rightArm );
		Animation.applyScale( scale2, this.leftArm );

		float scale1 = SCALE_1.apply( assembleTime, ageInTicks );
		Animation.applyScale( scale1, this.body );
		Animation.applyScale( scale1, this.head );
	}
}
