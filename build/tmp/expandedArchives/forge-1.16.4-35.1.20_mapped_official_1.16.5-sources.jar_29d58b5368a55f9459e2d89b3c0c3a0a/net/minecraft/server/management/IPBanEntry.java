package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IPBanEntry extends BanEntry<String> {
   public IPBanEntry(String p_i46330_1_) {
      this(p_i46330_1_, (Date)null, (String)null, (Date)null, (String)null);
   }

   public IPBanEntry(String p_i1159_1_, @Nullable Date p_i1159_2_, @Nullable String p_i1159_3_, @Nullable Date p_i1159_4_, @Nullable String p_i1159_5_) {
      super(p_i1159_1_, p_i1159_2_, p_i1159_3_, p_i1159_4_, p_i1159_5_);
   }

   public ITextComponent getDisplayName() {
      return new StringTextComponent(this.getUser());
   }

   public IPBanEntry(JsonObject p_i46331_1_) {
      super(createIpInfo(p_i46331_1_), p_i46331_1_);
   }

   private static String createIpInfo(JsonObject p_152647_0_) {
      return p_152647_0_.has("ip") ? p_152647_0_.get("ip").getAsString() : null;
   }

   protected void serialize(JsonObject p_152641_1_) {
      if (this.getUser() != null) {
         p_152641_1_.addProperty("ip", this.getUser());
         super.serialize(p_152641_1_);
      }
   }
}
