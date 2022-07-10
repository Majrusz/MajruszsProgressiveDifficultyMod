package com.majruszsdifficulty.lootmodifiers;

import com.google.common.base.Suppliers;
import com.majruszsdifficulty.GameStage;
import com.mlib.Random;
import com.mlib.loot_modifiers.LootHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Gives a chance to double loot from enemies. */
public class DoubleLoot extends LootModifier {
	public static final Supplier< Codec< DoubleLoot > > CODEC = Suppliers.memoize( ()->RecordCodecBuilder.create( inst->codecStart( inst ).apply( inst, DoubleLoot::new ) ) );
	final double normalModeChance = 0.0;
	final double expertModeChance = 0.2;
	final double masterModeChance = 0.4;
	final List< Item > forbiddenItemsToDuplicate = new ArrayList<>();

	public DoubleLoot( LootItemCondition[] conditions ) {
		super( conditions );

		this.forbiddenItemsToDuplicate.add( Items.NETHER_STAR );
		this.forbiddenItemsToDuplicate.add( Items.TOTEM_OF_UNDYING );
	}

	@Nonnull
	@Override
	public ObjectArrayList< ItemStack > doApply( ObjectArrayList< ItemStack > generatedLoot, LootContext context ) {
		double chance = GameStage.getCurrentGameStageDependentValue( this.normalModeChance, this.expertModeChance, this.masterModeChance );

		if( Random.tryChance( chance ) ) {
			Entity entity = LootHelper.getParameter( context, ( LootContextParams.THIS_ENTITY ) );
			if( generatedLoot.size() > 1 && !generatedLoot.get( 0 ).isEmpty() && entity != null ) {
				sendParticles( entity );
				return doubleLoot( generatedLoot );
			}
		}

		return generatedLoot;
	}

	protected void sendParticles( Entity entity ) {
		if( !( entity.level instanceof ServerLevel serverLevel ) )
			return;

		for( int i = 0; i < 8; i++ )
			serverLevel.sendParticles( ParticleTypes.HAPPY_VILLAGER, entity.getX(), entity.getY( 0.5 ), entity.getZ(), 1, 0.5, 0.5, 0.5, 0.5 );
	}

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

	@Override
	public Codec< ? extends IGlobalLootModifier > codec() {
		return CODEC.get();
	}
}