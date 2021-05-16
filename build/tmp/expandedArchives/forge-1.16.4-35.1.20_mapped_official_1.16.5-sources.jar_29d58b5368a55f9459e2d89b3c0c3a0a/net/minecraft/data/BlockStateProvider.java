package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockStateProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public BlockStateProvider(DataGenerator p_i232520_1_) {
      this.generator = p_i232520_1_;
   }

   public void run(DirectoryCache p_200398_1_) {
      Path path = this.generator.getOutputFolder();
      Map<Block, IFinishedBlockState> map = Maps.newHashMap();
      Consumer<IFinishedBlockState> consumer = (p_240085_1_) -> {
         Block block = p_240085_1_.getBlock();
         IFinishedBlockState ifinishedblockstate = map.put(block, p_240085_1_);
         if (ifinishedblockstate != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + block);
         }
      };
      Map<ResourceLocation, Supplier<JsonElement>> map1 = Maps.newHashMap();
      Set<Item> set = Sets.newHashSet();
      BiConsumer<ResourceLocation, Supplier<JsonElement>> biconsumer = (p_240086_1_, p_240086_2_) -> {
         Supplier<JsonElement> supplier = map1.put(p_240086_1_, p_240086_2_);
         if (supplier != null) {
            throw new IllegalStateException("Duplicate model definition for " + p_240086_1_);
         }
      };
      Consumer<Item> consumer1 = set::add;
      (new BlockModelProvider(consumer, biconsumer, consumer1)).run();
      (new ItemModelProvider(biconsumer)).run();
      List<Block> list = Registry.BLOCK.stream().filter((p_240084_1_) -> {
         return !map.containsKey(p_240084_1_);
      }).collect(Collectors.toList());
      if (!list.isEmpty()) {
         throw new IllegalStateException("Missing blockstate definitions for: " + list);
      } else {
         Registry.BLOCK.forEach((p_240087_2_) -> {
            Item item = Item.BY_BLOCK.get(p_240087_2_);
            if (item != null) {
               if (set.contains(item)) {
                  return;
               }

               ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(item);
               if (!map1.containsKey(resourcelocation)) {
                  map1.put(resourcelocation, new BlockModelWriter(ModelsResourceUtil.getModelLocation(p_240087_2_)));
               }
            }

         });
         this.saveCollection(p_200398_1_, path, map, BlockStateProvider::createBlockStatePath);
         this.saveCollection(p_200398_1_, path, map1, BlockStateProvider::createModelPath);
      }
   }

   private <T> void saveCollection(DirectoryCache p_240081_1_, Path p_240081_2_, Map<T, ? extends Supplier<JsonElement>> p_240081_3_, BiFunction<Path, T, Path> p_240081_4_) {
      p_240081_3_.forEach((p_240088_3_, p_240088_4_) -> {
         Path path = p_240081_4_.apply(p_240081_2_, p_240088_3_);

         try {
            IDataProvider.save(GSON, p_240081_1_, p_240088_4_.get(), path);
         } catch (Exception exception) {
            LOGGER.error("Couldn't save {}", path, exception);
         }

      });
   }

   private static Path createBlockStatePath(Path p_240082_0_, Block p_240082_1_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240082_1_);
      return p_240082_0_.resolve("assets/" + resourcelocation.getNamespace() + "/blockstates/" + resourcelocation.getPath() + ".json");
   }

   private static Path createModelPath(Path p_240083_0_, ResourceLocation p_240083_1_) {
      return p_240083_0_.resolve("assets/" + p_240083_1_.getNamespace() + "/models/" + p_240083_1_.getPath() + ".json");
   }

   public String getName() {
      return "Block State Definitions";
   }
}
