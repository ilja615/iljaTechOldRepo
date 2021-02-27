package ilja615.iljatech.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class SteamParticle extends SimpleAnimatedParticle
{
    protected SteamParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge)
    {
        super(world, x, y, z, spriteWithAge, (float) motionY);
        this.motionX = motionX + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.motionX = motionX + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.motionZ = motionZ + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.particleScale += 0.2f;
        this.maxAge = 32 + this.rand.nextInt(16);
        this.selectSpriteWithAge(spriteWithAge);
    }


    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
            this.particleScale += 0.003f;
            this.move(this.motionX, this.motionY, this.motionZ);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SteamParticle steamParticle = new SteamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            steamParticle.setColor(1.0f, 1.0f, 1.0f);
            return steamParticle;
        }
    }
}
