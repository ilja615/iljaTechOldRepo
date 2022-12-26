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
        super(MobEffectCategory.NEUTRAL, 0xD8E5F5);
    }
}