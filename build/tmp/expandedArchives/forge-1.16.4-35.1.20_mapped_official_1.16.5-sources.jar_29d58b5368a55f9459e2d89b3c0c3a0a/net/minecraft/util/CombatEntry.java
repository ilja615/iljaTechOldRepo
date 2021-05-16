package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public class CombatEntry {
   private final DamageSource source;
   private final int time;
   private final float damage;
   private final float health;
   private final String location;
   private final float fallDistance;

   public CombatEntry(DamageSource p_i1564_1_, int p_i1564_2_, float p_i1564_3_, float p_i1564_4_, String p_i1564_5_, float p_i1564_6_) {
      this.source = p_i1564_1_;
      this.time = p_i1564_2_;
      this.damage = p_i1564_4_;
      this.health = p_i1564_3_;
      this.location = p_i1564_5_;
      this.fallDistance = p_i1564_6_;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public float getDamage() {
      return this.damage;
   }

   public boolean isCombatRelated() {
      return this.source.getEntity() instanceof LivingEntity;
   }

   @Nullable
   public String getLocation() {
      return this.location;
   }

   @Nullable
   public ITextComponent getAttackerName() {
      return this.getSource().getEntity() == null ? null : this.getSource().getEntity().getDisplayName();
   }

   public float getFallDistance() {
      return this.source == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
   }
}
