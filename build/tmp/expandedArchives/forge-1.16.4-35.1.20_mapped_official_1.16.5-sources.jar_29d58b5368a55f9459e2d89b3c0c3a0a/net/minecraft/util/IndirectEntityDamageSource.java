package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IndirectEntityDamageSource extends EntityDamageSource {
   private final Entity owner;

   public IndirectEntityDamageSource(String p_i1568_1_, Entity p_i1568_2_, @Nullable Entity p_i1568_3_) {
      super(p_i1568_1_, p_i1568_2_);
      this.owner = p_i1568_3_;
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.entity;
   }

   @Nullable
   public Entity getEntity() {
      return this.owner;
   }

   public ITextComponent getLocalizedDeathMessage(LivingEntity p_151519_1_) {
      ITextComponent itextcomponent = this.owner == null ? this.entity.getDisplayName() : this.owner.getDisplayName();
      ItemStack itemstack = this.owner instanceof LivingEntity ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
      String s = "death.attack." + this.msgId;
      String s1 = s + ".item";
      return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslationTextComponent(s1, p_151519_1_.getDisplayName(), itextcomponent, itemstack.getDisplayName()) : new TranslationTextComponent(s, p_151519_1_.getDisplayName(), itextcomponent);
   }
}
