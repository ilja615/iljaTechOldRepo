package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft p_i46555_1_) {
      this.minecraft = p_i46555_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      BlockPos blockpos = this.minecraft.player.blockPosition();
      IWorldReader iworldreader = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
         FluidState fluidstate = iworldreader.getFluidState(blockpos1);
         if (fluidstate.is(FluidTags.WATER)) {
            double d0 = (double)((float)blockpos1.getY() + fluidstate.getHeight(iworldreader, blockpos1));
            DebugRenderer.renderFilledBox((new AxisAlignedBB((double)((float)blockpos1.getX() + 0.01F), (double)((float)blockpos1.getY() + 0.01F), (double)((float)blockpos1.getZ() + 0.01F), (double)((float)blockpos1.getX() + 0.99F), d0, (double)((float)blockpos1.getZ() + 0.99F))).move(-p_225619_3_, -p_225619_5_, -p_225619_7_), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      for(BlockPos blockpos2 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
         FluidState fluidstate1 = iworldreader.getFluidState(blockpos2);
         if (fluidstate1.is(FluidTags.WATER)) {
            DebugRenderer.renderFloatingText(String.valueOf(fluidstate1.getAmount()), (double)blockpos2.getX() + 0.5D, (double)((float)blockpos2.getY() + fluidstate1.getHeight(iworldreader, blockpos2)), (double)blockpos2.getZ() + 0.5D, -16777216);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}
