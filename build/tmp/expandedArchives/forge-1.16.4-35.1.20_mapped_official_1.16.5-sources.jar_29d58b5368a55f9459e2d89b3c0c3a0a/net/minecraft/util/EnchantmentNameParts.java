package net.minecraft.util;

import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentNameParts {
   private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
   private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
   private static final EnchantmentNameParts INSTANCE = new EnchantmentNameParts();
   private final Random random = new Random();
   private final String[] words = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

   private EnchantmentNameParts() {
   }

   public static EnchantmentNameParts getInstance() {
      return INSTANCE;
   }

   public ITextProperties getRandomName(FontRenderer p_238816_1_, int p_238816_2_) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = this.random.nextInt(2) + 3;

      for(int j = 0; j < i; ++j) {
         if (j != 0) {
            stringbuilder.append(" ");
         }

         stringbuilder.append(Util.getRandom(this.words, this.random));
      }

      return p_238816_1_.getSplitter().headByWidth((new StringTextComponent(stringbuilder.toString())).withStyle(ROOT_STYLE), p_238816_2_, Style.EMPTY);
   }

   public void initSeed(long p_148335_1_) {
      this.random.setSeed(p_148335_1_);
   }
}
