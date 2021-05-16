package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class BlockListReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BlockListReport(DataGenerator p_i48265_1_) {
      this.generator = p_i48265_1_;
   }

   public void run(DirectoryCache p_200398_1_) throws IOException {
      JsonObject jsonobject = new JsonObject();

      for(Block block : Registry.BLOCK) {
         ResourceLocation resourcelocation = Registry.BLOCK.getKey(block);
         JsonObject jsonobject1 = new JsonObject();
         StateContainer<Block, BlockState> statecontainer = block.getStateDefinition();
         if (!statecontainer.getProperties().isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();

            for(Property<?> property : statecontainer.getProperties()) {
               JsonArray jsonarray = new JsonArray();

               for(Comparable<?> comparable : property.getPossibleValues()) {
                  jsonarray.add(Util.getPropertyName(property, comparable));
               }

               jsonobject2.add(property.getName(), jsonarray);
            }

            jsonobject1.add("properties", jsonobject2);
         }

         JsonArray jsonarray1 = new JsonArray();

         for(BlockState blockstate : statecontainer.getPossibleStates()) {
            JsonObject jsonobject3 = new JsonObject();
            JsonObject jsonobject4 = new JsonObject();

            for(Property<?> property1 : statecontainer.getProperties()) {
               jsonobject4.addProperty(property1.getName(), Util.getPropertyName(property1, blockstate.getValue(property1)));
            }

            if (jsonobject4.size() > 0) {
               jsonobject3.add("properties", jsonobject4);
            }

            jsonobject3.addProperty("id", Block.getId(blockstate));
            if (blockstate == block.defaultBlockState()) {
               jsonobject3.addProperty("default", true);
            }

            jsonarray1.add(jsonobject3);
         }

         jsonobject1.add("states", jsonarray1);
         jsonobject.add(resourcelocation.toString(), jsonobject1);
      }

      Path path = this.generator.getOutputFolder().resolve("reports/blocks.json");
      IDataProvider.save(GSON, p_200398_1_, jsonobject, path);
   }

   public String getName() {
      return "Block List";
   }
}
