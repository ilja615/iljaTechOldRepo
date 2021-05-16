package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.INameable;
import net.minecraft.util.JSONUtils;

public class CopyName extends LootFunction {
   private final CopyName.Source source;

   private CopyName(ILootCondition[] p_i51242_1_, CopyName.Source p_i51242_2_) {
      super(p_i51242_1_);
      this.source = p_i51242_2_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.COPY_NAME;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Object object = p_215859_2_.getParamOrNull(this.source.param);
      if (object instanceof INameable) {
         INameable inameable = (INameable)object;
         if (inameable.hasCustomName()) {
            p_215859_1_.setHoverName(inameable.getDisplayName());
         }
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> copyName(CopyName.Source p_215893_0_) {
      return simpleBuilder((p_215891_1_) -> {
         return new CopyName(p_215891_1_, p_215893_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<CopyName> {
      public void serialize(JsonObject p_230424_1_, CopyName p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("source", p_230424_2_.source.name);
      }

      public CopyName deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         CopyName.Source copyname$source = CopyName.Source.getByName(JSONUtils.getAsString(p_186530_1_, "source"));
         return new CopyName(p_186530_3_, copyname$source);
      }
   }

   public static enum Source {
      THIS("this", LootParameters.THIS_ENTITY),
      KILLER("killer", LootParameters.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY);

      public final String name;
      public final LootParameter<?> param;

      private Source(String p_i50801_3_, LootParameter<?> p_i50801_4_) {
         this.name = p_i50801_3_;
         this.param = p_i50801_4_;
      }

      public static CopyName.Source getByName(String p_216235_0_) {
         for(CopyName.Source copyname$source : values()) {
            if (copyname$source.name.equals(p_216235_0_)) {
               return copyname$source;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + p_216235_0_);
      }
   }
}
