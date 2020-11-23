package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TagRegistry<T> {
   private ITagCollection<T> collection = ITagCollection.getEmptyTagCollection();
   private final List<TagRegistry.NamedTag<T>> tags = Lists.newArrayList();
   private final Function<ITagCollectionSupplier, ITagCollection<T>> supplierToCollectionFunction;

   public TagRegistry(Function<ITagCollectionSupplier, ITagCollection<T>> supplierToCollectionFunction) {
      this.supplierToCollectionFunction = supplierToCollectionFunction;
   }

   public ITag.INamedTag<T> createTag(String id) {
       return add(new TagRegistry.NamedTag<>(new ResourceLocation(id)));
   }

   public net.minecraftforge.common.Tags.IOptionalNamedTag<T> createOptional(ResourceLocation key, @Nullable java.util.function.Supplier<Set<T>> defaults) {
       return add(new TagRegistry.OptionalNamedTag<>(key, defaults));
   }

   private <R extends TagRegistry.NamedTag<T>> R add(R namedtag) {
      namedtag.fetchTag(collection::get);
      this.tags.add(namedtag);
      return namedtag;
   }

   @OnlyIn(Dist.CLIENT)
   public void fetchTags() {
      this.collection = ITagCollection.getEmptyTagCollection();
      ITag<T> itag = Tag.getEmptyTag();
      this.tags.forEach((tag) -> {
         tag.fetchTag((id) -> {
            return itag;
         });
      });
   }

   public void fetchTags(ITagCollectionSupplier supplier) {
      ITagCollection<T> itagcollection = this.supplierToCollectionFunction.apply(supplier);
      this.collection = itagcollection;
      this.tags.forEach((tag) -> {
         tag.fetchTag(itagcollection::get);
      });
   }

   public ITagCollection<T> getCollection() {
      return this.collection;
   }

   public List<? extends ITag.INamedTag<T>> getTags() {
      return this.tags;
   }

   public Set<ResourceLocation> getTagIdsFromSupplier(ITagCollectionSupplier supplier) {
      ITagCollection<T> itagcollection = this.supplierToCollectionFunction.apply(supplier);
      Set<ResourceLocation> set = this.tags.stream().filter(e -> !(e instanceof OptionalNamedTag)).map(TagRegistry.NamedTag::getName).collect(Collectors.toSet());
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.copyOf(itagcollection.getRegisteredTags());
      return Sets.difference(set, immutableset);
   }

   static class NamedTag<T> implements ITag.INamedTag<T> {
      @Nullable
      protected ITag<T> tag;
      protected final ResourceLocation id;

      private NamedTag(ResourceLocation id) {
         this.id = id;
      }

      public ResourceLocation getName() {
         return this.id;
      }

      private ITag<T> getTag() {
         if (this.tag == null) {
            throw new IllegalStateException("Tag " + this.id + " used before it was bound");
         } else {
            return this.tag;
         }
      }

      void fetchTag(Function<ResourceLocation, ITag<T>> idToTagFunction) {
         this.tag = idToTagFunction.apply(this.id);
      }

      public boolean contains(T element) {
         return this.getTag().contains(element);
      }

      public List<T> getAllElements() {
         return this.getTag().getAllElements();
      }

      @Override
      public String toString() {
          return "NamedTag[" + getName().toString() + ']';
      }
   }

   private static class OptionalNamedTag<T> extends NamedTag<T> implements net.minecraftforge.common.Tags.IOptionalNamedTag<T> {
      @Nullable
      private final java.util.function.Supplier<Set<T>> defaults;
      private boolean defaulted = true;

      private OptionalNamedTag(ResourceLocation name, @Nullable java.util.function.Supplier<Set<T>> defaults) {
         super(name);
         this.defaults = defaults;
      }

      @Override
      public boolean isDefaulted() {
         return defaulted;
      }

      @Override
      void fetchTag(Function<ResourceLocation, ITag<T>> idToTagFunction) {
         super.fetchTag(idToTagFunction);
         if (this.tag == null) {
            this.defaulted = true;
            Set<T> defs = defaults == null ? null : defaults.get();
            this.tag = defs == null ? Tag.getEmptyTag() : Tag.getTagFromContents(defs);
         } else {
            this.defaulted = false;
         }
      }

      @Override
      public String toString() {
          return "OptionalNamedTag[" + getName().toString() + ']';
      }
   }
}