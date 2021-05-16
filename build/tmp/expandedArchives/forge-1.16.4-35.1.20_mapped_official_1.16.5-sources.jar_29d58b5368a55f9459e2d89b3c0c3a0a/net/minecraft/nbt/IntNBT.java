package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IntNBT extends NumberNBT {
   public static final INBTType<IntNBT> TYPE = new INBTType<IntNBT>() {
      public IntNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(96L);
         return IntNBT.valueOf(p_225649_1_.readInt());
      }

      public String getName() {
         return "INT";
      }

      public String getPrettyName() {
         return "TAG_Int";
      }

      public boolean isValue() {
         return true;
      }
   };
   private final int data;

   private IntNBT(int p_i45133_1_) {
      this.data = p_i45133_1_;
   }

   public static IntNBT valueOf(int p_229692_0_) {
      return p_229692_0_ >= -128 && p_229692_0_ <= 1024 ? IntNBT.Cache.cache[p_229692_0_ + 128] : new IntNBT(p_229692_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data);
   }

   public byte getId() {
      return 3;
   }

   public INBTType<IntNBT> getType() {
      return TYPE;
   }

   public String toString() {
      return String.valueOf(this.data);
   }

   public IntNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof IntNBT && this.data == ((IntNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      return (new StringTextComponent(String.valueOf(this.data))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)(this.data & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(this.data & 255);
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
      static final IntNBT[] cache = new IntNBT[1153];

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new IntNBT(-128 + i);
         }

      }
   }
}
