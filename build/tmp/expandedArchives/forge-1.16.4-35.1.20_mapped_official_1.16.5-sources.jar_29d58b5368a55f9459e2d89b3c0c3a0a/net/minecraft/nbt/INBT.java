package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public interface INBT {
   TextFormatting SYNTAX_HIGHLIGHTING_KEY = TextFormatting.AQUA;
   TextFormatting SYNTAX_HIGHLIGHTING_STRING = TextFormatting.GREEN;
   TextFormatting SYNTAX_HIGHLIGHTING_NUMBER = TextFormatting.GOLD;
   TextFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE = TextFormatting.RED;

   void write(DataOutput p_74734_1_) throws IOException;

   String toString();

   byte getId();

   INBTType<?> getType();

   INBT copy();

   default String getAsString() {
      return this.toString();
   }

   default ITextComponent getPrettyDisplay() {
      return this.getPrettyDisplay("", 0);
   }

   ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_);
}
