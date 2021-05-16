package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path path;
   private final Path cachePath;
   private int hits;
   private final Map<Path, String> oldCache = Maps.newHashMap();
   private final Map<Path, String> newCache = Maps.newHashMap();
   private final Set<Path> keep = Sets.newHashSet();

   public DirectoryCache(Path p_i49352_1_, String p_i49352_2_) throws IOException {
      this.path = p_i49352_1_;
      Path path = p_i49352_1_.resolve(".cache");
      Files.createDirectories(path);
      this.cachePath = path.resolve(p_i49352_2_);
      this.walkOutputFiles().forEach((p_209395_1_) -> {
         String s = this.oldCache.put(p_209395_1_, "");
      });
      if (Files.isReadable(this.cachePath)) {
         IOUtils.readLines(Files.newInputStream(this.cachePath), Charsets.UTF_8).forEach((p_208315_2_) -> {
            int i = p_208315_2_.indexOf(32);
            this.oldCache.put(p_i49352_1_.resolve(p_208315_2_.substring(i + 1)), p_208315_2_.substring(0, i));
         });
      }

   }

   public void purgeStaleAndWrite() throws IOException {
      this.removeStale();

      Writer writer;
      try {
         writer = Files.newBufferedWriter(this.cachePath);
      } catch (IOException ioexception) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cachePath, ioexception.toString());
         return;
      }

      IOUtils.writeLines(this.newCache.entrySet().stream().map((p_208319_1_) -> {
         return (String)p_208319_1_.getValue() + ' ' + this.path.relativize(p_208319_1_.getKey()).toString().replace('\\', '/'); //Forge: Standardize file paths.
      }).sorted(java.util.Comparator.comparing(a -> a.split(" ")[1])).collect(Collectors.toList()), System.lineSeparator(), writer);
      writer.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.newCache.size() - this.hits, this.oldCache.size());
   }

   @Nullable
   public String getHash(Path p_208323_1_) {
      return this.oldCache.get(p_208323_1_);
   }

   public void putNew(Path p_208316_1_, String p_208316_2_) {
      this.newCache.put(p_208316_1_, p_208316_2_);
      if (Objects.equals(this.oldCache.remove(p_208316_1_), p_208316_2_)) {
         ++this.hits;
      }

   }

   public boolean had(Path p_208320_1_) {
      return this.oldCache.containsKey(p_208320_1_);
   }

   public void keep(Path p_218456_1_) {
      this.keep.add(p_218456_1_);
   }

   private void removeStale() throws IOException {
      this.walkOutputFiles().forEach((p_208322_1_) -> {
         if (this.had(p_208322_1_) && !this.keep.contains(p_208322_1_)) {
            try {
               Files.delete(p_208322_1_);
            } catch (IOException ioexception) {
               LOGGER.debug("Unable to delete: {} ({})", p_208322_1_, ioexception.toString());
            }
         }

      });
   }

   private Stream<Path> walkOutputFiles() throws IOException {
      return Files.walk(this.path).filter((p_209397_1_) -> {
         return !Objects.equals(this.cachePath, p_209397_1_) && !Files.isDirectory(p_209397_1_);
      });
   }
}
