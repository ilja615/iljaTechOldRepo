package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityRenderer<T extends TileEntity> {
   protected final TileEntityRendererDispatcher renderer;

   public TileEntityRenderer(TileEntityRendererDispatcher p_i226006_1_) {
      this.renderer = p_i226006_1_;
   }

   public abstract void render(T p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_);

   public boolean shouldRenderOffScreen(T p_188185_1_) {
      return false;
   }
}
