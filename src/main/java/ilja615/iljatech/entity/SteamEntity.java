package ilja615.iljatech.entity;

import ilja615.iljatech.init.ModParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class SteamEntity extends AbstractGasEntity
{
    public SteamEntity(EntityType<? extends AbstractGasEntity> entityTypeIn, Level worldIn)
    {
        super(entityTypeIn, worldIn);
    }

    @Override
    SimpleParticleType getParticle()
    {
        return ModParticles.STEAM_PARTICLE.get();
    }

    @Override
    int maxLifeTime()
    {
        return 500;
    }
}
