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
   private static java.util.Map<ResourceLocation, List<TagRegistry.NamedTag<?>>> toAdd = com.google.common.collect.Maps.newHashMap();

   public TagRegistry(Function<ITagCollectionSupplier, ITagCollection<T>> supplierToCollectionFunction) {
      this.supplierToCollectionFunction = supplierToCollectionFunction;
   }

   public ITag.INamedTag<T> createTag(String id) {
       return add(new TagRegistry.NamedTag<>(new ResourceLocation(id)));
   }

   public net.minecraftforge.common.Tags.IOptionalNamedTag<T> createOptional(ResourceLocation key, @Nullable Set<java.util.function.Supplier<T>> defaults) {
       return add(new TagRegistry.OptionalNamedTag<>(key, defaults));
   }

   /** Call via ForgeTagHandler#makeWrapperTag to avoid any exceptions due to calling this after it is safe to call {@link #createTag(String)} */
   public static <T> ITag.INamedTag<T> createDelayedTag(ResourceLocation tagRegistry, ResourceLocation name) {
      return delayedAdd(tagRegistry, new TagRegistry.NamedTag<>(name));
   }

   /** Call via ForgeTagHandler#createOptionalTag to avoid any exceptions due to calling this after it is safe to call {@link #createOptional(ResourceLocation, Set)} */
   public static <T> net.minecraftforge.common.Tags.IOptionalNamedTag<T> createDelayedOptional(ResourceLocation tagRegistry, ResourceLocation key, @Nullable Set<java.util.function.Supplier<T>> defaults) {
      return delayedAdd(tagRegistry, new TagRegistry.OptionalNamedTag<>(key, defaults));
   }

   private static synchronized <T, R extends TagRegistry.NamedTag<T>> R delayedAdd(ResourceLocation tagRegistry, R tag) {
      if (toAdd == null) throw new RuntimeException("Creating delayed tags or optional tags, is only supported before custom tag types have been added.");
      toAdd.computeIfAbsent(tagRegistry, registry -> Lists.newArrayList()).add(tag);
      return tag;
   }

   public static void performDelayedAdd() {
      if (toAdd != null) {
         for (java.util.Map.Entry<ResourceLocation, List<TagRegistry.NamedTag<?>>> entry : toAdd.entrySet()) {
            TagRegistry<?> tagRegistry = TagRegistryManager.get(entry.getKey());
            if (tagRegistry == null) throw new RuntimeException("A mod attempted to add a delayed tag for a registry that doesn't have custom tag support.");
            for (TagRegistry.NamedTag<?> tag : entry.getValue()) {
               tagRegistry.add((TagRegistry.NamedTag) tag);
            }
         }
         toAdd = null;
      }
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

   public ITagCollection<T> reinjectOptionalTags(ITagCollection<T> tagCollection) {
      java.util.Map<ResourceLocation, ITag<T>> currentTags = tagCollection.getIDTagMap();
      java.util.Map<ResourceLocation, ITag<T>> missingOptionals = this.tags.stream().filter(e -> e instanceof OptionalNamedTag && !currentTags.containsKey(e.getName())).collect(Collectors.toMap(NamedTag::getName, namedTag -> {
         OptionalNamedTag<T> optionalNamedTag = (OptionalNamedTag<T>) namedTag;
         optionalNamedTag.defaulted = true;
         return optionalNamedTag.resolveDefaulted();
      }));
      if (!missingOptionals.isEmpty()) {
         missingOptionals.putAll(currentTags);
         return ITagCollection.getTagCollectionFromMap(missingOptionals);
      }
      return tagCollection;
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
      @Override public boolean equals(Object o) { return (o == this) || (o instanceof ITag.INamedTag && java.util.Objects.equals(this.getName(), ((ITag.INamedTag<T>)o).getName())); }
      @Override public int hashCode() { return getName().hashCode(); }
   }

   private static class OptionalNamedTag<T> extends NamedTag<T> implements net.minecraftforge.common.Tags.IOptionalNamedTag<T> {
      @Nullable
      private final Set<java.util.function.Supplier<T>> defaults;
      private boolean defaulted = false;

      private OptionalNamedTag(ResourceLocation name, @Nullable Set<java.util.function.Supplier<T>> defaults) {
         super(name);
         this.defaults = defaults;
      }

      @Override
      public boolean isDefaulted() {
         return defaulted;
      }

      Tag<T> resolveDefaulted() {
         if (defaults == null || defaults.isEmpty()) {
            return Tag.getEmptyTag();
         }
         return Tag.getTagFromContents(ImmutableSet.copyOf(defaults.stream().map(java.util.function.Supplier::get).collect(Collectors.toSet())));
      }

      @Override
      public String toString() {
          return "OptionalNamedTag[" + getName().toString() + ']';
      }
   }
}
