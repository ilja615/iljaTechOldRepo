package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class BanList extends UserList<GameProfile, ProfileBanEntry> {
   public BanList(File p_i1138_1_) {
      super(p_i1138_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new ProfileBanEntry(p_152682_1_);
   }

   public boolean isBanned(GameProfile p_152702_1_) {
      return this.contains(p_152702_1_);
   }

   public String[] getUserList() {
      String[] astring = new String[this.getEntries().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.getEntries()) {
         astring[i++] = userlistentry.getUser().getName();
      }

      return astring;
   }

   protected String getKeyForUser(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }
}
