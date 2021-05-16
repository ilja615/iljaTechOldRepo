package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapeLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public CapeLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50950_1_) {
      super(p_i50950_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, AbstractClientPlayerEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (p_225628_4_.isCapeLoaded() && !p_225628_4_.isInvisible() && p_225628_4_.isModelPartShown(PlayerModelPart.CAPE) && p_225628_4_.getCloakTextureLocation() != null) {
         ItemStack itemstack = p_225628_4_.getItemBySlot(EquipmentSlotType.CHEST);
         if (itemstack.getItem() != Items.ELYTRA) {
            p_225628_1_.pushPose();
            p_225628_1_.translate(0.0D, 0.0D, 0.125D);
            double d0 = MathHelper.lerp((double)p_225628_7_, p_225628_4_.xCloakO, p_225628_4_.xCloak) - MathHelper.lerp((double)p_225628_7_, p_225628_4_.xo, p_225628_4_.getX());
            double d1 = MathHelper.lerp((double)p_225628_7_, p_225628_4_.yCloakO, p_225628_4_.yCloak) - MathHelper.lerp((double)p_225628_7_, p_225628_4_.yo, p_225628_4_.getY());
            double d2 = MathHelper.lerp((double)p_225628_7_, p_225628_4_.zCloakO, p_225628_4_.zCloak) - MathHelper.lerp((double)p_225628_7_, p_225628_4_.zo, p_225628_4_.getZ());
            float f = p_225628_4_.yBodyRotO + (p_225628_4_.yBodyRot - p_225628_4_.yBodyRotO);
            double d3 = (double)MathHelper.sin(f * ((float)Math.PI / 180F));
            double d4 = (double)(-MathHelper.cos(f * ((float)Math.PI / 180F)));
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
            f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
            if (f2 < 0.0F) {
               f2 = 0.0F;
            }

            float f4 = MathHelper.lerp(p_225628_7_, p_225628_4_.oBob, p_225628_4_.bob);
            f1 = f1 + MathHelper.sin(MathHelper.lerp(p_225628_7_, p_225628_4_.walkDistO, p_225628_4_.walkDist) * 6.0F) * 32.0F * f4;
            if (p_225628_4_.isCrouching()) {
               f1 += 25.0F;
            }

            p_225628_1_.mulPose(Vector3f.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
            p_225628_1_.mulPose(Vector3f.ZP.rotationDegrees(f3 / 2.0F));
            p_225628_1_.mulPose(Vector3f.YP.rotationDegrees(180.0F - f3 / 2.0F));
            IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.entitySolid(p_225628_4_.getCloakTextureLocation()));
            this.getParentModel().renderCloak(p_225628_1_, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY);
            p_225628_1_.popPose();
         }
      }
   }
}
