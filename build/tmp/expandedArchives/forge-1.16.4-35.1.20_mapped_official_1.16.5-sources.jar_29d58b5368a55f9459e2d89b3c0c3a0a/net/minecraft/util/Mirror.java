package net.minecraft.util;

import net.minecraft.util.math.vector.Orientation;

public enum Mirror {
   NONE(Orientation.IDENTITY),
   LEFT_RIGHT(Orientation.INVERT_Z),
   FRONT_BACK(Orientation.INVERT_X);

   private final Orientation rotation;

   private Mirror(Orientation p_i241181_3_) {
      this.rotation = p_i241181_3_;
   }

   public int mirror(int p_185802_1_, int p_185802_2_) {
      int i = p_185802_2_ / 2;
      int j = p_185802_1_ > i ? p_185802_1_ - p_185802_2_ : p_185802_1_;
      switch(this) {
      case FRONT_BACK:
         return (p_185802_2_ - j) % p_185802_2_;
      case LEFT_RIGHT:
         return (i - j + p_185802_2_) % p_185802_2_;
      default:
         return p_185802_1_;
      }
   }

   public Rotation getRotation(Direction p_185800_1_) {
      Direction.Axis direction$axis = p_185800_1_.getAxis();
      return (this != LEFT_RIGHT || direction$axis != Direction.Axis.Z) && (this != FRONT_BACK || direction$axis != Direction.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public Direction mirror(Direction p_185803_1_) {
      if (this == FRONT_BACK && p_185803_1_.getAxis() == Direction.Axis.X) {
         return p_185803_1_.getOpposite();
      } else {
         return this == LEFT_RIGHT && p_185803_1_.getAxis() == Direction.Axis.Z ? p_185803_1_.getOpposite() : p_185803_1_;
      }
   }

   public Orientation rotation() {
      return this.rotation;
   }
}
