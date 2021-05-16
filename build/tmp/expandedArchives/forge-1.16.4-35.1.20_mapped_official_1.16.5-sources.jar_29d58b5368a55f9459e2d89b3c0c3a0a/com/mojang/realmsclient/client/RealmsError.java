package com.mojang.realmsclient.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsError {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String errorMessage;
   private final int errorCode;

   private RealmsError(String p_i241823_1_, int p_i241823_2_) {
      this.errorMessage = p_i241823_1_;
      this.errorCode = p_i241823_2_;
   }

   public static RealmsError create(String p_241826_0_) {
      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_241826_0_).getAsJsonObject();
         String s = JsonUtils.getStringOr("errorMsg", jsonobject, "");
         int i = JsonUtils.getIntOr("errorCode", jsonobject, -1);
         return new RealmsError(s, i);
      } catch (Exception exception) {
         LOGGER.error("Could not parse RealmsError: " + exception.getMessage());
         LOGGER.error("The error was: " + p_241826_0_);
         return new RealmsError("Failed to parse response from server", -1);
      }
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public int getErrorCode() {
      return this.errorCode;
   }
}
