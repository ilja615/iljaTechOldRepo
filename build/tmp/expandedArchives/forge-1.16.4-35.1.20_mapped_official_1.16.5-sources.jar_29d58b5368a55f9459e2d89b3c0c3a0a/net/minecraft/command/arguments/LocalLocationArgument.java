package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class LocalLocationArgument implements ILocationArgument {
   private final double left;
   private final double up;
   private final double forwards;

   public LocalLocationArgument(double p_i48240_1_, double p_i48240_3_, double p_i48240_5_) {
      this.left = p_i48240_1_;
      this.up = p_i48240_3_;
      this.forwards = p_i48240_5_;
   }

   public Vector3d getPosition(CommandSource p_197281_1_) {
      Vector2f vector2f = p_197281_1_.getRotation();
      Vector3d vector3d = p_197281_1_.getAnchor().apply(p_197281_1_);
      float f = MathHelper.cos((vector2f.y + 90.0F) * ((float)Math.PI / 180F));
      float f1 = MathHelper.sin((vector2f.y + 90.0F) * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(-vector2f.x * ((float)Math.PI / 180F));
      float f3 = MathHelper.sin(-vector2f.x * ((float)Math.PI / 180F));
      float f4 = MathHelper.cos((-vector2f.x + 90.0F) * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin((-vector2f.x + 90.0F) * ((float)Math.PI / 180F));
      Vector3d vector3d1 = new Vector3d((double)(f * f2), (double)f3, (double)(f1 * f2));
      Vector3d vector3d2 = new Vector3d((double)(f * f4), (double)f5, (double)(f1 * f4));
      Vector3d vector3d3 = vector3d1.cross(vector3d2).scale(-1.0D);
      double d0 = vector3d1.x * this.forwards + vector3d2.x * this.up + vector3d3.x * this.left;
      double d1 = vector3d1.y * this.forwards + vector3d2.y * this.up + vector3d3.y * this.left;
      double d2 = vector3d1.z * this.forwards + vector3d2.z * this.up + vector3d3.z * this.left;
      return new Vector3d(vector3d.x + d0, vector3d.y + d1, vector3d.z + d2);
   }

   public Vector2f getRotation(CommandSource p_197282_1_) {
      return Vector2f.ZERO;
   }

   public boolean isXRelative() {
      return true;
   }

   public boolean isYRelative() {
      return true;
   }

   public boolean isZRelative() {
      return true;
   }

   public static LocalLocationArgument parse(StringReader p_200142_0_) throws CommandSyntaxException {
      int i = p_200142_0_.getCursor();
      double d0 = readDouble(p_200142_0_, i);
      if (p_200142_0_.canRead() && p_200142_0_.peek() == ' ') {
         p_200142_0_.skip();
         double d1 = readDouble(p_200142_0_, i);
         if (p_200142_0_.canRead() && p_200142_0_.peek() == ' ') {
            p_200142_0_.skip();
            double d2 = readDouble(p_200142_0_, i);
            return new LocalLocationArgument(d0, d1, d2);
         } else {
            p_200142_0_.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_200142_0_);
         }
      } else {
         p_200142_0_.setCursor(i);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_200142_0_);
      }
   }

   private static double readDouble(StringReader p_200143_0_, int p_200143_1_) throws CommandSyntaxException {
      if (!p_200143_0_.canRead()) {
         throw LocationPart.ERROR_EXPECTED_DOUBLE.createWithContext(p_200143_0_);
      } else if (p_200143_0_.peek() != '^') {
         p_200143_0_.setCursor(p_200143_1_);
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(p_200143_0_);
      } else {
         p_200143_0_.skip();
         return p_200143_0_.canRead() && p_200143_0_.peek() != ' ' ? p_200143_0_.readDouble() : 0.0D;
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocalLocationArgument)) {
         return false;
      } else {
         LocalLocationArgument locallocationargument = (LocalLocationArgument)p_equals_1_;
         return this.left == locallocationargument.left && this.up == locallocationargument.up && this.forwards == locallocationargument.forwards;
      }
   }

   public int hashCode() {
      return Objects.hash(this.left, this.up, this.forwards);
   }
}
