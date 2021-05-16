package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public LightDebugRenderer(Minecraft p_i48765_1_) {
      this.minecraft = p_i48765_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      World world = this.minecraft.level;
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(p_225619_3_, p_225619_5_, p_225619_7_);
      LongSet longset = new LongOpenHashSet();

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
         int i = world.getBrightness(LightType.SKY, blockpos1);
         float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
         int j = MathHelper.hsvToRgb(f, 0.9F, 0.9F);
         long k = SectionPos.blockToSection(blockpos1.asLong());
         if (longset.add(k)) {
            DebugRenderer.renderFloatingText(world.getChunkSource().getLightEngine().getDebugData(LightType.SKY, SectionPos.of(k)), (double)(SectionPos.x(k) * 16 + 8), (double)(SectionPos.y(k) * 16 + 8), (double)(SectionPos.z(k) * 16 + 8), 16711680, 0.3F);
         }

         if (i != 15) {
            DebugRenderer.renderFloatingText(String.valueOf(i), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.25D, (double)blockpos1.getZ() + 0.5D, j);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
