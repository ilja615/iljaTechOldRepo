package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDamageSource extends DamageSource {
   @Nullable
   protected final Entity entity;
   private boolean isThorns;

   public EntityDamageSource(String p_i1567_1_, @Nullable Entity p_i1567_2_) {
      super(p_i1567_1_);
      this.entity = p_i1567_2_;
   }

   public EntityDamageSource setThorns() {
      this.isThorns = true;
      return this;
   }

   public boolean isThorns() {
      return this.isThorns;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public ITextComponent getLocalizedDeathMessage(LivingEntity p_151519_1_) {
      ItemStack itemstack = this.entity instanceof LivingEntity ? ((LivingEntity)this.entity).getMainHandItem() : ItemStack.EMPTY;
      String s = "death.attack." + this.msgId;
      return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslationTextComponent(s + ".item", p_151519_1_.getDisplayName(), this.entity.getDisplayName(), itemstack.getDisplayName()) : new TranslationTextComponent(s, p_151519_1_.getDisplayName(), this.entity.getDisplayName());
   }

   public boolean scalesWithDifficulty() {
      return this.entity != null && this.entity instanceof LivingEntity && !(this.entity instanceof PlayerEntity);
   }

   @Nullable
   public Vector3d getSourcePosition() {
      return this.entity != null ? this.entity.position() : null;
   }

   public String toString() {
      return "EntityDamageSource (" + this.entity + ")";
   }
}
