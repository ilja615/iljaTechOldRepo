package net.minecraft.world.spawner;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;

public class WanderingTraderSpawner implements ISpecialSpawner {
   private final Random random = new Random();
   private final IServerWorldInfo serverLevelData;
   private int tickDelay;
   private int spawnDelay;
   private int spawnChance;

   public WanderingTraderSpawner(IServerWorldInfo p_i231576_1_) {
      this.serverLevelData = p_i231576_1_;
      this.tickDelay = 1200;
      this.spawnDelay = p_i231576_1_.getWanderingTraderSpawnDelay();
      this.spawnChance = p_i231576_1_.getWanderingTraderSpawnChance();
      if (this.spawnDelay == 0 && this.spawnChance == 0) {
         this.spawnDelay = 24000;
         p_i231576_1_.setWanderingTraderSpawnDelay(this.spawnDelay);
         this.spawnChance = 25;
         p_i231576_1_.setWanderingTraderSpawnChance(this.spawnChance);
      }

   }

   public int tick(ServerWorld p_230253_1_, boolean p_230253_2_, boolean p_230253_3_) {
      if (!p_230253_1_.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
         return 0;
      } else if (--this.tickDelay > 0) {
         return 0;
      } else {
         this.tickDelay = 1200;
         this.spawnDelay -= 1200;
         this.serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
         if (this.spawnDelay > 0) {
            return 0;
         } else {
            this.spawnDelay = 24000;
            if (!p_230253_1_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
               return 0;
            } else {
               int i = this.spawnChance;
               this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
               this.serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
               if (this.random.nextInt(100) > i) {
                  return 0;
               } else if (this.spawn(p_230253_1_)) {
                  this.spawnChance = 25;
                  return 1;
               } else {
                  return 0;
               }
            }
         }
      }
   }

   private boolean spawn(ServerWorld p_234562_1_) {
      PlayerEntity playerentity = p_234562_1_.getRandomPlayer();
      if (playerentity == null) {
         return true;
      } else if (this.random.nextInt(10) != 0) {
         return false;
      } else {
         BlockPos blockpos = playerentity.blockPosition();
         int i = 48;
         PointOfInterestManager pointofinterestmanager = p_234562_1_.getPoiManager();
         Optional<BlockPos> optional = pointofinterestmanager.find(PointOfInterestType.MEETING.getPredicate(), (p_221241_0_) -> {
            return true;
         }, blockpos, 48, PointOfInterestManager.Status.ANY);
         BlockPos blockpos1 = optional.orElse(blockpos);
         BlockPos blockpos2 = this.findSpawnPositionNear(p_234562_1_, blockpos1, 48);
         if (blockpos2 != null && this.hasEnoughSpace(p_234562_1_, blockpos2)) {
            if (p_234562_1_.getBiomeName(blockpos2).equals(Optional.of(Biomes.THE_VOID))) {
               return false;
            }

            WanderingTraderEntity wanderingtraderentity = EntityType.WANDERING_TRADER.spawn(p_234562_1_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos2, SpawnReason.EVENT, false, false);
            if (wanderingtraderentity != null) {
               for(int j = 0; j < 2; ++j) {
                  this.tryToSpawnLlamaFor(p_234562_1_, wanderingtraderentity, 4);
               }

               this.serverLevelData.setWanderingTraderId(wanderingtraderentity.getUUID());
               wanderingtraderentity.setDespawnDelay(48000);
               wanderingtraderentity.setWanderTarget(blockpos1);
               wanderingtraderentity.restrictTo(blockpos1, 16);
               return true;
            }
         }

         return false;
      }
   }

   private void tryToSpawnLlamaFor(ServerWorld p_242373_1_, WanderingTraderEntity p_242373_2_, int p_242373_3_) {
      BlockPos blockpos = this.findSpawnPositionNear(p_242373_1_, p_242373_2_.blockPosition(), p_242373_3_);
      if (blockpos != null) {
         TraderLlamaEntity traderllamaentity = EntityType.TRADER_LLAMA.spawn(p_242373_1_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos, SpawnReason.EVENT, false, false);
         if (traderllamaentity != null) {
            traderllamaentity.setLeashedTo(p_242373_2_, true);
         }
      }
   }

   @Nullable
   private BlockPos findSpawnPositionNear(IWorldReader p_234561_1_, BlockPos p_234561_2_, int p_234561_3_) {
      BlockPos blockpos = null;

      for(int i = 0; i < 10; ++i) {
         int j = p_234561_2_.getX() + this.random.nextInt(p_234561_3_ * 2) - p_234561_3_;
         int k = p_234561_2_.getZ() + this.random.nextInt(p_234561_3_ * 2) - p_234561_3_;
         int l = p_234561_1_.getHeight(Heightmap.Type.WORLD_SURFACE, j, k);
         BlockPos blockpos1 = new BlockPos(j, l, k);
         if (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_234561_1_, blockpos1, EntityType.WANDERING_TRADER)) {
            blockpos = blockpos1;
            break;
         }
      }

      return blockpos;
   }

   private boolean hasEnoughSpace(IBlockReader p_234560_1_, BlockPos p_234560_2_) {
      for(BlockPos blockpos : BlockPos.betweenClosed(p_234560_2_, p_234560_2_.offset(1, 2, 1))) {
         if (!p_234560_1_.getBlockState(blockpos).getCollisionShape(p_234560_1_, blockpos).isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
