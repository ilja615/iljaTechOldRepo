package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ResourceIndex {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, File> rootFiles = Maps.newHashMap();
   private final Map<ResourceLocation, File> namespacedFiles = Maps.newHashMap();

   protected ResourceIndex() {
   }

   public ResourceIndex(File p_i1047_1_, String p_i1047_2_) {
      File file1 = new File(p_i1047_1_, "objects");
      File file2 = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
      BufferedReader bufferedreader = null;

      try {
         bufferedreader = Files.newReader(file2, StandardCharsets.UTF_8);
         JsonObject jsonobject = JSONUtils.parse(bufferedreader);
         JsonObject jsonobject1 = JSONUtils.getAsJsonObject(jsonobject, "objects", (JsonObject)null);
         if (jsonobject1 != null) {
            for(Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
               JsonObject jsonobject2 = (JsonObject)entry.getValue();
               String s = entry.getKey();
               String[] astring = s.split("/", 2);
               String s1 = JSONUtils.getAsString(jsonobject2, "hash");
               File file3 = new File(file1, s1.substring(0, 2) + "/" + s1);
               if (astring.length == 1) {
                  this.rootFiles.put(astring[0], file3);
               } else {
                  this.namespacedFiles.put(new ResourceLocation(astring[0], astring[1]), file3);
               }
            }
         }
      } catch (JsonParseException jsonparseexception) {
         LOGGER.error("Unable to parse resource index file: {}", (Object)file2);
      } catch (FileNotFoundException filenotfoundexception) {
         LOGGER.error("Can't find the resource index file: {}", (Object)file2);
      } finally {
         IOUtils.closeQuietly((Reader)bufferedreader);
      }

   }

   @Nullable
   public File getFile(ResourceLocation p_188547_1_) {
      return this.namespacedFiles.get(p_188547_1_);
   }

   @Nullable
   public File getRootFile(String p_225638_1_) {
      return this.rootFiles.get(p_225638_1_);
   }

   public Collection<ResourceLocation> getFiles(String p_225639_1_, String p_225639_2_, int p_225639_3_, Predicate<String> p_225639_4_) {
      return this.namespacedFiles.keySet().stream().filter((p_229273_3_) -> {
         String s = p_229273_3_.getPath();
         return p_229273_3_.getNamespace().equals(p_225639_2_) && !s.endsWith(".mcmeta") && s.startsWith(p_225639_1_ + "/") && p_225639_4_.test(s);
      }).collect(Collectors.toList());
   }
}
