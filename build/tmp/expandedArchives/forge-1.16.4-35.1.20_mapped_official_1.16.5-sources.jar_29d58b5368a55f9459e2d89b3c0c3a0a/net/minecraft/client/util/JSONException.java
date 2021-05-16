package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class JSONException extends IOException {
   private final List<JSONException.Entry> entries = Lists.newArrayList();
   private final String message;

   public JSONException(String p_i45279_1_) {
      this.entries.add(new JSONException.Entry());
      this.message = p_i45279_1_;
   }

   public JSONException(String p_i45280_1_, Throwable p_i45280_2_) {
      super(p_i45280_2_);
      this.entries.add(new JSONException.Entry());
      this.message = p_i45280_1_;
   }

   public void prependJsonKey(String p_151380_1_) {
      this.entries.get(0).addJsonKey(p_151380_1_);
   }

   public void setFilenameAndFlush(String p_151381_1_) {
      (this.entries.get(0)).filename = p_151381_1_;
      this.entries.add(0, new JSONException.Entry());
   }

   public String getMessage() {
      return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
   }

   public static JSONException forException(Exception p_151379_0_) {
      if (p_151379_0_ instanceof JSONException) {
         return (JSONException)p_151379_0_;
      } else {
         String s = p_151379_0_.getMessage();
         if (p_151379_0_ instanceof FileNotFoundException) {
            s = "File not found";
         }

         return new JSONException(s, p_151379_0_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Entry {
      @Nullable
      private String filename;
      private final List<String> jsonKeys = Lists.newArrayList();

      private Entry() {
      }

      private void addJsonKey(String p_151373_1_) {
         this.jsonKeys.add(0, p_151373_1_);
      }

      public String getJsonKeys() {
         return StringUtils.join((Iterable<?>)this.jsonKeys, "->");
      }

      public String toString() {
         if (this.filename != null) {
            return this.jsonKeys.isEmpty() ? this.filename : this.filename + " " + this.getJsonKeys();
         } else {
            return this.jsonKeys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.getJsonKeys();
         }
      }
   }
}
