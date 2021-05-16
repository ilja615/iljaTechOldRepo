package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BiomeProvider(DataGenerator p_i242077_1_) {
      this.generator = p_i242077_1_;
   }

   public void run(DirectoryCache p_200398_1_) {
      Path path = this.generator.getOutputFolder();

      for(Entry<RegistryKey<Biome>, Biome> entry : WorldGenRegistries.BIOME.entrySet()) {
         Path path1 = createPath(path, entry.getKey().location());
         Biome biome = entry.getValue();
         Function<Supplier<Biome>, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(Biome.CODEC);

         try {
            Optional<JsonElement> optional = function.apply(() -> {
               return biome;
            }).result();
            if (optional.isPresent()) {
               IDataProvider.save(GSON, p_200398_1_, optional.get(), path1);
            } else {
               LOGGER.error("Couldn't serialize biome {}", (Object)path1);
            }
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save biome {}", path1, ioexception);
         }
      }

   }

   private static Path createPath(Path p_244199_0_, ResourceLocation p_244199_1_) {
      return p_244199_0_.resolve("reports/biomes/" + p_244199_1_.getPath() + ".json");
   }

   public String getName() {
      return "Biomes";
   }
}
