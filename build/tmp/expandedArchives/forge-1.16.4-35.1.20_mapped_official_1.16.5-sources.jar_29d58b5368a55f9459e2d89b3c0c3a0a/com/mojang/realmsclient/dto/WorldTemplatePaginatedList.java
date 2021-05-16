package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldTemplatePaginatedList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<WorldTemplate> templates;
   public int page;
   public int size;
   public int total;

   public WorldTemplatePaginatedList() {
   }

   public WorldTemplatePaginatedList(int p_i51733_1_) {
      this.templates = Collections.emptyList();
      this.page = 0;
      this.size = p_i51733_1_;
      this.total = -1;
   }

   public static WorldTemplatePaginatedList parse(String p_230804_0_) {
      WorldTemplatePaginatedList worldtemplatepaginatedlist = new WorldTemplatePaginatedList();
      worldtemplatepaginatedlist.templates = Lists.newArrayList();

      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_230804_0_).getAsJsonObject();
         if (jsonobject.get("templates").isJsonArray()) {
            Iterator<JsonElement> iterator = jsonobject.get("templates").getAsJsonArray().iterator();

            while(iterator.hasNext()) {
               worldtemplatepaginatedlist.templates.add(WorldTemplate.parse(iterator.next().getAsJsonObject()));
            }
         }

         worldtemplatepaginatedlist.page = JsonUtils.getIntOr("page", jsonobject, 0);
         worldtemplatepaginatedlist.size = JsonUtils.getIntOr("size", jsonobject, 0);
         worldtemplatepaginatedlist.total = JsonUtils.getIntOr("total", jsonobject, 0);
      } catch (Exception exception) {
         LOGGER.error("Could not parse WorldTemplatePaginatedList: " + exception.getMessage());
      }

      return worldtemplatepaginatedlist;
   }
}
