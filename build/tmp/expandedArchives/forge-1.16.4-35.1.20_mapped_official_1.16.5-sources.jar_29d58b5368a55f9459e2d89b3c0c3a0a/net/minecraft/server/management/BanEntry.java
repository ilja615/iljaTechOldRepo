package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public abstract class BanEntry<T> extends UserListEntry<T> {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   protected final Date created;
   protected final String source;
   protected final Date expires;
   protected final String reason;

   public BanEntry(T p_i46334_1_, @Nullable Date p_i46334_2_, @Nullable String p_i46334_3_, @Nullable Date p_i46334_4_, @Nullable String p_i46334_5_) {
      super(p_i46334_1_);
      this.created = p_i46334_2_ == null ? new Date() : p_i46334_2_;
      this.source = p_i46334_3_ == null ? "(Unknown)" : p_i46334_3_;
      this.expires = p_i46334_4_;
      this.reason = p_i46334_5_ == null ? "Banned by an operator." : p_i46334_5_;
   }

   protected BanEntry(T p_i1174_1_, JsonObject p_i1174_2_) {
      super(p_i1174_1_);

      Date date;
      try {
         date = p_i1174_2_.has("created") ? DATE_FORMAT.parse(p_i1174_2_.get("created").getAsString()) : new Date();
      } catch (ParseException parseexception1) {
         date = new Date();
      }

      this.created = date;
      this.source = p_i1174_2_.has("source") ? p_i1174_2_.get("source").getAsString() : "(Unknown)";

      Date date1;
      try {
         date1 = p_i1174_2_.has("expires") ? DATE_FORMAT.parse(p_i1174_2_.get("expires").getAsString()) : null;
      } catch (ParseException parseexception) {
         date1 = null;
      }

      this.expires = date1;
      this.reason = p_i1174_2_.has("reason") ? p_i1174_2_.get("reason").getAsString() : "Banned by an operator.";
   }

   public String getSource() {
      return this.source;
   }

   public Date getExpires() {
      return this.expires;
   }

   public String getReason() {
      return this.reason;
   }

   public abstract ITextComponent getDisplayName();

   boolean hasExpired() {
      return this.expires == null ? false : this.expires.before(new Date());
   }

   protected void serialize(JsonObject p_152641_1_) {
      p_152641_1_.addProperty("created", DATE_FORMAT.format(this.created));
      p_152641_1_.addProperty("source", this.source);
      p_152641_1_.addProperty("expires", this.expires == null ? "forever" : DATE_FORMAT.format(this.expires));
      p_152641_1_.addProperty("reason", this.reason);
   }
}
