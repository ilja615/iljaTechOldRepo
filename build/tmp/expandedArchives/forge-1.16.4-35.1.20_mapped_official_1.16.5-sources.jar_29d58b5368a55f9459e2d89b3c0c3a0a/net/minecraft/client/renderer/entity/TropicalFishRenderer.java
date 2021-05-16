package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.model.AbstractTropicalFishModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishRenderer extends MobRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
   private final TropicalFishAModel<TropicalFishEntity> modelA = new TropicalFishAModel<>(0.0F);
   private final TropicalFishBModel<TropicalFishEntity> modelB = new TropicalFishBModel<>(0.0F);

   public TropicalFishRenderer(EntityRendererManager p_i48889_1_) {
      super(p_i48889_1_, new TropicalFishAModel<>(0.0F), 0.15F);
      this.addLayer(new TropicalFishPatternLayer(this));
   }

   public ResourceLocation getTextureLocation(TropicalFishEntity p_110775_1_) {
      return p_110775_1_.getBaseTextureLocation();
   }

   public void render(TropicalFishEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      AbstractTropicalFishModel<TropicalFishEntity> abstracttropicalfishmodel = (AbstractTropicalFishModel<TropicalFishEntity>)(p_225623_1_.getBaseVariant() == 0 ? this.modelA : this.modelB);
      this.model = abstracttropicalfishmodel;
      float[] afloat = p_225623_1_.getBaseColor();
      abstracttropicalfishmodel.setColor(afloat[0], afloat[1], afloat[2]);
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      abstracttropicalfishmodel.setColor(1.0F, 1.0F, 1.0F);
   }

   protected void setupRotations(TropicalFishEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
      float f = 4.3F * MathHelper.sin(0.6F * p_225621_3_);
      p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(f));
      if (!p_225621_1_.isInWater()) {
         p_225621_2_.translate((double)0.2F, (double)0.1F, 0.0D);
         p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}
