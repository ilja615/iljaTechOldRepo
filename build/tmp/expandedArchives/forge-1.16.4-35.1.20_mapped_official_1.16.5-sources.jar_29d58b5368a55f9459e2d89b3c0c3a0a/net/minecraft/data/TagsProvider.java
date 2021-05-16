package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T> implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   protected final DataGenerator generator;
   protected final Registry<T> registry;
   protected final Map<ResourceLocation, ITag.Builder> builders = Maps.newLinkedHashMap();
   protected final String modId;
   protected final net.minecraftforge.common.data.ExistingFileHelper existingFileHelper;
   private final net.minecraftforge.common.data.ExistingFileHelper.IResourceType resourceType;

   @Deprecated//Forge, Use ModID version.
   protected TagsProvider(DataGenerator p_i49827_1_, Registry<T> p_i49827_2_) {
      this(p_i49827_1_, p_i49827_2_, "vanilla", null);
   }
   protected TagsProvider(DataGenerator p_i49827_1_, Registry<T> p_i49827_2_, String modId, @javax.annotation.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      this.generator = p_i49827_1_;
      this.registry = p_i49827_2_;
      this.modId = modId;
      this.existingFileHelper = existingFileHelper;
      this.resourceType = new net.minecraftforge.common.data.ExistingFileHelper.ResourceType(net.minecraft.resources.ResourcePackType.SERVER_DATA, ".json", "tags/" + getTagFolder());
   }

   protected abstract void addTags();

   public void run(DirectoryCache p_200398_1_) {
      this.builders.clear();
      this.addTags();
      ITag<T> itag = Tag.empty();
      Function<ResourceLocation, ITag<T>> function = (p_240523_2_) -> {
         return this.builders.containsKey(p_240523_2_) ? itag : null;
      };
      Function<ResourceLocation, T> function1 = (p_240527_1_) -> {
         return this.registry.getOptional(p_240527_1_).orElse((T)null);
      };
      this.builders.forEach((p_240524_4_, p_240524_5_) -> {
         // FORGE: Add validation via existing resources
         List<ITag.Proxy> list = p_240524_5_.getUnresolvedEntries(function, function1).filter(this::missing).collect(Collectors.toList());
         if (!list.isEmpty()) {
            throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", p_240524_4_, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
         } else {
            JsonObject jsonobject = p_240524_5_.serializeToJson();
            Path path = this.getPath(p_240524_4_);
            if (path == null) return; //Forge: Allow running this data provider without writing it. Recipe provider needs valid tags.

            try {
               String s = GSON.toJson((JsonElement)jsonobject);
               String s1 = SHA1.hashUnencodedChars(s).toString();
               if (!Objects.equals(p_200398_1_.getHash(path), s1) || !Files.exists(path)) {
                  Files.createDirectories(path.getParent());

                  try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                     bufferedwriter.write(s);
                  }
               }

               p_200398_1_.putNew(path, s1);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't save tags to {}", path, ioexception);
            }

         }
      });
   }

   private boolean missing(ITag.Proxy reference) {
      ITag.ITagEntry entry = reference.getEntry();
      // We only care about non-optional tag entries, this is the only type that can reference a resource and needs validation
      // Optional tags should not be validated
      if (entry instanceof ITag.TagEntry) {
         return existingFileHelper == null || !existingFileHelper.exists(((ITag.TagEntry)entry).getId(), resourceType);
      }
      return false;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   protected String getTagFolder() {
      return ((Registry)Registry.REGISTRY).getKey(registry).getPath() + "s";
   }

   protected abstract Path getPath(ResourceLocation p_200431_1_);

   protected TagsProvider.Builder<T> tag(ITag.INamedTag<T> p_240522_1_) {
      ITag.Builder itag$builder = this.getOrCreateRawBuilder(p_240522_1_);
      return new TagsProvider.Builder<>(itag$builder, this.registry, modId);
   }

   protected ITag.Builder getOrCreateRawBuilder(ITag.INamedTag<T> p_240525_1_) {
      return this.builders.computeIfAbsent(p_240525_1_.getName(), (p_240526_0_) -> {
         existingFileHelper.trackGenerated(p_240526_0_, resourceType);
         return new ITag.Builder();
      });
   }

   public static class Builder<T> implements net.minecraftforge.common.extensions.IForgeTagBuilder<T> {
      private final ITag.Builder builder;
      private final Registry<T> registry;
      private final String source;

      private Builder(ITag.Builder p_i232553_1_, Registry<T> p_i232553_2_, String p_i232553_3_) {
         this.builder = p_i232553_1_;
         this.registry = p_i232553_2_;
         this.source = p_i232553_3_;
      }

      public TagsProvider.Builder<T> add(T p_240532_1_) {
         this.builder.addElement(this.registry.getKey(p_240532_1_), this.source);
         return this;
      }

      public TagsProvider.Builder<T> addTag(ITag.INamedTag<T> p_240531_1_) {
         this.builder.addTag(p_240531_1_.getName(), this.source);
         return this;
      }

      @SafeVarargs
      public final TagsProvider.Builder<T> add(T... p_240534_1_) {
         Stream.<T>of(p_240534_1_).map(this.registry::getKey).forEach((p_240533_1_) -> {
            this.builder.addElement(p_240533_1_, this.source);
         });
         return this;
      }

      public TagsProvider.Builder<T> add(ITag.ITagEntry tag) {
          builder.add(tag, source);
          return this;
      }

      public ITag.Builder getInternalBuilder() {
          return builder;
      }

      public String getModID() {
          return source;
      }
   }
}
