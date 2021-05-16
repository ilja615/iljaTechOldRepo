package net.minecraft.client.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanguageManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Language DEFAULT_LANGUAGE = new Language("en_us", "US", "English", false);
   private Map<String, Language> languages = ImmutableMap.of("en_us", DEFAULT_LANGUAGE);
   private String currentCode;
   private Language currentLanguage = DEFAULT_LANGUAGE;

   public LanguageManager(String p_i48112_1_) {
      this.currentCode = p_i48112_1_;
   }

   private static Map<String, Language> extractLanguages(Stream<IResourcePack> p_239506_0_) {
      Map<String, Language> map = Maps.newHashMap();
      p_239506_0_.forEach((p_239505_1_) -> {
         try {
            LanguageMetadataSection languagemetadatasection = p_239505_1_.getMetadataSection(LanguageMetadataSection.SERIALIZER);
            if (languagemetadatasection != null) {
               for(Language language : languagemetadatasection.getLanguages()) {
                  map.putIfAbsent(language.getCode(), language);
               }
            }
         } catch (IOException | RuntimeException runtimeexception) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", p_239505_1_.getName(), runtimeexception);
         }

      });
      return ImmutableMap.copyOf(map);
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.languages = extractLanguages(p_195410_1_.listPacks());
      Language language = this.languages.getOrDefault("en_us", DEFAULT_LANGUAGE);
      this.currentLanguage = this.languages.getOrDefault(this.currentCode, language);
      List<Language> list = Lists.newArrayList(language);
      if (this.currentLanguage != language) {
         list.add(this.currentLanguage);
      }

      ClientLanguageMap clientlanguagemap = ClientLanguageMap.loadFrom(p_195410_1_, list);
      I18n.setLanguage(clientlanguagemap);
      LanguageMap.inject(clientlanguagemap);
   }

   public void setSelected(Language p_135045_1_) {
      this.currentCode = p_135045_1_.getCode();
      this.currentLanguage = p_135045_1_;
   }

   public Language getSelected() {
      return this.currentLanguage;
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languages.values());
   }

   public Language getLanguage(String p_191960_1_) {
      return this.languages.get(p_191960_1_);
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.LANGUAGES;
   }
}
