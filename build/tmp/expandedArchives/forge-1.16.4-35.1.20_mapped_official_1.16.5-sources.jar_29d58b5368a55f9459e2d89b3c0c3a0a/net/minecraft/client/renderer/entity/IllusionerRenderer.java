package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusionerRenderer extends IllagerRenderer<IllusionerEntity> {
   private static final ResourceLocation ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererManager p_i47477_1_) {
      super(p_i47477_1_, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<IllusionerEntity, IllagerModel<IllusionerEntity>>(this) {
         public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, IllusionerEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (p_225628_4_.isCastingSpell() || p_225628_4_.isAggressive()) {
               super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
            }

         }
      });
      this.model.getHat().visible = true;
   }

   public ResourceLocation getTextureLocation(IllusionerEntity p_110775_1_) {
      return ILLUSIONER;
   }

   public void render(IllusionerEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      if (p_225623_1_.isInvisible()) {
         Vector3d[] avector3d = p_225623_1_.getIllusionOffsets(p_225623_3_);
         float f = this.getBob(p_225623_1_, p_225623_3_);

         for(int i = 0; i < avector3d.length; ++i) {
            p_225623_4_.pushPose();
            p_225623_4_.translate(avector3d[i].x + (double)MathHelper.cos((float)i + f * 0.5F) * 0.025D, avector3d[i].y + (double)MathHelper.cos((float)i + f * 0.75F) * 0.0125D, avector3d[i].z + (double)MathHelper.cos((float)i + f * 0.7F) * 0.025D);
            super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
            p_225623_4_.popPose();
         }
      } else {
         super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      }

   }

   protected boolean isBodyVisible(IllusionerEntity p_225622_1_) {
      return true;
   }
}
