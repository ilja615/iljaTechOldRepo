package ilja615.iljatech.mixin;

import ilja615.iljatech.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
    @Redirect(method = "travel", at = @At (value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    private float friction(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            if (livingEntity.hasEffect(ModEffects.SLIPPERY.get()))
            {
                return switch (livingEntity.getEffect(ModEffects.SLIPPERY.get()).getAmplifier())
                        {
                            case 0 -> Blocks.PACKED_ICE.getFriction();
                            case 1 -> Blocks.BLUE_ICE.getFriction();
                            case 2 -> 0.99f;
                            default -> instance.getFriction(levelReader, blockPos, entity);
                        };
            }
        }
        
        return instance.getFriction(levelReader, blockPos, entity);
    }
}
