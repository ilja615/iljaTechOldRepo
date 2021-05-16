package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianModel extends SegmentedModel<GuardianEntity> {
   private static final float[] SPIKE_X_ROT = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
   private static final float[] SPIKE_Y_ROT = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] SPIKE_Z_ROT = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
   private static final float[] SPIKE_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
   private static final float[] SPIKE_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
   private static final float[] SPIKE_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
   private final ModelRenderer head;
   private final ModelRenderer eye;
   private final ModelRenderer[] spikeParts;
   private final ModelRenderer[] tailParts;

   public GuardianModel() {
      this.texWidth = 64;
      this.texHeight = 64;
      this.spikeParts = new ModelRenderer[12];
      this.head = new ModelRenderer(this);
      this.head.texOffs(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F);
      this.head.texOffs(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F);
      this.head.texOffs(0, 28).addBox(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true);
      this.head.texOffs(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F);
      this.head.texOffs(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F);

      for(int i = 0; i < this.spikeParts.length; ++i) {
         this.spikeParts[i] = new ModelRenderer(this, 0, 0);
         this.spikeParts[i].addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);
         this.head.addChild(this.spikeParts[i]);
      }

      this.eye = new ModelRenderer(this, 8, 0);
      this.eye.addBox(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F);
      this.head.addChild(this.eye);
      this.tailParts = new ModelRenderer[3];
      this.tailParts[0] = new ModelRenderer(this, 40, 0);
      this.tailParts[0].addBox(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F);
      this.tailParts[1] = new ModelRenderer(this, 0, 54);
      this.tailParts[1].addBox(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F);
      this.tailParts[2] = new ModelRenderer(this);
      this.tailParts[2].texOffs(41, 32).addBox(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F);
      this.tailParts[2].texOffs(25, 19).addBox(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F);
      this.head.addChild(this.tailParts[0]);
      this.tailParts[0].addChild(this.tailParts[1]);
      this.tailParts[1].addChild(this.tailParts[2]);
      this.setupSpikes(0.0F, 0.0F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.head);
   }

   public void setupAnim(GuardianEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = p_225597_4_ - (float)p_225597_1_.tickCount;
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      float f1 = (1.0F - p_225597_1_.getSpikesAnimation(f)) * 0.55F;
      this.setupSpikes(p_225597_4_, f1);
      this.eye.z = -8.25F;
      Entity entity = Minecraft.getInstance().getCameraEntity();
      if (p_225597_1_.hasActiveAttackTarget()) {
         entity = p_225597_1_.getActiveAttackTarget();
      }

      if (entity != null) {
         Vector3d vector3d = entity.getEyePosition(0.0F);
         Vector3d vector3d1 = p_225597_1_.getEyePosition(0.0F);
         double d0 = vector3d.y - vector3d1.y;
         if (d0 > 0.0D) {
            this.eye.y = 0.0F;
         } else {
            this.eye.y = 1.0F;
         }

         Vector3d vector3d2 = p_225597_1_.getViewVector(0.0F);
         vector3d2 = new Vector3d(vector3d2.x, 0.0D, vector3d2.z);
         Vector3d vector3d3 = (new Vector3d(vector3d1.x - vector3d.x, 0.0D, vector3d1.z - vector3d.z)).normalize().yRot(((float)Math.PI / 2F));
         double d1 = vector3d2.dot(vector3d3);
         this.eye.x = MathHelper.sqrt((float)Math.abs(d1)) * 2.0F * (float)Math.signum(d1);
      }

      this.eye.visible = true;
      float f2 = p_225597_1_.getTailAnimation(f);
      this.tailParts[0].yRot = MathHelper.sin(f2) * (float)Math.PI * 0.05F;
      this.tailParts[1].yRot = MathHelper.sin(f2) * (float)Math.PI * 0.1F;
      this.tailParts[1].x = -1.5F;
      this.tailParts[1].y = 0.5F;
      this.tailParts[1].z = 14.0F;
      this.tailParts[2].yRot = MathHelper.sin(f2) * (float)Math.PI * 0.15F;
      this.tailParts[2].x = 0.5F;
      this.tailParts[2].y = 0.5F;
      this.tailParts[2].z = 6.0F;
   }

   private void setupSpikes(float p_228261_1_, float p_228261_2_) {
      for(int i = 0; i < 12; ++i) {
         this.spikeParts[i].xRot = (float)Math.PI * SPIKE_X_ROT[i];
         this.spikeParts[i].yRot = (float)Math.PI * SPIKE_Y_ROT[i];
         this.spikeParts[i].zRot = (float)Math.PI * SPIKE_Z_ROT[i];
         this.spikeParts[i].x = SPIKE_X[i] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)i) * 0.01F - p_228261_2_);
         this.spikeParts[i].y = 16.0F + SPIKE_Y[i] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)i) * 0.01F - p_228261_2_);
         this.spikeParts[i].z = SPIKE_Z[i] * (1.0F + MathHelper.cos(p_228261_1_ * 1.5F + (float)i) * 0.01F - p_228261_2_);
      }

   }
}
