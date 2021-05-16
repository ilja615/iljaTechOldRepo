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
   public static final MobSpawnInfo EMPTY = new MobSpawnInfo(0.1F, Stream.of(EntityClassification.values()).collect(ImmutableMap.toImmutableMap((p_242565_0_) -> {
      return p_242565_0_;
   }, (p_242563_0_) -> {
      return ImmutableList.of();
   })), ImmutableMap.of(), false);
   public static final MapCodec<MobSpawnInfo> CODEC = RecordCodecBuilder.mapCodec((p_242561_0_) -> {
      return p_242561_0_.group(Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1F)).forGetter((p_242566_0_) -> {
         return p_242566_0_.creatureGenerationProbability;
      }), Codec.simpleMap(EntityClassification.CODEC, MobSpawnInfo.Spawners.CODEC.listOf().promotePartial(Util.prefix("Spawn data: ", LOGGER::error)), IStringSerializable.keys(EntityClassification.values())).fieldOf("spawners").forGetter((p_242564_0_) -> {
         return p_242564_0_.spawners;
      }), Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnInfo.SpawnCosts.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((p_242560_0_) -> {
         return p_242560_0_.mobSpawnCosts;
      }), Codec.BOOL.fieldOf("player_spawn_friendly").orElse(false).forGetter(MobSpawnInfo::playerSpawnFriendly)).apply(p_242561_0_, MobSpawnInfo::new);
   });
   private final float creatureGenerationProbability;
   private final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners;
   private final Map<EntityType<?>, MobSpawnInfo.SpawnCosts> mobSpawnCosts;
   private final boolean playerSpawnFriendly;
   private final java.util.Set<EntityClassification> typesView;
   private final java.util.Set<EntityType<?>> costView;

   private MobSpawnInfo(float p_i241946_1_, Map<EntityClassification, List<MobSpawnInfo.Spawners>> p_i241946_2_, Map<EntityType<?>, MobSpawnInfo.SpawnCosts> p_i241946_3_, boolean p_i241946_4_) {
      this.creatureGenerationProbability = p_i241946_1_;
      this.spawners = p_i241946_2_;
      this.mobSpawnCosts = p_i241946_3_;
      this.playerSpawnFriendly = p_i241946_4_;
      this.typesView = java.util.Collections.unmodifiableSet(this.spawners.keySet());
      this.costView = java.util.Collections.unmodifiableSet(this.mobSpawnCosts.keySet());
   }

   public List<MobSpawnInfo.Spawners> getMobs(EntityClassification p_242559_1_) {
      return this.spawners.getOrDefault(p_242559_1_, ImmutableList.of());
   }

   public java.util.Set<EntityClassification> getSpawnerTypes() {
       return this.typesView;
   }

   @Nullable
   public MobSpawnInfo.SpawnCosts getMobSpawnCost(EntityType<?> p_242558_1_) {
      return this.mobSpawnCosts.get(p_242558_1_);
   }

   public java.util.Set<EntityType<?>> getEntityTypes() {
       return this.costView;
   }

   public float getCreatureProbability() {
      return this.creatureGenerationProbability;
   }

   public boolean playerSpawnFriendly() {
      return this.playerSpawnFriendly;
   }

   public static class Builder {
      protected final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners = Stream.of(EntityClassification.values()).collect(ImmutableMap.toImmutableMap((p_242578_0_) -> {
         return p_242578_0_;
      }, (p_242574_0_) -> {
         return Lists.newArrayList();
      }));
      protected final Map<EntityType<?>, MobSpawnInfo.SpawnCosts> mobSpawnCosts = Maps.newLinkedHashMap();
      protected float creatureGenerationProbability = 0.1F;
      protected boolean playerCanSpawn;

      public MobSpawnInfo.Builder addSpawn(EntityClassification p_242575_1_, MobSpawnInfo.Spawners p_242575_2_) {
         this.spawners.get(p_242575_1_).add(p_242575_2_);
         return this;
      }

      public MobSpawnInfo.Builder addMobCharge(EntityType<?> p_242573_1_, double p_242573_2_, double p_242573_4_) {
         this.mobSpawnCosts.put(p_242573_1_, new MobSpawnInfo.SpawnCosts(p_242573_4_, p_242573_2_));
         return this;
      }

      public MobSpawnInfo.Builder creatureGenerationProbability(float p_242572_1_) {
         this.creatureGenerationProbability = p_242572_1_;
         return this;
      }

      public MobSpawnInfo.Builder setPlayerCanSpawn() {
         this.playerCanSpawn = true;
         return this;
      }

      public MobSpawnInfo build() {
         return new MobSpawnInfo(this.creatureGenerationProbability, this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_242576_0_) -> {
            return ImmutableList.copyOf((Collection)p_242576_0_.getValue());
         })), ImmutableMap.copyOf(this.mobSpawnCosts), this.playerCanSpawn);
      }
   }

   public static class SpawnCosts {
      public static final Codec<MobSpawnInfo.SpawnCosts> CODEC = RecordCodecBuilder.create((p_242584_0_) -> {
         return p_242584_0_.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((p_242586_0_) -> {
            return p_242586_0_.energyBudget;
         }), Codec.DOUBLE.fieldOf("charge").forGetter((p_242583_0_) -> {
            return p_242583_0_.charge;
         })).apply(p_242584_0_, MobSpawnInfo.SpawnCosts::new);
      });
      private final double energyBudget;
      private final double charge;

      private SpawnCosts(double p_i241948_1_, double p_i241948_3_) {
         this.energyBudget = p_i241948_1_;
         this.charge = p_i241948_3_;
      }

      public double getEnergyBudget() {
         return this.energyBudget;
      }

      public double getCharge() {
         return this.charge;
      }
   }

   public static class Spawners extends WeightedRandom.Item {
      public static final Codec<MobSpawnInfo.Spawners> CODEC = RecordCodecBuilder.create((p_242592_0_) -> {
         return p_242592_0_.group(Registry.ENTITY_TYPE.fieldOf("type").forGetter((p_242595_0_) -> {
            return p_242595_0_.type;
         }), Codec.INT.fieldOf("weight").forGetter((p_242594_0_) -> {
            return p_242594_0_.weight;
         }), Codec.INT.fieldOf("minCount").forGetter((p_242593_0_) -> {
            return p_242593_0_.minCount;
         }), Codec.INT.fieldOf("maxCount").forGetter((p_242591_0_) -> {
            return p_242591_0_.maxCount;
         })).apply(p_242592_0_, MobSpawnInfo.Spawners::new);
      });
      public final EntityType<?> type;
      public final int minCount;
      public final int maxCount;

      public Spawners(EntityType<?> p_i241950_1_, int p_i241950_2_, int p_i241950_3_, int p_i241950_4_) {
         super(p_i241950_2_);
         this.type = p_i241950_1_.getCategory() == EntityClassification.MISC ? EntityType.PIG : p_i241950_1_;
         this.minCount = p_i241950_3_;
         this.maxCount = p_i241950_4_;
      }

      public String toString() {
         return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.weight;
      }
   }
}
