package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModelTextures {
   private final Map<StockTextureAliases, ResourceLocation> slots = Maps.newHashMap();
   private final Set<StockTextureAliases> forcedSlots = Sets.newHashSet();

   public ModelTextures put(StockTextureAliases p_240349_1_, ResourceLocation p_240349_2_) {
      this.slots.put(p_240349_1_, p_240349_2_);
      return this;
   }

   public Stream<StockTextureAliases> getForced() {
      return this.forcedSlots.stream();
   }

   public ModelTextures copyForced(StockTextureAliases p_240355_1_, StockTextureAliases p_240355_2_) {
      this.slots.put(p_240355_2_, this.slots.get(p_240355_1_));
      this.forcedSlots.add(p_240355_2_);
      return this;
   }

   public ResourceLocation get(StockTextureAliases p_240348_1_) {
      for(StockTextureAliases stocktexturealiases = p_240348_1_; stocktexturealiases != null; stocktexturealiases = stocktexturealiases.getParent()) {
         ResourceLocation resourcelocation = this.slots.get(stocktexturealiases);
         if (resourcelocation != null) {
            return resourcelocation;
         }
      }

      throw new IllegalStateException("Can't find texture for slot " + p_240348_1_);
   }

   public ModelTextures copyAndUpdate(StockTextureAliases p_240360_1_, ResourceLocation p_240360_2_) {
      ModelTextures modeltextures = new ModelTextures();
      modeltextures.slots.putAll(this.slots);
      modeltextures.forcedSlots.addAll(this.forcedSlots);
      modeltextures.put(p_240360_1_, p_240360_2_);
      return modeltextures;
   }

   public static ModelTextures cube(Block p_240345_0_) {
      ResourceLocation resourcelocation = getBlockTexture(p_240345_0_);
      return cube(resourcelocation);
   }

   public static ModelTextures defaultTexture(Block p_240353_0_) {
      ResourceLocation resourcelocation = getBlockTexture(p_240353_0_);
      return defaultTexture(resourcelocation);
   }

   public static ModelTextures defaultTexture(ResourceLocation p_240350_0_) {
      return (new ModelTextures()).put(StockTextureAliases.TEXTURE, p_240350_0_);
   }

   public static ModelTextures cube(ResourceLocation p_240356_0_) {
      return (new ModelTextures()).put(StockTextureAliases.ALL, p_240356_0_);
   }

   public static ModelTextures cross(Block p_240358_0_) {
      return singleSlot(StockTextureAliases.CROSS, getBlockTexture(p_240358_0_));
   }

   public static ModelTextures cross(ResourceLocation p_240361_0_) {
      return singleSlot(StockTextureAliases.CROSS, p_240361_0_);
   }

   public static ModelTextures plant(Block p_240362_0_) {
      return singleSlot(StockTextureAliases.PLANT, getBlockTexture(p_240362_0_));
   }

   public static ModelTextures plant(ResourceLocation p_240365_0_) {
      return singleSlot(StockTextureAliases.PLANT, p_240365_0_);
   }

   public static ModelTextures rail(Block p_240366_0_) {
      return singleSlot(StockTextureAliases.RAIL, getBlockTexture(p_240366_0_));
   }

   public static ModelTextures rail(ResourceLocation p_240367_0_) {
      return singleSlot(StockTextureAliases.RAIL, p_240367_0_);
   }

   public static ModelTextures wool(Block p_240368_0_) {
      return singleSlot(StockTextureAliases.WOOL, getBlockTexture(p_240368_0_));
   }

   public static ModelTextures stem(Block p_240369_0_) {
      return singleSlot(StockTextureAliases.STEM, getBlockTexture(p_240369_0_));
   }

   public static ModelTextures attachedStem(Block p_240346_0_, Block p_240346_1_) {
      return (new ModelTextures()).put(StockTextureAliases.STEM, getBlockTexture(p_240346_0_)).put(StockTextureAliases.UPPER_STEM, getBlockTexture(p_240346_1_));
   }

   public static ModelTextures pattern(Block p_240371_0_) {
      return singleSlot(StockTextureAliases.PATTERN, getBlockTexture(p_240371_0_));
   }

   public static ModelTextures fan(Block p_240373_0_) {
      return singleSlot(StockTextureAliases.FAN, getBlockTexture(p_240373_0_));
   }

   public static ModelTextures crop(ResourceLocation p_240370_0_) {
      return singleSlot(StockTextureAliases.CROP, p_240370_0_);
   }

   public static ModelTextures pane(Block p_240354_0_, Block p_240354_1_) {
      return (new ModelTextures()).put(StockTextureAliases.PANE, getBlockTexture(p_240354_0_)).put(StockTextureAliases.EDGE, getBlockTexture(p_240354_1_, "_top"));
   }

   public static ModelTextures singleSlot(StockTextureAliases p_240364_0_, ResourceLocation p_240364_1_) {
      return (new ModelTextures()).put(p_240364_0_, p_240364_1_);
   }

   public static ModelTextures column(Block p_240375_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240375_0_, "_side")).put(StockTextureAliases.END, getBlockTexture(p_240375_0_, "_top"));
   }

   public static ModelTextures cubeTop(Block p_240377_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240377_0_, "_side")).put(StockTextureAliases.TOP, getBlockTexture(p_240377_0_, "_top"));
   }

   public static ModelTextures logColumn(Block p_240378_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240378_0_)).put(StockTextureAliases.END, getBlockTexture(p_240378_0_, "_top"));
   }

   public static ModelTextures column(ResourceLocation p_240351_0_, ResourceLocation p_240351_1_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, p_240351_0_).put(StockTextureAliases.END, p_240351_1_);
   }

   public static ModelTextures cubeBottomTop(Block p_240379_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240379_0_, "_side")).put(StockTextureAliases.TOP, getBlockTexture(p_240379_0_, "_top")).put(StockTextureAliases.BOTTOM, getBlockTexture(p_240379_0_, "_bottom"));
   }

   public static ModelTextures cubeBottomTopWithWall(Block p_240380_0_) {
      ResourceLocation resourcelocation = getBlockTexture(p_240380_0_);
      return (new ModelTextures()).put(StockTextureAliases.WALL, resourcelocation).put(StockTextureAliases.SIDE, resourcelocation).put(StockTextureAliases.TOP, getBlockTexture(p_240380_0_, "_top")).put(StockTextureAliases.BOTTOM, getBlockTexture(p_240380_0_, "_bottom"));
   }

   public static ModelTextures columnWithWall(Block p_240381_0_) {
      ResourceLocation resourcelocation = getBlockTexture(p_240381_0_);
      return (new ModelTextures()).put(StockTextureAliases.WALL, resourcelocation).put(StockTextureAliases.SIDE, resourcelocation).put(StockTextureAliases.END, getBlockTexture(p_240381_0_, "_top"));
   }

   public static ModelTextures door(Block p_240382_0_) {
      return (new ModelTextures()).put(StockTextureAliases.TOP, getBlockTexture(p_240382_0_, "_top")).put(StockTextureAliases.BOTTOM, getBlockTexture(p_240382_0_, "_bottom"));
   }

   public static ModelTextures particle(Block p_240383_0_) {
      return (new ModelTextures()).put(StockTextureAliases.PARTICLE, getBlockTexture(p_240383_0_));
   }

   public static ModelTextures particle(ResourceLocation p_240372_0_) {
      return (new ModelTextures()).put(StockTextureAliases.PARTICLE, p_240372_0_);
   }

   public static ModelTextures fire0(Block p_240384_0_) {
      return (new ModelTextures()).put(StockTextureAliases.FIRE, getBlockTexture(p_240384_0_, "_0"));
   }

   public static ModelTextures fire1(Block p_240385_0_) {
      return (new ModelTextures()).put(StockTextureAliases.FIRE, getBlockTexture(p_240385_0_, "_1"));
   }

   public static ModelTextures lantern(Block p_240386_0_) {
      return (new ModelTextures()).put(StockTextureAliases.LANTERN, getBlockTexture(p_240386_0_));
   }

   public static ModelTextures torch(Block p_240387_0_) {
      return (new ModelTextures()).put(StockTextureAliases.TORCH, getBlockTexture(p_240387_0_));
   }

   public static ModelTextures torch(ResourceLocation p_240374_0_) {
      return (new ModelTextures()).put(StockTextureAliases.TORCH, p_240374_0_);
   }

   public static ModelTextures particleFromItem(Item p_240343_0_) {
      return (new ModelTextures()).put(StockTextureAliases.PARTICLE, getItemTexture(p_240343_0_));
   }

   public static ModelTextures commandBlock(Block p_240388_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240388_0_, "_side")).put(StockTextureAliases.FRONT, getBlockTexture(p_240388_0_, "_front")).put(StockTextureAliases.BACK, getBlockTexture(p_240388_0_, "_back"));
   }

   public static ModelTextures orientableCube(Block p_240389_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240389_0_, "_side")).put(StockTextureAliases.FRONT, getBlockTexture(p_240389_0_, "_front")).put(StockTextureAliases.TOP, getBlockTexture(p_240389_0_, "_top")).put(StockTextureAliases.BOTTOM, getBlockTexture(p_240389_0_, "_bottom"));
   }

   public static ModelTextures orientableCubeOnlyTop(Block p_240390_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240390_0_, "_side")).put(StockTextureAliases.FRONT, getBlockTexture(p_240390_0_, "_front")).put(StockTextureAliases.TOP, getBlockTexture(p_240390_0_, "_top"));
   }

   public static ModelTextures orientableCubeSameEnds(Block p_240391_0_) {
      return (new ModelTextures()).put(StockTextureAliases.SIDE, getBlockTexture(p_240391_0_, "_side")).put(StockTextureAliases.FRONT, getBlockTexture(p_240391_0_, "_front")).put(StockTextureAliases.END, getBlockTexture(p_240391_0_, "_end"));
   }

   public static ModelTextures top(Block p_240392_0_) {
      return (new ModelTextures()).put(StockTextureAliases.TOP, getBlockTexture(p_240392_0_, "_top"));
   }

   public static ModelTextures craftingTable(Block p_240359_0_, Block p_240359_1_) {
      return (new ModelTextures()).put(StockTextureAliases.PARTICLE, getBlockTexture(p_240359_0_, "_front")).put(StockTextureAliases.DOWN, getBlockTexture(p_240359_1_)).put(StockTextureAliases.UP, getBlockTexture(p_240359_0_, "_top")).put(StockTextureAliases.NORTH, getBlockTexture(p_240359_0_, "_front")).put(StockTextureAliases.EAST, getBlockTexture(p_240359_0_, "_side")).put(StockTextureAliases.SOUTH, getBlockTexture(p_240359_0_, "_side")).put(StockTextureAliases.WEST, getBlockTexture(p_240359_0_, "_front"));
   }

   public static ModelTextures fletchingTable(Block p_240363_0_, Block p_240363_1_) {
      return (new ModelTextures()).put(StockTextureAliases.PARTICLE, getBlockTexture(p_240363_0_, "_front")).put(StockTextureAliases.DOWN, getBlockTexture(p_240363_1_)).put(StockTextureAliases.UP, getBlockTexture(p_240363_0_, "_top")).put(StockTextureAliases.NORTH, getBlockTexture(p_240363_0_, "_front")).put(StockTextureAliases.SOUTH, getBlockTexture(p_240363_0_, "_front")).put(StockTextureAliases.EAST, getBlockTexture(p_240363_0_, "_side")).put(StockTextureAliases.WEST, getBlockTexture(p_240363_0_, "_side"));
   }

   public static ModelTextures campfire(Block p_240339_0_) {
      return (new ModelTextures()).put(StockTextureAliases.LIT_LOG, getBlockTexture(p_240339_0_, "_log_lit")).put(StockTextureAliases.FIRE, getBlockTexture(p_240339_0_, "_fire"));
   }

   public static ModelTextures layer0(Item p_240352_0_) {
      return (new ModelTextures()).put(StockTextureAliases.LAYER0, getItemTexture(p_240352_0_));
   }

   public static ModelTextures layer0(Block p_240340_0_) {
      return (new ModelTextures()).put(StockTextureAliases.LAYER0, getBlockTexture(p_240340_0_));
   }

   public static ModelTextures layer0(ResourceLocation p_240376_0_) {
      return (new ModelTextures()).put(StockTextureAliases.LAYER0, p_240376_0_);
   }

   public static ResourceLocation getBlockTexture(Block p_240341_0_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240341_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath());
   }

   public static ResourceLocation getBlockTexture(Block p_240347_0_, String p_240347_1_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240347_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath() + p_240347_1_);
   }

   public static ResourceLocation getItemTexture(Item p_240357_0_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_240357_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath());
   }

   public static ResourceLocation getItemTexture(Item p_240344_0_, String p_240344_1_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_240344_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath() + p_240344_1_);
   }
}
