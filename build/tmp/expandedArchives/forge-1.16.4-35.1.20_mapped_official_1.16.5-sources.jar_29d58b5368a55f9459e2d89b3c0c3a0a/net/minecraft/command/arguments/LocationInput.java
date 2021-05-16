package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class LocationInput implements ILocationArgument {
   private final LocationPart x;
   private final LocationPart y;
   private final LocationPart z;

   public LocationInput(LocationPart p_i47962_1_, LocationPart p_i47962_2_, LocationPart p_i47962_3_) {
      this.x = p_i47962_1_;
      this.y = p_i47962_2_;
      this.z = p_i47962_3_;
   }

   public Vector3d getPosition(CommandSource p_197281_1_) {
      Vector3d vector3d = p_197281_1_.getPosition();
      return new Vector3d(this.x.get(vector3d.x), this.y.get(vector3d.y), this.z.get(vector3d.z));
   }

   public Vector2f getRotation(CommandSource p_197282_1_) {
      Vector2f vector2f = p_197282_1_.getRotation();
      return new Vector2f((float)this.x.get((double)vector2f.x), (float)this.y.get((double)vector2f.y));
   }

   public boolean isXRelative() {
      return this.x.isRelative();
   }

   public boolean isYRelative() {
      return this.y.isRelative();
   }

   public boolean isZRelative() {
      return this.z.isRelative();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocationInput)) {
         return false;
      } else {
         LocationInput locationinput = (LocationInput)p_equals_1_;
         if (!this.x.equals(locationinput.x)) {
            return false;
         } else {
            return !this.y.equals(locationinput.y) ? false : this.z.equals(locationinput.z);
         }
      }
   }

   public static LocationInput parseInt(StringReader p_200148_0_) throws CommandSyntaxException {
      int i = p_200148_0_.getCursor();
      LocationPart locationpart = LocationPart.parseInt(p_200148_0_);
      if (p_200148_0_.canRead() && p_200148_0_.peek() == ' ') {
         p_200148_0_.skip();
         LocationPart locationpart1 = LocationPart.parseInt(p_200148_0_);
         if (p_200148_0_.canRead() && p_200148_0_.peek() == ' ') {
            p_200148_0_.skip();
            LocationPart locationpart2 = LocationPart.parseInt(p_200148_0_);
            return new LocationInput(locationpart, locationpart1, locationpart2);
         } else {
            p_200148_0_.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_200148_0_);
         }
      } else {
         p_200148_0_.setCursor(i);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_200148_0_);
      }
   }

   public static LocationInput parseDouble(StringReader p_200147_0_, boolean p_200147_1_) throws CommandSyntaxException {
      int i = p_200147_0_.getCursor();
      LocationPart locationpart = LocationPart.parseDouble(p_200147_0_, p_200147_1_);
      if (p_200147_0_.canRead() && p_200147_0_.peek() == ' ') {
         p_200147_0_.skip();
         LocationPart locationpart1 = LocationPart.parseDouble(p_200147_0_, false);
         if (p_200147_0_.canRead() && p_200147_0_.peek() == ' ') {
            p_200147_0_.skip();
            LocationPart locationpart2 = LocationPart.parseDouble(p_200147_0_, p_200147_1_);
            return new LocationInput(locationpart, locationpart1, locationpart2);
         } else {
            p_200147_0_.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_200147_0_);
         }
      } else {
         p_200147_0_.setCursor(i);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_200147_0_);
      }
   }

   public static LocationInput current() {
      return new LocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D), new LocationPart(true, 0.0D));
   }

   public int hashCode() {
      int i = this.x.hashCode();
      i = 31 * i + this.y.hashCode();
      return 31 * i + this.z.hashCode();
   }
}
