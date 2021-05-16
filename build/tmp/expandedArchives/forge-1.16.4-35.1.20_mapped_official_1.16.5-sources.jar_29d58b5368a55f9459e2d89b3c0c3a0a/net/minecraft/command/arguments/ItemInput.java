package net.minecraft.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemInput implements Predicate<ItemStack> {
   private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((p_208695_0_, p_208695_1_) -> {
      return new TranslationTextComponent("arguments.item.overstacked", p_208695_0_, p_208695_1_);
   });
   private final Item item;
   @Nullable
   private final CompoundNBT tag;

   public ItemInput(Item p_i47961_1_, @Nullable CompoundNBT p_i47961_2_) {
      this.item = p_i47961_1_;
      this.tag = p_i47961_2_;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean test(ItemStack p_test_1_) {
      return p_test_1_.getItem() == this.item && NBTUtil.compareNbt(this.tag, p_test_1_.getTag(), true);
   }

   public ItemStack createItemStack(int p_197320_1_, boolean p_197320_2_) throws CommandSyntaxException {
      ItemStack itemstack = new ItemStack(this.item, p_197320_1_);
      if (this.tag != null) {
         itemstack.setTag(this.tag);
      }

      if (p_197320_2_ && p_197320_1_ > itemstack.getMaxStackSize()) {
         throw ERROR_STACK_TOO_BIG.create(Registry.ITEM.getKey(this.item), itemstack.getMaxStackSize());
      } else {
         return itemstack;
      }
   }

   public String serialize() {
      StringBuilder stringbuilder = new StringBuilder(Registry.ITEM.getId(this.item));
      if (this.tag != null) {
         stringbuilder.append((Object)this.tag);
      }

      return stringbuilder.toString();
   }
}
