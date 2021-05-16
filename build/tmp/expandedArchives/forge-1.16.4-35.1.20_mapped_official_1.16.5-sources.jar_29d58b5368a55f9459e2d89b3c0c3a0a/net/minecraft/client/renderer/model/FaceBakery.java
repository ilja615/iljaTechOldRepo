package net.minecraft.client.renderer.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FaceBakery {
   private static final float RESCALE_22_5 = 1.0F / (float)Math.cos((double)((float)Math.PI / 8F)) - 1.0F;
   private static final float RESCALE_45 = 1.0F / (float)Math.cos((double)((float)Math.PI / 4F)) - 1.0F;

   public BakedQuad bakeQuad(Vector3f p_228824_1_, Vector3f p_228824_2_, BlockPartFace p_228824_3_, TextureAtlasSprite p_228824_4_, Direction p_228824_5_, IModelTransform p_228824_6_, @Nullable BlockPartRotation p_228824_7_, boolean p_228824_8_, ResourceLocation p_228824_9_) {
      BlockFaceUV blockfaceuv = p_228824_3_.uv;
      if (p_228824_6_.isUvLocked()) {
         blockfaceuv = recomputeUVs(p_228824_3_.uv, p_228824_5_, p_228824_6_.getRotation(), p_228824_9_);
      }

      float[] afloat = new float[blockfaceuv.uvs.length];
      System.arraycopy(blockfaceuv.uvs, 0, afloat, 0, afloat.length);
      float f = p_228824_4_.uvShrinkRatio();
      float f1 = (blockfaceuv.uvs[0] + blockfaceuv.uvs[0] + blockfaceuv.uvs[2] + blockfaceuv.uvs[2]) / 4.0F;
      float f2 = (blockfaceuv.uvs[1] + blockfaceuv.uvs[1] + blockfaceuv.uvs[3] + blockfaceuv.uvs[3]) / 4.0F;
      blockfaceuv.uvs[0] = MathHelper.lerp(f, blockfaceuv.uvs[0], f1);
      blockfaceuv.uvs[2] = MathHelper.lerp(f, blockfaceuv.uvs[2], f1);
      blockfaceuv.uvs[1] = MathHelper.lerp(f, blockfaceuv.uvs[1], f2);
      blockfaceuv.uvs[3] = MathHelper.lerp(f, blockfaceuv.uvs[3], f2);
      int[] aint = this.makeVertices(blockfaceuv, p_228824_4_, p_228824_5_, this.setupShape(p_228824_1_, p_228824_2_), p_228824_6_.getRotation(), p_228824_7_, p_228824_8_);
      Direction direction = calculateFacing(aint);
      System.arraycopy(afloat, 0, blockfaceuv.uvs, 0, afloat.length);
      if (p_228824_7_ == null) {
         this.recalculateWinding(aint, direction);
      }

      net.minecraftforge.client.ForgeHooksClient.fillNormal(aint, direction);
      return new BakedQuad(aint, p_228824_3_.tintIndex, direction, p_228824_4_, p_228824_8_);
   }

   public static BlockFaceUV recomputeUVs(BlockFaceUV p_228821_0_, Direction p_228821_1_, TransformationMatrix p_228821_2_, ResourceLocation p_228821_3_) {
      Matrix4f matrix4f = UVTransformationUtil.getUVLockTransform(p_228821_2_, p_228821_1_, () -> {
         return "Unable to resolve UVLock for model: " + p_228821_3_;
      }).getMatrix();
      float f = p_228821_0_.getU(p_228821_0_.getReverseIndex(0));
      float f1 = p_228821_0_.getV(p_228821_0_.getReverseIndex(0));
      Vector4f vector4f = new Vector4f(f / 16.0F, f1 / 16.0F, 0.0F, 1.0F);
      vector4f.transform(matrix4f);
      float f2 = 16.0F * vector4f.x();
      float f3 = 16.0F * vector4f.y();
      float f4 = p_228821_0_.getU(p_228821_0_.getReverseIndex(2));
      float f5 = p_228821_0_.getV(p_228821_0_.getReverseIndex(2));
      Vector4f vector4f1 = new Vector4f(f4 / 16.0F, f5 / 16.0F, 0.0F, 1.0F);
      vector4f1.transform(matrix4f);
      float f6 = 16.0F * vector4f1.x();
      float f7 = 16.0F * vector4f1.y();
      float f8;
      float f9;
      if (Math.signum(f4 - f) == Math.signum(f6 - f2)) {
         f8 = f2;
         f9 = f6;
      } else {
         f8 = f6;
         f9 = f2;
      }

      float f10;
      float f11;
      if (Math.signum(f5 - f1) == Math.signum(f7 - f3)) {
         f10 = f3;
         f11 = f7;
      } else {
         f10 = f7;
         f11 = f3;
      }

      float f12 = (float)Math.toRadians((double)p_228821_0_.rotation);
      Vector3f vector3f = new Vector3f(MathHelper.cos(f12), MathHelper.sin(f12), 0.0F);
      Matrix3f matrix3f = new Matrix3f(matrix4f);
      vector3f.transform(matrix3f);
      int i = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)vector3f.y(), (double)vector3f.x())) / 90.0D)) * 90, 360);
      return new BlockFaceUV(new float[]{f8, f10, f9, f11}, i);
   }

   private int[] makeVertices(BlockFaceUV p_228820_1_, TextureAtlasSprite p_228820_2_, Direction p_228820_3_, float[] p_228820_4_, TransformationMatrix p_228820_5_, @Nullable BlockPartRotation p_228820_6_, boolean p_228820_7_) {
      int[] aint = new int[32];

      for(int i = 0; i < 4; ++i) {
         this.bakeVertex(aint, i, p_228820_3_, p_228820_1_, p_228820_4_, p_228820_2_, p_228820_5_, p_228820_6_, p_228820_7_);
      }

      return aint;
   }

   private float[] setupShape(Vector3f p_199337_1_, Vector3f p_199337_2_) {
      float[] afloat = new float[Direction.values().length];
      afloat[FaceDirection.Constants.MIN_X] = p_199337_1_.x() / 16.0F;
      afloat[FaceDirection.Constants.MIN_Y] = p_199337_1_.y() / 16.0F;
      afloat[FaceDirection.Constants.MIN_Z] = p_199337_1_.z() / 16.0F;
      afloat[FaceDirection.Constants.MAX_X] = p_199337_2_.x() / 16.0F;
      afloat[FaceDirection.Constants.MAX_Y] = p_199337_2_.y() / 16.0F;
      afloat[FaceDirection.Constants.MAX_Z] = p_199337_2_.z() / 16.0F;
      return afloat;
   }

   private void bakeVertex(int[] p_228827_1_, int p_228827_2_, Direction p_228827_3_, BlockFaceUV p_228827_4_, float[] p_228827_5_, TextureAtlasSprite p_228827_6_, TransformationMatrix p_228827_7_, @Nullable BlockPartRotation p_228827_8_, boolean p_228827_9_) {
      FaceDirection.VertexInformation facedirection$vertexinformation = FaceDirection.fromFacing(p_228827_3_).getVertexInfo(p_228827_2_);
      Vector3f vector3f = new Vector3f(p_228827_5_[facedirection$vertexinformation.xFace], p_228827_5_[facedirection$vertexinformation.yFace], p_228827_5_[facedirection$vertexinformation.zFace]);
      this.applyElementRotation(vector3f, p_228827_8_);
      this.applyModelRotation(vector3f, p_228827_7_);
      this.fillVertex(p_228827_1_, p_228827_2_, vector3f, p_228827_6_, p_228827_4_);
   }

   private void fillVertex(int[] p_239288_1_, int p_239288_2_, Vector3f p_239288_3_, TextureAtlasSprite p_239288_4_, BlockFaceUV p_239288_5_) {
      int i = p_239288_2_ * 8;
      p_239288_1_[i] = Float.floatToRawIntBits(p_239288_3_.x());
      p_239288_1_[i + 1] = Float.floatToRawIntBits(p_239288_3_.y());
      p_239288_1_[i + 2] = Float.floatToRawIntBits(p_239288_3_.z());
      p_239288_1_[i + 3] = -1;
      p_239288_1_[i + 4] = Float.floatToRawIntBits(p_239288_4_.getU((double)p_239288_5_.getU(p_239288_2_) * .999 + p_239288_5_.getU((p_239288_2_ + 2) % 4) * .001));
      p_239288_1_[i + 4 + 1] = Float.floatToRawIntBits(p_239288_4_.getV((double)p_239288_5_.getV(p_239288_2_) * .999 + p_239288_5_.getV((p_239288_2_ + 2) % 4) * .001));
   }

   private void applyElementRotation(Vector3f p_199336_1_, @Nullable BlockPartRotation p_199336_2_) {
      if (p_199336_2_ != null) {
         Vector3f vector3f;
         Vector3f vector3f1;
         switch(p_199336_2_.axis) {
         case X:
            vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
            vector3f1 = new Vector3f(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
            vector3f1 = new Vector3f(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            vector3f = new Vector3f(0.0F, 0.0F, 1.0F);
            vector3f1 = new Vector3f(1.0F, 1.0F, 0.0F);
            break;
         default:
            throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternion quaternion = new Quaternion(vector3f, p_199336_2_.angle, true);
         if (p_199336_2_.rescale) {
            if (Math.abs(p_199336_2_.angle) == 22.5F) {
               vector3f1.mul(RESCALE_22_5);
            } else {
               vector3f1.mul(RESCALE_45);
            }

            vector3f1.add(1.0F, 1.0F, 1.0F);
         } else {
            vector3f1.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateVertexBy(p_199336_1_, p_199336_2_.origin.copy(), new Matrix4f(quaternion), vector3f1);
      }
   }

   public void applyModelRotation(Vector3f p_228822_1_, TransformationMatrix p_228822_2_) {
      if (p_228822_2_ != TransformationMatrix.identity()) {
         this.rotateVertexBy(p_228822_1_, new Vector3f(0.5F, 0.5F, 0.5F), p_228822_2_.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
      }
   }

   private void rotateVertexBy(Vector3f p_228823_1_, Vector3f p_228823_2_, Matrix4f p_228823_3_, Vector3f p_228823_4_) {
      Vector4f vector4f = new Vector4f(p_228823_1_.x() - p_228823_2_.x(), p_228823_1_.y() - p_228823_2_.y(), p_228823_1_.z() - p_228823_2_.z(), 1.0F);
      vector4f.transform(p_228823_3_);
      vector4f.mul(p_228823_4_);
      p_228823_1_.set(vector4f.x() + p_228823_2_.x(), vector4f.y() + p_228823_2_.y(), vector4f.z() + p_228823_2_.z());
   }

   public static Direction calculateFacing(int[] p_178410_0_) {
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(p_178410_0_[0]), Float.intBitsToFloat(p_178410_0_[1]), Float.intBitsToFloat(p_178410_0_[2]));
      Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(p_178410_0_[8]), Float.intBitsToFloat(p_178410_0_[9]), Float.intBitsToFloat(p_178410_0_[10]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(p_178410_0_[16]), Float.intBitsToFloat(p_178410_0_[17]), Float.intBitsToFloat(p_178410_0_[18]));
      Vector3f vector3f3 = vector3f.copy();
      vector3f3.sub(vector3f1);
      Vector3f vector3f4 = vector3f2.copy();
      vector3f4.sub(vector3f1);
      Vector3f vector3f5 = vector3f4.copy();
      vector3f5.cross(vector3f3);
      vector3f5.normalize();
      Direction direction = null;
      float f = 0.0F;

      for(Direction direction1 : Direction.values()) {
         Vector3i vector3i = direction1.getNormal();
         Vector3f vector3f6 = new Vector3f((float)vector3i.getX(), (float)vector3i.getY(), (float)vector3i.getZ());
         float f1 = vector3f5.dot(vector3f6);
         if (f1 >= 0.0F && f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction == null ? Direction.UP : direction;
   }

   private void recalculateWinding(int[] p_178408_1_, Direction p_178408_2_) {
      int[] aint = new int[p_178408_1_.length];
      System.arraycopy(p_178408_1_, 0, aint, 0, p_178408_1_.length);
      float[] afloat = new float[Direction.values().length];
      afloat[FaceDirection.Constants.MIN_X] = 999.0F;
      afloat[FaceDirection.Constants.MIN_Y] = 999.0F;
      afloat[FaceDirection.Constants.MIN_Z] = 999.0F;
      afloat[FaceDirection.Constants.MAX_X] = -999.0F;
      afloat[FaceDirection.Constants.MAX_Y] = -999.0F;
      afloat[FaceDirection.Constants.MAX_Z] = -999.0F;

      for(int i = 0; i < 4; ++i) {
         int j = 8 * i;
         float f = Float.intBitsToFloat(aint[j]);
         float f1 = Float.intBitsToFloat(aint[j + 1]);
         float f2 = Float.intBitsToFloat(aint[j + 2]);
         if (f < afloat[FaceDirection.Constants.MIN_X]) {
            afloat[FaceDirection.Constants.MIN_X] = f;
         }

         if (f1 < afloat[FaceDirection.Constants.MIN_Y]) {
            afloat[FaceDirection.Constants.MIN_Y] = f1;
         }

         if (f2 < afloat[FaceDirection.Constants.MIN_Z]) {
            afloat[FaceDirection.Constants.MIN_Z] = f2;
         }

         if (f > afloat[FaceDirection.Constants.MAX_X]) {
            afloat[FaceDirection.Constants.MAX_X] = f;
         }

         if (f1 > afloat[FaceDirection.Constants.MAX_Y]) {
            afloat[FaceDirection.Constants.MAX_Y] = f1;
         }

         if (f2 > afloat[FaceDirection.Constants.MAX_Z]) {
            afloat[FaceDirection.Constants.MAX_Z] = f2;
         }
      }

      FaceDirection facedirection = FaceDirection.fromFacing(p_178408_2_);

      for(int i1 = 0; i1 < 4; ++i1) {
         int j1 = 8 * i1;
         FaceDirection.VertexInformation facedirection$vertexinformation = facedirection.getVertexInfo(i1);
         float f8 = afloat[facedirection$vertexinformation.xFace];
         float f3 = afloat[facedirection$vertexinformation.yFace];
         float f4 = afloat[facedirection$vertexinformation.zFace];
         p_178408_1_[j1] = Float.floatToRawIntBits(f8);
         p_178408_1_[j1 + 1] = Float.floatToRawIntBits(f3);
         p_178408_1_[j1 + 2] = Float.floatToRawIntBits(f4);

         for(int k = 0; k < 4; ++k) {
            int l = 8 * k;
            float f5 = Float.intBitsToFloat(aint[l]);
            float f6 = Float.intBitsToFloat(aint[l + 1]);
            float f7 = Float.intBitsToFloat(aint[l + 2]);
            if (MathHelper.equal(f8, f5) && MathHelper.equal(f3, f6) && MathHelper.equal(f4, f7)) {
               p_178408_1_[j1 + 4] = aint[l + 4];
               p_178408_1_[j1 + 4 + 1] = aint[l + 4 + 1];
            }
         }
      }

   }
}
