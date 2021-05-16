package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class LongNBT extends NumberNBT {
   public static final INBTType<LongNBT> TYPE = new INBTType<LongNBT>() {
      public LongNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(128L);
         return LongNBT.valueOf(p_225649_1_.readLong());
      }

      public String getName() {
         return "LONG";
      }

      public String getPrettyName() {
         return "TAG_Long";
      }

      public boolean isValue() {
         return true;
      }
   };
   private final long data;

   private LongNBT(long p_i45134_1_) {
      this.data = p_i45134_1_;
   }

   public static LongNBT valueOf(long p_229698_0_) {
      return p_229698_0_ >= -128L && p_229698_0_ <= 1024L ? LongNBT.Cache.cache[(int)p_229698_0_ + 128] : new LongNBT(p_229698_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeLong(this.data);
   }

   public byte getId() {
      return 4;
   }

   public INBTType<LongNBT> getType() {
      return TYPE;
   }

   public String toString() {
      return this.data + "L";
   }

   public LongNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof LongNBT && this.data == ((LongNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new StringTextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).append(itextcomponent).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return this.data;
   }

   public int getAsInt() {
      return (int)(this.data & -1L);
   }

   public short getAsShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getAsByte() {
      return (byte)((int)(this.data & 255L));
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   static class Cache {
      static final LongNBT[] cache = new LongNBT[1153];

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new LongNBT((long)(-128 + i));
         }

      }
   }
}
