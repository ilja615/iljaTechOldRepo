package net.minecraft.util;

public enum AxisRotation {
   NONE {
      public int cycle(int p_197517_1_, int p_197517_2_, int p_197517_3_, Direction.Axis p_197517_4_) {
         return p_197517_4_.choose(p_197517_1_, p_197517_2_, p_197517_3_);
      }

      public Direction.Axis cycle(Direction.Axis p_197513_1_) {
         return p_197513_1_;
      }

      public AxisRotation inverse() {
         return this;
      }
   },
   FORWARD {
      public int cycle(int p_197517_1_, int p_197517_2_, int p_197517_3_, Direction.Axis p_197517_4_) {
         return p_197517_4_.choose(p_197517_3_, p_197517_1_, p_197517_2_);
      }

      public Direction.Axis cycle(Direction.Axis p_197513_1_) {
         return AXIS_VALUES[Math.floorMod(p_197513_1_.ordinal() + 1, 3)];
      }

      public AxisRotation inverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int cycle(int p_197517_1_, int p_197517_2_, int p_197517_3_, Direction.Axis p_197517_4_) {
         return p_197517_4_.choose(p_197517_2_, p_197517_3_, p_197517_1_);
      }

      public Direction.Axis cycle(Direction.Axis p_197513_1_) {
         return AXIS_VALUES[Math.floorMod(p_197513_1_.ordinal() - 1, 3)];
      }

      public AxisRotation inverse() {
         return FORWARD;
      }
   };

   public static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   public static final AxisRotation[] VALUES = values();

   private AxisRotation() {
   }

   public abstract int cycle(int p_197517_1_, int p_197517_2_, int p_197517_3_, Direction.Axis p_197517_4_);

   public abstract Direction.Axis cycle(Direction.Axis p_197513_1_);

   public abstract AxisRotation inverse();

   public static AxisRotation between(Direction.Axis p_197516_0_, Direction.Axis p_197516_1_) {
      return VALUES[Math.floorMod(p_197516_1_.ordinal() - p_197516_0_.ordinal(), 3)];
   }
}
