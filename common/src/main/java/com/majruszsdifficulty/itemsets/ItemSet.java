package com.majruszsdifficulty.itemsets;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.client.ClientHelper;
import com.majruszlibrary.events.OnItemTooltip;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class ItemSet {
	private List< ItemSetRequirement > requirements = new ArrayList<>();
	private List< ItemSetBonus > bonuses = new ArrayList<>();
	private Component component;
	private ChatFormatting[] chatFormatting;

	public static ItemSet create() {
		return new ItemSet();
	}

	public ItemSet component( String id ) {
		this.component = TextHelper.translatable( id );

		return this;
	}

	public ItemSet format( ChatFormatting... chatFormatting ) {
		this.chatFormatting = chatFormatting;

		return this;
	}

	public ItemSet require( ItemSetRequirement... requirements ) {
		this.requirements = List.of( requirements );

		return this;
	}

	public ItemSet bonus( ItemSetBonus... bonuses ) {
		this.bonuses = List.of( bonuses );

		return this;
	}

	public int getRequirementsSize() {
		return this.requirements.size();
	}

	public ChatFormatting[] getFormatting() {
		return this.chatFormatting;
	}

	public boolean canTrigger( ItemSetBonus bonus, LivingEntity entity ) {
		return bonus.canTrigger( this.findRequirementsMet( entity ) );
	}

	public List< ItemSetRequirement > findRequirementsMet( LivingEntity entity ) {
		return this.requirements.stream().filter( requirement->requirement.check( entity ) ).toList();
	}

	private ItemSet() {
		Side.runOnClient( ()->()->{
			OnItemTooltip.listen( data->Client.addTooltip( data, this ) )
				.addCondition( data->this.requirements.stream().anyMatch( requirement->requirement.is( data.itemStack ) ) );
		} );
	}

	private Component getItemTitleComponent( List< ItemSetRequirement > requirementsMet ) {
		return TextHelper.translatable( "majruszsdifficulty.item_sets.item_title", this.component, requirementsMet.size(), this.requirements.size() )
			.withStyle( ChatFormatting.GRAY );
	}

	private Component getBonusTitleComponent() {
		return TextHelper.translatable( "majruszsdifficulty.item_sets.bonus_title", this.component )
			.withStyle( ChatFormatting.GRAY );
	}

	@OnlyIn( Dist.CLIENT )
	private static class Client {
		private static void addTooltip( OnItemTooltip data, ItemSet itemSet ) {
			if( !ClientHelper.isShiftDown() ) {
				data.components.add( TextHelper.empty() );
				data.components.add( TextHelper.translatable( "majruszsdifficulty.item_sets.hint" ).withStyle( ChatFormatting.GRAY ) );
				return;
			}

			List< ItemSetRequirement > requirementsMet = itemSet.findRequirementsMet( Side.getLocalPlayer() );

			data.components.add( TextHelper.empty() );
			data.components.add( itemSet.getItemTitleComponent( requirementsMet ) );
			data.components.addAll( itemSet.requirements.stream().map( requirement->requirement.toComponent( itemSet, requirementsMet ) ).toList() );
			data.components.add( TextHelper.empty() );
			data.components.add( itemSet.getBonusTitleComponent() );
			data.components.addAll( itemSet.bonuses.stream().map( bonus->bonus.toComponent( itemSet, requirementsMet ) ).toList() );
		}
	}
}
