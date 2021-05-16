package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Enchantment extends net.minecraftforge.registries.ForgeRegistryEntry<Enchantment> {
   private final net.minecraftforge.common.util.ReverseTagWrapper<Enchantment> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, () -> net.minecraft.tags.TagCollectionManager.getInstance().getCustomTypeCollection(net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS));
   private final EquipmentSlotType[] slots;
   private final Enchantment.Rarity rarity;
   public final EnchantmentType category;
   @Nullable
   protected String descriptionId;

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Enchantment byId(int p_185262_0_) {
      return Registry.ENCHANTMENT.byId(p_185262_0_);
   }

   protected Enchantment(Enchantment.Rarity p_i46731_1_, EnchantmentType p_i46731_2_, EquipmentSlotType[] p_i46731_3_) {
      this.rarity = p_i46731_1_;
      this.category = p_i46731_2_;
      this.slots = p_i46731_3_;
   }

   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   public boolean isIn(net.minecraft.tags.ITag<Enchantment> tag) {
      return tag.contains(this);
   }

   public Map<EquipmentSlotType, ItemStack> getSlotItems(LivingEntity p_222181_1_) {
      Map<EquipmentSlotType, ItemStack> map = Maps.newEnumMap(EquipmentSlotType.class);

      for(EquipmentSlotType equipmentslottype : this.slots) {
         ItemStack itemstack = p_222181_1_.getItemBySlot(equipmentslottype);
         if (!itemstack.isEmpty()) {
            map.put(equipmentslottype, itemstack);
         }
      }

      return map;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinCost(int p_77321_1_) {
      return 1 + p_77321_1_ * 10;
   }

   public int getMaxCost(int p_223551_1_) {
      return this.getMinCost(p_223551_1_) + 5;
   }

   public int getDamageProtection(int p_77318_1_, DamageSource p_77318_2_) {
      return 0;
   }

   public float getDamageBonus(int p_152376_1_, CreatureAttribute p_152376_2_) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment p_191560_1_) {
      return this.checkCompatibility(p_191560_1_) && p_191560_1_.checkCompatibility(this);
   }

   protected boolean checkCompatibility(Enchantment p_77326_1_) {
      return this != p_77326_1_;
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("enchantment", Registry.ENCHANTMENT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public ITextComponent getFullname(int p_200305_1_) {
      IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(this.getDescriptionId());
      if (this.isCurse()) {
         iformattabletextcomponent.withStyle(TextFormatting.RED);
      } else {
         iformattabletextcomponent.withStyle(TextFormatting.GRAY);
      }

      if (p_200305_1_ != 1 || this.getMaxLevel() != 1) {
         iformattabletextcomponent.append(" ").append(new TranslationTextComponent("enchantment.level." + p_200305_1_));
      }

      return iformattabletextcomponent;
   }

   public boolean canEnchant(ItemStack p_92089_1_) {
      return canApplyAtEnchantingTable(p_92089_1_);
   }

   public void doPostAttack(LivingEntity p_151368_1_, Entity p_151368_2_, int p_151368_3_) {
   }

   public void doPostHurt(LivingEntity p_151367_1_, Entity p_151367_2_, int p_151367_3_) {
   }

   public boolean isTreasureOnly() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public boolean isTradeable() {
      return true;
   }

   public boolean isDiscoverable() {
      return true;
   }

   /**
    * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
    * applies for <i>all possible</i> enchantments.
    * @param stack
    * @return
    */
   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.canApplyAtEnchantingTable(this);
   }

   /**
    * Is this enchantment allowed to be enchanted on books via Enchantment Table
    * @return false to disable the vanilla feature
    */
   public boolean isAllowedOnBooks() {
      return true;
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      private Rarity(int p_i47026_3_) {
         this.weight = p_i47026_3_;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
