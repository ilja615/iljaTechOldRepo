package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;

public enum EnchantmentType implements net.minecraftforge.common.IExtensibleEnum {
   ARMOR {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ArmorItem && ((ArmorItem)p_77557_1_).getSlot() == EquipmentSlotType.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof SwordItem;
      }
   },
   DIGGER {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof ToolItem;
      }
   },
   FISHING_ROD {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof FishingRodItem;
      }
   },
   TRIDENT {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof TridentItem;
      }
   },
   BREAKABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_.canBeDepleted();
      }
   },
   BOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof BowItem;
      }
   },
   WEARABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof IArmorVanishable || Block.byItem(p_77557_1_) instanceof IArmorVanishable;
      }
   },
   CROSSBOW {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof CrossbowItem;
      }
   },
   VANISHABLE {
      public boolean canEnchant(Item p_77557_1_) {
         return p_77557_1_ instanceof IVanishable || Block.byItem(p_77557_1_) instanceof IVanishable || BREAKABLE.canEnchant(p_77557_1_);
      }
   };

   private EnchantmentType() {
   }

   private java.util.function.Predicate<Item> delegate;
   private EnchantmentType(java.util.function.Predicate<Item> delegate) {
      this.delegate = delegate;
   }

   public static EnchantmentType create(String name, java.util.function.Predicate<Item> delegate) {
      throw new IllegalStateException("Enum not extended");
   }

   public boolean canEnchant(Item p_77557_1_) {
      return this.delegate == null ? false : this.delegate.test(p_77557_1_);
   }
}
