package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Session {
   private final String name;
   private final String uuid;
   private final String accessToken;
   private final Session.Type type;
   /** Forge: Cache of the local session's GameProfile properties. */
   private com.mojang.authlib.properties.PropertyMap properties;

   public Session(String p_i1098_1_, String p_i1098_2_, String p_i1098_3_, String p_i1098_4_) {
      if (p_i1098_1_ == null || p_i1098_1_.isEmpty()) {
         p_i1098_1_ = "MissingName";
         p_i1098_2_ = p_i1098_3_ = "NotValid";
         org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(getClass().getName());
         logger.log(org.apache.logging.log4j.Level.WARN, "=========================================================");
         logger.log(org.apache.logging.log4j.Level.WARN, "WARNING!! the username was not set for this session, typically");
         logger.log(org.apache.logging.log4j.Level.WARN, "this means you installed Forge incorrectly. We have set your");
         logger.log(org.apache.logging.log4j.Level.WARN, "name to \"MissingName\" and your session to nothing. Please");
         logger.log(org.apache.logging.log4j.Level.WARN, "check your installation and post a console log from the launcher");
         logger.log(org.apache.logging.log4j.Level.WARN, "when asking for help!");
         logger.log(org.apache.logging.log4j.Level.WARN, "=========================================================");
      }
      this.name = p_i1098_1_;
      this.uuid = p_i1098_2_;
      this.accessToken = p_i1098_3_;
      this.type = Session.Type.byName(p_i1098_4_);
   }

   public String getSessionId() {
      return "token:" + this.accessToken + ":" + this.uuid;
   }

   public String getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public GameProfile getGameProfile() {
      try {
         UUID uuid = UUIDTypeAdapter.fromString(this.getUuid());
         GameProfile ret = new GameProfile(uuid, this.getName());    //Forge: Adds cached GameProfile properties to returned GameProfile.
         if (properties != null) ret.getProperties().putAll(properties); // Helps to cut down on calls to the session service,
         return ret;                                                     // which helps to fix MC-52974.
      } catch (IllegalArgumentException illegalargumentexception) {
         return new GameProfile((UUID)null, this.getName());
      }
   }

   //For internal use only. Modders should never need to use this.
   public void setProperties(com.mojang.authlib.properties.PropertyMap properties) {
       if (this.properties == null)
           this.properties = properties;
   }

   public boolean hasCachedProperties() {
       return properties != null;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((p_199876_0_) -> {
         return p_199876_0_.name;
      }, Function.identity()));
      private final String name;

      private Type(String p_i1096_3_) {
         this.name = p_i1096_3_;
      }

      @Nullable
      public static Session.Type byName(String p_152421_0_) {
         return BY_NAME.get(p_152421_0_.toLowerCase(Locale.ROOT));
      }
   }
}
