package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class OpList extends UserList<GameProfile, OpEntry> {
   public OpList(File p_i1152_1_) {
      super(p_i1152_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject p_152682_1_) {
      return new OpEntry(p_152682_1_);
   }

   public String[] getUserList() {
      String[] astring = new String[this.getEntries().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.getEntries()) {
         astring[i++] = userlistentry.getUser().getName();
      }

      return astring;
   }

   public boolean canBypassPlayerLimit(GameProfile p_183026_1_) {
      OpEntry opentry = this.get(p_183026_1_);
      return opentry != null ? opentry.getBypassesPlayerLimit() : false;
   }

   protected String getKeyForUser(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }
}
