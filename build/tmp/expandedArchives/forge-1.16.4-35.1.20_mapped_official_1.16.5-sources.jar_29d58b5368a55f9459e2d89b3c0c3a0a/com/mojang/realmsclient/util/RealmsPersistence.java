package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.IPersistentSerializable;
import net.minecraft.realms.PersistenceSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class RealmsPersistence {
   private static final PersistenceSerializer GSON = new PersistenceSerializer();

   public static RealmsPersistence.RealmsPersistenceData readFile() {
      File file1 = getPathToData();

      try {
         return GSON.fromJson(FileUtils.readFileToString(file1, StandardCharsets.UTF_8), RealmsPersistence.RealmsPersistenceData.class);
      } catch (IOException ioexception) {
         return new RealmsPersistence.RealmsPersistenceData();
      }
   }

   public static void writeFile(RealmsPersistence.RealmsPersistenceData p_225187_0_) {
      File file1 = getPathToData();

      try {
         FileUtils.writeStringToFile(file1, GSON.toJson(p_225187_0_), StandardCharsets.UTF_8);
      } catch (IOException ioexception) {
      }

   }

   private static File getPathToData() {
      return new File(Minecraft.getInstance().gameDirectory, "realms_persistence.json");
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsPersistenceData implements IPersistentSerializable {
      @SerializedName("newsLink")
      public String newsLink;
      @SerializedName("hasUnreadNews")
      public boolean hasUnreadNews;
   }
}
