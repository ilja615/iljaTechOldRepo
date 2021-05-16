package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class NibbleArray {
   @Nullable
   protected byte[] data;

   public NibbleArray() {
   }

   public NibbleArray(byte[] p_i45646_1_) {
      this.data = p_i45646_1_;
      if (p_i45646_1_.length != 2048) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + p_i45646_1_.length));
      }
   }

   protected NibbleArray(int p_i49951_1_) {
      this.data = new byte[p_i49951_1_];
   }

   public int get(int p_76582_1_, int p_76582_2_, int p_76582_3_) {
      return this.get(this.getIndex(p_76582_1_, p_76582_2_, p_76582_3_));
   }

   public void set(int p_76581_1_, int p_76581_2_, int p_76581_3_, int p_76581_4_) {
      this.set(this.getIndex(p_76581_1_, p_76581_2_, p_76581_3_), p_76581_4_);
   }

   protected int getIndex(int p_177483_1_, int p_177483_2_, int p_177483_3_) {
      return p_177483_2_ << 8 | p_177483_3_ << 4 | p_177483_1_;
   }

   private int get(int p_177480_1_) {
      if (this.data == null) {
         return 0;
      } else {
         int i = this.getPosition(p_177480_1_);
         return this.isFirst(p_177480_1_) ? this.data[i] & 15 : this.data[i] >> 4 & 15;
      }
   }

   private void set(int p_177482_1_, int p_177482_2_) {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      int i = this.getPosition(p_177482_1_);
      if (this.isFirst(p_177482_1_)) {
         this.data[i] = (byte)(this.data[i] & 240 | p_177482_2_ & 15);
      } else {
         this.data[i] = (byte)(this.data[i] & 15 | (p_177482_2_ & 15) << 4);
      }

   }

   private boolean isFirst(int p_177479_1_) {
      return (p_177479_1_ & 1) == 0;
   }

   private int getPosition(int p_177478_1_) {
      return p_177478_1_ >> 1;
   }

   public byte[] getData() {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      return this.data;
   }

   public NibbleArray copy() {
      return this.data == null ? new NibbleArray() : new NibbleArray((byte[])this.data.clone());
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < 4096; ++i) {
         stringbuilder.append(Integer.toHexString(this.get(i)));
         if ((i & 15) == 15) {
            stringbuilder.append("\n");
         }

         if ((i & 255) == 255) {
            stringbuilder.append("\n");
         }
      }

      return stringbuilder.toString();
   }

   public boolean isEmpty() {
      return this.data == null;
   }
}
