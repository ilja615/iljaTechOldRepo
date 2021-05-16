package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.storage.IWorldInfo;

public interface IWorld extends IBiomeReader, IDayTimeReader {
   default long dayTime() {
      return this.getLevelData().getDayTime();
   }

   ITickList<Block> getBlockTicks();

   ITickList<Fluid> getLiquidTicks();

   IWorldInfo getLevelData();

   DifficultyInstance getCurrentDifficultyAt(BlockPos p_175649_1_);

   default Difficulty getDifficulty() {
      return this.getLevelData().getDifficulty();
   }

   AbstractChunkProvider getChunkSource();

   default boolean hasChunk(int p_217354_1_, int p_217354_2_) {
      return this.getChunkSource().hasChunk(p_217354_1_, p_217354_2_);
   }

   Random getRandom();

   default void blockUpdated(BlockPos p_230547_1_, Block p_230547_2_) {
   }

   void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_);

   void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_);

   void levelEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_);

   default int getHeight() {
      return this.dimensionType().logicalHeight();
   }

   default void levelEvent(int p_217379_1_, BlockPos p_217379_2_, int p_217379_3_) {
      this.levelEvent((PlayerEntity)null, p_217379_1_, p_217379_2_, p_217379_3_);
   }
}
