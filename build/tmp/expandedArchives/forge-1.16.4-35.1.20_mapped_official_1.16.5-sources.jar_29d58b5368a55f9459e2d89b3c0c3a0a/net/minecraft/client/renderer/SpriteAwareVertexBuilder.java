package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteAwareVertexBuilder implements IVertexBuilder {
   private final IVertexBuilder delegate;
   private final TextureAtlasSprite sprite;

   public SpriteAwareVertexBuilder(IVertexBuilder p_i225999_1_, TextureAtlasSprite p_i225999_2_) {
      this.delegate = p_i225999_1_;
      this.sprite = p_i225999_2_;
   }

   public IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_) {
      return this.delegate.vertex(p_225582_1_, p_225582_3_, p_225582_5_);
   }

   public IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_) {
      return this.delegate.color(p_225586_1_, p_225586_2_, p_225586_3_, p_225586_4_);
   }

   public IVertexBuilder uv(float p_225583_1_, float p_225583_2_) {
      return this.delegate.uv(this.sprite.getU((double)(p_225583_1_ * 16.0F)), this.sprite.getV((double)(p_225583_2_ * 16.0F)));
   }

   public IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_) {
      return this.delegate.overlayCoords(p_225585_1_, p_225585_2_);
   }

   public IVertexBuilder uv2(int p_225587_1_, int p_225587_2_) {
      return this.delegate.uv2(p_225587_1_, p_225587_2_);
   }

   public IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_) {
      return this.delegate.normal(p_225584_1_, p_225584_2_, p_225584_3_);
   }

   public void endVertex() {
      this.delegate.endVertex();
   }

   public void vertex(float p_225588_1_, float p_225588_2_, float p_225588_3_, float p_225588_4_, float p_225588_5_, float p_225588_6_, float p_225588_7_, float p_225588_8_, float p_225588_9_, int p_225588_10_, int p_225588_11_, float p_225588_12_, float p_225588_13_, float p_225588_14_) {
      this.delegate.vertex(p_225588_1_, p_225588_2_, p_225588_3_, p_225588_4_, p_225588_5_, p_225588_6_, p_225588_7_, this.sprite.getU((double)(p_225588_8_ * 16.0F)), this.sprite.getV((double)(p_225588_9_ * 16.0F)), p_225588_10_, p_225588_11_, p_225588_12_, p_225588_13_, p_225588_14_);
   }
}
