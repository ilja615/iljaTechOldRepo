package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiggingParticle extends SpriteTexturedParticle {
   private final BlockState blockState;
   private BlockPos pos;
   private final float uo;
   private final float vo;

   public DiggingParticle(ClientWorld p_i232446_1_, double p_i232446_2_, double p_i232446_4_, double p_i232446_6_, double p_i232446_8_, double p_i232446_10_, double p_i232446_12_, BlockState p_i232446_14_) {
      super(p_i232446_1_, p_i232446_2_, p_i232446_4_, p_i232446_6_, p_i232446_8_, p_i232446_10_, p_i232446_12_);
      this.blockState = p_i232446_14_;
      this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(p_i232446_14_));
      this.gravity = 1.0F;
      this.rCol = 0.6F;
      this.gCol = 0.6F;
      this.bCol = 0.6F;
      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.TERRAIN_SHEET;
   }

   public DiggingParticle init(BlockPos p_174846_1_) {
      updateSprite(p_174846_1_);
      this.pos = p_174846_1_;
      if (this.blockState.is(Blocks.GRASS_BLOCK)) {
         return this;
      } else {
         this.multiplyColor(p_174846_1_);
         return this;
      }
   }

   public DiggingParticle init() {
      this.pos = new BlockPos(this.x, this.y, this.z);
      if (this.blockState.is(Blocks.GRASS_BLOCK)) {
         return this;
      } else {
         this.multiplyColor(this.pos);
         return this;
      }
   }

   protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
      int i = Minecraft.getInstance().getBlockColors().getColor(this.blockState, this.level, p_187154_1_, 0);
      this.rCol *= (float)(i >> 16 & 255) / 255.0F;
      this.gCol *= (float)(i >> 8 & 255) / 255.0F;
      this.bCol *= (float)(i & 255) / 255.0F;
   }

   protected float getU0() {
      return this.sprite.getU((double)((this.uo + 1.0F) / 4.0F * 16.0F));
   }

   protected float getU1() {
      return this.sprite.getU((double)(this.uo / 4.0F * 16.0F));
   }

   protected float getV0() {
      return this.sprite.getV((double)(this.vo / 4.0F * 16.0F));
   }

   protected float getV1() {
      return this.sprite.getV((double)((this.vo + 1.0F) / 4.0F * 16.0F));
   }

   public int getLightColor(float p_189214_1_) {
      int i = super.getLightColor(p_189214_1_);
      int j = 0;
      if (this.level.hasChunkAt(this.pos)) {
         j = WorldRenderer.getLightColor(this.level, this.pos);
      }

      return i == 0 ? j : i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      public Particle createParticle(BlockParticleData p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         BlockState blockstate = p_199234_1_.getState();
         return !blockstate.isAir() && !blockstate.is(Blocks.MOVING_PISTON) ? (new DiggingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, blockstate)).init().updateSprite(p_199234_1_.getPos()) : null;
      }
   }

   private Particle updateSprite(BlockPos pos) { //FORGE: we cannot assume that the x y z of the particles match the block pos of the block.
      if (pos != null) // There are cases where we are not able to obtain the correct source pos, and need to fallback to the non-model data version
         this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(blockState, level, pos));
      return this;
   }
}
