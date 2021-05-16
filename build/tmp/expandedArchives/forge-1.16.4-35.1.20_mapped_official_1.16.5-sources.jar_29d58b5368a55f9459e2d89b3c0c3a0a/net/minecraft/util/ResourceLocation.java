package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ResourceLocation implements Comparable<ResourceLocation> {
   public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("argument.id.invalid"));
   protected final String namespace;
   protected final String path;

   protected ResourceLocation(String[] p_i47923_1_) {
      this.namespace = org.apache.commons.lang3.StringUtils.isEmpty(p_i47923_1_[0]) ? "minecraft" : p_i47923_1_[0];
      this.path = p_i47923_1_[1];
      if (!isValidNamespace(this.namespace)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
      } else if (!isValidPath(this.path)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
      }
   }

   public ResourceLocation(String p_i1293_1_) {
      this(decompose(p_i1293_1_, ':'));
   }

   public ResourceLocation(String p_i1292_1_, String p_i1292_2_) {
      this(new String[]{p_i1292_1_, p_i1292_2_});
   }

   public static ResourceLocation of(String p_195828_0_, char p_195828_1_) {
      return new ResourceLocation(decompose(p_195828_0_, p_195828_1_));
   }

   @Nullable
   public static ResourceLocation tryParse(String p_208304_0_) {
      try {
         return new ResourceLocation(p_208304_0_);
      } catch (ResourceLocationException resourcelocationexception) {
         return null;
      }
   }

   protected static String[] decompose(String p_195823_0_, char p_195823_1_) {
      String[] astring = new String[]{"minecraft", p_195823_0_};
      int i = p_195823_0_.indexOf(p_195823_1_);
      if (i >= 0) {
         astring[1] = p_195823_0_.substring(i + 1, p_195823_0_.length());
         if (i >= 1) {
            astring[0] = p_195823_0_.substring(0, i);
         }
      }

      return astring;
   }

   private static DataResult<ResourceLocation> read(String p_240911_0_) {
      try {
         return DataResult.success(new ResourceLocation(p_240911_0_));
      } catch (ResourceLocationException resourcelocationexception) {
         return DataResult.error("Not a valid resource location: " + p_240911_0_ + " " + resourcelocationexception.getMessage());
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String toString() {
      return this.namespace + ':' + this.path;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation resourcelocation = (ResourceLocation)p_equals_1_;
         return this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation p_compareTo_1_) {
      int i = this.path.compareTo(p_compareTo_1_.path);
      if (i == 0) {
         i = this.namespace.compareTo(p_compareTo_1_.namespace);
      }

      return i;
   }

   // Normal compare sorts by path first, this compares namespace first.
   public int compareNamespaced(ResourceLocation o) {
      int ret = this.namespace.compareTo(o.namespace);
      return ret != 0 ? ret : this.path.compareTo(o.path);
   }

   public static ResourceLocation read(StringReader p_195826_0_) throws CommandSyntaxException {
      int i = p_195826_0_.getCursor();

      while(p_195826_0_.canRead() && isAllowedInResourceLocation(p_195826_0_.peek())) {
         p_195826_0_.skip();
      }

      String s = p_195826_0_.getString().substring(i, p_195826_0_.getCursor());

      try {
         return new ResourceLocation(s);
      } catch (ResourceLocationException resourcelocationexception) {
         p_195826_0_.setCursor(i);
         throw ERROR_INVALID.createWithContext(p_195826_0_);
      }
   }

   public static boolean isAllowedInResourceLocation(char p_195824_0_) {
      return p_195824_0_ >= '0' && p_195824_0_ <= '9' || p_195824_0_ >= 'a' && p_195824_0_ <= 'z' || p_195824_0_ == '_' || p_195824_0_ == ':' || p_195824_0_ == '/' || p_195824_0_ == '.' || p_195824_0_ == '-';
   }

   private static boolean isValidPath(String p_217856_0_) {
      for(int i = 0; i < p_217856_0_.length(); ++i) {
         if (!validPathChar(p_217856_0_.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   private static boolean isValidNamespace(String p_217858_0_) {
      for(int i = 0; i < p_217858_0_.length(); ++i) {
         if (!validNamespaceChar(p_217858_0_.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean validPathChar(char p_240909_0_) {
      return p_240909_0_ == '_' || p_240909_0_ == '-' || p_240909_0_ >= 'a' && p_240909_0_ <= 'z' || p_240909_0_ >= '0' && p_240909_0_ <= '9' || p_240909_0_ == '/' || p_240909_0_ == '.';
   }

   private static boolean validNamespaceChar(char p_240910_0_) {
      return p_240910_0_ == '_' || p_240910_0_ == '-' || p_240910_0_ >= 'a' && p_240910_0_ <= 'z' || p_240910_0_ >= '0' && p_240910_0_ <= '9' || p_240910_0_ == '.';
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isValidResourceLocation(String p_217855_0_) {
      String[] astring = decompose(p_217855_0_, ':');
      return isValidNamespace(org.apache.commons.lang3.StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0]) && isValidPath(astring[1]);
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public ResourceLocation deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new ResourceLocation(JSONUtils.convertToString(p_deserialize_1_, "location"));
      }

      public JsonElement serialize(ResourceLocation p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return new JsonPrimitive(p_serialize_1_.toString());
      }
   }
}
