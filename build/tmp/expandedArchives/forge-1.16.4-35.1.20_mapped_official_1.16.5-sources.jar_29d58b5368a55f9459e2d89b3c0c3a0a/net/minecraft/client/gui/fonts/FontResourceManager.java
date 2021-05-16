package net.minecraft.client.gui.fonts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontResourceManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
   private final Font missingFontSet;
   private final Map<ResourceLocation, Font> fontSets = Maps.newHashMap();
   private final TextureManager textureManager;
   private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
   private final IFutureReloadListener reloadListener = new ReloadListener<Map<ResourceLocation, List<IGlyphProvider>>>() {
      protected Map<ResourceLocation, List<IGlyphProvider>> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
         p_212854_2_.startTick();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         Map<ResourceLocation, List<IGlyphProvider>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_212854_1_.listResources("font", (p_215274_0_) -> {
            return p_215274_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring("font/".length(), s.length() - ".json".length()));
            List<IGlyphProvider> list = map.computeIfAbsent(resourcelocation1, (p_215272_0_) -> {
               return Lists.newArrayList(new DefaultGlyphProvider());
            });
            p_212854_2_.push(resourcelocation1::toString);

            try {
               for(IResource iresource : p_212854_1_.getResources(resourcelocation)) {
                  p_212854_2_.push(iresource::getSourceName);

                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     p_212854_2_.push("reading");
                     JsonArray jsonarray = JSONUtils.getAsJsonArray(JSONUtils.fromJson(gson, reader, JsonObject.class), "providers");
                     p_212854_2_.popPush("parsing");

                     for(int i = jsonarray.size() - 1; i >= 0; --i) {
                        JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonarray.get(i), "providers[" + i + "]");

                        try {
                           String s1 = JSONUtils.getAsString(jsonobject, "type");
                           GlyphProviderTypes glyphprovidertypes = GlyphProviderTypes.byName(s1);
                           p_212854_2_.push(s1);
                           IGlyphProvider iglyphprovider = glyphprovidertypes.create(jsonobject).create(p_212854_1_);
                           if (iglyphprovider != null) {
                              list.add(iglyphprovider);
                           }

                           p_212854_2_.pop();
                        } catch (RuntimeException runtimeexception) {
                           FontResourceManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getSourceName(), runtimeexception.getMessage());
                        }
                     }

                     p_212854_2_.pop();
                  } catch (RuntimeException runtimeexception1) {
                     FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getSourceName(), runtimeexception1.getMessage());
                  }

                  p_212854_2_.pop();
               }
            } catch (IOException ioexception) {
               FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", resourcelocation1, ioexception.getMessage());
            }

            p_212854_2_.push("caching");
            IntSet intset = new IntOpenHashSet();

            for(IGlyphProvider iglyphprovider1 : list) {
               intset.addAll(iglyphprovider1.getSupportedGlyphs());
            }

            intset.forEach((int p_238555_1_) -> {
               if (p_238555_1_ != 32) {
                  for(IGlyphProvider iglyphprovider2 : Lists.reverse(list)) {
                     if (iglyphprovider2.getGlyph(p_238555_1_) != null) {
                        break;
                     }
                  }

               }
            });
            p_212854_2_.pop();
            p_212854_2_.pop();
         }

         p_212854_2_.endTick();
         return map;
      }

      protected void apply(Map<ResourceLocation, List<IGlyphProvider>> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
         p_212853_3_.startTick();
         p_212853_3_.push("closing");
         FontResourceManager.this.fontSets.values().forEach(Font::close);
         FontResourceManager.this.fontSets.clear();
         p_212853_3_.popPush("reloading");
         p_212853_1_.forEach((p_238556_1_, p_238556_2_) -> {
            Font font = new Font(FontResourceManager.this.textureManager, p_238556_1_);
            font.reload(Lists.reverse(p_238556_2_));
            FontResourceManager.this.fontSets.put(p_238556_1_, font);
         });
         p_212853_3_.pop();
         p_212853_3_.endTick();
      }

      public String getName() {
         return "FontManager";
      }
   };

   public FontResourceManager(TextureManager p_i49772_1_) {
      this.textureManager = p_i49772_1_;
      this.missingFontSet = Util.make(new Font(p_i49772_1_, MISSING_FONT), (p_238550_0_) -> {
         p_238550_0_.reload(Lists.newArrayList(new DefaultGlyphProvider()));
      });
   }

   public void setRenames(Map<ResourceLocation, ResourceLocation> p_238551_1_) {
      this.renames = p_238551_1_;
   }

   public FontRenderer createFont() {
      return new FontRenderer((p_238552_1_) -> {
         return this.fontSets.getOrDefault(this.renames.getOrDefault(p_238552_1_, p_238552_1_), this.missingFontSet);
      });
   }

   public IFutureReloadListener getReloadListener() {
      return this.reloadListener;
   }

   public void close() {
      this.fontSets.values().forEach(Font::close);
      this.missingFontSet.close();
   }
}
