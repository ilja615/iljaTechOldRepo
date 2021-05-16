package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServiceException extends Exception {
   public final int httpResultCode;
   public final String httpResponseContent;
   public final int errorCode;
   public final String errorMsg;

   public RealmsServiceException(int p_i51784_1_, String p_i51784_2_, RealmsError p_i51784_3_) {
      super(p_i51784_2_);
      this.httpResultCode = p_i51784_1_;
      this.httpResponseContent = p_i51784_2_;
      this.errorCode = p_i51784_3_.getErrorCode();
      this.errorMsg = p_i51784_3_.getErrorMessage();
   }

   public RealmsServiceException(int p_i51785_1_, String p_i51785_2_, int p_i51785_3_, String p_i51785_4_) {
      super(p_i51785_2_);
      this.httpResultCode = p_i51785_1_;
      this.httpResponseContent = p_i51785_2_;
      this.errorCode = p_i51785_3_;
      this.errorMsg = p_i51785_4_;
   }

   public String toString() {
      if (this.errorCode == -1) {
         return "Realms (" + this.httpResultCode + ") " + this.httpResponseContent;
      } else {
         String s = "mco.errorMessage." + this.errorCode;
         String s1 = I18n.get(s);
         return (s1.equals(s) ? this.errorMsg : s1) + " - " + this.errorCode;
      }
   }
}
