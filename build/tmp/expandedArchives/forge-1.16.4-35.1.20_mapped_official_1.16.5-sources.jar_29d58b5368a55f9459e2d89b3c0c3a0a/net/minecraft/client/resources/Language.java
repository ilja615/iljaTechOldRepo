package net.minecraft.client.resources;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Language implements com.mojang.bridge.game.Language, Comparable<Language> {
   private final String code;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public Language(String p_i1303_1_, String p_i1303_2_, String p_i1303_3_, boolean p_i1303_4_) {
      this.code = p_i1303_1_;
      this.region = p_i1303_2_;
      this.name = p_i1303_3_;
      this.bidirectional = p_i1303_4_;
      String[] splitLangCode = code.split("_", 2);
      if (splitLangCode.length == 1) { // Vanilla has some languages without underscores
         this.javaLocale = new java.util.Locale(code);
      } else {
         this.javaLocale = new java.util.Locale(splitLangCode[0], splitLangCode[1]);
      }
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public String getRegion() {
      return this.region;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return !(p_equals_1_ instanceof Language) ? false : this.code.equals(((Language)p_equals_1_).code);
      }
   }

   public int hashCode() {
      return this.code.hashCode();
   }

   public int compareTo(Language p_compareTo_1_) {
      return this.code.compareTo(p_compareTo_1_.code);
   }

   // Forge: add access to Locale so modders can create correct string and number formatters
   private final java.util.Locale javaLocale;
   public java.util.Locale getJavaLocale() { return javaLocale; }
}
