package net.minecraft.client.particle;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BarrierParticle extends SpriteTexturedParticle {
   private BarrierParticle(ClientWorld p_i232343_1_, double p_i232343_2_, double p_i232343_4_, double p_i232343_6_, IItemProvider p_i232343_8_) {
      super(p_i232343_1_, p_i232343_2_, p_i232343_4_, p_i232343_6_);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getParticleIcon(p_i232343_8_));
      this.gravity = 0.0F;
      this.lifetime = 80;
      this.hasPhysics = false;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.TERRAIN_SHEET;
   }

   public float getQuadSize(float p_217561_1_) {
      return 0.5F;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BarrierParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Blocks.BARRIER.asItem());
      }
   }
}
