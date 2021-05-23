package ilja615.iljatech.particles;

import net.minecraft.block.Blocks;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class StarParticle extends SpriteTexturedParticle
{
    protected StarParticle(ClientWorld world, double x, double y, double z)
    {
        super(world, x, y, z);
        this.lifetime = 35;
    }


    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.xd = 0.15d * Math.cos(this.age / 5.0d);
            this.yd = 0.03d * Math.cos(this.age / 3.0d);
            this.zd = 0.15d * Math.sin(this.age / 5.0d);
            this.move(this.xd, this.yd, this.zd);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite p_i232340_1_) {
            this.sprite = p_i232340_1_;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            StarParticle starParticle = new StarParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
            starParticle.pickSprite(this.sprite);
            starParticle.setColor(1.0F, 1.0F, 1.0F);
            return starParticle;
        }
    }
}
