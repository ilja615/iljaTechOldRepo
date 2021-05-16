package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PendingInvite extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String invitationId;
   public String worldName;
   public String worldOwnerName;
   public String worldOwnerUuid;
   public Date date;

   public static PendingInvite parse(JsonObject p_230755_0_) {
      PendingInvite pendinginvite = new PendingInvite();

      try {
         pendinginvite.invitationId = JsonUtils.getStringOr("invitationId", p_230755_0_, "");
         pendinginvite.worldName = JsonUtils.getStringOr("worldName", p_230755_0_, "");
         pendinginvite.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", p_230755_0_, "");
         pendinginvite.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", p_230755_0_, "");
         pendinginvite.date = JsonUtils.getDateOr("date", p_230755_0_);
      } catch (Exception exception) {
         LOGGER.error("Could not parse PendingInvite: " + exception.getMessage());
      }

      return pendinginvite;
   }
}
