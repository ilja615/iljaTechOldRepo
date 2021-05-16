package net.minecraft.entity;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class EntitySize {
   public final float width;
   public final float height;
   public final boolean fixed;

   public EntitySize(float p_i50388_1_, float p_i50388_2_, boolean p_i50388_3_) {
      this.width = p_i50388_1_;
      this.height = p_i50388_2_;
      this.fixed = p_i50388_3_;
   }

   public AxisAlignedBB makeBoundingBox(Vector3d p_242286_1_) {
      return this.makeBoundingBox(p_242286_1_.x, p_242286_1_.y, p_242286_1_.z);
   }

   public AxisAlignedBB makeBoundingBox(double p_242285_1_, double p_242285_3_, double p_242285_5_) {
      float f = this.width / 2.0F;
      float f1 = this.height;
      return new AxisAlignedBB(p_242285_1_ - (double)f, p_242285_3_, p_242285_5_ - (double)f, p_242285_1_ + (double)f, p_242285_3_ + (double)f1, p_242285_5_ + (double)f);
   }

   public EntitySize scale(float p_220313_1_) {
      return this.scale(p_220313_1_, p_220313_1_);
   }

   public EntitySize scale(float p_220312_1_, float p_220312_2_) {
      return !this.fixed && (p_220312_1_ != 1.0F || p_220312_2_ != 1.0F) ? scalable(this.width * p_220312_1_, this.height * p_220312_2_) : this;
   }

   public static EntitySize scalable(float p_220314_0_, float p_220314_1_) {
      return new EntitySize(p_220314_0_, p_220314_1_, false);
   }

   public static EntitySize fixed(float p_220311_0_, float p_220311_1_) {
      return new EntitySize(p_220311_0_, p_220311_1_, true);
   }

   public String toString() {
      return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
   }
}
