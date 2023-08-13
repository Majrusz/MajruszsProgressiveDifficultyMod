package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class MobInfo extends SerializableStructure {
	public EntityType< ? > type;
	public ResourceLocation equipment;
	public BlockPos position;
	public boolean isBoss = false;
	public UUID uuid = null;

	public MobInfo() {
		this.defineEntityType( "type", ()->this.type, x->this.type = x );
		this.defineLocation( "equipment", ()->this.equipment, x->this.equipment = x );
		this.defineBlockPos( "position", ()->this.position, x->this.position = x );
		this.defineBoolean( "is_boss", ()->this.isBoss, x->this.isBoss = x );
		this.defineUUID( "uuid", ()->this.uuid, x->this.uuid = x );
	}

	public MobInfo( MobDef def, BlockPos position, boolean isBoss ) {
		this();

		this.type = def.type;
		this.equipment = def.equipment;
		this.position = position;
		this.isBoss = isBoss;
	}

	@Nullable
	public Entity toEntity( ServerLevel level ) {
		return this.uuid != null ? level.getEntity( this.uuid ) : null;
	}

	public float getHealth( ServerLevel level ) {
		return this.toEntity( level ) instanceof LivingEntity entity ? entity.getHealth() : 0.0f;
	}

	public float getMaxHealth( ServerLevel level ) {
		return this.toEntity( level ) instanceof LivingEntity entity ? entity.getMaxHealth() : 0.0f;
	}
}