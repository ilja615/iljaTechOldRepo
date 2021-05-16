package net.minecraft.world.spawner;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class PhantomSpawner implements ISpecialSpawner {
   private int nextTick;

   public int tick(ServerWorld p_230253_1_, boolean p_230253_2_, boolean p_230253_3_) {
      if (!p_230253_2_) {
         return 0;
      } else if (!p_230253_1_.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
         return 0;
      } else {
         Random random = p_230253_1_.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += (60 + random.nextInt(60)) * 20;
            if (p_230253_1_.getSkyDarken() < 5 && p_230253_1_.dimensionType().hasSkyLight()) {
               return 0;
            } else {
               int i = 0;

               for(PlayerEntity playerentity : p_230253_1_.players()) {
                  if (!playerentity.isSpectator()) {
                     BlockPos blockpos = playerentity.blockPosition();
                     if (!p_230253_1_.dimensionType().hasSkyLight() || blockpos.getY() >= p_230253_1_.getSeaLevel() && p_230253_1_.canSeeSky(blockpos)) {
                        DifficultyInstance difficultyinstance = p_230253_1_.getCurrentDifficultyAt(blockpos);
                        if (difficultyinstance.isHarderThan(random.nextFloat() * 3.0F)) {
                           ServerStatisticsManager serverstatisticsmanager = ((ServerPlayerEntity)playerentity).getStats();
                           int j = MathHelper.clamp(serverstatisticsmanager.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                           int k = 24000;
                           if (random.nextInt(j) >= 72000) {
                              BlockPos blockpos1 = blockpos.above(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                              BlockState blockstate = p_230253_1_.getBlockState(blockpos1);
                              FluidState fluidstate = p_230253_1_.getFluidState(blockpos1);
                              if (WorldEntitySpawner.isValidEmptySpawnBlock(p_230253_1_, blockpos1, blockstate, fluidstate, EntityType.PHANTOM)) {
                                 ILivingEntityData ilivingentitydata = null;
                                 int l = 1 + random.nextInt(difficultyinstance.getDifficulty().getId() + 1);

                                 for(int i1 = 0; i1 < l; ++i1) {
                                    PhantomEntity phantomentity = EntityType.PHANTOM.create(p_230253_1_);
                                    phantomentity.moveTo(blockpos1, 0.0F, 0.0F);
                                    ilivingentitydata = phantomentity.finalizeSpawn(p_230253_1_, difficultyinstance, SpawnReason.NATURAL, ilivingentitydata, (CompoundNBT)null);
                                    p_230253_1_.addFreshEntityWithPassengers(phantomentity);
                                 }

                                 i += l;
                              }
                           }
                        }
                     }
                  }
               }

               return i;
            }
         }
      }
   }
}
