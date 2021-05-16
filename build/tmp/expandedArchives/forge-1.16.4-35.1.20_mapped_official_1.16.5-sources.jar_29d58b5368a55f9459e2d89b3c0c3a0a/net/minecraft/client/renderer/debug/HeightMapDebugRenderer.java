package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeightMapDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public HeightMapDebugRenderer(Minecraft p_i47133_1_) {
      this.minecraft = p_i47133_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      IWorld iworld = this.minecraft.level;
      RenderSystem.pushMatrix();
      RenderSystem.disableBlend();
      RenderSystem.disableTexture();
      RenderSystem.enableDepthTest();
      BlockPos blockpos = new BlockPos(p_225619_3_, 0.0D, p_225619_7_);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(int i = -32; i <= 32; i += 16) {
         for(int j = -32; j <= 32; j += 16) {
            IChunk ichunk = iworld.getChunk(blockpos.offset(i, 0, j));

            for(Entry<Heightmap.Type, Heightmap> entry : ichunk.getHeightmaps()) {
               Heightmap.Type heightmap$type = entry.getKey();
               ChunkPos chunkpos = ichunk.getPos();
               Vector3f vector3f = this.getColor(heightmap$type);

               for(int k = 0; k < 16; ++k) {
                  for(int l = 0; l < 16; ++l) {
                     int i1 = chunkpos.x * 16 + k;
                     int j1 = chunkpos.z * 16 + l;
                     float f = (float)((double)((float)iworld.getHeight(heightmap$type, i1, j1) + (float)heightmap$type.ordinal() * 0.09375F) - p_225619_5_);
                     WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)i1 + 0.25F) - p_225619_3_, (double)f, (double)((float)j1 + 0.25F) - p_225619_7_, (double)((float)i1 + 0.75F) - p_225619_3_, (double)(f + 0.09375F), (double)((float)j1 + 0.75F) - p_225619_7_, vector3f.x(), vector3f.y(), vector3f.z(), 1.0F);
                  }
               }
            }
         }
      }

      tessellator.end();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }

   private Vector3f getColor(Heightmap.Type p_239373_1_) {
      switch(p_239373_1_) {
      case WORLD_SURFACE_WG:
         return new Vector3f(1.0F, 1.0F, 0.0F);
      case OCEAN_FLOOR_WG:
         return new Vector3f(1.0F, 0.0F, 1.0F);
      case WORLD_SURFACE:
         return new Vector3f(0.0F, 0.7F, 0.0F);
      case OCEAN_FLOOR:
         return new Vector3f(0.0F, 0.0F, 0.5F);
      case MOTION_BLOCKING:
         return new Vector3f(0.0F, 0.3F, 0.3F);
      case MOTION_BLOCKING_NO_LEAVES:
         return new Vector3f(0.0F, 0.5F, 0.5F);
      default:
         return new Vector3f(0.0F, 0.0F, 0.0F);
      }
   }
}
