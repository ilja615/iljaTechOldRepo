package com.mojang.blaze3d.matrix;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MatrixStack {
   private final Deque<MatrixStack.Entry> poseStack = Util.make(Queues.newArrayDeque(), (p_227864_0_) -> {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      Matrix3f matrix3f = new Matrix3f();
      matrix3f.setIdentity();
      p_227864_0_.add(new MatrixStack.Entry(matrix4f, matrix3f));
   });

   public void translate(double p_227861_1_, double p_227861_3_, double p_227861_5_) {
      MatrixStack.Entry matrixstack$entry = this.poseStack.getLast();
      matrixstack$entry.pose.multiply(Matrix4f.createTranslateMatrix((float)p_227861_1_, (float)p_227861_3_, (float)p_227861_5_));
   }

   public void scale(float p_227862_1_, float p_227862_2_, float p_227862_3_) {
      MatrixStack.Entry matrixstack$entry = this.poseStack.getLast();
      matrixstack$entry.pose.multiply(Matrix4f.createScaleMatrix(p_227862_1_, p_227862_2_, p_227862_3_));
      if (p_227862_1_ == p_227862_2_ && p_227862_2_ == p_227862_3_) {
         if (p_227862_1_ > 0.0F) {
            return;
         }

         matrixstack$entry.normal.mul(-1.0F);
      }

      float f = 1.0F / p_227862_1_;
      float f1 = 1.0F / p_227862_2_;
      float f2 = 1.0F / p_227862_3_;
      float f3 = MathHelper.fastInvCubeRoot(f * f1 * f2);
      matrixstack$entry.normal.mul(Matrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
   }

   public void mulPose(Quaternion p_227863_1_) {
      MatrixStack.Entry matrixstack$entry = this.poseStack.getLast();
      matrixstack$entry.pose.multiply(p_227863_1_);
      matrixstack$entry.normal.mul(p_227863_1_);
   }

   public void pushPose() {
      MatrixStack.Entry matrixstack$entry = this.poseStack.getLast();
      this.poseStack.addLast(new MatrixStack.Entry(matrixstack$entry.pose.copy(), matrixstack$entry.normal.copy()));
   }

   public void popPose() {
      this.poseStack.removeLast();
   }

   public MatrixStack.Entry last() {
      return this.poseStack.getLast();
   }

   public boolean clear() {
      return this.poseStack.size() == 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Entry {
      private final Matrix4f pose;
      private final Matrix3f normal;

      private Entry(Matrix4f p_i225909_1_, Matrix3f p_i225909_2_) {
         this.pose = p_i225909_1_;
         this.normal = p_i225909_2_;
      }

      public Matrix4f pose() {
         return this.pose;
      }

      public Matrix3f normal() {
         return this.normal;
      }
   }
}
