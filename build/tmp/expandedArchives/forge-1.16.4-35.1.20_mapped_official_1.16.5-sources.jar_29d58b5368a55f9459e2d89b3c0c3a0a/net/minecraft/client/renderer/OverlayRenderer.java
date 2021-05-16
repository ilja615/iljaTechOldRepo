package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayRenderer {
   private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

   public static void renderScreenEffect(Minecraft p_228734_0_, MatrixStack p_228734_1_) {
      RenderSystem.disableAlphaTest();
      PlayerEntity playerentity = p_228734_0_.player;
      if (!playerentity.noPhysics) {
         org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> overlay = getOverlayBlock(playerentity);
         if (overlay != null) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderBlockOverlay(playerentity, p_228734_1_, net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType.BLOCK, overlay.getLeft(), overlay.getRight()))
            renderTex(p_228734_0_, p_228734_0_.getBlockRenderer().getBlockModelShaper().getTexture(overlay.getLeft(), p_228734_0_.level, overlay.getRight()), p_228734_1_);
         }
      }

      if (!p_228734_0_.player.isSpectator()) {
         if (p_228734_0_.player.isEyeInFluid(FluidTags.WATER)) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderWaterOverlay(playerentity, p_228734_1_))
            renderWater(p_228734_0_, p_228734_1_);
         }

         if (p_228734_0_.player.isOnFire()) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderFireOverlay(playerentity, p_228734_1_))
            renderFire(p_228734_0_, p_228734_1_);
         }
      }

      RenderSystem.enableAlphaTest();
   }

   @Nullable
   private static BlockState getViewBlockingState(PlayerEntity p_230018_0_) {
      return getOverlayBlock(p_230018_0_).getLeft();
   }

   @Nullable
   private static org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> getOverlayBlock(PlayerEntity p_230018_0_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < 8; ++i) {
         double d0 = p_230018_0_.getX() + (double)(((float)((i >> 0) % 2) - 0.5F) * p_230018_0_.getBbWidth() * 0.8F);
         double d1 = p_230018_0_.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
         double d2 = p_230018_0_.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * p_230018_0_.getBbWidth() * 0.8F);
         blockpos$mutable.set(d0, d1, d2);
         BlockState blockstate = p_230018_0_.level.getBlockState(blockpos$mutable);
         if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE && blockstate.isViewBlocking(p_230018_0_.level, blockpos$mutable)) {
            return org.apache.commons.lang3.tuple.Pair.of(blockstate, blockpos$mutable.immutable());
         }
      }

      return null;
   }

   private static void renderTex(Minecraft p_228735_0_, TextureAtlasSprite p_228735_1_, MatrixStack p_228735_2_) {
      p_228735_0_.getTextureManager().bind(p_228735_1_.atlas().location());
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
      float f = 0.1F;
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = p_228735_1_.getU0();
      float f7 = p_228735_1_.getU1();
      float f8 = p_228735_1_.getV0();
      float f9 = p_228735_1_.getV1();
      Matrix4f matrix4f = p_228735_2_.last().pose();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
      bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f7, f9).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f6, f9).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f6, f8).endVertex();
      bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f7, f8).endVertex();
      bufferbuilder.end();
      WorldVertexBufferUploader.end(bufferbuilder);
   }

   private static void renderWater(Minecraft p_228736_0_, MatrixStack p_228736_1_) {
      RenderSystem.enableTexture();
      p_228736_0_.getTextureManager().bind(UNDERWATER_LOCATION);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
      float f = p_228736_0_.player.getBrightness();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -p_228736_0_.player.yRot / 64.0F;
      float f8 = p_228736_0_.player.xRot / 64.0F;
      Matrix4f matrix4f = p_228736_1_.last().pose();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
      bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).uv(4.0F + f7, 4.0F + f8).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).uv(0.0F + f7, 4.0F + f8).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).uv(0.0F + f7, 0.0F + f8).endVertex();
      bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).uv(4.0F + f7, 0.0F + f8).endVertex();
      bufferbuilder.end();
      WorldVertexBufferUploader.end(bufferbuilder);
      RenderSystem.disableBlend();
   }

   private static void renderFire(Minecraft p_228737_0_, MatrixStack p_228737_1_) {
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
      RenderSystem.depthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableTexture();
      TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_1.sprite();
      p_228737_0_.getTextureManager().bind(textureatlassprite.atlas().location());
      float f = textureatlassprite.getU0();
      float f1 = textureatlassprite.getU1();
      float f2 = (f + f1) / 2.0F;
      float f3 = textureatlassprite.getV0();
      float f4 = textureatlassprite.getV1();
      float f5 = (f3 + f4) / 2.0F;
      float f6 = textureatlassprite.uvShrinkRatio();
      float f7 = MathHelper.lerp(f6, f, f2);
      float f8 = MathHelper.lerp(f6, f1, f2);
      float f9 = MathHelper.lerp(f6, f3, f5);
      float f10 = MathHelper.lerp(f6, f4, f5);
      float f11 = 1.0F;

      for(int i = 0; i < 2; ++i) {
         p_228737_1_.pushPose();
         float f12 = -0.5F;
         float f13 = 0.5F;
         float f14 = -0.5F;
         float f15 = 0.5F;
         float f16 = -0.5F;
         p_228737_1_.translate((double)((float)(-(i * 2 - 1)) * 0.24F), (double)-0.3F, 0.0D);
         p_228737_1_.mulPose(Vector3f.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
         Matrix4f matrix4f = p_228737_1_.last().pose();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
         bufferbuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f10).endVertex();
         bufferbuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f10).endVertex();
         bufferbuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f9).endVertex();
         bufferbuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f9).endVertex();
         bufferbuilder.end();
         WorldVertexBufferUploader.end(bufferbuilder);
         p_228737_1_.popPose();
      }

      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.depthFunc(515);
   }
}
