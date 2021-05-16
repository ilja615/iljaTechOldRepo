package net.minecraft.data;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModelsResourceUtil {
   @Deprecated
   public static ResourceLocation decorateBlockModelLocation(String p_240223_0_) {
      return new ResourceLocation("minecraft", "block/" + p_240223_0_);
   }

   public static ResourceLocation decorateItemModelLocation(String p_240224_0_) {
      return new ResourceLocation("minecraft", "item/" + p_240224_0_);
   }

   public static ResourceLocation getModelLocation(Block p_240222_0_, String p_240222_1_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240222_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath() + p_240222_1_);
   }

   public static ResourceLocation getModelLocation(Block p_240221_0_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240221_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath());
   }

   public static ResourceLocation getModelLocation(Item p_240219_0_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_240219_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath());
   }

   public static ResourceLocation getModelLocation(Item p_240220_0_, String p_240220_1_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(p_240220_0_);
      return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath() + p_240220_1_);
   }
}
