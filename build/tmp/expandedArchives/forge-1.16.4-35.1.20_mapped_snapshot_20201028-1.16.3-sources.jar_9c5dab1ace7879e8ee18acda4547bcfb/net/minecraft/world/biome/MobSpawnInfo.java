package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobSpawnInfo {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final MobSpawnInfo EMPTY = new MobSpawnInfo(0.1F, Stream.of(EntityClassification.values()).collect(ImmutableMap.toImmutableMap((classification) -> {
      return classification;
   }, (classification) -> {
      return ImmutableList.of();
   })), ImmutableMap.of(), false);
   public static final MapCodec<MobSpawnInfo> CODEC = RecordCodecBuilder.mapCodec((builder) -> {
      return builder.group(Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1F)).forGetter((spawnInfo) -> {
         return spawnInfo.creatureSpawnProbability;
      }), Codec.simpleMap(EntityClassification.CODEC, MobSpawnInfo.Spawners.CODEC.listOf().promotePartial(Util.func_240982_a_("Spawn data: ", LOGGER::error)), IStringSerializable.createKeyable(EntityClassification.values())).fieldOf("spawners").forGetter((spawnInfo) -> {
         return spawnInfo.spawners;
      }), Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnInfo.SpawnCosts.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((spawnInfo) -> {
         return spawnInfo.spawnCosts;
      }), Codec.BOOL.fieldOf("player_spawn_friendly").orElse(false).forGetter(MobSpawnInfo::isValidSpawnBiomeForPlayer)).apply(builder, MobSpawnInfo::new);
   });
   private final float creatureSpawnProbability;
   private final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners;
   private final Map<EntityType<?>, MobSpawnInfo.SpawnCosts> spawnCosts;
   private final boolean validSpawnBiomeForPlayer;
   private final java.util.Set<EntityClassification> typesView;
   private final java.util.Set<EntityType<?>> costView;

   private MobSpawnInfo(float creatureSpawnProbability, Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners, Map<EntityType<?>, MobSpawnInfo.SpawnCosts> spawnCosts, boolean isValidSpawnBiomeForPlayer) {
      this.creatureSpawnProbability = creatureSpawnProbability;
      this.spawners = spawners;
      this.spawnCosts = spawnCosts;
      this.validSpawnBiomeForPlayer = isValidSpawnBiomeForPlayer;
      this.typesView = java.util.Collections.unmodifiableSet(this.spawners.keySet());
      this.costView = java.util.Collections.unmodifiableSet(this.spawnCosts.keySet());
   }

   public List<MobSpawnInfo.Spawners> getSpawners(EntityClassification classification) {
      return this.spawners.getOrDefault(classification, ImmutableList.of());
   }

   public java.util.Set<EntityClassification> getSpawnerTypes() {
       return this.typesView;
   }

   @Nullable
   public MobSpawnInfo.SpawnCosts getSpawnCost(EntityType<?> entityType) {
      return this.spawnCosts.get(entityType);
   }

   public java.util.Set<EntityType<?>> getEntityTypes() {
       return this.costView;
   }

   public float getCreatureSpawnProbability() {
      return this.creatureSpawnProbability;
   }

   public boolean isValidSpawnBiomeForPlayer() {
      return this.validSpawnBiomeForPlayer;
   }

   public static class Builder {
      protected final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners = Stream.of(EntityClassification.values()).collect(ImmutableMap.toImmutableMap((classification) -> {
         return classification;
      }, (classification) -> {
         return Lists.newArrayList();
      }));
      protected final Map<EntityType<?>, MobSpawnInfo.SpawnCosts> spawnCosts = Maps.newLinkedHashMap();
      protected float creatureSpawnProbability = 0.1F;
      protected boolean validSpawnBiomeForPlayer;

      public MobSpawnInfo.Builder withSpawner(EntityClassification classification, MobSpawnInfo.Spawners spawner) {
         this.spawners.get(classification).add(spawner);
         return this;
      }

      public MobSpawnInfo.Builder withSpawnCost(EntityType<?> entityType, double spawnCostPerEntity, double maxSpawnCost) {
         this.spawnCosts.put(entityType, new MobSpawnInfo.SpawnCosts(maxSpawnCost, spawnCostPerEntity));
         return this;
      }

      public MobSpawnInfo.Builder withCreatureSpawnProbability(float probability) {
         this.creatureSpawnProbability = probability;
         return this;
      }

      public MobSpawnInfo.Builder isValidSpawnBiomeForPlayer() {
         this.validSpawnBiomeForPlayer = true;
         return this;
      }

      public MobSpawnInfo copy() {
         return new MobSpawnInfo(this.creatureSpawnProbability, this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ImmutableList.copyOf((Collection)entry.getValue());
         })), ImmutableMap.copyOf(this.spawnCosts), this.validSpawnBiomeForPlayer);
      }
   }

   public static class SpawnCosts {
      public static final Codec<MobSpawnInfo.SpawnCosts> CODEC = RecordCodecBuilder.create((builder) -> {
         return builder.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((spawnCosts) -> {
            return spawnCosts.maxSpawnCost;
         }), Codec.DOUBLE.fieldOf("charge").forGetter((spawnCosts) -> {
            return spawnCosts.entitySpawnCost;
         })).apply(builder, MobSpawnInfo.SpawnCosts::new);
      });
      /**
       * Determines the total amount of entities that can spawn in a location based on their current cost (e.g. a cost
       * of 0.1 and a max total of 1 means at most ten entities can spawn in the given locatoin).
       */
      private final double maxSpawnCost;
      /** Determines the cost per entity towards the maximum spawn cap. */
      private final double entitySpawnCost;

      private SpawnCosts(double maxSpawnCost, double entitySpawnCost) {
         this.maxSpawnCost = maxSpawnCost;
         this.entitySpawnCost = entitySpawnCost;
      }

      public double getMaxSpawnCost() {
         return this.maxSpawnCost;
      }

      public double getEntitySpawnCost() {
         return this.entitySpawnCost;
      }
   }

   public static class Spawners extends WeightedRandom.Item {
      public static final Codec<MobSpawnInfo.Spawners> CODEC = RecordCodecBuilder.create((builder) -> {
         return builder.group(Registry.ENTITY_TYPE.fieldOf("type").forGetter((spawner) -> {
            return spawner.type;
         }), Codec.INT.fieldOf("weight").forGetter((spawner) -> {
            return spawner.itemWeight;
         }), Codec.INT.fieldOf("minCount").forGetter((spawner) -> {
            return spawner.minCount;
         }), Codec.INT.fieldOf("maxCount").forGetter((spawner) -> {
            return spawner.maxCount;
         })).apply(builder, MobSpawnInfo.Spawners::new);
      });
      public final EntityType<?> type;
      public final int minCount;
      public final int maxCount;

      public Spawners(EntityType<?> type, int weight, int minCount, int maxCount) {
         super(weight);
         this.type = type.getClassification() == EntityClassification.MISC ? EntityType.PIG : type;
         this.minCount = minCount;
         this.maxCount = maxCount;
      }

      public String toString() {
         return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.itemWeight;
      }
   }
}
