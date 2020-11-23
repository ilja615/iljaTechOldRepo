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

   private final Orientation orientation;

   private Rotation(Orientation orientation) {
      this.orientation = orientation;
   }

   public Rotation add(Rotation rotation) {
      switch(rotation) {
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

   public Orientation getOrientation() {
      return this.orientation;
   }

   public Direction rotate(Direction facing) {
      if (facing.getAxis() == Direction.Axis.Y) {
         return facing;
      } else {
         switch(this) {
         case CLOCKWISE_90:
            return facing.rotateY();
         case CLOCKWISE_180:
            return facing.getOpposite();
         case COUNTERCLOCKWISE_90:
            return facing.rotateYCCW();
         default:
            return facing;
         }
      }
   }

   public int rotate(int rotation, int positionCount) {
      switch(this) {
      case CLOCKWISE_90:
         return (rotation + positionCount / 4) % positionCount;
      case CLOCKWISE_180:
         return (rotation + positionCount / 2) % positionCount;
      case COUNTERCLOCKWISE_90:
         return (rotation + positionCount * 3 / 4) % positionCount;
      default:
         return rotation;
      }
   }

   /**
    * Chooses a random rotation from {@link Rotation}.
    */
   public static Rotation randomRotation(Random rand) {
      return Util.getRandomObject(values(), rand);
   }

   /**
    * Generates a shuffled list with all rotations.
    */
   public static List<Rotation> shuffledRotations(Random rand) {
      List<Rotation> list = Lists.newArrayList(values());
      Collections.shuffle(list, rand);
      return list;
   }
}