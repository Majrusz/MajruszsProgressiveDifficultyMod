package com.majruszs_difficulty.loot_modifiers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** Gives a chance to double loot from enemies. */
public class DoubleLoot extends LootModifier {
	private final double normalModeChance, expertModeChance, masterModeChance;
	private final List< Item > forbiddenItemsToDuplicate;

	public DoubleLoot( ILootCondition[] conditions, double normalChance, double expertChance, double masterChance, List< Item > forbiddenItems ) {
		super( conditions );

		this.normalModeChance = normalChance;
		this.expertModeChance = expertChance;
		this.masterModeChance = masterChance;
		this.forbiddenItemsToDuplicate = forbiddenItems;
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		double chance = GameState.getValueDependingOnGameState( this.normalModeChance, this.expertModeChance, this.masterModeChance );

		if( MajruszsHelper.tryChance( chance ) ) {
			Entity entity = context.get( LootParameters.THIS_ENTITY );
			if( generatedLoot.size() > 0 && entity != null )
				spawnParticles( entity );

			return doubleLoot( generatedLoot );
		}

		return generatedLoot;
	}

	/** Spawning particles to let the player know that the loot was doubled. */
	protected void spawnParticles( Entity entity ) {
		if( !( entity.world instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )entity.world;
		for( int i = 0; i < 8; i++ )
			world.spawnParticle( ParticleTypes.HAPPY_VILLAGER, entity.getPosX(), entity.getPosYHeight( 0.5 ), entity.getPosZ(), 1, 0.5, 0.5, 0.5,
				0.5
			);
	}

	/** Doubles given generated loot. Does not duplicate items from forbidden items list. */
	protected List< ItemStack > doubleLoot( List< ItemStack > generatedLoot ) {
		List< ItemStack > doubledLoot = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			boolean isItemForbidden = isForbidden( itemStack.getItem() );

			for( int i = 0; i < 2 && ( i < 1 || !isItemForbidden ); i++ )
				doubledLoot.add( itemStack );
		}
		return doubledLoot;
	}

	/** Check if item is forbidden. */
	protected boolean isForbidden( Item item ) {
		for( Item forbidden : this.forbiddenItemsToDuplicate )
			if( forbidden.equals( item ) )
				return true;

		return false;
	}

	public static class Serializer extends GlobalLootModifierSerializer< DoubleLoot > {
		@Override
		public DoubleLoot read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
			double normalModeChance = JSONUtils.getFloat( object, "normal_chance" );
			double expertModeChance = JSONUtils.getFloat( object, "expert_chance" );
			double masterModeChance = JSONUtils.getFloat( object, "master_chance" );
			JsonArray items = JSONUtils.getJsonArray( object, "forbidden_items" );

			List< Item > forbiddenItemsToDuplicate = new ArrayList<>();
			for( int i = 0; i < items.size(); i++ ) {
				JsonObject item = items.get( i )
					.getAsJsonObject();

				forbiddenItemsToDuplicate.add( ForgeRegistries.ITEMS.getValue( new ResourceLocation( item.get( "name" )
					.getAsString() ) ) );
			}

			return new DoubleLoot( conditions, normalModeChance, expertModeChance, masterModeChance, forbiddenItemsToDuplicate );
		}

		@Override
		public JsonObject write( DoubleLoot instance ) {
			return null;
		}
	}
}