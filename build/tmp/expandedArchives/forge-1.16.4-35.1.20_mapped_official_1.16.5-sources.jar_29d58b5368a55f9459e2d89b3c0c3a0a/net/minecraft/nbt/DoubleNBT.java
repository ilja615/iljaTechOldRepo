package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DoubleNBT extends NumberNBT {
   public static final DoubleNBT ZERO = new DoubleNBT(0.0D);
   public static final INBTType<DoubleNBT> TYPE = new INBTType<DoubleNBT>() {
      public DoubleNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(128L);
         return DoubleNBT.valueOf(p_225649_1_.readDouble());
      }

      public String getName() {
         return "DOUBLE";
      }

      public String getPrettyName() {
         return "TAG_Double";
      }

      public boolean isValue() {
         return true;
      }
   };
   private final double data;

   private DoubleNBT(double p_i45130_1_) {
      this.data = p_i45130_1_;
   }

   public static DoubleNBT valueOf(double p_229684_0_) {
      return p_229684_0_ == 0.0D ? ZERO : new DoubleNBT(p_229684_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeDouble(this.data);
   }

   public byte getId() {
      return 6;
   }

   public INBTType<DoubleNBT> getType() {
      return TYPE;
   }

   public String toString() {
      return this.data + "d";
   }

   public DoubleNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof DoubleNBT && this.data == ((DoubleNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.data);
      return (int)(i ^ i >>> 32);
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new StringTextComponent("d")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).append(itextcomponent).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)Math.floor(this.data);
   }

   public int getAsInt() {
      return MathHelper.floor(this.data);
   }

   public short getAsShort() {
      return (short)(MathHelper.floor(this.data) & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(MathHelper.floor(this.data) & 255);
   }

   public double getAsDouble() {
      return this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }
}
