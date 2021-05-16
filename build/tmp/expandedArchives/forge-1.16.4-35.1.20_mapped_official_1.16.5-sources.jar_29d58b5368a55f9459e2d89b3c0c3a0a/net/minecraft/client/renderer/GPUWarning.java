package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.PlatformDescriptors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GPUWarning extends ReloadListener<GPUWarning.GPUInfo> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation GPU_WARNLIST_LOCATION = new ResourceLocation("gpu_warnlist.json");
   private ImmutableMap<String, String> warnings = ImmutableMap.of();
   private boolean showWarning;
   private boolean warningDismissed;
   private boolean skipFabulous;

   public boolean hasWarnings() {
      return !this.warnings.isEmpty();
   }

   public boolean willShowWarning() {
      return this.hasWarnings() && !this.warningDismissed;
   }

   public void showWarning() {
      this.showWarning = true;
   }

   public void dismissWarning() {
      this.warningDismissed = true;
   }

   public void dismissWarningAndSkipFabulous() {
      this.warningDismissed = true;
      this.skipFabulous = true;
   }

   public boolean isShowingWarning() {
      return this.showWarning && !this.warningDismissed;
   }

   public boolean isSkippingFabulous() {
      return this.skipFabulous;
   }

   public void resetWarnings() {
      this.showWarning = false;
      this.warningDismissed = false;
      this.skipFabulous = false;
   }

   @Nullable
   public String getRendererWarnings() {
      return this.warnings.get("renderer");
   }

   @Nullable
   public String getVersionWarnings() {
      return this.warnings.get("version");
   }

   @Nullable
   public String getVendorWarnings() {
      return this.warnings.get("vendor");
   }

   @Nullable
   public String getAllWarnings() {
      StringBuilder stringbuilder = new StringBuilder();
      this.warnings.forEach((p_243498_1_, p_243498_2_) -> {
         stringbuilder.append(p_243498_1_).append(": ").append(p_243498_2_);
      });
      return stringbuilder.length() == 0 ? null : stringbuilder.toString();
   }

   protected GPUWarning.GPUInfo prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      List<Pattern> list = Lists.newArrayList();
      List<Pattern> list1 = Lists.newArrayList();
      List<Pattern> list2 = Lists.newArrayList();
      p_212854_2_.startTick();
      JsonObject jsonobject = parseJson(p_212854_1_, p_212854_2_);
      if (jsonobject != null) {
         p_212854_2_.push("compile_regex");
         compilePatterns(jsonobject.getAsJsonArray("renderer"), list);
         compilePatterns(jsonobject.getAsJsonArray("version"), list1);
         compilePatterns(jsonobject.getAsJsonArray("vendor"), list2);
         p_212854_2_.pop();
      }

      p_212854_2_.endTick();
      return new GPUWarning.GPUInfo(list, list1, list2);
   }

   protected void apply(GPUWarning.GPUInfo p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      this.warnings = p_212853_1_.apply();
   }

   private static void compilePatterns(JsonArray p_241693_0_, List<Pattern> p_241693_1_) {
      p_241693_0_.forEach((p_241694_1_) -> {
         p_241693_1_.add(Pattern.compile(p_241694_1_.getAsString(), 2));
      });
   }

   @Nullable
   private static JsonObject parseJson(IResourceManager p_241696_0_, IProfiler p_241696_1_) {
      p_241696_1_.push("parse_json");
      JsonObject jsonobject = null;

      try (
         IResource iresource = p_241696_0_.getResource(GPU_WARNLIST_LOCATION);
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
      ) {
         jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
      } catch (JsonSyntaxException | IOException ioexception) {
         LOGGER.warn("Failed to load GPU warnlist");
      }

      p_241696_1_.pop();
      return jsonobject;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class GPUInfo {
      private final List<Pattern> rendererPatterns;
      private final List<Pattern> versionPatterns;
      private final List<Pattern> vendorPatterns;

      private GPUInfo(List<Pattern> p_i241261_1_, List<Pattern> p_i241261_2_, List<Pattern> p_i241261_3_) {
         this.rendererPatterns = p_i241261_1_;
         this.versionPatterns = p_i241261_2_;
         this.vendorPatterns = p_i241261_3_;
      }

      private static String matchAny(List<Pattern> p_241711_0_, String p_241711_1_) {
         List<String> list = Lists.newArrayList();

         for(Pattern pattern : p_241711_0_) {
            Matcher matcher = pattern.matcher(p_241711_1_);

            while(matcher.find()) {
               list.add(matcher.group());
            }
         }

         return String.join(", ", list);
      }

      private ImmutableMap<String, String> apply() {
         Builder<String, String> builder = new Builder<>();
         String s = matchAny(this.rendererPatterns, PlatformDescriptors.getRenderer());
         if (!s.isEmpty()) {
            builder.put("renderer", s);
         }

         String s1 = matchAny(this.versionPatterns, PlatformDescriptors.getOpenGLVersion());
         if (!s1.isEmpty()) {
            builder.put("version", s1);
         }

         String s2 = matchAny(this.vendorPatterns, PlatformDescriptors.getVendor());
         if (!s2.isEmpty()) {
            builder.put("vendor", s2);
         }

         return builder.build();
      }
   }
}
