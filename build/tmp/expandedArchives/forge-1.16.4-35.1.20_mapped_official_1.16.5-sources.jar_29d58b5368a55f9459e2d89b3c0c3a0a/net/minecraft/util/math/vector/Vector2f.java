package net.minecraft.util.math.vector;

public class Vector2f {
   public static final Vector2f ZERO = new Vector2f(0.0F, 0.0F);
   public static final Vector2f ONE = new Vector2f(1.0F, 1.0F);
   public static final Vector2f UNIT_X = new Vector2f(1.0F, 0.0F);
   public static final Vector2f NEG_UNIT_X = new Vector2f(-1.0F, 0.0F);
   public static final Vector2f UNIT_Y = new Vector2f(0.0F, 1.0F);
   public static final Vector2f NEG_UNIT_Y = new Vector2f(0.0F, -1.0F);
   public static final Vector2f MAX = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
   public static final Vector2f MIN = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
   public final float x;
   public final float y;

   public Vector2f(float p_i47143_1_, float p_i47143_2_) {
      this.x = p_i47143_1_;
      this.y = p_i47143_2_;
   }

   public boolean equals(Vector2f p_201069_1_) {
      return this.x == p_201069_1_.x && this.y == p_201069_1_.y;
   }
}
