package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = LootSerializers.createLootTableSerializer().create();
   private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();
   private final LootPredicateManager predicateManager;

   public LootTableManager(LootPredicateManager p_i225887_1_) {
      super(GSON, "loot_tables");
      this.predicateManager = p_i225887_1_;
   }

   public LootTable get(ResourceLocation p_186521_1_) {
      return this.tables.getOrDefault(p_186521_1_, LootTable.EMPTY);
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
      JsonElement jsonelement = p_212853_1_.remove(LootTables.EMPTY);
      if (jsonelement != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
      }

      p_212853_1_.forEach((p_237403_1_, p_237403_2_) -> {
         try (net.minecraft.resources.IResource res = p_212853_2_.getResource(getPreparedPath(p_237403_1_));){
            LootTable loottable = net.minecraftforge.common.ForgeHooks.loadLootTable(GSON, p_237403_1_, p_237403_2_, res == null || !res.getSourceName().equals("Default"), this);
            builder.put(p_237403_1_, loottable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_237403_1_, exception);
         }

      });
      builder.put(LootTables.EMPTY, LootTable.EMPTY);
      ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
      ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.ALL_PARAMS, this.predicateManager::get, immutablemap::get);
      immutablemap.forEach((p_227509_1_, p_227509_2_) -> {
         validate(validationtracker, p_227509_1_, p_227509_2_);
      });
      validationtracker.getProblems().forEach((p_215303_0_, p_215303_1_) -> {
         LOGGER.warn("Found validation problem in " + p_215303_0_ + ": " + p_215303_1_);
      });
      this.tables = immutablemap;
   }

   public static void validate(ValidationTracker p_227508_0_, ResourceLocation p_227508_1_, LootTable p_227508_2_) {
      p_227508_2_.validate(p_227508_0_.setParams(p_227508_2_.getParamSet()).enterTable("{" + p_227508_1_ + "}", p_227508_1_));
   }

   public static JsonElement serialize(LootTable p_215301_0_) {
      return GSON.toJsonTree(p_215301_0_);
   }

   public Set<ResourceLocation> getIds() {
      return this.tables.keySet();
   }
}
