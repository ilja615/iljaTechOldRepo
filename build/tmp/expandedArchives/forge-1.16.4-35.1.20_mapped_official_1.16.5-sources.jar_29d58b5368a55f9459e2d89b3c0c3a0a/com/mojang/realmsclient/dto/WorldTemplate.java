package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldTemplate extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String id = "";
   public String name = "";
   public String version = "";
   public String author = "";
   public String link = "";
   @Nullable
   public String image;
   public String trailer = "";
   public String recommendedPlayers = "";
   public WorldTemplate.Type type = WorldTemplate.Type.WORLD_TEMPLATE;

   public static WorldTemplate parse(JsonObject p_230803_0_) {
      WorldTemplate worldtemplate = new WorldTemplate();

      try {
         worldtemplate.id = JsonUtils.getStringOr("id", p_230803_0_, "");
         worldtemplate.name = JsonUtils.getStringOr("name", p_230803_0_, "");
         worldtemplate.version = JsonUtils.getStringOr("version", p_230803_0_, "");
         worldtemplate.author = JsonUtils.getStringOr("author", p_230803_0_, "");
         worldtemplate.link = JsonUtils.getStringOr("link", p_230803_0_, "");
         worldtemplate.image = JsonUtils.getStringOr("image", p_230803_0_, (String)null);
         worldtemplate.trailer = JsonUtils.getStringOr("trailer", p_230803_0_, "");
         worldtemplate.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", p_230803_0_, "");
         worldtemplate.type = WorldTemplate.Type.valueOf(JsonUtils.getStringOr("type", p_230803_0_, WorldTemplate.Type.WORLD_TEMPLATE.name()));
      } catch (Exception exception) {
         LOGGER.error("Could not parse WorldTemplate: " + exception.getMessage());
      }

      return worldtemplate;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}
