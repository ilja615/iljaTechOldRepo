package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Map<BlockPos, BlockPos> tunnelsList = Maps.newHashMap();
   private final Map<BlockPos, Float> thicknessMap = Maps.newHashMap();
   private final List<BlockPos> startPoses = Lists.newArrayList();

   public void addTunnel(BlockPos p_201742_1_, List<BlockPos> p_201742_2_, List<Float> p_201742_3_) {
      for(int i = 0; i < p_201742_2_.size(); ++i) {
         this.tunnelsList.put(p_201742_2_.get(i), p_201742_1_);
         this.thicknessMap.put(p_201742_2_.get(i), p_201742_3_.get(i));
      }

      this.startPoses.add(p_201742_1_);
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(p_225619_3_, 0.0D, p_225619_7_);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(Entry<BlockPos, BlockPos> entry : this.tunnelsList.entrySet()) {
         BlockPos blockpos1 = entry.getKey();
         BlockPos blockpos2 = entry.getValue();
         float f = (float)(blockpos2.getX() * 128 % 256) / 256.0F;
         float f1 = (float)(blockpos2.getY() * 128 % 256) / 256.0F;
         float f2 = (float)(blockpos2.getZ() * 128 % 256) / 256.0F;
         float f3 = this.thicknessMap.get(blockpos1);
         if (blockpos.closerThan(blockpos1, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos1.getX() + 0.5F) - p_225619_3_ - (double)f3, (double)((float)blockpos1.getY() + 0.5F) - p_225619_5_ - (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - p_225619_7_ - (double)f3, (double)((float)blockpos1.getX() + 0.5F) - p_225619_3_ + (double)f3, (double)((float)blockpos1.getY() + 0.5F) - p_225619_5_ + (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - p_225619_7_ + (double)f3, f, f1, f2, 0.5F);
         }
      }

      for(BlockPos blockpos3 : this.startPoses) {
         if (blockpos.closerThan(blockpos3, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)blockpos3.getX() - p_225619_3_, (double)blockpos3.getY() - p_225619_5_, (double)blockpos3.getZ() - p_225619_7_, (double)((float)blockpos3.getX() + 1.0F) - p_225619_3_, (double)((float)blockpos3.getY() + 1.0F) - p_225619_5_, (double)((float)blockpos3.getZ() + 1.0F) - p_225619_7_, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      tessellator.end();
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
