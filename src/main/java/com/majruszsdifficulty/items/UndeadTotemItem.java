package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.levels.LevelHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

public class UndeadTotemItem extends Item {
	public UndeadTotemItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}

	@AutoInstance
	public static class Effect extends GameModifier {
		public Effect() {
			super( Registries.Modifiers.DEFAULT, "UndeadTotem", "Totem of Undead Army makes the user respawn with all items." );

			new OnDeath.Context( this::cancelDeath )
				.addCondition( data->this.findTotem( data.target ) != null )
				.addCondition( data->data.target instanceof ServerPlayer );
		}

		private void cancelDeath( OnDeath.Data data ) {
			Totem totem = this.findTotem( data.target );
			data.event.setCanceled( true );
			data.target.setItemInHand( totem.hand, ItemStack.EMPTY );
			EntityHelper.cheatDeath( data.target, 1.0f, true );
			LevelHelper.teleportToSpawnPosition( ( ServerPlayer )data.target );
			EntityHelper.cheatDeath( data.target, 1.0f, true );
		}

		@Nullable
		private Totem findTotem( LivingEntity entity ) {
			if( entity.getItemInHand( InteractionHand.MAIN_HAND ).getItem() instanceof UndeadTotemItem ) {
				return new Totem( entity.getItemInHand( InteractionHand.MAIN_HAND ), InteractionHand.MAIN_HAND );
			} else if( entity.getItemInHand( InteractionHand.OFF_HAND ).getItem() instanceof UndeadTotemItem ) {
				return new Totem( entity.getItemInHand( InteractionHand.OFF_HAND ), InteractionHand.OFF_HAND );
			} else {
				return null;
			}
		}

		private record Totem( ItemStack itemStack, InteractionHand hand ) {}
	}
}
