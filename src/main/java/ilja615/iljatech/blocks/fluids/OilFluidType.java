package ilja615.iljatech.blocks.fluids;

import com.mojang.math.Vector3f;
import ilja615.iljatech.init.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class OilFluidType extends BaseFluidType
{
    public OilFluidType(ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, int tintColor, Vector3f fogColor, Properties properties)
    {
        super(stillTexture, flowingTexture, overlayTexture, tintColor, fogColor, properties);
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity)
    {
        entity.addEffect(new MobEffectInstance(ModEffects.SLIPPERY.get(), 20*30, 0));
        return super.move(state, entity, movementVector, gravity);
    }
}
