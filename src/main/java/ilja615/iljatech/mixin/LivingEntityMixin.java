package ilja615.iljatech.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    private static final UUID SLOW_FALLING_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA");
    private static final AttributeModifier SLOW_FALLING = new AttributeModifier(SLOW_FALLING_ID, "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

    @Redirect(method = "travel", at = @At (value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void travel(LivingEntity instance, Vec3 orig)
    {
        if (instance.isEffectiveAi() || instance.isControlledByLocalInstance()) {
            FluidState fluidstate = instance.level.getFluidState(instance.blockPosition());
            if ((!instance.isInWater() && !instance.isInLava() && !(instance.isInFluidType(fluidstate) && fluidstate.getFluidType() != net.minecraftforge.common.ForgeMod.LAVA_TYPE.get())) || instance.canStandOnFluid(fluidstate))
            {
                if (!(instance.isFallFlying()))
                {
                    float f2 = Blocks.PACKED_ICE.getFriction();
                    float f3 = instance.isOnGround() ? f2 * 0.91F : 0.91F;
                    Vec3 vec35 = instance.handleRelativeFrictionAndCalculateMovement(orig, f2);
                    double d2 = vec35.y;
                    AttributeInstance gravity = instance.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
                    boolean flag = instance.getDeltaMovement().y <= 0.0D;
                    if (flag && instance.hasEffect(MobEffects.SLOW_FALLING))
                    {
                        if (!gravity.hasModifier(SLOW_FALLING)) gravity.addTransientModifier(SLOW_FALLING);
                        instance.resetFallDistance();
                    } else if (gravity.hasModifier(SLOW_FALLING))
                    {
                        gravity.removeModifier(SLOW_FALLING);
                    }
                    double d0 = gravity.getValue();
                    if (instance.hasEffect(MobEffects.LEVITATION))
                    {
                        d2 += (0.05D * (double)(instance.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec35.y) * 0.2D;
                        instance.resetFallDistance();
                    } else if (instance.level.isClientSide && !instance.level.hasChunkAt(new BlockPos(instance.position().x, instance.getBoundingBox().minY - 0.5000001D, instance.position().z)))
                    {
                        if (instance.getY() > (double)instance.level.getMinBuildHeight())
                        {
                            d2 = -0.1D;
                        } else
                        {
                            d2 = 0.0D;
                        }
                    } else if (!instance.isNoGravity())
                    {
                        d2 -= d0;
                    }
                    instance.setDeltaMovement(vec35.x * (double)f3, d2 * (double)0.98F, vec35.z * (double)f3);
                }
            }
        }
    }
}
