package com.mojang.realmsclient.gui.screens;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadResult {
   public final int statusCode;
   public final String errorMessage;

   private UploadResult(int p_i51746_1_, String p_i51746_2_) {
      this.statusCode = p_i51746_1_;
      this.errorMessage = p_i51746_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private int statusCode = -1;
      private String errorMessage;

      public UploadResult.Builder withStatusCode(int p_225175_1_) {
         this.statusCode = p_225175_1_;
         return this;
      }

      public UploadResult.Builder withErrorMessage(String p_225176_1_) {
         this.errorMessage = p_225176_1_;
         return this;
      }

      public UploadResult build() {
         return new UploadResult(this.statusCode, this.errorMessage);
      }
   }
}
