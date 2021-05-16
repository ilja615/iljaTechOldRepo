package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolidFaceDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceDebugRenderer(Minecraft p_i47478_1_) {
      this.minecraft = p_i47478_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      IBlockReader iblockreader = this.minecraft.player.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.lineWidth(2.0F);
      RenderSystem.disableTexture();
      RenderSystem.depthMask(false);
      BlockPos blockpos = new BlockPos(p_225619_3_, p_225619_5_, p_225619_7_);

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-6, -6, -6), blockpos.offset(6, 6, 6))) {
         BlockState blockstate = iblockreader.getBlockState(blockpos1);
         if (!blockstate.is(Blocks.AIR)) {
            VoxelShape voxelshape = blockstate.getShape(iblockreader, blockpos1);

            for(AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
               AxisAlignedBB axisalignedbb1 = axisalignedbb.move(blockpos1).inflate(0.002D).move(-p_225619_3_, -p_225619_5_, -p_225619_7_);
               double d0 = axisalignedbb1.minX;
               double d1 = axisalignedbb1.minY;
               double d2 = axisalignedbb1.minZ;
               double d3 = axisalignedbb1.maxX;
               double d4 = axisalignedbb1.maxY;
               double d5 = axisalignedbb1.maxZ;
               float f = 1.0F;
               float f1 = 0.0F;
               float f2 = 0.0F;
               float f3 = 0.5F;
               if (blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.WEST)) {
                  Tessellator tessellator = Tessellator.getInstance();
                  BufferBuilder bufferbuilder = tessellator.getBuilder();
                  bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder.vertex(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.vertex(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.vertex(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.vertex(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator.end();
               }

               if (blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.SOUTH)) {
                  Tessellator tessellator1 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
                  bufferbuilder1.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder1.vertex(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.vertex(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.vertex(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.vertex(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator1.end();
               }

               if (blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.EAST)) {
                  Tessellator tessellator2 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder2 = tessellator2.getBuilder();
                  bufferbuilder2.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder2.vertex(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.vertex(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.vertex(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.vertex(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator2.end();
               }

               if (blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.NORTH)) {
                  Tessellator tessellator3 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder3 = tessellator3.getBuilder();
                  bufferbuilder3.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder3.vertex(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.vertex(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.vertex(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.vertex(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator3.end();
               }

               if (blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.DOWN)) {
                  Tessellator tessellator4 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder4 = tessellator4.getBuilder();
                  bufferbuilder4.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder4.vertex(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.vertex(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.vertex(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.vertex(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator4.end();
               }

               if (blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.UP)) {
                  Tessellator tessellator5 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder5 = tessellator5.getBuilder();
                  bufferbuilder5.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder5.vertex(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.vertex(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.vertex(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.vertex(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator5.end();
               }
            }
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}
