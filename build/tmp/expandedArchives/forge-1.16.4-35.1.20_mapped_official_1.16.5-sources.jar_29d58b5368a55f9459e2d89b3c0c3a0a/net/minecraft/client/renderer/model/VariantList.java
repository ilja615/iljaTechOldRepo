package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VariantList implements IUnbakedModel {
   private final List<Variant> variants;

   public VariantList(List<Variant> p_i46568_1_) {
      this.variants = p_i46568_1_;
   }

   public List<Variant> getVariants() {
      return this.variants;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof VariantList) {
         VariantList variantlist = (VariantList)p_equals_1_;
         return this.variants.equals(variantlist.variants);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.variants.hashCode();
   }

   public Collection<ResourceLocation> getDependencies() {
      return this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
   }

   public Collection<RenderMaterial> getMaterials(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<Pair<String, String>> p_225614_2_) {
      return this.getVariants().stream().map(Variant::getModelLocation).distinct().flatMap((p_228831_2_) -> {
         return p_225614_1_.apply(p_228831_2_).getMaterials(p_225614_1_, p_225614_2_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel bake(ModelBakery p_225613_1_, Function<RenderMaterial, TextureAtlasSprite> p_225613_2_, IModelTransform p_225613_3_, ResourceLocation p_225613_4_) {
      if (this.getVariants().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();

         for(Variant variant : this.getVariants()) {
            IBakedModel ibakedmodel = p_225613_1_.getBakedModel(variant.getModelLocation(), variant, p_225613_2_);
            weightedbakedmodel$builder.add(ibakedmodel, variant.getWeight());
         }

         return weightedbakedmodel$builder.build();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<VariantList> {
      public VariantList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         List<Variant> list = Lists.newArrayList();
         if (p_deserialize_1_.isJsonArray()) {
            JsonArray jsonarray = p_deserialize_1_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            for(JsonElement jsonelement : jsonarray) {
               list.add(p_deserialize_3_.deserialize(jsonelement, Variant.class));
            }
         } else {
            list.add(p_deserialize_3_.deserialize(p_deserialize_1_, Variant.class));
         }

         return new VariantList(list);
      }
   }
}
