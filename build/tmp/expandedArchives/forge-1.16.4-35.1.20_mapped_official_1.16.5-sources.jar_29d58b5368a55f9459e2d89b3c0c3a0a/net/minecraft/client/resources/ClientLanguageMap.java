package net.minecraft.client.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.BidiReorderer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientLanguageMap extends LanguageMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, String> storage;
   private final boolean defaultRightToLeft;

   private ClientLanguageMap(Map<String, String> p_i232487_1_, boolean p_i232487_2_) {
      this.storage = p_i232487_1_;
      this.defaultRightToLeft = p_i232487_2_;
   }

   public static ClientLanguageMap loadFrom(IResourceManager p_239497_0_, List<Language> p_239497_1_) {
      Map<String, String> map = Maps.newHashMap();
      boolean flag = false;

      for(Language language : p_239497_1_) {
         flag |= language.isBidirectional();
         String s = String.format("lang/%s.json", language.getCode());

         for(String s1 : p_239497_0_.getNamespaces()) {
            try {
               ResourceLocation resourcelocation = new ResourceLocation(s1, s);
               appendFrom(p_239497_0_.getResources(resourcelocation), map);
            } catch (FileNotFoundException filenotfoundexception) {
            } catch (Exception exception) {
               LOGGER.warn("Skipped language file: {}:{} ({})", s1, s, exception.toString());
            }
         }
      }

      return new ClientLanguageMap(ImmutableMap.copyOf(map), flag);
   }

   private static void appendFrom(List<IResource> p_239498_0_, Map<String, String> p_239498_1_) {
      for(IResource iresource : p_239498_0_) {
         try (InputStream inputstream = iresource.getInputStream()) {
            LanguageMap.loadFromJson(inputstream, p_239498_1_::put);
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to load translations from {}", iresource, ioexception);
         }
      }

   }

   public String getOrDefault(String p_230503_1_) {
      return this.storage.getOrDefault(p_230503_1_, p_230503_1_);
   }

   public boolean has(String p_230506_1_) {
      return this.storage.containsKey(p_230506_1_);
   }

   public boolean isDefaultRightToLeft() {
      return this.defaultRightToLeft;
   }

   public IReorderingProcessor getVisualOrder(ITextProperties p_241870_1_) {
      return BidiReorderer.reorder(p_241870_1_, this.defaultRightToLeft);
   }

   @Override
   public Map<String, String> getLanguageData() {
      return storage;
   }
}
