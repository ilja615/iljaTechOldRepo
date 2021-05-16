package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class StandaloneLootEntry extends LootEntry {
   protected final int weight;
   protected final int quality;
   protected final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
   private final ILootGenerator entry = new StandaloneLootEntry.Generator() {
      public void createItemStack(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_) {
         StandaloneLootEntry.this.createItemStack(ILootFunction.decorate(StandaloneLootEntry.this.compositeFunction, p_216188_1_, p_216188_2_), p_216188_2_);
      }
   };

   protected StandaloneLootEntry(int p_i51253_1_, int p_i51253_2_, ILootCondition[] p_i51253_3_, ILootFunction[] p_i51253_4_) {
      super(p_i51253_3_);
      this.weight = p_i51253_1_;
      this.quality = p_i51253_2_;
      this.functions = p_i51253_4_;
      this.compositeFunction = LootFunctionManager.compose(p_i51253_4_);
   }

   public void validate(ValidationTracker p_225579_1_) {
      super.validate(p_225579_1_);

      for(int i = 0; i < this.functions.length; ++i) {
         this.functions[i].validate(p_225579_1_.forChild(".functions[" + i + "]"));
      }

   }

   protected abstract void createItemStack(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_);

   public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      if (this.canRun(p_expand_1_)) {
         p_expand_2_.accept(this.entry);
         return true;
      } else {
         return false;
      }
   }

   public static StandaloneLootEntry.Builder<?> simpleBuilder(StandaloneLootEntry.ILootEntryBuilder p_216156_0_) {
      return new StandaloneLootEntry.BuilderImpl(p_216156_0_);
   }

   public abstract static class Builder<T extends StandaloneLootEntry.Builder<T>> extends LootEntry.Builder<T> implements ILootFunctionConsumer<T> {
      protected int weight = 1;
      protected int quality = 0;
      private final List<ILootFunction> functions = Lists.newArrayList();

      public T apply(ILootFunction.IBuilder p_212841_1_) {
         this.functions.add(p_212841_1_.build());
         return this.getThis();
      }

      protected ILootFunction[] getFunctions() {
         return this.functions.toArray(new ILootFunction[0]);
      }

      public T setWeight(int p_216086_1_) {
         this.weight = p_216086_1_;
         return this.getThis();
      }

      public T setQuality(int p_216085_1_) {
         this.quality = p_216085_1_;
         return this.getThis();
      }
   }

   static class BuilderImpl extends StandaloneLootEntry.Builder<StandaloneLootEntry.BuilderImpl> {
      private final StandaloneLootEntry.ILootEntryBuilder constructor;

      public BuilderImpl(StandaloneLootEntry.ILootEntryBuilder p_i50485_1_) {
         this.constructor = p_i50485_1_;
      }

      protected StandaloneLootEntry.BuilderImpl getThis() {
         return this;
      }

      public LootEntry build() {
         return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
      }
   }

   public abstract class Generator implements ILootGenerator {
      protected Generator() {
      }

      public int getWeight(float p_186361_1_) {
         return Math.max(MathHelper.floor((float)StandaloneLootEntry.this.weight + (float)StandaloneLootEntry.this.quality * p_186361_1_), 0);
      }
   }

   @FunctionalInterface
   public interface ILootEntryBuilder {
      StandaloneLootEntry build(int p_build_1_, int p_build_2_, ILootCondition[] p_build_3_, ILootFunction[] p_build_4_);
   }

   public abstract static class Serializer<T extends StandaloneLootEntry> extends LootEntry.Serializer<T> {
      public void serializeCustom(JsonObject p_230422_1_, T p_230422_2_, JsonSerializationContext p_230422_3_) {
         if (p_230422_2_.weight != 1) {
            p_230422_1_.addProperty("weight", p_230422_2_.weight);
         }

         if (p_230422_2_.quality != 0) {
            p_230422_1_.addProperty("quality", p_230422_2_.quality);
         }

         if (!ArrayUtils.isEmpty((Object[])p_230422_2_.functions)) {
            p_230422_1_.add("functions", p_230422_3_.serialize(p_230422_2_.functions));
         }

      }

      public final T deserializeCustom(JsonObject p_230421_1_, JsonDeserializationContext p_230421_2_, ILootCondition[] p_230421_3_) {
         int i = JSONUtils.getAsInt(p_230421_1_, "weight", 1);
         int j = JSONUtils.getAsInt(p_230421_1_, "quality", 0);
         ILootFunction[] ailootfunction = JSONUtils.getAsObject(p_230421_1_, "functions", new ILootFunction[0], p_230421_2_, ILootFunction[].class);
         return this.deserialize(p_230421_1_, p_230421_2_, i, j, p_230421_3_, ailootfunction);
      }

      protected abstract T deserialize(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_);
   }
}
