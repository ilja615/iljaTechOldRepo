package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayNBT extends CollectionNBT<IntNBT> {
   public static final INBTType<IntArrayNBT> TYPE = new INBTType<IntArrayNBT>() {
      public IntArrayNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(192L);
         int i = p_225649_1_.readInt();
         p_225649_3_.accountBits(32L * (long)i);
         int[] aint = new int[i];

         for(int j = 0; j < i; ++j) {
            aint[j] = p_225649_1_.readInt();
         }

         return new IntArrayNBT(aint);
      }

      public String getName() {
         return "INT[]";
      }

      public String getPrettyName() {
         return "TAG_Int_Array";
      }
   };
   private int[] data;

   public IntArrayNBT(int[] p_i45132_1_) {
      this.data = p_i45132_1_;
   }

   public IntArrayNBT(List<Integer> p_i47528_1_) {
      this(toArray(p_i47528_1_));
   }

   private static int[] toArray(List<Integer> p_193584_0_) {
      int[] aint = new int[p_193584_0_.size()];

      for(int i = 0; i < p_193584_0_.size(); ++i) {
         Integer integer = p_193584_0_.get(i);
         aint[i] = integer == null ? 0 : integer;
      }

      return aint;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data.length);

      for(int i : this.data) {
         p_74734_1_.writeInt(i);
      }

   }

   public byte getId() {
      return 11;
   }

   public INBTType<IntArrayNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[I;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.data[i]);
      }

      return stringbuilder.append(']').toString();
   }

   public IntArrayNBT copy() {
      int[] aint = new int[this.data.length];
      System.arraycopy(this.data, 0, aint, 0, this.data.length);
      return new IntArrayNBT(aint);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof IntArrayNBT && Arrays.equals(this.data, ((IntArrayNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public int[] getAsIntArray() {
      return this.data;
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new StringTextComponent("I")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("[")).append(itextcomponent).append(";");

      for(int i = 0; i < this.data.length; ++i) {
         iformattabletextcomponent.append(" ").append((new StringTextComponent(String.valueOf(this.data[i]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (i != this.data.length - 1) {
            iformattabletextcomponent.append(",");
         }
      }

      iformattabletextcomponent.append("]");
      return iformattabletextcomponent;
   }

   public int size() {
      return this.data.length;
   }

   public IntNBT get(int p_get_1_) {
      return IntNBT.valueOf(this.data[p_get_1_]);
   }

   public IntNBT set(int p_set_1_, IntNBT p_set_2_) {
      int i = this.data[p_set_1_];
      this.data[p_set_1_] = p_set_2_.getAsInt();
      return IntNBT.valueOf(i);
   }

   public void add(int p_add_1_, IntNBT p_add_2_) {
      this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getAsInt());
   }

   public boolean setTag(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.data[p_218659_1_] = ((NumberNBT)p_218659_2_).getAsInt();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.data = ArrayUtils.add(this.data, p_218660_1_, ((NumberNBT)p_218660_2_).getAsInt());
         return true;
      } else {
         return false;
      }
   }

   public IntNBT remove(int p_remove_1_) {
      int i = this.data[p_remove_1_];
      this.data = ArrayUtils.remove(this.data, p_remove_1_);
      return IntNBT.valueOf(i);
   }

   public byte getElementType() {
      return 3;
   }

   public void clear() {
      this.data = new int[0];
   }
}
