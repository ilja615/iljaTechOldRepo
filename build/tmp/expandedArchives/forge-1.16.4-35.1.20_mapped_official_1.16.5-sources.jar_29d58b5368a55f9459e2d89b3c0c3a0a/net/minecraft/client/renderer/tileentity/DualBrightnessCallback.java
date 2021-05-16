package net.minecraft.client.renderer.tileentity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DualBrightnessCallback<S extends TileEntity> implements TileEntityMerger.ICallback<S, Int2IntFunction> {
   public Int2IntFunction acceptDouble(S p_225539_1_, S p_225539_2_) {
      return (p_228860_2_) -> {
         int i = WorldRenderer.getLightColor(p_225539_1_.getLevel(), p_225539_1_.getBlockPos());
         int j = WorldRenderer.getLightColor(p_225539_2_.getLevel(), p_225539_2_.getBlockPos());
         int k = LightTexture.block(i);
         int l = LightTexture.block(j);
         int i1 = LightTexture.sky(i);
         int j1 = LightTexture.sky(j);
         return LightTexture.pack(Math.max(k, l), Math.max(i1, j1));
      };
   }

   public Int2IntFunction acceptSingle(S p_225538_1_) {
      return (p_228861_0_) -> {
         return p_228861_0_;
      };
   }

   public Int2IntFunction acceptNone() {
      return (p_228859_0_) -> {
         return p_228859_0_;
      };
   }
}
