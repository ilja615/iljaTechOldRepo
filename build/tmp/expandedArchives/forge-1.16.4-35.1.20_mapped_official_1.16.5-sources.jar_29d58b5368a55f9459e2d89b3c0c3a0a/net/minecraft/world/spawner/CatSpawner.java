package net.minecraft.world.spawner;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class CatSpawner implements ISpecialSpawner {
   private int nextTick;

   public int tick(ServerWorld p_230253_1_, boolean p_230253_2_, boolean p_230253_3_) {
      if (p_230253_3_ && p_230253_1_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick = 1200;
            PlayerEntity playerentity = p_230253_1_.getRandomPlayer();
            if (playerentity == null) {
               return 0;
            } else {
               Random random = p_230253_1_.random;
               int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               BlockPos blockpos = playerentity.blockPosition().offset(i, 0, j);
               if (!p_230253_1_.hasChunksAt(blockpos.getX() - 10, blockpos.getY() - 10, blockpos.getZ() - 10, blockpos.getX() + 10, blockpos.getY() + 10, blockpos.getZ() + 10)) {
                  return 0;
               } else {
                  if (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_230253_1_, blockpos, EntityType.CAT)) {
                     if (p_230253_1_.isCloseToVillage(blockpos, 2)) {
                        return this.spawnInVillage(p_230253_1_, blockpos);
                     }

                     if (p_230253_1_.structureFeatureManager().getStructureAt(blockpos, true, Structure.SWAMP_HUT).isValid()) {
                        return this.spawnInHut(p_230253_1_, blockpos);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int spawnInVillage(ServerWorld p_221121_1_, BlockPos p_221121_2_) {
      int i = 48;
      if (p_221121_1_.getPoiManager().getCountInRange(PointOfInterestType.HOME.getPredicate(), p_221121_2_, 48, PointOfInterestManager.Status.IS_OCCUPIED) > 4L) {
         List<CatEntity> list = p_221121_1_.getEntitiesOfClass(CatEntity.class, (new AxisAlignedBB(p_221121_2_)).inflate(48.0D, 8.0D, 48.0D));
         if (list.size() < 5) {
            return this.spawnCat(p_221121_2_, p_221121_1_);
         }
      }

      return 0;
   }

   private int spawnInHut(ServerWorld p_221123_1_, BlockPos p_221123_2_) {
      int i = 16;
      List<CatEntity> list = p_221123_1_.getEntitiesOfClass(CatEntity.class, (new AxisAlignedBB(p_221123_2_)).inflate(16.0D, 8.0D, 16.0D));
      return list.size() < 1 ? this.spawnCat(p_221123_2_, p_221123_1_) : 0;
   }

   private int spawnCat(BlockPos p_221122_1_, ServerWorld p_221122_2_) {
      CatEntity catentity = EntityType.CAT.create(p_221122_2_);
      if (catentity == null) {
         return 0;
      } else {
         catentity.finalizeSpawn(p_221122_2_, p_221122_2_.getCurrentDifficultyAt(p_221122_1_), SpawnReason.NATURAL, (ILivingEntityData)null, (CompoundNBT)null);
         catentity.moveTo(p_221122_1_, 0.0F, 0.0F);
         p_221122_2_.addFreshEntityWithPassengers(catentity);
         return 1;
      }
   }
}
