package ilja615.iljatech.effects;

import ilja615.iljatech.init.ModParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public class StunnedEffect extends MobEffect
{
    public StunnedEffect()
    {
        super(MobEffectCategory.HARMFUL, 0x2E2E34);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_)
    {
        entity.setDeltaMovement(0.0d, 0.0d, 0.0d);

        if (entity.level.random.nextFloat() < 0.22f && !entity.level.isClientSide())
        {
            ((ServerLevel)entity.level).sendParticles(ModParticles.STAR_PARTICLE.get(), entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()) + 1.0d, entity.getZ() - 0.3d, 1, 0.0D, 0.0D, 0.0D, 1.0D);
        }
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_)
    {
        return true;
    }
}