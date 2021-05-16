package net.minecraft.tags;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public interface ITagCollection<T> {
   Map<ResourceLocation, ITag<T>> getAllTags();

   @Nullable
   default ITag<T> getTag(ResourceLocation p_199910_1_) {
      return this.getAllTags().get(p_199910_1_);
   }

   ITag<T> getTagOrEmpty(ResourceLocation p_241834_1_);

   @Nullable
   ResourceLocation getId(ITag<T> p_232973_1_);

   default ResourceLocation getIdOrThrow(ITag<T> p_232975_1_) {
      ResourceLocation resourcelocation = this.getId(p_232975_1_);
      if (resourcelocation == null) {
         throw new IllegalStateException("Unrecognized tag");
      } else {
         return resourcelocation;
      }
   }

   default Collection<ResourceLocation> getAvailableTags() {
      return this.getAllTags().keySet();
   }

   default Collection<ResourceLocation> getMatchingTags(T p_199913_1_) {
      List<ResourceLocation> list = Lists.newArrayList();

      for(Entry<ResourceLocation, ITag<T>> entry : this.getAllTags().entrySet()) {
         if (entry.getValue().contains(p_199913_1_)) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   default void serializeToNetwork(PacketBuffer p_242203_1_, DefaultedRegistry<T> p_242203_2_) {
      Map<ResourceLocation, ITag<T>> map = this.getAllTags();
      p_242203_1_.writeVarInt(map.size());

      for(Entry<ResourceLocation, ITag<T>> entry : map.entrySet()) {
         p_242203_1_.writeResourceLocation(entry.getKey());
         p_242203_1_.writeVarInt(entry.getValue().getValues().size());

         for(T t : entry.getValue().getValues()) {
            p_242203_1_.writeVarInt(p_242203_2_.getId(t));
         }
      }

   }

   static <T> ITagCollection<T> loadFromNetwork(PacketBuffer p_242204_0_, Registry<T> p_242204_1_) {
      Map<ResourceLocation, ITag<T>> map = Maps.newHashMap();
      int i = p_242204_0_.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = p_242204_0_.readResourceLocation();
         int k = p_242204_0_.readVarInt();
         Builder<T> builder = ImmutableSet.builder();

         for(int l = 0; l < k; ++l) {
            builder.add(p_242204_1_.byId(p_242204_0_.readVarInt()));
         }

         map.put(resourcelocation, ITag.fromSet(builder.build()));
      }

      return of(map);
   }

   static <T> ITagCollection<T> empty() {
      return of(ImmutableBiMap.of());
   }

   static <T> ITagCollection<T> of(Map<ResourceLocation, ITag<T>> p_242202_0_) {
      final BiMap<ResourceLocation, ITag<T>> bimap = ImmutableBiMap.copyOf(p_242202_0_);
      return new ITagCollection<T>() {
         private final ITag<T> empty = Tag.empty();

         public ITag<T> getTagOrEmpty(ResourceLocation p_241834_1_) {
            return bimap.getOrDefault(p_241834_1_, this.empty);
         }

         @Nullable
         public ResourceLocation getId(ITag<T> p_232973_1_) {
            return p_232973_1_ instanceof ITag.INamedTag ? ((ITag.INamedTag)p_232973_1_).getName() : bimap.inverse().get(p_232973_1_);
         }

         public Map<ResourceLocation, ITag<T>> getAllTags() {
            return bimap;
         }
      };
   }
}
