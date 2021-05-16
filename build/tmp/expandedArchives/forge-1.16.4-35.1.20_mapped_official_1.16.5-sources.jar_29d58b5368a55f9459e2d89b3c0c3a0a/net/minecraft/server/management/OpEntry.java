package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class OpEntry extends UserListEntry<GameProfile> {
   private final int level;
   private final boolean bypassesPlayerLimit;

   public OpEntry(GameProfile p_i46492_1_, int p_i46492_2_, boolean p_i46492_3_) {
      super(p_i46492_1_);
      this.level = p_i46492_2_;
      this.bypassesPlayerLimit = p_i46492_3_;
   }

   public OpEntry(JsonObject p_i1150_1_) {
      super(createGameProfile(p_i1150_1_));
      this.level = p_i1150_1_.has("level") ? p_i1150_1_.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = p_i1150_1_.has("bypassesPlayerLimit") && p_i1150_1_.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getLevel() {
      return this.level;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void serialize(JsonObject p_152641_1_) {
      if (this.getUser() != null) {
         p_152641_1_.addProperty("uuid", this.getUser().getId() == null ? "" : this.getUser().getId().toString());
         p_152641_1_.addProperty("name", this.getUser().getName());
         p_152641_1_.addProperty("level", this.level);
         p_152641_1_.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }

   private static GameProfile createGameProfile(JsonObject p_152643_0_) {
      if (p_152643_0_.has("uuid") && p_152643_0_.has("name")) {
         String s = p_152643_0_.get("uuid").getAsString();

         UUID uuid;
         try {
            uuid = UUID.fromString(s);
         } catch (Throwable throwable) {
            return null;
         }

         return new GameProfile(uuid, p_152643_0_.get("name").getAsString());
      } else {
         return null;
      }
   }
}
