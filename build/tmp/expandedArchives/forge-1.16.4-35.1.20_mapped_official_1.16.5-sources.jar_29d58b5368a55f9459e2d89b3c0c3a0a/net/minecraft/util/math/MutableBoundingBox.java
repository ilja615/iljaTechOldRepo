package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

public class MutableBoundingBox {
   public int x0;
   public int y0;
   public int z0;
   public int x1;
   public int y1;
   public int z1;

   public MutableBoundingBox() {
   }

   public MutableBoundingBox(int[] p_i43000_1_) {
      if (p_i43000_1_.length == 6) {
         this.x0 = p_i43000_1_[0];
         this.y0 = p_i43000_1_[1];
         this.z0 = p_i43000_1_[2];
         this.x1 = p_i43000_1_[3];
         this.y1 = p_i43000_1_[4];
         this.z1 = p_i43000_1_[5];
      }

   }

   public static MutableBoundingBox getUnknownBox() {
      return new MutableBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public static MutableBoundingBox infinite() {
      return new MutableBoundingBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public static MutableBoundingBox orientBox(int p_175897_0_, int p_175897_1_, int p_175897_2_, int p_175897_3_, int p_175897_4_, int p_175897_5_, int p_175897_6_, int p_175897_7_, int p_175897_8_, Direction p_175897_9_) {
      switch(p_175897_9_) {
      case NORTH:
         return new MutableBoundingBox(p_175897_0_ + p_175897_3_, p_175897_1_ + p_175897_4_, p_175897_2_ - p_175897_8_ + 1 + p_175897_5_, p_175897_0_ + p_175897_6_ - 1 + p_175897_3_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_5_);
      case SOUTH:
         return new MutableBoundingBox(p_175897_0_ + p_175897_3_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_5_, p_175897_0_ + p_175897_6_ - 1 + p_175897_3_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_8_ - 1 + p_175897_5_);
      case WEST:
         return new MutableBoundingBox(p_175897_0_ - p_175897_8_ + 1 + p_175897_5_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_3_, p_175897_0_ + p_175897_5_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_6_ - 1 + p_175897_3_);
      case EAST:
         return new MutableBoundingBox(p_175897_0_ + p_175897_5_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_3_, p_175897_0_ + p_175897_8_ - 1 + p_175897_5_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_6_ - 1 + p_175897_3_);
      default:
         return new MutableBoundingBox(p_175897_0_ + p_175897_3_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_5_, p_175897_0_ + p_175897_6_ - 1 + p_175897_3_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_8_ - 1 + p_175897_5_);
      }
   }

   public static MutableBoundingBox createProper(int p_175899_0_, int p_175899_1_, int p_175899_2_, int p_175899_3_, int p_175899_4_, int p_175899_5_) {
      return new MutableBoundingBox(Math.min(p_175899_0_, p_175899_3_), Math.min(p_175899_1_, p_175899_4_), Math.min(p_175899_2_, p_175899_5_), Math.max(p_175899_0_, p_175899_3_), Math.max(p_175899_1_, p_175899_4_), Math.max(p_175899_2_, p_175899_5_));
   }

   public MutableBoundingBox(MutableBoundingBox p_i2031_1_) {
      this.x0 = p_i2031_1_.x0;
      this.y0 = p_i2031_1_.y0;
      this.z0 = p_i2031_1_.z0;
      this.x1 = p_i2031_1_.x1;
      this.y1 = p_i2031_1_.y1;
      this.z1 = p_i2031_1_.z1;
   }

   public MutableBoundingBox(int p_i2032_1_, int p_i2032_2_, int p_i2032_3_, int p_i2032_4_, int p_i2032_5_, int p_i2032_6_) {
      this.x0 = p_i2032_1_;
      this.y0 = p_i2032_2_;
      this.z0 = p_i2032_3_;
      this.x1 = p_i2032_4_;
      this.y1 = p_i2032_5_;
      this.z1 = p_i2032_6_;
   }

   public MutableBoundingBox(Vector3i p_i45626_1_, Vector3i p_i45626_2_) {
      this.x0 = Math.min(p_i45626_1_.getX(), p_i45626_2_.getX());
      this.y0 = Math.min(p_i45626_1_.getY(), p_i45626_2_.getY());
      this.z0 = Math.min(p_i45626_1_.getZ(), p_i45626_2_.getZ());
      this.x1 = Math.max(p_i45626_1_.getX(), p_i45626_2_.getX());
      this.y1 = Math.max(p_i45626_1_.getY(), p_i45626_2_.getY());
      this.z1 = Math.max(p_i45626_1_.getZ(), p_i45626_2_.getZ());
   }

   public MutableBoundingBox(int p_i2033_1_, int p_i2033_2_, int p_i2033_3_, int p_i2033_4_) {
      this.x0 = p_i2033_1_;
      this.z0 = p_i2033_2_;
      this.x1 = p_i2033_3_;
      this.z1 = p_i2033_4_;
      this.y0 = 1;
      this.y1 = 512;
   }

   public boolean intersects(MutableBoundingBox p_78884_1_) {
      return this.x1 >= p_78884_1_.x0 && this.x0 <= p_78884_1_.x1 && this.z1 >= p_78884_1_.z0 && this.z0 <= p_78884_1_.z1 && this.y1 >= p_78884_1_.y0 && this.y0 <= p_78884_1_.y1;
   }

   public boolean intersects(int p_78885_1_, int p_78885_2_, int p_78885_3_, int p_78885_4_) {
      return this.x1 >= p_78885_1_ && this.x0 <= p_78885_3_ && this.z1 >= p_78885_2_ && this.z0 <= p_78885_4_;
   }

   public void expand(MutableBoundingBox p_78888_1_) {
      this.x0 = Math.min(this.x0, p_78888_1_.x0);
      this.y0 = Math.min(this.y0, p_78888_1_.y0);
      this.z0 = Math.min(this.z0, p_78888_1_.z0);
      this.x1 = Math.max(this.x1, p_78888_1_.x1);
      this.y1 = Math.max(this.y1, p_78888_1_.y1);
      this.z1 = Math.max(this.z1, p_78888_1_.z1);
   }

   public void move(int p_78886_1_, int p_78886_2_, int p_78886_3_) {
      this.x0 += p_78886_1_;
      this.y0 += p_78886_2_;
      this.z0 += p_78886_3_;
      this.x1 += p_78886_1_;
      this.y1 += p_78886_2_;
      this.z1 += p_78886_3_;
   }

   public MutableBoundingBox moved(int p_215127_1_, int p_215127_2_, int p_215127_3_) {
      return new MutableBoundingBox(this.x0 + p_215127_1_, this.y0 + p_215127_2_, this.z0 + p_215127_3_, this.x1 + p_215127_1_, this.y1 + p_215127_2_, this.z1 + p_215127_3_);
   }

   public void move(Vector3i p_236989_1_) {
      this.move(p_236989_1_.getX(), p_236989_1_.getY(), p_236989_1_.getZ());
   }

   public boolean isInside(Vector3i p_175898_1_) {
      return p_175898_1_.getX() >= this.x0 && p_175898_1_.getX() <= this.x1 && p_175898_1_.getZ() >= this.z0 && p_175898_1_.getZ() <= this.z1 && p_175898_1_.getY() >= this.y0 && p_175898_1_.getY() <= this.y1;
   }

   public Vector3i getLength() {
      return new Vector3i(this.x1 - this.x0, this.y1 - this.y0, this.z1 - this.z0);
   }

   public int getXSpan() {
      return this.x1 - this.x0 + 1;
   }

   public int getYSpan() {
      return this.y1 - this.y0 + 1;
   }

   public int getZSpan() {
      return this.z1 - this.z0 + 1;
   }

   public Vector3i getCenter() {
      return new BlockPos(this.x0 + (this.x1 - this.x0 + 1) / 2, this.y0 + (this.y1 - this.y0 + 1) / 2, this.z0 + (this.z1 - this.z0 + 1) / 2);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x0", this.x0).add("y0", this.y0).add("z0", this.z0).add("x1", this.x1).add("y1", this.y1).add("z1", this.z1).toString();
   }

   public IntArrayNBT createTag() {
      return new IntArrayNBT(new int[]{this.x0, this.y0, this.z0, this.x1, this.y1, this.z1});
   }
}
