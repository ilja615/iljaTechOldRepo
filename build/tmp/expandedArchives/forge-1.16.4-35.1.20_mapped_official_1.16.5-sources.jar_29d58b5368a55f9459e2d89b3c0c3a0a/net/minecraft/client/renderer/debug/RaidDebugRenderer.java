package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaidDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private Collection<BlockPos> raidCenters = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft p_i51517_1_) {
      this.minecraft = p_i51517_1_;
   }

   public void setRaidCenters(Collection<BlockPos> p_222906_1_) {
      this.raidCenters = p_222906_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      BlockPos blockpos = this.getCamera().getBlockPosition();

      for(BlockPos blockpos1 : this.raidCenters) {
         if (blockpos.closerThan(blockpos1, 160.0D)) {
            highlightRaidCenter(blockpos1);
         }
      }

   }

   private static void highlightRaidCenter(BlockPos p_222903_0_) {
      DebugRenderer.renderFilledBox(p_222903_0_.offset(-0.5D, -0.5D, -0.5D), p_222903_0_.offset(1.5D, 1.5D, 1.5D), 1.0F, 0.0F, 0.0F, 0.15F);
      int i = -65536;
      renderTextOverBlock("Raid center", p_222903_0_, -65536);
   }

   private static void renderTextOverBlock(String p_222905_0_, BlockPos p_222905_1_, int p_222905_2_) {
      double d0 = (double)p_222905_1_.getX() + 0.5D;
      double d1 = (double)p_222905_1_.getY() + 1.3D;
      double d2 = (double)p_222905_1_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_222905_0_, d0, d1, d2, p_222905_2_, 0.04F, true, 0.0F, true);
   }

   private ActiveRenderInfo getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
