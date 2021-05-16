package net.minecraft.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollectionReader<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Function<ResourceLocation, Optional<T>> idToValue;
   private final String directory;
   private final String name;

   public TagCollectionReader(Function<ResourceLocation, Optional<T>> p_i241899_1_, String p_i241899_2_, String p_i241899_3_) {
      this.idToValue = p_i241899_1_;
      this.directory = p_i241899_2_;
      this.name = p_i241899_3_;
   }

   public CompletableFuture<Map<ResourceLocation, ITag.Builder>> prepare(IResourceManager p_242224_1_, Executor p_242224_2_) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceLocation, ITag.Builder> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_242224_1_.listResources(this.directory, (p_242225_0_) -> {
            return p_242225_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.directory.length() + 1, s.length() - PATH_SUFFIX_LENGTH));

            try {
               for(IResource iresource : p_242224_1_.getResources(resourcelocation)) {
                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     JsonObject jsonobject = JSONUtils.fromJson(GSON, reader, JsonObject.class);
                     if (jsonobject == null) {
                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", this.name, resourcelocation1, resourcelocation, iresource.getSourceName());
                     } else {
                        map.computeIfAbsent(resourcelocation1, (p_242229_0_) -> {
                           return ITag.Builder.tag();
                        }).addFromJson(jsonobject, iresource.getSourceName());
                     }
                  } catch (RuntimeException | IOException ioexception) {
                     LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.name, resourcelocation1, resourcelocation, iresource.getSourceName(), ioexception);
                  } finally {
                     IOUtils.closeQuietly((Closeable)iresource);
                  }
               }
            } catch (IOException ioexception1) {
               LOGGER.error("Couldn't read {} tag list {} from {}", this.name, resourcelocation1, resourcelocation, ioexception1);
            }
         }

         return map;
      }, p_242224_2_);
   }

   public ITagCollection<T> load(Map<ResourceLocation, ITag.Builder> p_242226_1_) {
      Map<ResourceLocation, ITag<T>> map = Maps.newHashMap();
      Function<ResourceLocation, ITag<T>> function = map::get;
      Function<ResourceLocation, T> function1 = (p_242228_1_) -> {
         return this.idToValue.apply(p_242228_1_).orElse((T)null);
      };

      while(!p_242226_1_.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, ITag.Builder>> iterator = p_242226_1_.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, ITag.Builder> entry = iterator.next();
            Optional<ITag<T>> optional = entry.getValue().build(function, function1);
            if (optional.isPresent()) {
               map.put(entry.getKey(), optional.get());
               iterator.remove();
               flag = true;
            }
         }

         if (!flag) {
            break;
         }
      }

      p_242226_1_.forEach((p_242227_3_, p_242227_4_) -> {
         LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", this.name, p_242227_3_, p_242227_4_.getUnresolvedEntries(function, function1).map(Objects::toString).collect(Collectors.joining(",")));
      });
      return ITagCollection.of(map);
   }
}
