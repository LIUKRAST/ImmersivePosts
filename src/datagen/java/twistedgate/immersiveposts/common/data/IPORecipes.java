package twistedgate.immersiveposts.common.data;

import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import twistedgate.immersiveposts.IPOMod;
import twistedgate.immersiveposts.common.IPOContent.Blocks.Fences;
import twistedgate.immersiveposts.common.IPOContent.Items;
import twistedgate.immersiveposts.common.IPOTags;
import twistedgate.immersiveposts.common.crafting.IPOConfigConditionSerializer.IPOConfigCondition;

/**
 * @author TwistedGate
 */
public class IPORecipes extends RecipeProvider{
	private Consumer<IFinishedRecipe> out;
	public IPORecipes(DataGenerator generatorIn){
		super(generatorIn);
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> out){
		this.out=out;
		
		ShapedRecipeBuilder.shapedRecipe(twistedgate.immersiveposts.common.IPOContent.Blocks.POST_BASE.get(), 6)
			.key('w', Tags.Items.COBBLESTONE)
			.key('s', Blocks.STONE_BRICKS)
			.patternLine("s s")
			.patternLine("sws")
			.patternLine("sws")
			.addCriterion("has_cobblestone", hasItem(Blocks.COBBLESTONE))
			.addCriterion("has_stone_bricks", hasItem(ItemTags.STONE_BRICKS))
			.build(out);
		
		fenceAndStickRecipe(Fences.IRON.get(), null, IPOTags.Rods.IRON, IPOTags.Ingots.IRON);
		fenceAndStickRecipe(Fences.GOLD.get(), Items.ROD_GOLD.get(), IPOTags.Rods.GOLD, IPOTags.Ingots.GOLD);
		fenceAndStickRecipe(Fences.COPPER.get(), Items.ROD_COPPER.get(), IPOTags.Rods.COPPER, IPOTags.Ingots.COPPER);
		fenceAndStickRecipe(Fences.LEAD.get(), Items.ROD_LEAD.get(), IPOTags.Rods.LEAD, IPOTags.Ingots.LEAD);
		fenceAndStickRecipe(Fences.SILVER.get(), Items.ROD_SILVER.get(), IPOTags.Rods.SILVER, IPOTags.Ingots.SILVER);
		fenceAndStickRecipe(Fences.NICKEL.get(), Items.ROD_NICKEL.get(), IPOTags.Rods.NICKEL, IPOTags.Ingots.NICKEL);
		fenceAndStickRecipe(Fences.CONSTANTAN.get(), Items.ROD_CONSTANTAN.get(), IPOTags.Rods.CONSTANTAN, IPOTags.Ingots.CONSTANTAN);
		fenceAndStickRecipe(Fences.ELECTRUM.get(), Items.ROD_ELECTRUM.get(), IPOTags.Rods.ELECTRUM, IPOTags.Ingots.ELECTRUM);
		fenceAndStickRecipe(Fences.URANIUM.get(), Items.ROD_URANIUM.get(), IPOTags.Rods.URANIUM, IPOTags.Ingots.URANIUM);
	}
	
	/** Creates both a recipe for fences and the stick needed */
	private void fenceAndStickRecipe(FenceBlock fence, Item rod, ITag.INamedTag<Item> stickTag, ITag.INamedTag<Item> ingotTag){
		String stickMat = getMaterialName(stickTag.getName()); // Stick Material
		String ingotMat = getMaterialName(ingotTag.getName()); // Ingot Material
		
		if(fence!=Fences.IRON.get())
			ShapedRecipeBuilder.shapedRecipe(rod, 4)
				.patternLine("i")
				.patternLine("i")
				.key('i', ingotTag)
				.addCriterion("has_"+ingotMat+"_ingot", hasItem(ingotTag))
				.build(involveConfig(this.out, new IPOConfigCondition(ingotMat, true)), new ResourceLocation(IPOMod.ID, "has_"+stickMat+"_rod"));
		
		ShapedRecipeBuilder.shapedRecipe(fence, 3)
			.patternLine("isi")
			.patternLine("isi")
			.key('i', ingotTag)
			.key('s', stickTag)
			.addCriterion("has_"+stickMat+"_rod", hasItem(stickTag))
			.addCriterion("has_"+ingotMat+"_ingot", hasItem(ingotTag))
			.build(involveConfig(this.out, new IPOConfigCondition(ingotMat, true)));
	}
	
	private String getMaterialName(ResourceLocation in){
		return in.getPath().substring(in.getPath().indexOf('/') + 1);
	}
	
	private Consumer<IFinishedRecipe> involveConfig(Consumer<IFinishedRecipe> out, ICondition... conditions){
		return recipe -> {
			out.accept(new IFinishedRecipe(){
				
				@Override
				public void serialize(JsonObject json){
					if(conditions.length > 0){
						JsonArray conArray = new JsonArray();
						for(ICondition con:conditions)
							conArray.add(CraftingHelper.serialize(con));
						json.add("conditions", conArray);
					}
					
					recipe.serialize(json);
				}
				
				@Override
				public IRecipeSerializer<?> getSerializer(){
					return recipe.getSerializer();
				}
				
				@Override
				public ResourceLocation getID(){
					return recipe.getID();
				}
				
				@Override
				public JsonObject getAdvancementJson(){
					return recipe.getAdvancementJson();
				}
				
				@Override
				public ResourceLocation getAdvancementID(){
					return recipe.getAdvancementID();
				}
			});
		};
	}
}
