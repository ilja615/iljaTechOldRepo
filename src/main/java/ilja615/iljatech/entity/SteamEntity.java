package ilja615.iljatech.entity;

import ilja615.iljatech.init.ModParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class SteamEntity extends AbstractGasEntity
{
    public SteamEntity(EntityType<? extends AbstractGasEntity> entityTypeIn, World worldIn)
    {
        super(entityTypeIn, worldIn);
    }

    @Override
    BasicParticleType getParticle()
    {
        return ModParticles.STEAM_PARTICLE.get();
    }

    @Override
    int maxLifeTime()
    {
        return 500;
    }
}
