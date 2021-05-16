package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
   protected final EntityRendererManager entityRenderDispatcher;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;

   protected EntityRenderer(EntityRendererManager p_i46179_1_) {
      this.entityRenderDispatcher = p_i46179_1_;
   }

   public final int getPackedLightCoords(T p_229100_1_, float p_229100_2_) {
      BlockPos blockpos = new BlockPos(p_229100_1_.getLightProbePosition(p_229100_2_));
      return LightTexture.pack(this.getBlockLightLevel(p_229100_1_, blockpos), this.getSkyLightLevel(p_229100_1_, blockpos));
   }

   protected int getSkyLightLevel(T p_239381_1_, BlockPos p_239381_2_) {
      return p_239381_1_.level.getBrightness(LightType.SKY, p_239381_2_);
   }

   protected int getBlockLightLevel(T p_225624_1_, BlockPos p_225624_2_) {
      return p_225624_1_.isOnFire() ? 15 : p_225624_1_.level.getBrightness(LightType.BLOCK, p_225624_2_);
   }

   public boolean shouldRender(T p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (!p_225626_1_.shouldRender(p_225626_3_, p_225626_5_, p_225626_7_)) {
         return false;
      } else if (p_225626_1_.noCulling) {
         return true;
      } else {
         AxisAlignedBB axisalignedbb = p_225626_1_.getBoundingBoxForCulling().inflate(0.5D);
         if (axisalignedbb.hasNaN() || axisalignedbb.getSize() == 0.0D) {
            axisalignedbb = new AxisAlignedBB(p_225626_1_.getX() - 2.0D, p_225626_1_.getY() - 2.0D, p_225626_1_.getZ() - 2.0D, p_225626_1_.getX() + 2.0D, p_225626_1_.getY() + 2.0D, p_225626_1_.getZ() + 2.0D);
         }

         return p_225626_2_.isVisible(axisalignedbb);
      }
   }

   public Vector3d getRenderOffset(T p_225627_1_, float p_225627_2_) {
      return Vector3d.ZERO;
   }

   public void render(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(p_225623_1_, p_225623_1_.getDisplayName(), this, p_225623_4_, p_225623_5_, p_225623_6_, p_225623_3_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
      if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(p_225623_1_))) {
         this.renderNameTag(p_225623_1_, renderNameplateEvent.getContent(), p_225623_4_, p_225623_5_, p_225623_6_);
      }
   }

   protected boolean shouldShowName(T p_177070_1_) {
      return p_177070_1_.shouldShowName() && p_177070_1_.hasCustomName();
   }

   public abstract ResourceLocation getTextureLocation(T p_110775_1_);

   public FontRenderer getFont() {
      return this.entityRenderDispatcher.getFont();
   }

   protected void renderNameTag(T p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
      if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_225629_1_, d0)) {
         boolean flag = !p_225629_1_.isDiscrete();
         float f = p_225629_1_.getBbHeight() + 0.5F;
         int i = "deadmau5".equals(p_225629_2_.getString()) ? -10 : 0;
         p_225629_3_.pushPose();
         p_225629_3_.translate(0.0D, (double)f, 0.0D);
         p_225629_3_.mulPose(this.entityRenderDispatcher.cameraOrientation());
         p_225629_3_.scale(-0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = p_225629_3_.last().pose();
         float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
         int j = (int)(f1 * 255.0F) << 24;
         FontRenderer fontrenderer = this.getFont();
         float f2 = (float)(-fontrenderer.width(p_225629_2_) / 2);
         fontrenderer.drawInBatch(p_225629_2_, f2, (float)i, 553648127, false, matrix4f, p_225629_4_, flag, j, p_225629_5_);
         if (flag) {
            fontrenderer.drawInBatch(p_225629_2_, f2, (float)i, -1, false, matrix4f, p_225629_4_, false, 0, p_225629_5_);
         }

         p_225629_3_.popPose();
      }
   }

   public EntityRendererManager getDispatcher() {
      return this.entityRenderDispatcher;
   }
}
