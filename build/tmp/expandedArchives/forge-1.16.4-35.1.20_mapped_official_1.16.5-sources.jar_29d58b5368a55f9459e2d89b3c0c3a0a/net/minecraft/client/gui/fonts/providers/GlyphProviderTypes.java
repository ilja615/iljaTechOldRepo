package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GlyphProviderTypes {
   BITMAP("bitmap", TextureGlyphProvider.Factory::fromJson),
   TTF("ttf", TrueTypeGlyphProviderFactory::fromJson),
   LEGACY_UNICODE("legacy_unicode", UnicodeTextureGlyphProvider.Factory::fromJson);

   private static final Map<String, GlyphProviderTypes> BY_NAME = Util.make(Maps.newHashMap(), (p_211639_0_) -> {
      for(GlyphProviderTypes glyphprovidertypes : values()) {
         p_211639_0_.put(glyphprovidertypes.name, glyphprovidertypes);
      }

   });
   private final String name;
   private final Function<JsonObject, IGlyphProviderFactory> factory;

   private GlyphProviderTypes(String p_i49766_3_, Function<JsonObject, IGlyphProviderFactory> p_i49766_4_) {
      this.name = p_i49766_3_;
      this.factory = p_i49766_4_;
   }

   public static GlyphProviderTypes byName(String p_211638_0_) {
      GlyphProviderTypes glyphprovidertypes = BY_NAME.get(p_211638_0_);
      if (glyphprovidertypes == null) {
         throw new IllegalArgumentException("Invalid type: " + p_211638_0_);
      } else {
         return glyphprovidertypes;
      }
   }

   public IGlyphProviderFactory create(JsonObject p_211637_1_) {
      return this.factory.apply(p_211637_1_);
   }
}
