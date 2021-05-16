package net.minecraft.client.settings;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum PointOfView {
   FIRST_PERSON(true, false),
   THIRD_PERSON_BACK(false, false),
   THIRD_PERSON_FRONT(false, true);

   private static final PointOfView[] VALUES = values();
   private boolean firstPerson;
   private boolean mirrored;

   private PointOfView(boolean p_i242049_3_, boolean p_i242049_4_) {
      this.firstPerson = p_i242049_3_;
      this.mirrored = p_i242049_4_;
   }

   public boolean isFirstPerson() {
      return this.firstPerson;
   }

   public boolean isMirrored() {
      return this.mirrored;
   }

   public PointOfView cycle() {
      return VALUES[(this.ordinal() + 1) % VALUES.length];
   }
}
