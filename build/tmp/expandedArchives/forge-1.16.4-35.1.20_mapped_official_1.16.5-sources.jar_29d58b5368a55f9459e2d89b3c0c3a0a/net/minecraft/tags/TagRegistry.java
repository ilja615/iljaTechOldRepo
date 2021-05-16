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
   private ITagCollection<T> source = ITagCollection.empty();
   private final List<TagRegistry.NamedTag<T>> wrappers = Lists.newArrayList();
   private final Function<ITagCollectionSupplier, ITagCollection<T>> collectionGetter;
   private static java.util.Map<ResourceLocation, List<TagRegistry.NamedTag<?>>> toAdd = com.google.common.collect.Maps.newHashMap();

   public TagRegistry(Function<ITagCollectionSupplier, ITagCollection<T>> p_i241894_1_) {
      this.collectionGetter = p_i241894_1_;
   }

   public ITag.INamedTag<T> bind(String p_232937_1_) {
       return add(new TagRegistry.NamedTag<>(new ResourceLocation(p_232937_1_)));
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
      namedtag.rebind(source::getTag);
      this.wrappers.add(namedtag);
      return namedtag;
   }

   @OnlyIn(Dist.CLIENT)
   public void resetToEmpty() {
      this.source = ITagCollection.empty();
      ITag<T> itag = Tag.empty();
      this.wrappers.forEach((p_232933_1_) -> {
         p_232933_1_.rebind((p_232934_1_) -> {
            return itag;
         });
      });
   }

   public void reset(ITagCollectionSupplier p_242188_1_) {
      ITagCollection<T> itagcollection = this.collectionGetter.apply(p_242188_1_);
      this.source = itagcollection;
      this.wrappers.forEach((p_232936_1_) -> {
         p_232936_1_.rebind(itagcollection::getTag);
      });
   }

   public ITagCollection<T> reinjectOptionalTags(ITagCollection<T> tagCollection) {
      java.util.Map<ResourceLocation, ITag<T>> currentTags = tagCollection.getAllTags();
      java.util.Map<ResourceLocation, ITag<T>> missingOptionals = this.wrappers.stream().filter(e -> e instanceof OptionalNamedTag && !currentTags.containsKey(e.getName())).collect(Collectors.toMap(NamedTag::getName, namedTag -> {
         OptionalNamedTag<T> optionalNamedTag = (OptionalNamedTag<T>) namedTag;
         optionalNamedTag.defaulted = true;
         return optionalNamedTag.resolveDefaulted();
      }));
      if (!missingOptionals.isEmpty()) {
         missingOptionals.putAll(currentTags);
         return ITagCollection.of(missingOptionals);
      }
      return tagCollection;
   }

   public ITagCollection<T> getAllTags() {
      return this.source;
   }

   public List<? extends ITag.INamedTag<T>> getWrappers() {
      return this.wrappers;
   }

   public Set<ResourceLocation> getMissingTags(ITagCollectionSupplier p_242189_1_) {
      ITagCollection<T> itagcollection = this.collectionGetter.apply(p_242189_1_);
      Set<ResourceLocation> set = this.wrappers.stream().filter(e -> !(e instanceof OptionalNamedTag)).map(TagRegistry.NamedTag::getName).collect(Collectors.toSet());
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.copyOf(itagcollection.getAvailableTags());
      return Sets.difference(set, immutableset);
   }

   static class NamedTag<T> implements ITag.INamedTag<T> {
      @Nullable
      protected ITag<T> tag;
      protected final ResourceLocation name;

      private NamedTag(ResourceLocation p_i231430_1_) {
         this.name = p_i231430_1_;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      private ITag<T> resolve() {
         if (this.tag == null) {
            throw new IllegalStateException("Tag " + this.name + " used before it was bound");
         } else {
            return this.tag;
         }
      }

      void rebind(Function<ResourceLocation, ITag<T>> p_232943_1_) {
         this.tag = p_232943_1_.apply(this.name);
      }

      public boolean contains(T p_230235_1_) {
         return this.resolve().contains(p_230235_1_);
      }

      public List<T> getValues() {
         return this.resolve().getValues();
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
            return Tag.empty();
         }
         return Tag.create(ImmutableSet.copyOf(defaults.stream().map(java.util.function.Supplier::get).collect(Collectors.toSet())));
      }

      @Override
      public String toString() {
          return "OptionalNamedTag[" + getName().toString() + ']';
      }
   }
}
