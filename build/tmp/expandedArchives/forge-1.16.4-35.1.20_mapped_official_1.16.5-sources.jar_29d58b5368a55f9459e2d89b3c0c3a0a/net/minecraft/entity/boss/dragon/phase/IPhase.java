package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public interface IPhase {
   boolean isSitting();

   void doClientTick();

   void doServerTick();

   void onCrystalDestroyed(EnderCrystalEntity p_188655_1_, BlockPos p_188655_2_, DamageSource p_188655_3_, @Nullable PlayerEntity p_188655_4_);

   void begin();

   void end();

   float getFlySpeed();

   float getTurnSpeed();

   PhaseType<? extends IPhase> getPhase();

   @Nullable
   Vector3d getFlyTargetLocation();

   float onHurt(DamageSource p_221113_1_, float p_221113_2_);
}
