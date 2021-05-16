package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

@Immutable
public class LockCode {
   public static final LockCode NO_LOCK = new LockCode("");
   private final String key;

   public LockCode(String p_i45903_1_) {
      this.key = p_i45903_1_;
   }

   public boolean unlocksWith(ItemStack p_219964_1_) {
      return this.key.isEmpty() || !p_219964_1_.isEmpty() && p_219964_1_.hasCustomHoverName() && this.key.equals(p_219964_1_.getHoverName().getString());
   }

   public void addToTag(CompoundNBT p_180157_1_) {
      if (!this.key.isEmpty()) {
         p_180157_1_.putString("Lock", this.key);
      }

   }

   public static LockCode fromTag(CompoundNBT p_180158_0_) {
      return p_180158_0_.contains("Lock", 8) ? new LockCode(p_180158_0_.getString("Lock")) : NO_LOCK;
   }
}
