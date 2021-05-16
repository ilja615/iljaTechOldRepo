package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class WhitelistEntry extends UserListEntry<GameProfile> {
   public WhitelistEntry(GameProfile p_i1129_1_) {
      super(p_i1129_1_);
   }

   public WhitelistEntry(JsonObject p_i1130_1_) {
      super(createGameProfile(p_i1130_1_));
   }

   protected void serialize(JsonObject p_152641_1_) {
      if (this.getUser() != null) {
         p_152641_1_.addProperty("uuid", this.getUser().getId() == null ? "" : this.getUser().getId().toString());
         p_152641_1_.addProperty("name", this.getUser().getName());
      }
   }

   private static GameProfile createGameProfile(JsonObject p_152646_0_) {
      if (p_152646_0_.has("uuid") && p_152646_0_.has("name")) {
         String s = p_152646_0_.get("uuid").getAsString();

         UUID uuid;
         try {
            uuid = UUID.fromString(s);
         } catch (Throwable throwable) {
            return null;
         }

         return new GameProfile(uuid, p_152646_0_.get("name").getAsString());
      } else {
         return null;
      }
   }
}
