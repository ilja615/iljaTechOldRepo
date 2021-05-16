package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.data.loot.FishingLootTables;
import net.minecraft.data.loot.GiftLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.PiglinBarteringAddition;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;
   private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> subProviders = ImmutableList.of(Pair.of(FishingLootTables::new, LootParameterSets.FISHING), Pair.of(ChestLootTables::new, LootParameterSets.CHEST), Pair.of(EntityLootTables::new, LootParameterSets.ENTITY), Pair.of(BlockLootTables::new, LootParameterSets.BLOCK), Pair.of(PiglinBarteringAddition::new, LootParameterSets.PIGLIN_BARTER), Pair.of(GiftLootTables::new, LootParameterSets.GIFT));

   public LootTableProvider(DataGenerator p_i50789_1_) {
      this.generator = p_i50789_1_;
   }

   public void run(DirectoryCache p_200398_1_) {
      Path path = this.generator.getOutputFolder();
      Map<ResourceLocation, LootTable> map = Maps.newHashMap();
      this.getTables().forEach((p_218438_1_) -> {
         p_218438_1_.getFirst().get().accept((p_218437_2_, p_218437_3_) -> {
            if (map.put(p_218437_2_, p_218437_3_.setParamSet(p_218438_1_.getSecond()).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + p_218437_2_);
            }
         });
      });
      ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.ALL_PARAMS, (p_229442_0_) -> {
         return null;
      }, map::get);

      validate(map, validationtracker);

      Multimap<String, String> multimap = validationtracker.getProblems();
      if (!multimap.isEmpty()) {
         multimap.forEach((p_229440_0_, p_229440_1_) -> {
            LOGGER.warn("Found validation problem in " + p_229440_0_ + ": " + p_229440_1_);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         map.forEach((p_229441_2_, p_229441_3_) -> {
            Path path1 = createPath(path, p_229441_2_);

            try {
               IDataProvider.save(GSON, p_200398_1_, LootTableManager.serialize(p_229441_3_), path1);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't save loot table {}", path1, ioexception);
            }

         });
      }
   }

   protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
      return subProviders;
   }

   protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
      for(ResourceLocation resourcelocation : Sets.difference(LootTables.all(), map.keySet())) {
         validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
      }

      map.forEach((p_218436_2_, p_218436_3_) -> {
         LootTableManager.validate(validationtracker, p_218436_2_, p_218436_3_);
      });
   }

   private static Path createPath(Path p_218439_0_, ResourceLocation p_218439_1_) {
      return p_218439_0_.resolve("data/" + p_218439_1_.getNamespace() + "/loot_tables/" + p_218439_1_.getPath() + ".json");
   }

   public String getName() {
      return "LootTables";
   }
}
