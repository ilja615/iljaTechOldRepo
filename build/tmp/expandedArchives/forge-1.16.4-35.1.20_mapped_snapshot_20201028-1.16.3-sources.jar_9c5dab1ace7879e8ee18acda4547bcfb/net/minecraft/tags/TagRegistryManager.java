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
   private static final Map<ResourceLocation, TagRegistry<?>> idToRegistryMap = Maps.newHashMap();

   public static <T> TagRegistry<T> create(ResourceLocation id, Function<ITagCollectionSupplier, ITagCollection<T>> supplierToCollectionFunction) {
      TagRegistry<T> tagregistry = new TagRegistry<>(supplierToCollectionFunction);
      TagRegistry<?> tagregistry1 = idToRegistryMap.putIfAbsent(id, tagregistry);
      if (tagregistry1 != null) {
         throw new IllegalStateException("Duplicate entry for static tag collection: " + id);
      } else {
         return tagregistry;
      }
   }

   public static void fetchTags(ITagCollectionSupplier supplier) {
      idToRegistryMap.values().forEach((registry) -> {
         registry.fetchTags(supplier);
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static void fetchTags() {
      idToRegistryMap.values().forEach(TagRegistry::fetchTags);
   }

   public static Multimap<ResourceLocation, ResourceLocation> validateTags(ITagCollectionSupplier supplier) {
      Multimap<ResourceLocation, ResourceLocation> multimap = HashMultimap.create();
      idToRegistryMap.forEach((id, registry) -> {
         multimap.putAll(id, registry.getTagIdsFromSupplier(supplier));
      });
      return multimap;
   }

   public static void checkHelperRegistrations() {
      TagRegistry[] atagregistry = new TagRegistry[]{BlockTags.collection, ItemTags.collection, FluidTags.collection, EntityTypeTags.tagCollection};
      boolean flag = Stream.of(atagregistry).anyMatch((registry) -> {
         return !idToRegistryMap.containsValue(registry);
      });
      if (flag) {
         throw new IllegalStateException("Missing helper registrations");
      }
   }

   @javax.annotation.Nullable
   public static TagRegistry<?> get(ResourceLocation rl) {
      return idToRegistryMap.get(rl);
   }

   public static Multimap<ResourceLocation, ResourceLocation> validateVanillaTags(ITagCollectionSupplier tagCollectionSupplier) {
      Multimap<ResourceLocation, ResourceLocation> missingTags = HashMultimap.create();
      for (java.util.Map.Entry<ResourceLocation, TagRegistry<?>> entry : idToRegistryMap.entrySet()) {
         if (!net.minecraftforge.common.ForgeTagHandler.getCustomTagTypeNames().contains(entry.getKey())) {
            missingTags.putAll(entry.getKey(), entry.getValue().getTagIdsFromSupplier(tagCollectionSupplier));
         }
      }
      return missingTags;
   }

   public static void fetchCustomTagTypes(ITagCollectionSupplier tagCollectionSupplier) {
      net.minecraftforge.common.ForgeTagHandler.getCustomTagTypeNames().forEach(tagRegistry -> idToRegistryMap.get(tagRegistry).fetchTags(tagCollectionSupplier));
   }
}
