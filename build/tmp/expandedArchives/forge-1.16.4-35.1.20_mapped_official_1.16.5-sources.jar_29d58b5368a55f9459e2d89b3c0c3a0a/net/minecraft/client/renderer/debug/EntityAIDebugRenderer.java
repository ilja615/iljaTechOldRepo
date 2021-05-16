package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityAIDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Integer, List<EntityAIDebugRenderer.Entry>> goalSelectors = Maps.newHashMap();

   public void clear() {
      this.goalSelectors.clear();
   }

   public void addGoalSelector(int p_217682_1_, List<EntityAIDebugRenderer.Entry> p_217682_2_) {
      this.goalSelectors.put(p_217682_1_, p_217682_2_);
   }

   public EntityAIDebugRenderer(Minecraft p_i50977_1_) {
      this.minecraft = p_i50977_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getMainCamera();
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(activerenderinfo.getPosition().x, 0.0D, activerenderinfo.getPosition().z);
      this.goalSelectors.forEach((p_217683_1_, p_217683_2_) -> {
         for(int i = 0; i < p_217683_2_.size(); ++i) {
            EntityAIDebugRenderer.Entry entityaidebugrenderer$entry = p_217683_2_.get(i);
            if (blockpos.closerThan(entityaidebugrenderer$entry.pos, 160.0D)) {
               double d0 = (double)entityaidebugrenderer$entry.pos.getX() + 0.5D;
               double d1 = (double)entityaidebugrenderer$entry.pos.getY() + 2.0D + (double)i * 0.25D;
               double d2 = (double)entityaidebugrenderer$entry.pos.getZ() + 0.5D;
               int j = entityaidebugrenderer$entry.isRunning ? -16711936 : -3355444;
               DebugRenderer.renderFloatingText(entityaidebugrenderer$entry.name, d0, d1, d2, j);
            }
         }

      });
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Entry {
      public final BlockPos pos;
      public final int priority;
      public final String name;
      public final boolean isRunning;

      public Entry(BlockPos p_i50834_1_, int p_i50834_2_, String p_i50834_3_, boolean p_i50834_4_) {
         this.pos = p_i50834_1_;
         this.priority = p_i50834_2_;
         this.name = p_i50834_3_;
         this.isRunning = p_i50834_4_;
      }
   }
}
