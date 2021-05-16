package net.minecraft.client.renderer;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class VideoMode {
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final int refreshRate;
   private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int p_i47669_1_, int p_i47669_2_, int p_i47669_3_, int p_i47669_4_, int p_i47669_5_, int p_i47669_6_) {
      this.width = p_i47669_1_;
      this.height = p_i47669_2_;
      this.redBits = p_i47669_3_;
      this.greenBits = p_i47669_4_;
      this.blueBits = p_i47669_5_;
      this.refreshRate = p_i47669_6_;
   }

   public VideoMode(Buffer p_i47670_1_) {
      this.width = p_i47670_1_.width();
      this.height = p_i47670_1_.height();
      this.redBits = p_i47670_1_.redBits();
      this.greenBits = p_i47670_1_.greenBits();
      this.blueBits = p_i47670_1_.blueBits();
      this.refreshRate = p_i47670_1_.refreshRate();
   }

   public VideoMode(GLFWVidMode p_i47671_1_) {
      this.width = p_i47671_1_.width();
      this.height = p_i47671_1_.height();
      this.redBits = p_i47671_1_.redBits();
      this.greenBits = p_i47671_1_.greenBits();
      this.blueBits = p_i47671_1_.blueBits();
      this.refreshRate = p_i47671_1_.refreshRate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VideoMode videomode = (VideoMode)p_equals_1_;
         return this.width == videomode.width && this.height == videomode.height && this.redBits == videomode.redBits && this.greenBits == videomode.greenBits && this.blueBits == videomode.blueBits && this.refreshRate == videomode.refreshRate;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate);
   }

   public String toString() {
      return String.format("%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }

   public static Optional<VideoMode> read(@Nullable String p_198061_0_) {
      if (p_198061_0_ == null) {
         return Optional.empty();
      } else {
         try {
            Matcher matcher = PATTERN.matcher(p_198061_0_);
            if (matcher.matches()) {
               int i = Integer.parseInt(matcher.group(1));
               int j = Integer.parseInt(matcher.group(2));
               String s = matcher.group(3);
               int k;
               if (s == null) {
                  k = 60;
               } else {
                  k = Integer.parseInt(s);
               }

               String s1 = matcher.group(4);
               int l;
               if (s1 == null) {
                  l = 24;
               } else {
                  l = Integer.parseInt(s1);
               }

               int i1 = l / 3;
               return Optional.of(new VideoMode(i, j, i1, i1, i1, k));
            }
         } catch (Exception exception) {
         }

         return Optional.empty();
      }
   }

   public String write() {
      return String.format("%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }
}
