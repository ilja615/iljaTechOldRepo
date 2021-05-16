package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random RANDOM = new Random();

   public static void dropContents(World p_180175_0_, BlockPos p_180175_1_, IInventory p_180175_2_) {
      dropContents(p_180175_0_, (double)p_180175_1_.getX(), (double)p_180175_1_.getY(), (double)p_180175_1_.getZ(), p_180175_2_);
   }

   public static void dropContents(World p_180176_0_, Entity p_180176_1_, IInventory p_180176_2_) {
      dropContents(p_180176_0_, p_180176_1_.getX(), p_180176_1_.getY(), p_180176_1_.getZ(), p_180176_2_);
   }

   private static void dropContents(World p_180174_0_, double p_180174_1_, double p_180174_3_, double p_180174_5_, IInventory p_180174_7_) {
      for(int i = 0; i < p_180174_7_.getContainerSize(); ++i) {
         dropItemStack(p_180174_0_, p_180174_1_, p_180174_3_, p_180174_5_, p_180174_7_.getItem(i));
      }

   }

   public static void dropContents(World p_219961_0_, BlockPos p_219961_1_, NonNullList<ItemStack> p_219961_2_) {
      p_219961_2_.forEach((p_219962_2_) -> {
         dropItemStack(p_219961_0_, (double)p_219961_1_.getX(), (double)p_219961_1_.getY(), (double)p_219961_1_.getZ(), p_219962_2_);
      });
   }

   public static void dropItemStack(World p_180173_0_, double p_180173_1_, double p_180173_3_, double p_180173_5_, ItemStack p_180173_7_) {
      double d0 = (double)EntityType.ITEM.getWidth();
      double d1 = 1.0D - d0;
      double d2 = d0 / 2.0D;
      double d3 = Math.floor(p_180173_1_) + RANDOM.nextDouble() * d1 + d2;
      double d4 = Math.floor(p_180173_3_) + RANDOM.nextDouble() * d1;
      double d5 = Math.floor(p_180173_5_) + RANDOM.nextDouble() * d1 + d2;

      while(!p_180173_7_.isEmpty()) {
         ItemEntity itementity = new ItemEntity(p_180173_0_, d3, d4, d5, p_180173_7_.split(RANDOM.nextInt(21) + 10));
         float f = 0.05F;
         itementity.setDeltaMovement(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
         p_180173_0_.addFreshEntity(itementity);
      }

   }
}
