package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryDumpReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public RegistryDumpReport(DataGenerator p_i50790_1_) {
      this.generator = p_i50790_1_;
   }

   public void run(DirectoryCache p_200398_1_) throws IOException {
      JsonObject jsonobject = new JsonObject();
      Registry.REGISTRY.keySet().forEach((p_218431_1_) -> {
         jsonobject.add(p_218431_1_.toString(), dumpRegistry(Registry.REGISTRY.get(p_218431_1_)));
      });
      Path path = this.generator.getOutputFolder().resolve("reports/registries.json");
      IDataProvider.save(GSON, p_200398_1_, jsonobject, path);
   }

   private static <T> JsonElement dumpRegistry(Registry<T> p_239828_0_) {
      JsonObject jsonobject = new JsonObject();
      if (p_239828_0_ instanceof DefaultedRegistry) {
         ResourceLocation resourcelocation = ((DefaultedRegistry)p_239828_0_).getDefaultKey();
         jsonobject.addProperty("default", resourcelocation.toString());
      }

      int j = ((Registry)Registry.REGISTRY).getId(p_239828_0_);
      jsonobject.addProperty("protocol_id", j);
      JsonObject jsonobject1 = new JsonObject();

      for(ResourceLocation resourcelocation1 : p_239828_0_.keySet()) {
         T t = p_239828_0_.get(resourcelocation1);
         int i = p_239828_0_.getId(t);
         JsonObject jsonobject2 = new JsonObject();
         jsonobject2.addProperty("protocol_id", i);
         jsonobject1.add(resourcelocation1.toString(), jsonobject2);
      }

      jsonobject.add("entries", jsonobject1);
      return jsonobject;
   }

   public String getName() {
      return "Registry Dump";
   }
}
