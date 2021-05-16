package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.vector.Orientation;

public enum Rotation {
   NONE(Orientation.IDENTITY),
   CLOCKWISE_90(Orientation.ROT_90_Y_NEG),
   CLOCKWISE_180(Orientation.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90(Orientation.ROT_90_Y_POS);

   private final Orientation rotation;

   private Rotation(Orientation p_i231796_3_) {
      this.rotation = p_i231796_3_;
   }

   public Rotation getRotated(Rotation p_185830_1_) {
      switch(p_185830_1_) {
      case CLOCKWISE_180:
         switch(this) {
         case NONE:
            return CLOCKWISE_180;
         case CLOCKWISE_90:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_180:
            return NONE;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_90;
         }
      case COUNTERCLOCKWISE_90:
         switch(this) {
         case NONE:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_90:
            return NONE;
         case CLOCKWISE_180:
            return CLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_180;
         }
      case CLOCKWISE_90:
         switch(this) {
         case NONE:
            return CLOCKWISE_90;
         case CLOCKWISE_90:
            return CLOCKWISE_180;
         case CLOCKWISE_180:
            return COUNTERCLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return NONE;
         }
      default:
         return this;
      }
   }

   public Orientation rotation() {
      return this.rotation;
   }

   public Direction rotate(Direction p_185831_1_) {
      if (p_185831_1_.getAxis() == Direction.Axis.Y) {
         return p_185831_1_;
      } else {
         switch(this) {
         case CLOCKWISE_90:
            return p_185831_1_.getClockWise();
         case CLOCKWISE_180:
            return p_185831_1_.getOpposite();
         case COUNTERCLOCKWISE_90:
            return p_185831_1_.getCounterClockWise();
         default:
            return p_185831_1_;
         }
      }
   }

   public int rotate(int p_185833_1_, int p_185833_2_) {
      switch(this) {
      case CLOCKWISE_90:
         return (p_185833_1_ + p_185833_2_ / 4) % p_185833_2_;
      case CLOCKWISE_180:
         return (p_185833_1_ + p_185833_2_ / 2) % p_185833_2_;
      case COUNTERCLOCKWISE_90:
         return (p_185833_1_ + p_185833_2_ * 3 / 4) % p_185833_2_;
      default:
         return p_185833_1_;
      }
   }

   public static Rotation getRandom(Random p_222466_0_) {
      return Util.getRandom(values(), p_222466_0_);
   }

   public static List<Rotation> getShuffled(Random p_222467_0_) {
      List<Rotation> list = Lists.newArrayList(values());
      Collections.shuffle(list, p_222467_0_);
      return list;
   }
}
