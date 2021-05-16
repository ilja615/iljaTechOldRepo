package net.minecraft.client.resources;

import java.util.IllegalFormatException;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class I18n {
   private static volatile LanguageMap language = LanguageMap.getInstance();

   static void setLanguage(LanguageMap p_239502_0_) {
      language = p_239502_0_;
      net.minecraftforge.fml.ForgeI18n.loadLanguageData(p_239502_0_.getLanguageData());
   }

   public static String get(String p_135052_0_, Object... p_135052_1_) {
      String s = language.getOrDefault(p_135052_0_);

      try {
         return String.format(s, p_135052_1_);
      } catch (IllegalFormatException illegalformatexception) {
         return "Format error: " + s;
      }
   }

   public static boolean exists(String p_188566_0_) {
      return language.has(p_188566_0_);
   }
}
