package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum TextFormatting {
   BLACK("BLACK", '0', 0, 0),
   DARK_BLUE("DARK_BLUE", '1', 1, 170),
   DARK_GREEN("DARK_GREEN", '2', 2, 43520),
   DARK_AQUA("DARK_AQUA", '3', 3, 43690),
   DARK_RED("DARK_RED", '4', 4, 11141120),
   DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
   GOLD("GOLD", '6', 6, 16755200),
   GRAY("GRAY", '7', 7, 11184810),
   DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
   BLUE("BLUE", '9', 9, 5592575),
   GREEN("GREEN", 'a', 10, 5635925),
   AQUA("AQUA", 'b', 11, 5636095),
   RED("RED", 'c', 12, 16733525),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
   YELLOW("YELLOW", 'e', 14, 16777045),
   WHITE("WHITE", 'f', 15, 16777215),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   private static final Map<String, TextFormatting> FORMATTING_BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((p_199746_0_) -> {
      return cleanName(p_199746_0_.name);
   }, (p_199747_0_) -> {
      return p_199747_0_;
   }));
   private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   private final String name;
   private final char code;
   private final boolean isFormat;
   private final String toString;
   private final int id;
   @Nullable
   private final Integer color;

   private static String cleanName(String p_175745_0_) {
      return p_175745_0_.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private TextFormatting(String p_i49745_3_, char p_i49745_4_, int p_i49745_5_, @Nullable Integer p_i49745_6_) {
      this(p_i49745_3_, p_i49745_4_, false, p_i49745_5_, p_i49745_6_);
   }

   private TextFormatting(String p_i46292_3_, char p_i46292_4_, boolean p_i46292_5_) {
      this(p_i46292_3_, p_i46292_4_, p_i46292_5_, -1, (Integer)null);
   }

   private TextFormatting(String p_i49746_3_, char p_i49746_4_, boolean p_i49746_5_, int p_i49746_6_, @Nullable Integer p_i49746_7_) {
      this.name = p_i49746_3_;
      this.code = p_i49746_4_;
      this.isFormat = p_i49746_5_;
      this.id = p_i49746_6_;
      this.color = p_i49746_7_;
      this.toString = "\u00a7" + p_i49746_4_;
   }

   public int getId() {
      return this.id;
   }

   public boolean isFormat() {
      return this.isFormat;
   }

   public boolean isColor() {
      return !this.isFormat && this != RESET;
   }

   @Nullable
   public Integer getColor() {
      return this.color;
   }

   public String getName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.toString;
   }

   @Nullable
   public static String stripFormatting(@Nullable String p_110646_0_) {
      return p_110646_0_ == null ? null : STRIP_FORMATTING_PATTERN.matcher(p_110646_0_).replaceAll("");
   }

   @Nullable
   public static TextFormatting getByName(@Nullable String p_96300_0_) {
      return p_96300_0_ == null ? null : FORMATTING_BY_NAME.get(cleanName(p_96300_0_));
   }

   @Nullable
   public static TextFormatting getById(int p_175744_0_) {
      if (p_175744_0_ < 0) {
         return RESET;
      } else {
         for(TextFormatting textformatting : values()) {
            if (textformatting.getId() == p_175744_0_) {
               return textformatting;
            }
         }

         return null;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static TextFormatting getByCode(char p_211165_0_) {
      char c0 = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);

      for(TextFormatting textformatting : values()) {
         if (textformatting.code == c0) {
            return textformatting;
         }
      }

      return null;
   }

   public static Collection<String> getNames(boolean p_96296_0_, boolean p_96296_1_) {
      List<String> list = Lists.newArrayList();

      for(TextFormatting textformatting : values()) {
         if ((!textformatting.isColor() || p_96296_0_) && (!textformatting.isFormat() || p_96296_1_)) {
            list.add(textformatting.getName());
         }
      }

      return list;
   }
}
