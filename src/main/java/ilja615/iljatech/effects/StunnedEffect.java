package ilja615.iljatech.effects;

import ilja615.iljatech.init.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public class StunnedEffect extends Effect
{
    public StunnedEffect()
    {
        super(EffectType.HARMFUL, 0x2E2E34);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_)
    {
        entity.setDeltaMovement(0.0d, 0.0d, 0.0d);

        if (entity.level.random.nextFloat() < 0.1f) entity.level.addParticle(ModParticles.STAR_PARTICLE.get(), entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()) + 1.0d, entity.getZ() - 0.3d, 0,0,0);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_)
    {
        return true;
    }
}