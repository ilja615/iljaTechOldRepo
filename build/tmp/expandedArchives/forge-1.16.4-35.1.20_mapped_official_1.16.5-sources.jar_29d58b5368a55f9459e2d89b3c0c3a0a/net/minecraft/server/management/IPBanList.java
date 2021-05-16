package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class IPBanList extends UserList<String, IPBanEntry> {
   public IPBanList(File p_i1490_1_) {
      super(p_i1490_1_);
   }

   protected UserListEntry<String> createEntry(JsonObject p_152682_1_) {
      return new IPBanEntry(p_152682_1_);
   }

   public boolean isBanned(SocketAddress p_152708_1_) {
      String s = this.getIpFromAddress(p_152708_1_);
      return this.contains(s);
   }

   public boolean isBanned(String p_199044_1_) {
      return this.contains(p_199044_1_);
   }

   public IPBanEntry get(SocketAddress p_152709_1_) {
      String s = this.getIpFromAddress(p_152709_1_);
      return this.get(s);
   }

   private String getIpFromAddress(SocketAddress p_152707_1_) {
      String s = p_152707_1_.toString();
      if (s.contains("/")) {
         s = s.substring(s.indexOf(47) + 1);
      }

      if (s.contains(":")) {
         s = s.substring(0, s.indexOf(58));
      }

      return s;
   }
}
