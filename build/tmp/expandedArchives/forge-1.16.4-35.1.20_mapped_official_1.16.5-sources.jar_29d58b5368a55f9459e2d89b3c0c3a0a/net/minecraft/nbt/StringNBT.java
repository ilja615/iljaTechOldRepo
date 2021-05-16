package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class StringNBT implements INBT {
   public static final INBTType<StringNBT> TYPE = new INBTType<StringNBT>() {
      public StringNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(288L);
         String s = p_225649_1_.readUTF();
         p_225649_3_.readUTF(s);
         return StringNBT.valueOf(s);
      }

      public String getName() {
         return "STRING";
      }

      public String getPrettyName() {
         return "TAG_String";
      }

      public boolean isValue() {
         return true;
      }
   };
   private static final StringNBT EMPTY = new StringNBT("");
   private final String data;

   private StringNBT(String p_i1389_1_) {
      Objects.requireNonNull(p_i1389_1_, "Null string not allowed");
      this.data = p_i1389_1_;
   }

   public static StringNBT valueOf(String p_229705_0_) {
      return p_229705_0_.isEmpty() ? EMPTY : new StringNBT(p_229705_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeUTF(this.data);
   }

   public byte getId() {
      return 8;
   }

   public INBTType<StringNBT> getType() {
      return TYPE;
   }

   public String toString() {
      return quoteAndEscape(this.data);
   }

   public StringNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof StringNBT && Objects.equals(this.data, ((StringNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getAsString() {
      return this.data;
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      String s = quoteAndEscape(this.data);
      String s1 = s.substring(0, 1);
      ITextComponent itextcomponent = (new StringTextComponent(s.substring(1, s.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      return (new StringTextComponent(s1)).append(itextcomponent).append(s1);
   }

   public static String quoteAndEscape(String p_197654_0_) {
      StringBuilder stringbuilder = new StringBuilder(" ");
      char c0 = 0;

      for(int i = 0; i < p_197654_0_.length(); ++i) {
         char c1 = p_197654_0_.charAt(i);
         if (c1 == '\\') {
            stringbuilder.append('\\');
         } else if (c1 == '"' || c1 == '\'') {
            if (c0 == 0) {
               c0 = (char)(c1 == '"' ? 39 : 34);
            }

            if (c0 == c1) {
               stringbuilder.append('\\');
            }
         }

         stringbuilder.append(c1);
      }

      if (c0 == 0) {
         c0 = '"';
      }

      stringbuilder.setCharAt(0, c0);
      stringbuilder.append(c0);
      return stringbuilder.toString();
   }
}
