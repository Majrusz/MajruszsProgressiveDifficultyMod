package com.majruszsdifficulty.loot_modifiers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.majruszsdifficulty.GameState;
import com.mlib.Random;
import com.mlib.loot_modifiers.LootHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
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

	public DoubleLoot( LootItemCondition[] conditions, double normalChance, double expertChance, double masterChance, List< Item > forbiddenItems ) {
		super( conditions );

		this.normalModeChance = normalChance;
		this.expertModeChance = expertChance;
		this.masterModeChance = masterChance;
		this.forbiddenItemsToDuplicate = forbiddenItems;
	}

	@Nonnull
	@Override
	public ObjectArrayList< ItemStack > doApply( ObjectArrayList< ItemStack > generatedLoot, LootContext context ) {
		double chance = GameState.getCurrentGameStateValue( this.normalModeChance, this.expertModeChance, this.masterModeChance );

		if( Random.tryChance( chance ) ) {
			Entity entity = LootHelper.getParameter( context, ( LootContextParams.THIS_ENTITY ) );
			if( generatedLoot.size() > 0 && entity != null )
				sendParticles( entity );

			return doubleLoot( generatedLoot );
		}

		return generatedLoot;
	}

	protected void sendParticles( Entity entity ) {
		if( !( entity.level instanceof ServerLevel serverLevel ) )
			return;

		for( int i = 0; i < 8; i++ )
			serverLevel.sendParticles( ParticleTypes.HAPPY_VILLAGER, entity.getX(), entity.getY( 0.5 ), entity.getZ(), 1, 0.5, 0.5, 0.5, 0.5 );
	}

	/** Doubles given generated loot. Does not duplicate items from forbidden items list. */
	protected ObjectArrayList< ItemStack > doubleLoot( ObjectArrayList< ItemStack > generatedLoot ) {
		ObjectArrayList< ItemStack > doubledLoot = new ObjectArrayList<>();
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
		public DoubleLoot read( ResourceLocation name, JsonObject object, LootItemCondition[] conditions ) {
			double normalModeChance = GsonHelper.getAsFloat( object, "normal_chance" );
			double expertModeChance = GsonHelper.getAsFloat( object, "expert_chance" );
			double masterModeChance = GsonHelper.getAsFloat( object, "master_chance" );
			JsonArray items = GsonHelper.getAsJsonArray( object, "forbidden_items" );

			List< Item > forbiddenItemsToDuplicate = new ArrayList<>();
			for( int i = 0; i < items.size(); i++ ) {
				JsonObject item = items.get( i ).getAsJsonObject();

				forbiddenItemsToDuplicate.add( ForgeRegistries.ITEMS.getValue( new ResourceLocation( item.get( "name" ).getAsString() ) ) );
			}

			return new DoubleLoot( conditions, normalModeChance, expertModeChance, masterModeChance, forbiddenItemsToDuplicate );
		}

		@Override
		public JsonObject write( DoubleLoot instance ) {
			return null;
		}
	}
}