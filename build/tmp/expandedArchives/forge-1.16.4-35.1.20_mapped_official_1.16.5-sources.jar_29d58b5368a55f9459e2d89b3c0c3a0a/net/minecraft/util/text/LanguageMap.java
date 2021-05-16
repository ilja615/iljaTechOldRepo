package net.minecraft.util.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LanguageMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
   private static volatile LanguageMap instance = loadDefault();

   private static LanguageMap loadDefault() {
      Builder<String, String> builder = ImmutableMap.builder();
      BiConsumer<String, String> biconsumer = builder::put;

      try (InputStream inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json")) {
         loadFromJson(inputstream, biconsumer);
      } catch (JsonParseException | IOException ioexception) {
         LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)ioexception);
      }

      final Map<String, String> map = new java.util.HashMap<>(builder.build());
      net.minecraftforge.fml.server.LanguageHook.captureLanguageMap(map);
      return new LanguageMap() {
         public String getOrDefault(String p_230503_1_) {
            return map.getOrDefault(p_230503_1_, p_230503_1_);
         }

         public boolean has(String p_230506_1_) {
            return map.containsKey(p_230506_1_);
         }

         @OnlyIn(Dist.CLIENT)
         public boolean isDefaultRightToLeft() {
            return false;
         }

         @OnlyIn(Dist.CLIENT)
         public IReorderingProcessor getVisualOrder(ITextProperties p_241870_1_) {
            return (p_244262_1_) -> {
               return p_241870_1_.visit((p_244261_1_, p_244261_2_) -> {
                  return TextProcessing.iterateFormatted(p_244261_2_, p_244261_1_, p_244262_1_) ? Optional.empty() : ITextProperties.STOP_ITERATION;
               }, Style.EMPTY).isPresent();
            };
         }
         
         @Override
         public Map<String, String> getLanguageData() {
            return map;
         }
      };
   }

   public static void loadFromJson(InputStream p_240593_0_, BiConsumer<String, String> p_240593_1_) {
      JsonObject jsonobject = GSON.fromJson(new InputStreamReader(p_240593_0_, StandardCharsets.UTF_8), JsonObject.class);

      for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
         String s = UNSUPPORTED_FORMAT_PATTERN.matcher(JSONUtils.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
         p_240593_1_.accept(entry.getKey(), s);
      }

   }

   public static LanguageMap getInstance() {
      return instance;
   }

   @OnlyIn(Dist.CLIENT)
   public static void inject(LanguageMap p_240594_0_) {
      instance = p_240594_0_;
   }
   
   // FORGE START
   public Map<String, String> getLanguageData() { return ImmutableMap.of(); }

   public abstract String getOrDefault(String p_230503_1_);

   public abstract boolean has(String p_230506_1_);

   @OnlyIn(Dist.CLIENT)
   public abstract boolean isDefaultRightToLeft();

   @OnlyIn(Dist.CLIENT)
   public abstract IReorderingProcessor getVisualOrder(ITextProperties p_241870_1_);

   @OnlyIn(Dist.CLIENT)
   public List<IReorderingProcessor> getVisualOrder(List<ITextProperties> p_244260_1_) {
      return p_244260_1_.stream().map(getInstance()::getVisualOrder).collect(ImmutableList.toImmutableList());
   }
}
