package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JSONUtils;

public class NBTPredicate {
   public static final NBTPredicate ANY = new NBTPredicate((CompoundNBT)null);
   @Nullable
   private final CompoundNBT tag;

   public NBTPredicate(@Nullable CompoundNBT p_i47536_1_) {
      this.tag = p_i47536_1_;
   }

   public boolean matches(ItemStack p_193478_1_) {
      return this == ANY ? true : this.matches(p_193478_1_.getTag());
   }

   public boolean matches(Entity p_193475_1_) {
      return this == ANY ? true : this.matches(getEntityTagToCompare(p_193475_1_));
   }

   public boolean matches(@Nullable INBT p_193477_1_) {
      if (p_193477_1_ == null) {
         return this == ANY;
      } else {
         return this.tag == null || NBTUtil.compareNbt(this.tag, p_193477_1_, true);
      }
   }

   public JsonElement serializeToJson() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NBTPredicate fromJson(@Nullable JsonElement p_193476_0_) {
      if (p_193476_0_ != null && !p_193476_0_.isJsonNull()) {
         CompoundNBT compoundnbt;
         try {
            compoundnbt = JsonToNBT.parseTag(JSONUtils.convertToString(p_193476_0_, "nbt"));
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
         }

         return new NBTPredicate(compoundnbt);
      } else {
         return ANY;
      }
   }

   public static CompoundNBT getEntityTagToCompare(Entity p_196981_0_) {
      CompoundNBT compoundnbt = p_196981_0_.saveWithoutId(new CompoundNBT());
      if (p_196981_0_ instanceof PlayerEntity) {
         ItemStack itemstack = ((PlayerEntity)p_196981_0_).inventory.getSelected();
         if (!itemstack.isEmpty()) {
            compoundnbt.put("SelectedItem", itemstack.save(new CompoundNBT()));
         }
      }

      return compoundnbt;
   }
}
