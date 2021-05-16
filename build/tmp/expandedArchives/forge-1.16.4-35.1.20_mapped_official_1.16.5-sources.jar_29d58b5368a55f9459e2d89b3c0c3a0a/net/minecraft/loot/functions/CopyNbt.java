package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;

public class CopyNbt extends LootFunction {
   private final CopyNbt.Source source;
   private final List<CopyNbt.Operation> operations;
   private static final Function<Entity, INBT> ENTITY_GETTER = NBTPredicate::getEntityTagToCompare;
   private static final Function<TileEntity, INBT> BLOCK_ENTITY_GETTER = (p_215882_0_) -> {
      return p_215882_0_.save(new CompoundNBT());
   };

   private CopyNbt(ILootCondition[] p_i51240_1_, CopyNbt.Source p_i51240_2_, List<CopyNbt.Operation> p_i51240_3_) {
      super(p_i51240_1_);
      this.source = p_i51240_2_;
      this.operations = ImmutableList.copyOf(p_i51240_3_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.COPY_NBT;
   }

   private static NBTPathArgument.NBTPath compileNbtPath(String p_215880_0_) {
      try {
         return (new NBTPathArgument()).parse(new StringReader(p_215880_0_));
      } catch (CommandSyntaxException commandsyntaxexception) {
         throw new IllegalArgumentException("Failed to parse path " + p_215880_0_, commandsyntaxexception);
      }
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      INBT inbt = this.source.getter.apply(p_215859_2_);
      if (inbt != null) {
         this.operations.forEach((p_215885_2_) -> {
            p_215885_2_.apply(p_215859_1_::getOrCreateTag, inbt);
         });
      }

      return p_215859_1_;
   }

   public static CopyNbt.Builder copyData(CopyNbt.Source p_215881_0_) {
      return new CopyNbt.Builder(p_215881_0_);
   }

   public static enum Action {
      REPLACE("replace") {
         public void merge(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            p_216227_2_.set(p_216227_1_, Iterables.getLast(p_216227_3_)::copy);
         }
      },
      APPEND("append") {
         public void merge(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            List<INBT> list = p_216227_2_.getOrCreate(p_216227_1_, ListNBT::new);
            list.forEach((p_216232_1_) -> {
               if (p_216232_1_ instanceof ListNBT) {
                  p_216227_3_.forEach((p_216231_1_) -> {
                     ((ListNBT)p_216232_1_).add(p_216231_1_.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void merge(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            List<INBT> list = p_216227_2_.getOrCreate(p_216227_1_, CompoundNBT::new);
            list.forEach((p_216234_1_) -> {
               if (p_216234_1_ instanceof CompoundNBT) {
                  p_216227_3_.forEach((p_216233_1_) -> {
                     if (p_216233_1_ instanceof CompoundNBT) {
                        ((CompoundNBT)p_216234_1_).merge((CompoundNBT)p_216233_1_);
                     }

                  });
               }

            });
         }
      };

      private final String name;

      public abstract void merge(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException;

      private Action(String p_i50670_3_) {
         this.name = p_i50670_3_;
      }

      public static CopyNbt.Action getByName(String p_216229_0_) {
         for(CopyNbt.Action copynbt$action : values()) {
            if (copynbt$action.name.equals(p_216229_0_)) {
               return copynbt$action;
            }
         }

         throw new IllegalArgumentException("Invalid merge strategy" + p_216229_0_);
      }
   }

   public static class Builder extends LootFunction.Builder<CopyNbt.Builder> {
      private final CopyNbt.Source source;
      private final List<CopyNbt.Operation> ops = Lists.newArrayList();

      private Builder(CopyNbt.Source p_i50675_1_) {
         this.source = p_i50675_1_;
      }

      public CopyNbt.Builder copy(String p_216055_1_, String p_216055_2_, CopyNbt.Action p_216055_3_) {
         this.ops.add(new CopyNbt.Operation(p_216055_1_, p_216055_2_, p_216055_3_));
         return this;
      }

      public CopyNbt.Builder copy(String p_216056_1_, String p_216056_2_) {
         return this.copy(p_216056_1_, p_216056_2_, CopyNbt.Action.REPLACE);
      }

      protected CopyNbt.Builder getThis() {
         return this;
      }

      public ILootFunction build() {
         return new CopyNbt(this.getConditions(), this.source, this.ops);
      }
   }

   static class Operation {
      private final String sourcePathText;
      private final NBTPathArgument.NBTPath sourcePath;
      private final String targetPathText;
      private final NBTPathArgument.NBTPath targetPath;
      private final CopyNbt.Action op;

      private Operation(String p_i50673_1_, String p_i50673_2_, CopyNbt.Action p_i50673_3_) {
         this.sourcePathText = p_i50673_1_;
         this.sourcePath = CopyNbt.compileNbtPath(p_i50673_1_);
         this.targetPathText = p_i50673_2_;
         this.targetPath = CopyNbt.compileNbtPath(p_i50673_2_);
         this.op = p_i50673_3_;
      }

      public void apply(Supplier<INBT> p_216216_1_, INBT p_216216_2_) {
         try {
            List<INBT> list = this.sourcePath.get(p_216216_2_);
            if (!list.isEmpty()) {
               this.op.merge(p_216216_1_.get(), this.targetPath, list);
            }
         } catch (CommandSyntaxException commandsyntaxexception) {
         }

      }

      public JsonObject toJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("source", this.sourcePathText);
         jsonobject.addProperty("target", this.targetPathText);
         jsonobject.addProperty("op", this.op.name);
         return jsonobject;
      }

      public static CopyNbt.Operation fromJson(JsonObject p_216215_0_) {
         String s = JSONUtils.getAsString(p_216215_0_, "source");
         String s1 = JSONUtils.getAsString(p_216215_0_, "target");
         CopyNbt.Action copynbt$action = CopyNbt.Action.getByName(JSONUtils.getAsString(p_216215_0_, "op"));
         return new CopyNbt.Operation(s, s1, copynbt$action);
      }
   }

   public static class Serializer extends LootFunction.Serializer<CopyNbt> {
      public void serialize(JsonObject p_230424_1_, CopyNbt p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("source", p_230424_2_.source.name);
         JsonArray jsonarray = new JsonArray();
         p_230424_2_.operations.stream().map(CopyNbt.Operation::toJson).forEach(jsonarray::add);
         p_230424_1_.add("ops", jsonarray);
      }

      public CopyNbt deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         CopyNbt.Source copynbt$source = CopyNbt.Source.getByName(JSONUtils.getAsString(p_186530_1_, "source"));
         List<CopyNbt.Operation> list = Lists.newArrayList();

         for(JsonElement jsonelement : JSONUtils.getAsJsonArray(p_186530_1_, "ops")) {
            JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonelement, "op");
            list.add(CopyNbt.Operation.fromJson(jsonobject));
         }

         return new CopyNbt(p_186530_3_, copynbt$source, list);
      }
   }

   public static enum Source {
      THIS("this", LootParameters.THIS_ENTITY, CopyNbt.ENTITY_GETTER),
      KILLER("killer", LootParameters.KILLER_ENTITY, CopyNbt.ENTITY_GETTER),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER, CopyNbt.ENTITY_GETTER),
      BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY, CopyNbt.BLOCK_ENTITY_GETTER);

      public final String name;
      public final LootParameter<?> param;
      public final Function<LootContext, INBT> getter;

      private <T> Source(String p_i50672_3_, LootParameter<T> p_i50672_4_, Function<? super T, INBT> p_i50672_5_) {
         this.name = p_i50672_3_;
         this.param = p_i50672_4_;
         this.getter = (p_216222_2_) -> {
            T t = p_216222_2_.getParamOrNull(p_i50672_4_);
            return t != null ? p_i50672_5_.apply(t) : null;
         };
      }

      public static CopyNbt.Source getByName(String p_216223_0_) {
         for(CopyNbt.Source copynbt$source : values()) {
            if (copynbt$source.name.equals(p_216223_0_)) {
               return copynbt$source;
            }
         }

         throw new IllegalArgumentException("Invalid tag source " + p_216223_0_);
      }
   }
}
