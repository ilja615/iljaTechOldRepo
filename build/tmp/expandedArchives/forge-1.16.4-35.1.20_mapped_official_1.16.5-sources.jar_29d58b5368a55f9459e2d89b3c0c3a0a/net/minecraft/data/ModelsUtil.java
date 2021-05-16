package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class ModelsUtil {
   private final Optional<ResourceLocation> model;
   private final Set<StockTextureAliases> requiredSlots;
   private Optional<String> suffix;

   public ModelsUtil(Optional<ResourceLocation> p_i232546_1_, Optional<String> p_i232546_2_, StockTextureAliases... p_i232546_3_) {
      this.model = p_i232546_1_;
      this.suffix = p_i232546_2_;
      this.requiredSlots = ImmutableSet.copyOf(p_i232546_3_);
   }

   public ResourceLocation create(Block p_240228_1_, ModelTextures p_240228_2_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240228_3_) {
      return this.create(ModelsResourceUtil.getModelLocation(p_240228_1_, this.suffix.orElse("")), p_240228_2_, p_240228_3_);
   }

   public ResourceLocation createWithSuffix(Block p_240229_1_, String p_240229_2_, ModelTextures p_240229_3_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240229_4_) {
      return this.create(ModelsResourceUtil.getModelLocation(p_240229_1_, p_240229_2_ + (String)this.suffix.orElse("")), p_240229_3_, p_240229_4_);
   }

   public ResourceLocation createWithOverride(Block p_240235_1_, String p_240235_2_, ModelTextures p_240235_3_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240235_4_) {
      return this.create(ModelsResourceUtil.getModelLocation(p_240235_1_, p_240235_2_), p_240235_3_, p_240235_4_);
   }

   public ResourceLocation create(ResourceLocation p_240234_1_, ModelTextures p_240234_2_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240234_3_) {
      Map<StockTextureAliases, ResourceLocation> map = this.createMap(p_240234_2_);
      p_240234_3_.accept(p_240234_1_, () -> {
         JsonObject jsonobject = new JsonObject();
         this.model.ifPresent((p_240231_1_) -> {
            jsonobject.addProperty("parent", p_240231_1_.toString());
         });
         if (!map.isEmpty()) {
            JsonObject jsonobject1 = new JsonObject();
            map.forEach((p_240230_1_, p_240230_2_) -> {
               jsonobject1.addProperty(p_240230_1_.getId(), p_240230_2_.toString());
            });
            jsonobject.add("textures", jsonobject1);
         }

         return jsonobject;
      });
      return p_240234_1_;
   }

   private Map<StockTextureAliases, ResourceLocation> createMap(ModelTextures p_240232_1_) {
      return Streams.concat(this.requiredSlots.stream(), p_240232_1_.getForced()).collect(ImmutableMap.toImmutableMap(Function.identity(), p_240232_1_::get));
   }
}
