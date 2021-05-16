package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldOptions extends ValueObject {
   public Boolean pvp;
   public Boolean spawnAnimals;
   public Boolean spawnMonsters;
   public Boolean spawnNPCs;
   public Integer spawnProtection;
   public Boolean commandBlocks;
   public Boolean forceGameMode;
   public Integer difficulty;
   public Integer gameMode;
   public String slotName;
   public long templateId;
   public String templateImage;
   public boolean adventureMap;
   public boolean empty;
   private static final String DEFAULT_TEMPLATE_IMAGE = null;

   public RealmsWorldOptions(Boolean p_i51651_1_, Boolean p_i51651_2_, Boolean p_i51651_3_, Boolean p_i51651_4_, Integer p_i51651_5_, Boolean p_i51651_6_, Integer p_i51651_7_, Integer p_i51651_8_, Boolean p_i51651_9_, String p_i51651_10_) {
      this.pvp = p_i51651_1_;
      this.spawnAnimals = p_i51651_2_;
      this.spawnMonsters = p_i51651_3_;
      this.spawnNPCs = p_i51651_4_;
      this.spawnProtection = p_i51651_5_;
      this.commandBlocks = p_i51651_6_;
      this.difficulty = p_i51651_7_;
      this.gameMode = p_i51651_8_;
      this.forceGameMode = p_i51651_9_;
      this.slotName = p_i51651_10_;
   }

   public static RealmsWorldOptions createDefaults() {
      return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
   }

   public static RealmsWorldOptions createEmptyDefaults() {
      RealmsWorldOptions realmsworldoptions = createDefaults();
      realmsworldoptions.setEmpty(true);
      return realmsworldoptions;
   }

   public void setEmpty(boolean p_230789_1_) {
      this.empty = p_230789_1_;
   }

   public static RealmsWorldOptions parse(JsonObject p_230788_0_) {
      RealmsWorldOptions realmsworldoptions = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", p_230788_0_, true), JsonUtils.getBooleanOr("spawnAnimals", p_230788_0_, true), JsonUtils.getBooleanOr("spawnMonsters", p_230788_0_, true), JsonUtils.getBooleanOr("spawnNPCs", p_230788_0_, true), JsonUtils.getIntOr("spawnProtection", p_230788_0_, 0), JsonUtils.getBooleanOr("commandBlocks", p_230788_0_, false), JsonUtils.getIntOr("difficulty", p_230788_0_, 2), JsonUtils.getIntOr("gameMode", p_230788_0_, 0), JsonUtils.getBooleanOr("forceGameMode", p_230788_0_, false), JsonUtils.getStringOr("slotName", p_230788_0_, ""));
      realmsworldoptions.templateId = JsonUtils.getLongOr("worldTemplateId", p_230788_0_, -1L);
      realmsworldoptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", p_230788_0_, DEFAULT_TEMPLATE_IMAGE);
      realmsworldoptions.adventureMap = JsonUtils.getBooleanOr("adventureMap", p_230788_0_, false);
      return realmsworldoptions;
   }

   public String getSlotName(int p_230787_1_) {
      if (this.slotName != null && !this.slotName.isEmpty()) {
         return this.slotName;
      } else {
         return this.empty ? I18n.get("mco.configure.world.slot.empty") : this.getDefaultSlotName(p_230787_1_);
      }
   }

   public String getDefaultSlotName(int p_230790_1_) {
      return I18n.get("mco.configure.world.slot", p_230790_1_);
   }

   public String toJson() {
      JsonObject jsonobject = new JsonObject();
      if (!this.pvp) {
         jsonobject.addProperty("pvp", this.pvp);
      }

      if (!this.spawnAnimals) {
         jsonobject.addProperty("spawnAnimals", this.spawnAnimals);
      }

      if (!this.spawnMonsters) {
         jsonobject.addProperty("spawnMonsters", this.spawnMonsters);
      }

      if (!this.spawnNPCs) {
         jsonobject.addProperty("spawnNPCs", this.spawnNPCs);
      }

      if (this.spawnProtection != 0) {
         jsonobject.addProperty("spawnProtection", this.spawnProtection);
      }

      if (this.commandBlocks) {
         jsonobject.addProperty("commandBlocks", this.commandBlocks);
      }

      if (this.difficulty != 2) {
         jsonobject.addProperty("difficulty", this.difficulty);
      }

      if (this.gameMode != 0) {
         jsonobject.addProperty("gameMode", this.gameMode);
      }

      if (this.forceGameMode) {
         jsonobject.addProperty("forceGameMode", this.forceGameMode);
      }

      if (!Objects.equals(this.slotName, "")) {
         jsonobject.addProperty("slotName", this.slotName);
      }

      return jsonobject.toString();
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
   }
}
