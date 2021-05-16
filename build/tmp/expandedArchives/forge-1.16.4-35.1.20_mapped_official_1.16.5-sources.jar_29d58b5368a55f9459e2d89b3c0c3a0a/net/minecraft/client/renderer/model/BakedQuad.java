package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuad implements net.minecraftforge.client.model.pipeline.IVertexProducer {
   protected final int[] vertices;
   protected final int tintIndex;
   protected final Direction direction;
   protected final TextureAtlasSprite sprite;
   private final boolean shade;

   public BakedQuad(int[] p_i232466_1_, int p_i232466_2_, Direction p_i232466_3_, TextureAtlasSprite p_i232466_4_, boolean p_i232466_5_) {
      this.vertices = p_i232466_1_;
      this.tintIndex = p_i232466_2_;
      this.direction = p_i232466_3_;
      this.sprite = p_i232466_4_;
      this.shade = p_i232466_5_;
   }

   public int[] getVertices() {
      return this.vertices;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public Direction getDirection() {
      return this.direction;
   }

   @Override
   public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer) {
      net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
   }

   public TextureAtlasSprite getSprite() {
      return sprite;
   }

   public boolean isShade() {
      return this.shade;
   }
}
