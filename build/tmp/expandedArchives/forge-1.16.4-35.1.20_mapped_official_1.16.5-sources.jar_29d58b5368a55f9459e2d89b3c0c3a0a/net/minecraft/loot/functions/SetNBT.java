package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;

public class SetNBT extends LootFunction {
   private final CompoundNBT tag;

   private SetNBT(ILootCondition[] p_i46620_1_, CompoundNBT p_i46620_2_) {
      super(p_i46620_1_);
      this.tag = p_i46620_2_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_NBT;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      p_215859_1_.getOrCreateTag().merge(this.tag);
      return p_215859_1_;
   }

   public static LootFunction.Builder<?> setTag(CompoundNBT p_215952_0_) {
      return simpleBuilder((p_215951_1_) -> {
         return new SetNBT(p_215951_1_, p_215952_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<SetNBT> {
      public void serialize(JsonObject p_230424_1_, SetNBT p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("tag", p_230424_2_.tag.toString());
      }

      public SetNBT deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         try {
            CompoundNBT compoundnbt = JsonToNBT.parseTag(JSONUtils.getAsString(p_186530_1_, "tag"));
            return new SetNBT(p_186530_3_, compoundnbt);
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException(commandsyntaxexception.getMessage());
         }
      }
   }
}
