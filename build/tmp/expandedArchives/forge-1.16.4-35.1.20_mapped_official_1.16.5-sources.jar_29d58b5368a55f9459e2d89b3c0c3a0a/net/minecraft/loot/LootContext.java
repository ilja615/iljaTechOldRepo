package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class LootContext {
   private final Random random;
   private final float luck;
   private final ServerWorld level;
   private final Function<ResourceLocation, LootTable> lootTables;
   private final Set<LootTable> visitedTables = Sets.newLinkedHashSet();
   private final Function<ResourceLocation, ILootCondition> conditions;
   private final Set<ILootCondition> visitedConditions = Sets.newLinkedHashSet();
   private final Map<LootParameter<?>, Object> params;
   private final Map<ResourceLocation, LootContext.IDynamicDropProvider> dynamicDrops;

   private LootContext(Random p_i225885_1_, float p_i225885_2_, ServerWorld p_i225885_3_, Function<ResourceLocation, LootTable> p_i225885_4_, Function<ResourceLocation, ILootCondition> p_i225885_5_, Map<LootParameter<?>, Object> p_i225885_6_, Map<ResourceLocation, LootContext.IDynamicDropProvider> p_i225885_7_) {
      this.random = p_i225885_1_;
      this.luck = p_i225885_2_;
      this.level = p_i225885_3_;
      this.lootTables = p_i225885_4_;
      this.conditions = p_i225885_5_;
      this.params = ImmutableMap.copyOf(p_i225885_6_);
      this.dynamicDrops = ImmutableMap.copyOf(p_i225885_7_);
   }

   public boolean hasParam(LootParameter<?> p_216033_1_) {
      return this.params.containsKey(p_216033_1_);
   }

   public void addDynamicDrops(ResourceLocation p_216034_1_, Consumer<ItemStack> p_216034_2_) {
      LootContext.IDynamicDropProvider lootcontext$idynamicdropprovider = this.dynamicDrops.get(p_216034_1_);
      if (lootcontext$idynamicdropprovider != null) {
         lootcontext$idynamicdropprovider.add(this, p_216034_2_);
      }

   }

   @Nullable
   public <T> T getParamOrNull(LootParameter<T> p_216031_1_) {
      return (T)this.params.get(p_216031_1_);
   }

   public boolean addVisitedTable(LootTable p_186496_1_) {
      return this.visitedTables.add(p_186496_1_);
   }

   public void removeVisitedTable(LootTable p_186490_1_) {
      this.visitedTables.remove(p_186490_1_);
   }

   public boolean addVisitedCondition(ILootCondition p_227501_1_) {
      return this.visitedConditions.add(p_227501_1_);
   }

   public void removeVisitedCondition(ILootCondition p_227503_1_) {
      this.visitedConditions.remove(p_227503_1_);
   }

   public LootTable getLootTable(ResourceLocation p_227502_1_) {
      return this.lootTables.apply(p_227502_1_);
   }

   public ILootCondition getCondition(ResourceLocation p_227504_1_) {
      return this.conditions.apply(p_227504_1_);
   }

   public Random getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.luck;
   }

   public ServerWorld getLevel() {
      return this.level;
   }

   public int getLootingModifier() {
      return net.minecraftforge.common.ForgeHooks.getLootingLevel(getParamOrNull(LootParameters.THIS_ENTITY), getParamOrNull(LootParameters.KILLER_ENTITY), getParamOrNull(LootParameters.DAMAGE_SOURCE));
   }

   public static class Builder {
      private final ServerWorld level;
      private final Map<LootParameter<?>, Object> params = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootContext.IDynamicDropProvider> dynamicDrops = Maps.newHashMap();
      private Random random;
      private float luck;

      public Builder(ServerWorld p_i46993_1_) {
         this.level = p_i46993_1_;
      }

      public Builder(LootContext context) {
         this.level = context.level;
         this.params.putAll(context.params);
         this.dynamicDrops.putAll(context.dynamicDrops);
         this.random = context.random;
         this.luck = context.luck;
      }

      public LootContext.Builder withRandom(Random p_216023_1_) {
         this.random = p_216023_1_;
         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long p_216016_1_) {
         if (p_216016_1_ != 0L) {
            this.random = new Random(p_216016_1_);
         }

         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long p_216020_1_, Random p_216020_3_) {
         if (p_216020_1_ == 0L) {
            this.random = p_216020_3_;
         } else {
            this.random = new Random(p_216020_1_);
         }

         return this;
      }

      public LootContext.Builder withLuck(float p_186469_1_) {
         this.luck = p_186469_1_;
         return this;
      }

      public <T> LootContext.Builder withParameter(LootParameter<T> p_216015_1_, T p_216015_2_) {
         this.params.put(p_216015_1_, p_216015_2_);
         return this;
      }

      public <T> LootContext.Builder withOptionalParameter(LootParameter<T> p_216021_1_, @Nullable T p_216021_2_) {
         if (p_216021_2_ == null) {
            this.params.remove(p_216021_1_);
         } else {
            this.params.put(p_216021_1_, p_216021_2_);
         }

         return this;
      }

      public LootContext.Builder withDynamicDrop(ResourceLocation p_216017_1_, LootContext.IDynamicDropProvider p_216017_2_) {
         LootContext.IDynamicDropProvider lootcontext$idynamicdropprovider = this.dynamicDrops.put(p_216017_1_, p_216017_2_);
         if (lootcontext$idynamicdropprovider != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
         } else {
            return this;
         }
      }

      public ServerWorld getLevel() {
         return this.level;
      }

      public <T> T getParameter(LootParameter<T> p_216024_1_) {
         T t = (T)this.params.get(p_216024_1_);
         if (t == null) {
            throw new IllegalArgumentException("No parameter " + p_216024_1_);
         } else {
            return t;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(LootParameter<T> p_216019_1_) {
         return (T)this.params.get(p_216019_1_);
      }

      public LootContext create(LootParameterSet p_216022_1_) {
         Set<LootParameter<?>> set = Sets.difference(this.params.keySet(), p_216022_1_.getAllowed());
         if (!set.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
         } else {
            Set<LootParameter<?>> set1 = Sets.difference(p_216022_1_.getRequired(), this.params.keySet());
            if (!set1.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + set1);
            } else {
               Random random = this.random;
               if (random == null) {
                  random = new Random();
               }

               MinecraftServer minecraftserver = this.level.getServer();
               return new LootContext(random, this.luck, this.level, minecraftserver.getLootTables()::get, minecraftserver.getPredicateManager()::get, this.params, this.dynamicDrops);
            }
         }
      }
   }

   public static enum EntityTarget {
      THIS("this", LootParameters.THIS_ENTITY),
      KILLER("killer", LootParameters.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootParameters.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER);

      private final String name;
      private final LootParameter<? extends Entity> param;

      private EntityTarget(String p_i50476_3_, LootParameter<? extends Entity> p_i50476_4_) {
         this.name = p_i50476_3_;
         this.param = p_i50476_4_;
      }

      public LootParameter<? extends Entity> getParam() {
         return this.param;
      }

      public static LootContext.EntityTarget getByName(String p_186482_0_) {
         for(LootContext.EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.name.equals(p_186482_0_)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + p_186482_0_);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public void write(JsonWriter p_write_1_, LootContext.EntityTarget p_write_2_) throws IOException {
            p_write_1_.value(p_write_2_.name);
         }

         public LootContext.EntityTarget read(JsonReader p_read_1_) throws IOException {
            return LootContext.EntityTarget.getByName(p_read_1_.nextString());
         }
      }
   }

   @FunctionalInterface
   public interface IDynamicDropProvider {
      void add(LootContext p_add_1_, Consumer<ItemStack> p_add_2_);
   }
}
