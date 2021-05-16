package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TagRegistryManager {
   private static final Map<ResourceLocation, TagRegistry<?>> HELPERS = Maps.newHashMap();

   public static <T> TagRegistry<T> create(ResourceLocation p_242196_0_, Function<ITagCollectionSupplier, ITagCollection<T>> p_242196_1_) {
      TagRegistry<T> tagregistry = new TagRegistry<>(p_242196_1_);
      TagRegistry<?> tagregistry1 = HELPERS.putIfAbsent(p_242196_0_, tagregistry);
      if (tagregistry1 != null) {
         throw new IllegalStateException("Duplicate entry for static tag collection: " + p_242196_0_);
      } else {
         return tagregistry;
      }
   }

   public static void resetAll(ITagCollectionSupplier p_242193_0_) {
      HELPERS.values().forEach((p_242194_1_) -> {
         p_242194_1_.reset(p_242193_0_);
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static void resetAllToEmpty() {
      HELPERS.values().forEach(TagRegistry::resetToEmpty);
   }

   public static Multimap<ResourceLocation, ResourceLocation> getAllMissingTags(ITagCollectionSupplier p_242198_0_) {
      Multimap<ResourceLocation, ResourceLocation> multimap = HashMultimap.create();
      HELPERS.forEach((p_242195_2_, p_242195_3_) -> {
         multimap.putAll(p_242195_2_, p_242195_3_.getMissingTags(p_242198_0_));
      });
      return multimap;
   }

   public static void bootStrap() {
      TagRegistry[] atagregistry = new TagRegistry[]{BlockTags.HELPER, ItemTags.HELPER, FluidTags.HELPER, EntityTypeTags.HELPER};
      boolean flag = Stream.of(atagregistry).anyMatch((p_242192_0_) -> {
         return !HELPERS.containsValue(p_242192_0_);
      });
      if (flag) {
         throw new IllegalStateException("Missing helper registrations");
      }
   }

   @javax.annotation.Nullable
   public static TagRegistry<?> get(ResourceLocation rl) {
      return HELPERS.get(rl);
   }

   public static Multimap<ResourceLocation, ResourceLocation> validateVanillaTags(ITagCollectionSupplier tagCollectionSupplier) {
      Multimap<ResourceLocation, ResourceLocation> missingTags = HashMultimap.create();
      for (java.util.Map.Entry<ResourceLocation, TagRegistry<?>> entry : HELPERS.entrySet()) {
         if (!net.minecraftforge.common.ForgeTagHandler.getCustomTagTypeNames().contains(entry.getKey())) {
            missingTags.putAll(entry.getKey(), entry.getValue().getMissingTags(tagCollectionSupplier));
         }
      }
      return missingTags;
   }

   public static void fetchCustomTagTypes(ITagCollectionSupplier tagCollectionSupplier) {
      net.minecraftforge.common.ForgeTagHandler.getCustomTagTypeNames().forEach(tagRegistry -> HELPERS.get(tagRegistry).reset(tagCollectionSupplier));
   }
}
