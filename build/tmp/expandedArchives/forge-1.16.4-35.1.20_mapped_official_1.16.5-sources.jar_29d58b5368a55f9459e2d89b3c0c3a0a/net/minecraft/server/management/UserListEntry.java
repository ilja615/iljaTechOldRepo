package net.minecraft.server.management;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public abstract class UserListEntry<T> {
   @Nullable
   private final T user;

   public UserListEntry(@Nullable T p_i1146_1_) {
      this.user = p_i1146_1_;
   }

   @Nullable
   T getUser() {
      return this.user;
   }

   boolean hasExpired() {
      return false;
   }

   protected abstract void serialize(JsonObject p_152641_1_);
}
