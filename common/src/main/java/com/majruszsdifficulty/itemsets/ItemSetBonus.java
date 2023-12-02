package com.majruszsdifficulty.itemsets;

import com.majruszlibrary.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ItemSetBonus {
	private final Predicate< List< ItemSetRequirement > > predicate;
	private final Function< ItemSet, Component > requirementComponent;
	private String id;
	private Object[] args;

	public static ItemSetBonus any( int count ) {
		return new ItemSetBonus( requirements->requirements.size() >= count, itemSet->TextHelper.literal( "%1$s/%2$s", count, itemSet.getRequirementsSize() ) );
	}

	public static ItemSetBonus requires( ItemSetRequirement requirement ) {
		return new ItemSetBonus( requirements->requirements.contains( requirement ), itemSet->requirement.getComponent() );
	}

	public ItemSetBonus component( String id, Object... args ) {
		this.id = id;
		this.args = args;

		return this;
	}

	public boolean canTrigger( List< ItemSetRequirement > requirementsMet ) {
		return this.predicate.test( requirementsMet );
	}

	public Component toComponent( ItemSet itemSet, List< ItemSetRequirement > requirementsMet ) {
		boolean canTrigger = this.canTrigger( requirementsMet );
		ChatFormatting[] formatting = canTrigger ? itemSet.getFormatting() : new ChatFormatting[]{ ChatFormatting.DARK_GRAY };
		Object[] args = Arrays.stream( this.args )
			.map( arg->( arg instanceof MutableComponent component ? component : TextHelper.literal( arg.toString() ) ).withStyle( formatting ) )
			.toArray();
		Component bonus = TextHelper.translatable( this.id, args ).withStyle( canTrigger ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY );

		return TextHelper.translatable( "majruszsdifficulty.item_sets.bonus", this.requirementComponent.apply( itemSet ), bonus )
			.withStyle( canTrigger ? itemSet.getFormatting() : new ChatFormatting[]{ ChatFormatting.DARK_GRAY } );
	}

	private ItemSetBonus( Predicate< List< ItemSetRequirement > > predicate, Function< ItemSet, Component > requirementComponent ) {
		this.predicate = predicate;
		this.requirementComponent = requirementComponent;
	}
}
