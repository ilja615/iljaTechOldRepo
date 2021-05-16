package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Subscription extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long startDate;
   public int daysLeft;
   public Subscription.Type type = Subscription.Type.NORMAL;

   public static Subscription parse(String p_230793_0_) {
      Subscription subscription = new Subscription();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_230793_0_).getAsJsonObject();
         subscription.startDate = JsonUtils.getLongOr("startDate", jsonobject, 0L);
         subscription.daysLeft = JsonUtils.getIntOr("daysLeft", jsonobject, 0);
         subscription.type = typeFrom(JsonUtils.getStringOr("subscriptionType", jsonobject, Subscription.Type.NORMAL.name()));
      } catch (Exception exception) {
         LOGGER.error("Could not parse Subscription: " + exception.getMessage());
      }

      return subscription;
   }

   private static Subscription.Type typeFrom(String p_230794_0_) {
      try {
         return Subscription.Type.valueOf(p_230794_0_);
      } catch (Exception exception) {
         return Subscription.Type.NORMAL;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      NORMAL,
      RECURRING;
   }
}
