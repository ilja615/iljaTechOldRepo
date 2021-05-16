package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   public static int getItemEnchantmentLevel(Enchantment p_77506_0_, ItemStack p_77506_1_) {
      if (p_77506_1_.isEmpty()) {
         return 0;
      } else {
         ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(p_77506_0_);
         ListNBT listnbt = p_77506_1_.getEnchantmentTags();

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.tryParse(compoundnbt.getString("id"));
            if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
               return MathHelper.clamp(compoundnbt.getInt("lvl"), 0, 255);
            }
         }

         return 0;
      }
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack p_82781_0_) {
      ListNBT listnbt = p_82781_0_.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(p_82781_0_) : p_82781_0_.getEnchantmentTags();
      return deserializeEnchantments(listnbt);
   }

   public static Map<Enchantment, Integer> deserializeEnchantments(ListNBT p_226652_0_) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

      for(int i = 0; i < p_226652_0_.size(); ++i) {
         CompoundNBT compoundnbt = p_226652_0_.getCompound(i);
         Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(compoundnbt.getString("id"))).ifPresent((p_226651_2_) -> {
            Integer integer = map.put(p_226651_2_, compoundnbt.getInt("lvl"));
         });
      }

      return map;
   }

   public static void setEnchantments(Map<Enchantment, Integer> p_82782_0_, ItemStack p_82782_1_) {
      ListNBT listnbt = new ListNBT();

      for(Entry<Enchantment, Integer> entry : p_82782_0_.entrySet()) {
         Enchantment enchantment = entry.getKey();
         if (enchantment != null) {
            int i = entry.getValue();
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("id", String.valueOf((Object)Registry.ENCHANTMENT.getKey(enchantment)));
            compoundnbt.putShort("lvl", (short)i);
            listnbt.add(compoundnbt);
            if (p_82782_1_.getItem() == Items.ENCHANTED_BOOK) {
               EnchantedBookItem.addEnchantment(p_82782_1_, new EnchantmentData(enchantment, i));
            }
         }
      }

      if (listnbt.isEmpty()) {
         p_82782_1_.removeTagKey("Enchantments");
      } else if (p_82782_1_.getItem() != Items.ENCHANTED_BOOK) {
         p_82782_1_.addTagElement("Enchantments", listnbt);
      }

   }

   private static void runIterationOnItem(EnchantmentHelper.IEnchantmentVisitor p_77518_0_, ItemStack p_77518_1_) {
      if (!p_77518_1_.isEmpty()) {
         ListNBT listnbt = p_77518_1_.getEnchantmentTags();

         for(int i = 0; i < listnbt.size(); ++i) {
            String s = listnbt.getCompound(i).getString("id");
            int j = listnbt.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(s)).ifPresent((p_222184_2_) -> {
               p_77518_0_.accept(p_222184_2_, j);
            });
         }

      }
   }

   private static void runIterationOnInventory(EnchantmentHelper.IEnchantmentVisitor p_77516_0_, Iterable<ItemStack> p_77516_1_) {
      for(ItemStack itemstack : p_77516_1_) {
         runIterationOnItem(p_77516_0_, itemstack);
      }

   }

   public static int getDamageProtection(Iterable<ItemStack> p_77508_0_, DamageSource p_77508_1_) {
      MutableInt mutableint = new MutableInt();
      runIterationOnInventory((p_212576_2_, p_212576_3_) -> {
         mutableint.add(p_212576_2_.getDamageProtection(p_212576_3_, p_77508_1_));
      }, p_77508_0_);
      return mutableint.intValue();
   }

   public static float getDamageBonus(ItemStack p_152377_0_, CreatureAttribute p_152377_1_) {
      MutableFloat mutablefloat = new MutableFloat();
      runIterationOnItem((p_212573_2_, p_212573_3_) -> {
         mutablefloat.add(p_212573_2_.getDamageBonus(p_212573_3_, p_152377_1_));
      }, p_152377_0_);
      return mutablefloat.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity p_191527_0_) {
      int i = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, p_191527_0_);
      return i > 0 ? SweepingEnchantment.getSweepingDamageRatio(i) : 0.0F;
   }

   public static void doPostHurtEffects(LivingEntity p_151384_0_, Entity p_151384_1_) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212575_2_, p_212575_3_) -> {
         p_212575_2_.doPostHurt(p_151384_0_, p_151384_1_, p_212575_3_);
      };
      if (p_151384_0_ != null) {
         runIterationOnInventory(enchantmenthelper$ienchantmentvisitor, p_151384_0_.getAllSlots());
      }

      if (p_151384_1_ instanceof PlayerEntity) {
         runIterationOnItem(enchantmenthelper$ienchantmentvisitor, p_151384_0_.getMainHandItem());
      }

   }

   public static void doPostDamageEffects(LivingEntity p_151385_0_, Entity p_151385_1_) {
      EnchantmentHelper.IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (p_212574_2_, p_212574_3_) -> {
         p_212574_2_.doPostAttack(p_151385_0_, p_151385_1_, p_212574_3_);
      };
      if (p_151385_0_ != null) {
         runIterationOnInventory(enchantmenthelper$ienchantmentvisitor, p_151385_0_.getAllSlots());
      }

      if (p_151385_0_ instanceof PlayerEntity) {
         runIterationOnItem(enchantmenthelper$ienchantmentvisitor, p_151385_0_.getMainHandItem());
      }

   }

   public static int getEnchantmentLevel(Enchantment p_185284_0_, LivingEntity p_185284_1_) {
      Iterable<ItemStack> iterable = p_185284_0_.getSlotItems(p_185284_1_).values();
      if (iterable == null) {
         return 0;
      } else {
         int i = 0;

         for(ItemStack itemstack : iterable) {
            int j = getItemEnchantmentLevel(p_185284_0_, itemstack);
            if (j > i) {
               i = j;
            }
         }

         return i;
      }
   }

   public static int getKnockbackBonus(LivingEntity p_77501_0_) {
      return getEnchantmentLevel(Enchantments.KNOCKBACK, p_77501_0_);
   }

   public static int getFireAspect(LivingEntity p_90036_0_) {
      return getEnchantmentLevel(Enchantments.FIRE_ASPECT, p_90036_0_);
   }

   public static int getRespiration(LivingEntity p_185292_0_) {
      return getEnchantmentLevel(Enchantments.RESPIRATION, p_185292_0_);
   }

   public static int getDepthStrider(LivingEntity p_185294_0_) {
      return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, p_185294_0_);
   }

   public static int getBlockEfficiency(LivingEntity p_185293_0_) {
      return getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, p_185293_0_);
   }

   public static int getFishingLuckBonus(ItemStack p_191529_0_) {
      return getItemEnchantmentLevel(Enchantments.FISHING_LUCK, p_191529_0_);
   }

   public static int getFishingSpeedBonus(ItemStack p_191528_0_) {
      return getItemEnchantmentLevel(Enchantments.FISHING_SPEED, p_191528_0_);
   }

   public static int getMobLooting(LivingEntity p_185283_0_) {
      return getEnchantmentLevel(Enchantments.MOB_LOOTING, p_185283_0_);
   }

   public static boolean hasAquaAffinity(LivingEntity p_185287_0_) {
      return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, p_185287_0_) > 0;
   }

   public static boolean hasFrostWalker(LivingEntity p_189869_0_) {
      return getEnchantmentLevel(Enchantments.FROST_WALKER, p_189869_0_) > 0;
   }

   public static boolean hasSoulSpeed(LivingEntity p_234846_0_) {
      return getEnchantmentLevel(Enchantments.SOUL_SPEED, p_234846_0_) > 0;
   }

   public static boolean hasBindingCurse(ItemStack p_190938_0_) {
      return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, p_190938_0_) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack p_190939_0_) {
      return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, p_190939_0_) > 0;
   }

   public static int getLoyalty(ItemStack p_203191_0_) {
      return getItemEnchantmentLevel(Enchantments.LOYALTY, p_203191_0_);
   }

   public static int getRiptide(ItemStack p_203190_0_) {
      return getItemEnchantmentLevel(Enchantments.RIPTIDE, p_203190_0_);
   }

   public static boolean hasChanneling(ItemStack p_203192_0_) {
      return getItemEnchantmentLevel(Enchantments.CHANNELING, p_203192_0_) > 0;
   }

   @Nullable
   public static Entry<EquipmentSlotType, ItemStack> getRandomItemWith(Enchantment p_222189_0_, LivingEntity p_222189_1_) {
      return getRandomItemWith(p_222189_0_, p_222189_1_, (p_234845_0_) -> {
         return true;
      });
   }

   @Nullable
   public static Entry<EquipmentSlotType, ItemStack> getRandomItemWith(Enchantment p_234844_0_, LivingEntity p_234844_1_, Predicate<ItemStack> p_234844_2_) {
      Map<EquipmentSlotType, ItemStack> map = p_234844_0_.getSlotItems(p_234844_1_);
      if (map.isEmpty()) {
         return null;
      } else {
         List<Entry<EquipmentSlotType, ItemStack>> list = Lists.newArrayList();

         for(Entry<EquipmentSlotType, ItemStack> entry : map.entrySet()) {
            ItemStack itemstack = entry.getValue();
            if (!itemstack.isEmpty() && getItemEnchantmentLevel(p_234844_0_, itemstack) > 0 && p_234844_2_.test(itemstack)) {
               list.add(entry);
            }
         }

         return list.isEmpty() ? null : list.get(p_234844_1_.getRandom().nextInt(list.size()));
      }
   }

   public static int getEnchantmentCost(Random p_77514_0_, int p_77514_1_, int p_77514_2_, ItemStack p_77514_3_) {
      Item item = p_77514_3_.getItem();
      int i = p_77514_3_.getItemEnchantability();
      if (i <= 0) {
         return 0;
      } else {
         if (p_77514_2_ > 15) {
            p_77514_2_ = 15;
         }

         int j = p_77514_0_.nextInt(8) + 1 + (p_77514_2_ >> 1) + p_77514_0_.nextInt(p_77514_2_ + 1);
         if (p_77514_1_ == 0) {
            return Math.max(j / 3, 1);
         } else {
            return p_77514_1_ == 1 ? j * 2 / 3 + 1 : Math.max(j, p_77514_2_ * 2);
         }
      }
   }

   public static ItemStack enchantItem(Random p_77504_0_, ItemStack p_77504_1_, int p_77504_2_, boolean p_77504_3_) {
      List<EnchantmentData> list = selectEnchantment(p_77504_0_, p_77504_1_, p_77504_2_, p_77504_3_);
      boolean flag = p_77504_1_.getItem() == Items.BOOK;
      if (flag) {
         p_77504_1_ = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentData enchantmentdata : list) {
         if (flag) {
            EnchantedBookItem.addEnchantment(p_77504_1_, enchantmentdata);
         } else {
            p_77504_1_.enchant(enchantmentdata.enchantment, enchantmentdata.level);
         }
      }

      return p_77504_1_;
   }

   public static List<EnchantmentData> selectEnchantment(Random p_77513_0_, ItemStack p_77513_1_, int p_77513_2_, boolean p_77513_3_) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = p_77513_1_.getItem();
      int i = p_77513_1_.getItemEnchantability();
      if (i <= 0) {
         return list;
      } else {
         p_77513_2_ = p_77513_2_ + 1 + p_77513_0_.nextInt(i / 4 + 1) + p_77513_0_.nextInt(i / 4 + 1);
         float f = (p_77513_0_.nextFloat() + p_77513_0_.nextFloat() - 1.0F) * 0.15F;
         p_77513_2_ = MathHelper.clamp(Math.round((float)p_77513_2_ + (float)p_77513_2_ * f), 1, Integer.MAX_VALUE);
         List<EnchantmentData> list1 = getAvailableEnchantmentResults(p_77513_2_, p_77513_1_, p_77513_3_);
         if (!list1.isEmpty()) {
            list.add(WeightedRandom.getRandomItem(p_77513_0_, list1));

            while(p_77513_0_.nextInt(50) <= p_77513_2_) {
               filterCompatibleEnchantments(list1, Util.lastOf(list));
               if (list1.isEmpty()) {
                  break;
               }

               list.add(WeightedRandom.getRandomItem(p_77513_0_, list1));
               p_77513_2_ /= 2;
            }
         }

         return list;
      }
   }

   public static void filterCompatibleEnchantments(List<EnchantmentData> p_185282_0_, EnchantmentData p_185282_1_) {
      Iterator<EnchantmentData> iterator = p_185282_0_.iterator();

      while(iterator.hasNext()) {
         if (!p_185282_1_.enchantment.isCompatibleWith((iterator.next()).enchantment)) {
            iterator.remove();
         }
      }

   }

   public static boolean isEnchantmentCompatible(Collection<Enchantment> p_201840_0_, Enchantment p_201840_1_) {
      for(Enchantment enchantment : p_201840_0_) {
         if (!enchantment.isCompatibleWith(p_201840_1_)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentData> getAvailableEnchantmentResults(int p_185291_0_, ItemStack p_185291_1_, boolean p_185291_2_) {
      List<EnchantmentData> list = Lists.newArrayList();
      Item item = p_185291_1_.getItem();
      boolean flag = p_185291_1_.getItem() == Items.BOOK;

      for(Enchantment enchantment : Registry.ENCHANTMENT) {
         if ((!enchantment.isTreasureOnly() || p_185291_2_) && enchantment.isDiscoverable() && (enchantment.canApplyAtEnchantingTable(p_185291_1_) || (flag && enchantment.isAllowedOnBooks()))) {
            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
               if (p_185291_0_ >= enchantment.getMinCost(i) && p_185291_0_ <= enchantment.getMaxCost(i)) {
                  list.add(new EnchantmentData(enchantment, i));
                  break;
               }
            }
         }
      }

      return list;
   }

   @FunctionalInterface
   interface IEnchantmentVisitor {
      void accept(Enchantment p_accept_1_, int p_accept_2_);
   }
}
