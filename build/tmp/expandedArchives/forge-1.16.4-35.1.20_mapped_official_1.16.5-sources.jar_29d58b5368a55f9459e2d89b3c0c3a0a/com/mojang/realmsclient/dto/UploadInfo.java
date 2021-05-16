package com.mojang.realmsclient.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UploadInfo extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern URI_SCHEMA_PATTERN = Pattern.compile("^[a-zA-Z][-a-zA-Z0-9+.]+:");
   private final boolean worldClosed;
   @Nullable
   private final String token;
   private final URI uploadEndpoint;

   private UploadInfo(boolean p_i242046_1_, @Nullable String p_i242046_2_, URI p_i242046_3_) {
      this.worldClosed = p_i242046_1_;
      this.token = p_i242046_2_;
      this.uploadEndpoint = p_i242046_3_;
   }

   @Nullable
   public static UploadInfo parse(String p_230796_0_) {
      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_230796_0_).getAsJsonObject();
         String s = JsonUtils.getStringOr("uploadEndpoint", jsonobject, (String)null);
         if (s != null) {
            int i = JsonUtils.getIntOr("port", jsonobject, -1);
            URI uri = assembleUri(s, i);
            if (uri != null) {
               boolean flag = JsonUtils.getBooleanOr("worldClosed", jsonobject, false);
               String s1 = JsonUtils.getStringOr("token", jsonobject, (String)null);
               return new UploadInfo(flag, s1, uri);
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Could not parse UploadInfo: " + exception.getMessage());
      }

      return null;
   }

   @Nullable
   @VisibleForTesting
   public static URI assembleUri(String p_243087_0_, int p_243087_1_) {
      Matcher matcher = URI_SCHEMA_PATTERN.matcher(p_243087_0_);
      String s = ensureEndpointSchema(p_243087_0_, matcher);

      try {
         URI uri = new URI(s);
         int i = selectPortOrDefault(p_243087_1_, uri.getPort());
         return i != uri.getPort() ? new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), i, uri.getPath(), uri.getQuery(), uri.getFragment()) : uri;
      } catch (URISyntaxException urisyntaxexception) {
         LOGGER.warn("Failed to parse URI {}", s, urisyntaxexception);
         return null;
      }
   }

   private static int selectPortOrDefault(int p_243086_0_, int p_243086_1_) {
      if (p_243086_0_ != -1) {
         return p_243086_0_;
      } else {
         return p_243086_1_ != -1 ? p_243086_1_ : 8080;
      }
   }

   private static String ensureEndpointSchema(String p_243088_0_, Matcher p_243088_1_) {
      return p_243088_1_.find() ? p_243088_0_ : "http://" + p_243088_0_;
   }

   public static String createRequest(@Nullable String p_243090_0_) {
      JsonObject jsonobject = new JsonObject();
      if (p_243090_0_ != null) {
         jsonobject.addProperty("token", p_243090_0_);
      }

      return jsonobject.toString();
   }

   @Nullable
   public String getToken() {
      return this.token;
   }

   public URI getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }
}
