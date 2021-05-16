package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayNBT extends CollectionNBT<LongNBT> {
   public static final INBTType<LongArrayNBT> TYPE = new INBTType<LongArrayNBT>() {
      public LongArrayNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(192L);
         int i = p_225649_1_.readInt();
         p_225649_3_.accountBits(64L * (long)i);
         long[] along = new long[i];

         for(int j = 0; j < i; ++j) {
            along[j] = p_225649_1_.readLong();
         }

         return new LongArrayNBT(along);
      }

      public String getName() {
         return "LONG[]";
      }

      public String getPrettyName() {
         return "TAG_Long_Array";
      }
   };
   private long[] data;

   public LongArrayNBT(long[] p_i47524_1_) {
      this.data = p_i47524_1_;
   }

   public LongArrayNBT(LongSet p_i48736_1_) {
      this.data = p_i48736_1_.toLongArray();
   }

   public LongArrayNBT(List<Long> p_i47525_1_) {
      this(toArray(p_i47525_1_));
   }

   private static long[] toArray(List<Long> p_193586_0_) {
      long[] along = new long[p_193586_0_.size()];

      for(int i = 0; i < p_193586_0_.size(); ++i) {
         Long olong = p_193586_0_.get(i);
         along[i] = olong == null ? 0L : olong;
      }

      return along;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data.length);

      for(long i : this.data) {
         p_74734_1_.writeLong(i);
      }

   }

   public byte getId() {
      return 12;
   }

   public INBTType<LongArrayNBT> getType() {
      return TYPE;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[L;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.data[i]).append('L');
      }

      return stringbuilder.append(']').toString();
   }

   public LongArrayNBT copy() {
      long[] along = new long[this.data.length];
      System.arraycopy(this.data, 0, along, 0, this.data.length);
      return new LongArrayNBT(along);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof LongArrayNBT && Arrays.equals(this.data, ((LongArrayNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new StringTextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("[")).append(itextcomponent).append(";");

      for(int i = 0; i < this.data.length; ++i) {
         IFormattableTextComponent iformattabletextcomponent1 = (new StringTextComponent(String.valueOf(this.data[i]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         iformattabletextcomponent.append(" ").append(iformattabletextcomponent1).append(itextcomponent);
         if (i != this.data.length - 1) {
            iformattabletextcomponent.append(",");
         }
      }

      iformattabletextcomponent.append("]");
      return iformattabletextcomponent;
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongNBT get(int p_get_1_) {
      return LongNBT.valueOf(this.data[p_get_1_]);
   }

   public LongNBT set(int p_set_1_, LongNBT p_set_2_) {
      long i = this.data[p_set_1_];
      this.data[p_set_1_] = p_set_2_.getAsLong();
      return LongNBT.valueOf(i);
   }

   public void add(int p_add_1_, LongNBT p_add_2_) {
      this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getAsLong());
   }

   public boolean setTag(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.data[p_218659_1_] = ((NumberNBT)p_218659_2_).getAsLong();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.data = ArrayUtils.add(this.data, p_218660_1_, ((NumberNBT)p_218660_2_).getAsLong());
         return true;
      } else {
         return false;
      }
   }

   public LongNBT remove(int p_remove_1_) {
      long i = this.data[p_remove_1_];
      this.data = ArrayUtils.remove(this.data, p_remove_1_);
      return LongNBT.valueOf(i);
   }

   public byte getElementType() {
      return 4;
   }

   public void clear() {
      this.data = new long[0];
   }
}
