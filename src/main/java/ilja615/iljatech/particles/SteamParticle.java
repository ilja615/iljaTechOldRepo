package ilja615.iljatech.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class SteamParticle extends SimpleAnimatedParticle
{
    protected SteamParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet spriteWithAge)
    {
        super(world, x, y, z, spriteWithAge, (float) motionY);
        this.xd = motionX + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.yd = motionY + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.zd = motionZ + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.quadSize += 0.2f;
        this.lifetime = 32 + this.random.nextInt(16);
        this.setSpriteFromAge(spriteWithAge);
    }


    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            this.setSpriteFromAge(this.sprites);
            this.quadSize += 0.003f;
            this.move(this.xd, this.yd, this.zd);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SteamParticle steamParticle = new SteamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            steamParticle.setColor(1.0f, 1.0f, 1.0f);
            return steamParticle;
        }
    }
}
