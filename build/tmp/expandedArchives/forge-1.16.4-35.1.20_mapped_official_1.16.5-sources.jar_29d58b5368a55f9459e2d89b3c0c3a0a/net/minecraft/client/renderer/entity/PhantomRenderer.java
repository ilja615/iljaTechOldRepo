package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomRenderer extends MobRenderer<PhantomEntity, PhantomModel<PhantomEntity>> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public PhantomRenderer(EntityRendererManager p_i48829_1_) {
      super(p_i48829_1_, new PhantomModel<>(), 0.75F);
      this.addLayer(new PhantomEyesLayer<>(this));
   }

   public ResourceLocation getTextureLocation(PhantomEntity p_110775_1_) {
      return PHANTOM_LOCATION;
   }

   protected void scale(PhantomEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      int i = p_225620_1_.getPhantomSize();
      float f = 1.0F + 0.15F * (float)i;
      p_225620_2_.scale(f, f, f);
      p_225620_2_.translate(0.0D, 1.3125D, 0.1875D);
   }

   protected void setupRotations(PhantomEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
      p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(p_225621_1_.xRot));
   }
}
