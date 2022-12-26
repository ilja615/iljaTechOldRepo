package ilja615.iljatech.effects;

import ilja615.iljatech.init.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SlipperyEffect extends MobEffect
{
    public SlipperyEffect()
    {
        super(MobEffectCategory.HARMFUL, 0x2E2E34);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_)
    {

    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_)
    {
        return true;
    }
}