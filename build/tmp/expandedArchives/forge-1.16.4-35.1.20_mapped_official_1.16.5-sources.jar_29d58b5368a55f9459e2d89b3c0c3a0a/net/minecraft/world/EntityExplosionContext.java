package net.minecraft.world;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public class EntityExplosionContext extends ExplosionContext {
   private final Entity source;

   public EntityExplosionContext(Entity p_i231609_1_) {
      this.source = p_i231609_1_;
   }

   public Optional<Float> getBlockExplosionResistance(Explosion p_230312_1_, IBlockReader p_230312_2_, BlockPos p_230312_3_, BlockState p_230312_4_, FluidState p_230312_5_) {
      return super.getBlockExplosionResistance(p_230312_1_, p_230312_2_, p_230312_3_, p_230312_4_, p_230312_5_).map((p_234890_6_) -> {
         return this.source.getBlockExplosionResistance(p_230312_1_, p_230312_2_, p_230312_3_, p_230312_4_, p_230312_5_, p_234890_6_);
      });
   }

   public boolean shouldBlockExplode(Explosion p_230311_1_, IBlockReader p_230311_2_, BlockPos p_230311_3_, BlockState p_230311_4_, float p_230311_5_) {
      return this.source.shouldBlockExplode(p_230311_1_, p_230311_2_, p_230311_3_, p_230311_4_, p_230311_5_);
   }
}
