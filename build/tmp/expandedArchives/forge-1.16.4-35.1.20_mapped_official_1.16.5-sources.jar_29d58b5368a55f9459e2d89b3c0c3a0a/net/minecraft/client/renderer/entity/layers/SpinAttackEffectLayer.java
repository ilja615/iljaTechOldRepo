package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpinAttackEffectLayer<T extends LivingEntity> extends LayerRenderer<T, PlayerModel<T>> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");
   private final ModelRenderer box = new ModelRenderer(64, 64, 0, 0);

   public SpinAttackEffectLayer(IEntityRenderer<T, PlayerModel<T>> p_i50920_1_) {
      super(p_i50920_1_);
      this.box.addBox(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (p_225628_4_.isAutoSpinAttack()) {
         IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

         for(int i = 0; i < 3; ++i) {
            p_225628_1_.pushPose();
            float f = p_225628_8_ * (float)(-(45 + i * 5));
            p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(f));
            float f1 = 0.75F * (float)i;
            p_225628_1_.scale(f1, f1, f1);
            p_225628_1_.translate(0.0D, (double)(-0.2F + 0.6F * (float)i), 0.0D);
            this.box.render(p_225628_1_, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY);
            p_225628_1_.popPose();
         }

      }
   }
}
