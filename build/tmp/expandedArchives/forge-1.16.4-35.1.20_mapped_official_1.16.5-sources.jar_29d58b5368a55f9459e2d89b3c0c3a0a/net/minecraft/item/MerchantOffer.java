package net.minecraft.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.MathHelper;

public class MerchantOffer {
   private final ItemStack baseCostA;
   private final ItemStack costB;
   private final ItemStack result;
   private int uses;
   private final int maxUses;
   private boolean rewardExp = true;
   private int specialPriceDiff;
   private int demand;
   private float priceMultiplier;
   private int xp = 1;

   public MerchantOffer(CompoundNBT p_i50012_1_) {
      this.baseCostA = ItemStack.of(p_i50012_1_.getCompound("buy"));
      this.costB = ItemStack.of(p_i50012_1_.getCompound("buyB"));
      this.result = ItemStack.of(p_i50012_1_.getCompound("sell"));
      this.uses = p_i50012_1_.getInt("uses");
      if (p_i50012_1_.contains("maxUses", 99)) {
         this.maxUses = p_i50012_1_.getInt("maxUses");
      } else {
         this.maxUses = 4;
      }

      if (p_i50012_1_.contains("rewardExp", 1)) {
         this.rewardExp = p_i50012_1_.getBoolean("rewardExp");
      }

      if (p_i50012_1_.contains("xp", 3)) {
         this.xp = p_i50012_1_.getInt("xp");
      }

      if (p_i50012_1_.contains("priceMultiplier", 5)) {
         this.priceMultiplier = p_i50012_1_.getFloat("priceMultiplier");
      }

      this.specialPriceDiff = p_i50012_1_.getInt("specialPrice");
      this.demand = p_i50012_1_.getInt("demand");
   }

   public MerchantOffer(ItemStack p_i50013_1_, ItemStack p_i50013_2_, int p_i50013_3_, int p_i50013_4_, float p_i50013_5_) {
      this(p_i50013_1_, ItemStack.EMPTY, p_i50013_2_, p_i50013_3_, p_i50013_4_, p_i50013_5_);
   }

   public MerchantOffer(ItemStack p_i50014_1_, ItemStack p_i50014_2_, ItemStack p_i50014_3_, int p_i50014_4_, int p_i50014_5_, float p_i50014_6_) {
      this(p_i50014_1_, p_i50014_2_, p_i50014_3_, 0, p_i50014_4_, p_i50014_5_, p_i50014_6_);
   }

   public MerchantOffer(ItemStack p_i50015_1_, ItemStack p_i50015_2_, ItemStack p_i50015_3_, int p_i50015_4_, int p_i50015_5_, int p_i50015_6_, float p_i50015_7_) {
      this(p_i50015_1_, p_i50015_2_, p_i50015_3_, p_i50015_4_, p_i50015_5_, p_i50015_6_, p_i50015_7_, 0);
   }

   public MerchantOffer(ItemStack p_i51550_1_, ItemStack p_i51550_2_, ItemStack p_i51550_3_, int p_i51550_4_, int p_i51550_5_, int p_i51550_6_, float p_i51550_7_, int p_i51550_8_) {
      this.baseCostA = p_i51550_1_;
      this.costB = p_i51550_2_;
      this.result = p_i51550_3_;
      this.uses = p_i51550_4_;
      this.maxUses = p_i51550_5_;
      this.xp = p_i51550_6_;
      this.priceMultiplier = p_i51550_7_;
      this.demand = p_i51550_8_;
   }

   public ItemStack getBaseCostA() {
      return this.baseCostA;
   }

   public ItemStack getCostA() {
      int i = this.baseCostA.getCount();
      ItemStack itemstack = this.baseCostA.copy();
      int j = Math.max(0, MathHelper.floor((float)(i * this.demand) * this.priceMultiplier));
      itemstack.setCount(MathHelper.clamp(i + j + this.specialPriceDiff, 1, this.baseCostA.getItem().getMaxStackSize()));
      return itemstack;
   }

   public ItemStack getCostB() {
      return this.costB;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public void updateDemand() {
      this.demand = this.demand + this.uses - (this.maxUses - this.uses);
   }

   public ItemStack assemble() {
      return this.result.copy();
   }

   public int getUses() {
      return this.uses;
   }

   public void resetUses() {
      this.uses = 0;
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public void increaseUses() {
      ++this.uses;
   }

   public int getDemand() {
      return this.demand;
   }

   public void addToSpecialPriceDiff(int p_222207_1_) {
      this.specialPriceDiff += p_222207_1_;
   }

   public void resetSpecialPriceDiff() {
      this.specialPriceDiff = 0;
   }

   public int getSpecialPriceDiff() {
      return this.specialPriceDiff;
   }

   public void setSpecialPriceDiff(int p_222209_1_) {
      this.specialPriceDiff = p_222209_1_;
   }

   public float getPriceMultiplier() {
      return this.priceMultiplier;
   }

   public int getXp() {
      return this.xp;
   }

   public boolean isOutOfStock() {
      return this.uses >= this.maxUses;
   }

   public void setToOutOfStock() {
      this.uses = this.maxUses;
   }

   public boolean needsRestock() {
      return this.uses > 0;
   }

   public boolean shouldRewardExp() {
      return this.rewardExp;
   }

   public CompoundNBT createTag() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("buy", this.baseCostA.save(new CompoundNBT()));
      compoundnbt.put("sell", this.result.save(new CompoundNBT()));
      compoundnbt.put("buyB", this.costB.save(new CompoundNBT()));
      compoundnbt.putInt("uses", this.uses);
      compoundnbt.putInt("maxUses", this.maxUses);
      compoundnbt.putBoolean("rewardExp", this.rewardExp);
      compoundnbt.putInt("xp", this.xp);
      compoundnbt.putFloat("priceMultiplier", this.priceMultiplier);
      compoundnbt.putInt("specialPrice", this.specialPriceDiff);
      compoundnbt.putInt("demand", this.demand);
      return compoundnbt;
   }

   public boolean satisfiedBy(ItemStack p_222204_1_, ItemStack p_222204_2_) {
      return this.isRequiredItem(p_222204_1_, this.getCostA()) && p_222204_1_.getCount() >= this.getCostA().getCount() && this.isRequiredItem(p_222204_2_, this.costB) && p_222204_2_.getCount() >= this.costB.getCount();
   }

   private boolean isRequiredItem(ItemStack p_222201_1_, ItemStack p_222201_2_) {
      if (p_222201_2_.isEmpty() && p_222201_1_.isEmpty()) {
         return true;
      } else {
         ItemStack itemstack = p_222201_1_.copy();
         if (itemstack.getItem().isDamageable(itemstack)) {
            itemstack.setDamageValue(itemstack.getDamageValue());
         }

         return ItemStack.isSame(itemstack, p_222201_2_) && (!p_222201_2_.hasTag() || itemstack.hasTag() && NBTUtil.compareNbt(p_222201_2_.getTag(), itemstack.getTag(), false));
      }
   }

   public boolean take(ItemStack p_222215_1_, ItemStack p_222215_2_) {
      if (!this.satisfiedBy(p_222215_1_, p_222215_2_)) {
         return false;
      } else {
         p_222215_1_.shrink(this.getCostA().getCount());
         if (!this.getCostB().isEmpty()) {
            p_222215_2_.shrink(this.getCostB().getCount());
         }

         return true;
      }
   }
}
