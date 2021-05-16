package net.minecraft.world.spawner;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeMagnifier;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldEntitySpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MAGIC_NUMBER = (int)Math.pow(17.0D, 2.0D);
   private static final EntityClassification[] SPAWNING_CATEGORIES = Stream.of(EntityClassification.values()).filter((p_234965_0_) -> {
      return p_234965_0_ != EntityClassification.MISC;
   }).toArray((p_234963_0_) -> {
      return new EntityClassification[p_234963_0_];
   });

   public static WorldEntitySpawner.EntityDensityManager createState(int p_234964_0_, Iterable<Entity> p_234964_1_, WorldEntitySpawner.IInitialDensityAdder p_234964_2_) {
      MobDensityTracker mobdensitytracker = new MobDensityTracker();
      Object2IntOpenHashMap<EntityClassification> object2intopenhashmap = new Object2IntOpenHashMap<>();
      Iterator iterator = p_234964_1_.iterator();

      while(true) {
         Entity entity;
         MobEntity mobentity;
         do {
            if (!iterator.hasNext()) {
               return new WorldEntitySpawner.EntityDensityManager(p_234964_0_, object2intopenhashmap, mobdensitytracker);
            }

            entity = (Entity)iterator.next();
            if (!(entity instanceof MobEntity)) {
               break;
            }

            mobentity = (MobEntity)entity;
         } while(mobentity.isPersistenceRequired() || mobentity.requiresCustomPersistence());

         final Entity entity_f = entity;
         EntityClassification entityclassification = entity.getClassification(true);
         if (entityclassification != EntityClassification.MISC) {
            BlockPos blockpos = entity.blockPosition();
            long i = ChunkPos.asLong(blockpos.getX() >> 4, blockpos.getZ() >> 4);
            p_234964_2_.query(i, (p_234971_5_) -> {
               MobSpawnInfo.SpawnCosts mobspawninfo$spawncosts = getRoughBiome(blockpos, p_234971_5_).getMobSettings().getMobSpawnCost(entity_f.getType());
               if (mobspawninfo$spawncosts != null) {
                  mobdensitytracker.addCharge(entity_f.blockPosition(), mobspawninfo$spawncosts.getCharge());
               }

               object2intopenhashmap.addTo(entityclassification, 1);
            });
         }
      }
   }

   private static Biome getRoughBiome(BlockPos p_234980_0_, IChunk p_234980_1_) {
      return DefaultBiomeMagnifier.INSTANCE.getBiome(0L, p_234980_0_.getX(), p_234980_0_.getY(), p_234980_0_.getZ(), p_234980_1_.getBiomes());
   }

   public static void spawnForChunk(ServerWorld p_234979_0_, Chunk p_234979_1_, WorldEntitySpawner.EntityDensityManager p_234979_2_, boolean p_234979_3_, boolean p_234979_4_, boolean p_234979_5_) {
      p_234979_0_.getProfiler().push("spawner");

      for(EntityClassification entityclassification : SPAWNING_CATEGORIES) {
         if ((p_234979_3_ || !entityclassification.isFriendly()) && (p_234979_4_ || entityclassification.isFriendly()) && (p_234979_5_ || !entityclassification.isPersistent()) && p_234979_2_.canSpawnForCategory(entityclassification)) {
            spawnCategoryForChunk(entityclassification, p_234979_0_, p_234979_1_, (p_234969_1_, p_234969_2_, p_234969_3_) -> {
               return p_234979_2_.canSpawn(p_234969_1_, p_234969_2_, p_234969_3_);
            }, (p_234970_1_, p_234970_2_) -> {
               p_234979_2_.afterSpawn(p_234970_1_, p_234970_2_);
            });
         }
      }

      p_234979_0_.getProfiler().pop();
   }

   public static void spawnCategoryForChunk(EntityClassification p_234967_0_, ServerWorld p_234967_1_, Chunk p_234967_2_, WorldEntitySpawner.IDensityCheck p_234967_3_, WorldEntitySpawner.IOnSpawnDensityAdder p_234967_4_) {
      BlockPos blockpos = getRandomPosWithin(p_234967_1_, p_234967_2_);
      if (blockpos.getY() >= 1) {
         spawnCategoryForPosition(p_234967_0_, p_234967_1_, p_234967_2_, blockpos, p_234967_3_, p_234967_4_);
      }
   }

   public static void spawnCategoryForPosition(EntityClassification p_234966_0_, ServerWorld p_234966_1_, IChunk p_234966_2_, BlockPos p_234966_3_, WorldEntitySpawner.IDensityCheck p_234966_4_, WorldEntitySpawner.IOnSpawnDensityAdder p_234966_5_) {
      StructureManager structuremanager = p_234966_1_.structureFeatureManager();
      ChunkGenerator chunkgenerator = p_234966_1_.getChunkSource().getGenerator();
      int i = p_234966_3_.getY();
      BlockState blockstate = p_234966_2_.getBlockState(p_234966_3_);
      if (!blockstate.isRedstoneConductor(p_234966_2_, p_234966_3_)) {
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         int j = 0;

         for(int k = 0; k < 3; ++k) {
            int l = p_234966_3_.getX();
            int i1 = p_234966_3_.getZ();
            int j1 = 6;
            MobSpawnInfo.Spawners mobspawninfo$spawners = null;
            ILivingEntityData ilivingentitydata = null;
            int k1 = MathHelper.ceil(p_234966_1_.random.nextFloat() * 4.0F);
            int l1 = 0;

            for(int i2 = 0; i2 < k1; ++i2) {
               l += p_234966_1_.random.nextInt(6) - p_234966_1_.random.nextInt(6);
               i1 += p_234966_1_.random.nextInt(6) - p_234966_1_.random.nextInt(6);
               blockpos$mutable.set(l, i, i1);
               double d0 = (double)l + 0.5D;
               double d1 = (double)i1 + 0.5D;
               PlayerEntity playerentity = p_234966_1_.getNearestPlayer(d0, (double)i, d1, -1.0D, false);
               if (playerentity != null) {
                  double d2 = playerentity.distanceToSqr(d0, (double)i, d1);
                  if (isRightDistanceToPlayerAndSpawnPoint(p_234966_1_, p_234966_2_, blockpos$mutable, d2)) {
                     if (mobspawninfo$spawners == null) {
                        mobspawninfo$spawners = getRandomSpawnMobAt(p_234966_1_, structuremanager, chunkgenerator, p_234966_0_, p_234966_1_.random, blockpos$mutable);
                        if (mobspawninfo$spawners == null) {
                           break;
                        }

                        k1 = mobspawninfo$spawners.minCount + p_234966_1_.random.nextInt(1 + mobspawninfo$spawners.maxCount - mobspawninfo$spawners.minCount);
                     }

                     if (isValidSpawnPostitionForType(p_234966_1_, p_234966_0_, structuremanager, chunkgenerator, mobspawninfo$spawners, blockpos$mutable, d2) && p_234966_4_.test(mobspawninfo$spawners.type, blockpos$mutable, p_234966_2_)) {
                        MobEntity mobentity = getMobForSpawn(p_234966_1_, mobspawninfo$spawners.type);
                        if (mobentity == null) {
                           return;
                        }

                        mobentity.moveTo(d0, (double)i, d1, p_234966_1_.random.nextFloat() * 360.0F, 0.0F);
                        int canSpawn = net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobentity, p_234966_1_, d0, i, d1, null, SpawnReason.NATURAL);
                        if (canSpawn != -1 && (canSpawn == 1 || isValidPositionForMob(p_234966_1_, mobentity, d2))) {
                           if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mobentity, p_234966_1_, (float)d0, (float)i, (float)d1, null, SpawnReason.NATURAL))
                           ilivingentitydata = mobentity.finalizeSpawn(p_234966_1_, p_234966_1_.getCurrentDifficultyAt(mobentity.blockPosition()), SpawnReason.NATURAL, ilivingentitydata, (CompoundNBT)null);
                           ++j;
                           ++l1;
                           p_234966_1_.addFreshEntityWithPassengers(mobentity);
                           p_234966_5_.run(mobentity, p_234966_2_);
                           if (j >= net.minecraftforge.event.ForgeEventFactory.getMaxSpawnPackSize(mobentity)) {
                              return;
                           }

                           if (mobentity.isMaxGroupSizeReached(l1)) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerWorld p_234978_0_, IChunk p_234978_1_, BlockPos.Mutable p_234978_2_, double p_234978_3_) {
      if (p_234978_3_ <= 576.0D) {
         return false;
      } else if (p_234978_0_.getSharedSpawnPos().closerThan(new Vector3d((double)p_234978_2_.getX() + 0.5D, (double)p_234978_2_.getY(), (double)p_234978_2_.getZ() + 0.5D), 24.0D)) {
         return false;
      } else {
         ChunkPos chunkpos = new ChunkPos(p_234978_2_);
         return Objects.equals(chunkpos, p_234978_1_.getPos()) || p_234978_0_.getChunkSource().isEntityTickingChunk(chunkpos);
      }
   }

   private static boolean isValidSpawnPostitionForType(ServerWorld p_234975_0_, EntityClassification p_234975_1_, StructureManager p_234975_2_, ChunkGenerator p_234975_3_, MobSpawnInfo.Spawners p_234975_4_, BlockPos.Mutable p_234975_5_, double p_234975_6_) {
      EntityType<?> entitytype = p_234975_4_.type;
      if (entitytype.getCategory() == EntityClassification.MISC) {
         return false;
      } else if (!entitytype.canSpawnFarFromPlayer() && p_234975_6_ > (double)(entitytype.getCategory().getDespawnDistance() * entitytype.getCategory().getDespawnDistance())) {
         return false;
      } else if (entitytype.canSummon() && canSpawnMobAt(p_234975_0_, p_234975_2_, p_234975_3_, p_234975_1_, p_234975_4_, p_234975_5_)) {
         EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);
         if (!isSpawnPositionOk(entityspawnplacementregistry$placementtype, p_234975_0_, p_234975_5_, entitytype)) {
            return false;
         } else if (!EntitySpawnPlacementRegistry.checkSpawnRules(entitytype, p_234975_0_, SpawnReason.NATURAL, p_234975_5_, p_234975_0_.random)) {
            return false;
         } else {
            return p_234975_0_.noCollision(entitytype.getAABB((double)p_234975_5_.getX() + 0.5D, (double)p_234975_5_.getY(), (double)p_234975_5_.getZ() + 0.5D));
         }
      } else {
         return false;
      }
   }

   @Nullable
   private static MobEntity getMobForSpawn(ServerWorld p_234973_0_, EntityType<?> p_234973_1_) {
      try {
         Entity entity = p_234973_1_.create(p_234973_0_);
         if (!(entity instanceof MobEntity)) {
            throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(p_234973_1_));
         } else {
            return (MobEntity)entity;
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed to create mob", (Throwable)exception);
         return null;
      }
   }

   private static boolean isValidPositionForMob(ServerWorld p_234974_0_, MobEntity p_234974_1_, double p_234974_2_) {
      if (p_234974_2_ > (double)(p_234974_1_.getType().getCategory().getDespawnDistance() * p_234974_1_.getType().getCategory().getDespawnDistance()) && p_234974_1_.removeWhenFarAway(p_234974_2_)) {
         return false;
      } else {
         return p_234974_1_.checkSpawnRules(p_234974_0_, SpawnReason.NATURAL) && p_234974_1_.checkSpawnObstruction(p_234974_0_);
      }
   }

   @Nullable
   private static MobSpawnInfo.Spawners getRandomSpawnMobAt(ServerWorld p_234977_0_, StructureManager p_234977_1_, ChunkGenerator p_234977_2_, EntityClassification p_234977_3_, Random p_234977_4_, BlockPos p_234977_5_) {
      Biome biome = p_234977_0_.getBiome(p_234977_5_);
      if (p_234977_3_ == EntityClassification.WATER_AMBIENT && biome.getBiomeCategory() == Biome.Category.RIVER && p_234977_4_.nextFloat() < 0.98F) {
         return null;
      } else {
         List<MobSpawnInfo.Spawners> list = mobsAt(p_234977_0_, p_234977_1_, p_234977_2_, p_234977_3_, p_234977_5_, biome);
         list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(p_234977_0_, p_234977_3_, p_234977_5_, list);
         return list.isEmpty() ? null : WeightedRandom.getRandomItem(p_234977_4_, list);
      }
   }

   private static boolean canSpawnMobAt(ServerWorld p_234976_0_, StructureManager p_234976_1_, ChunkGenerator p_234976_2_, EntityClassification p_234976_3_, MobSpawnInfo.Spawners p_234976_4_, BlockPos p_234976_5_) {
      return mobsAt(p_234976_0_, p_234976_1_, p_234976_2_, p_234976_3_, p_234976_5_, (Biome)null).contains(p_234976_4_);
   }

   private static List<MobSpawnInfo.Spawners> mobsAt(ServerWorld p_241463_0_, StructureManager p_241463_1_, ChunkGenerator p_241463_2_, EntityClassification p_241463_3_, BlockPos p_241463_4_, @Nullable Biome p_241463_5_) {
      return p_241463_3_ == EntityClassification.MONSTER && p_241463_0_.getBlockState(p_241463_4_.below()).getBlock() == Blocks.NETHER_BRICKS && p_241463_1_.getStructureAt(p_241463_4_, false, Structure.NETHER_BRIDGE).isValid() ? Structure.NETHER_BRIDGE.getSpecialEnemies() : p_241463_2_.getMobsAt(p_241463_5_ != null ? p_241463_5_ : p_241463_0_.getBiome(p_241463_4_), p_241463_1_, p_241463_3_, p_241463_4_);
   }

   private static BlockPos getRandomPosWithin(World p_222262_0_, Chunk p_222262_1_) {
      ChunkPos chunkpos = p_222262_1_.getPos();
      int i = chunkpos.getMinBlockX() + p_222262_0_.random.nextInt(16);
      int j = chunkpos.getMinBlockZ() + p_222262_0_.random.nextInt(16);
      int k = p_222262_1_.getHeight(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
      int l = p_222262_0_.random.nextInt(k + 1);
      return new BlockPos(i, l, j);
   }

   public static boolean isValidEmptySpawnBlock(IBlockReader p_234968_0_, BlockPos p_234968_1_, BlockState p_234968_2_, FluidState p_234968_3_, EntityType<?> p_234968_4_) {
      if (p_234968_2_.isCollisionShapeFullBlock(p_234968_0_, p_234968_1_)) {
         return false;
      } else if (p_234968_2_.isSignalSource()) {
         return false;
      } else if (!p_234968_3_.isEmpty()) {
         return false;
      } else if (p_234968_2_.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
         return false;
      } else {
         return !p_234968_4_.isBlockDangerous(p_234968_2_);
      }
   }

   public static boolean isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType p_209382_0_, IWorldReader p_209382_1_, BlockPos p_209382_2_, @Nullable EntityType<?> p_209382_3_) {
      if (p_209382_0_ == EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS) {
         return true;
      } else if (p_209382_3_ != null && p_209382_1_.getWorldBorder().isWithinBounds(p_209382_2_)) {
         return p_209382_0_.canSpawnAt(p_209382_1_, p_209382_2_, p_209382_3_);
      }
      return false;
   }

   public static boolean canSpawnAtBody(EntitySpawnPlacementRegistry.PlacementType p_209382_0_, IWorldReader p_209382_1_, BlockPos p_209382_2_, @Nullable EntityType<?> p_209382_3_) {
      {
         BlockState blockstate = p_209382_1_.getBlockState(p_209382_2_);
         FluidState fluidstate = p_209382_1_.getFluidState(p_209382_2_);
         BlockPos blockpos = p_209382_2_.above();
         BlockPos blockpos1 = p_209382_2_.below();
         switch(p_209382_0_) {
         case IN_WATER:
            return fluidstate.is(FluidTags.WATER) && p_209382_1_.getFluidState(blockpos1).is(FluidTags.WATER) && !p_209382_1_.getBlockState(blockpos).isRedstoneConductor(p_209382_1_, blockpos);
         case IN_LAVA:
            return fluidstate.is(FluidTags.LAVA);
         case ON_GROUND:
         default:
            BlockState blockstate1 = p_209382_1_.getBlockState(blockpos1);
            if (!blockstate1.canCreatureSpawn(p_209382_1_, blockpos1, p_209382_0_, p_209382_3_)) {
               return false;
            } else {
               return isValidEmptySpawnBlock(p_209382_1_, p_209382_2_, blockstate, fluidstate, p_209382_3_) && isValidEmptySpawnBlock(p_209382_1_, blockpos, p_209382_1_.getBlockState(blockpos), p_209382_1_.getFluidState(blockpos), p_209382_3_);
            }
         }
      }
   }

   public static void spawnMobsForChunkGeneration(IServerWorld p_77191_0_, Biome p_77191_1_, int p_77191_2_, int p_77191_3_, Random p_77191_4_) {
      MobSpawnInfo mobspawninfo = p_77191_1_.getMobSettings();
      List<MobSpawnInfo.Spawners> list = mobspawninfo.getMobs(EntityClassification.CREATURE);
      if (!list.isEmpty()) {
         int i = p_77191_2_ << 4;
         int j = p_77191_3_ << 4;

         while(p_77191_4_.nextFloat() < mobspawninfo.getCreatureProbability()) {
            MobSpawnInfo.Spawners mobspawninfo$spawners = WeightedRandom.getRandomItem(p_77191_4_, list);
            int k = mobspawninfo$spawners.minCount + p_77191_4_.nextInt(1 + mobspawninfo$spawners.maxCount - mobspawninfo$spawners.minCount);
            ILivingEntityData ilivingentitydata = null;
            int l = i + p_77191_4_.nextInt(16);
            int i1 = j + p_77191_4_.nextInt(16);
            int j1 = l;
            int k1 = i1;

            for(int l1 = 0; l1 < k; ++l1) {
               boolean flag = false;

               for(int i2 = 0; !flag && i2 < 4; ++i2) {
                  BlockPos blockpos = getTopNonCollidingPos(p_77191_0_, mobspawninfo$spawners.type, l, i1);
                  if (mobspawninfo$spawners.type.canSummon() && isSpawnPositionOk(EntitySpawnPlacementRegistry.getPlacementType(mobspawninfo$spawners.type), p_77191_0_, blockpos, mobspawninfo$spawners.type)) {
                     float f = mobspawninfo$spawners.type.getWidth();
                     double d0 = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0D - (double)f);
                     double d1 = MathHelper.clamp((double)i1, (double)j + (double)f, (double)j + 16.0D - (double)f);
                     if (!p_77191_0_.noCollision(mobspawninfo$spawners.type.getAABB(d0, (double)blockpos.getY(), d1)) || !EntitySpawnPlacementRegistry.checkSpawnRules(mobspawninfo$spawners.type, p_77191_0_, SpawnReason.CHUNK_GENERATION, new BlockPos(d0, (double)blockpos.getY(), d1), p_77191_0_.getRandom())) {
                        continue;
                     }

                     Entity entity;
                     try {
                        entity = mobspawninfo$spawners.type.create(p_77191_0_.getLevel());
                     } catch (Exception exception) {
                        LOGGER.warn("Failed to create mob", (Throwable)exception);
                        continue;
                     }

                     entity.moveTo(d0, (double)blockpos.getY(), d1, p_77191_4_.nextFloat() * 360.0F, 0.0F);
                     if (entity instanceof MobEntity) {
                        MobEntity mobentity = (MobEntity)entity;
                        if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobentity, p_77191_0_, d0, blockpos.getY(), d1, null, SpawnReason.CHUNK_GENERATION) == -1) continue;
                        if (mobentity.checkSpawnRules(p_77191_0_, SpawnReason.CHUNK_GENERATION) && mobentity.checkSpawnObstruction(p_77191_0_)) {
                           ilivingentitydata = mobentity.finalizeSpawn(p_77191_0_, p_77191_0_.getCurrentDifficultyAt(mobentity.blockPosition()), SpawnReason.CHUNK_GENERATION, ilivingentitydata, (CompoundNBT)null);
                           p_77191_0_.addFreshEntityWithPassengers(mobentity);
                           flag = true;
                        }
                     }
                  }

                  l += p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5);

                  for(i1 += p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5)) {
                     l = j1 + p_77191_4_.nextInt(5) - p_77191_4_.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopNonCollidingPos(IWorldReader p_208498_0_, EntityType<?> p_208498_1_, int p_208498_2_, int p_208498_3_) {
      int i = p_208498_0_.getHeight(EntitySpawnPlacementRegistry.getHeightmapType(p_208498_1_), p_208498_2_, p_208498_3_);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_208498_2_, i, p_208498_3_);
      if (p_208498_0_.dimensionType().hasCeiling()) {
         do {
            blockpos$mutable.move(Direction.DOWN);
         } while(!p_208498_0_.getBlockState(blockpos$mutable).isAir());

         do {
            blockpos$mutable.move(Direction.DOWN);
         } while(p_208498_0_.getBlockState(blockpos$mutable).isAir() && blockpos$mutable.getY() > 0);
      }

      if (EntitySpawnPlacementRegistry.getPlacementType(p_208498_1_) == EntitySpawnPlacementRegistry.PlacementType.ON_GROUND) {
         BlockPos blockpos = blockpos$mutable.below();
         if (p_208498_0_.getBlockState(blockpos).isPathfindable(p_208498_0_, blockpos, PathType.LAND)) {
            return blockpos;
         }
      }

      return blockpos$mutable.immutable();
   }

   public static class EntityDensityManager {
      private final int spawnableChunkCount;
      private final Object2IntOpenHashMap<EntityClassification> mobCategoryCounts;
      private final MobDensityTracker spawnPotential;
      private final Object2IntMap<EntityClassification> unmodifiableMobCategoryCounts;
      @Nullable
      private BlockPos lastCheckedPos;
      @Nullable
      private EntityType<?> lastCheckedType;
      private double lastCharge;

      private EntityDensityManager(int p_i231621_1_, Object2IntOpenHashMap<EntityClassification> p_i231621_2_, MobDensityTracker p_i231621_3_) {
         this.spawnableChunkCount = p_i231621_1_;
         this.mobCategoryCounts = p_i231621_2_;
         this.spawnPotential = p_i231621_3_;
         this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(p_i231621_2_);
      }

      private boolean canSpawn(EntityType<?> p_234989_1_, BlockPos p_234989_2_, IChunk p_234989_3_) {
         this.lastCheckedPos = p_234989_2_;
         this.lastCheckedType = p_234989_1_;
         MobSpawnInfo.SpawnCosts mobspawninfo$spawncosts = WorldEntitySpawner.getRoughBiome(p_234989_2_, p_234989_3_).getMobSettings().getMobSpawnCost(p_234989_1_);
         if (mobspawninfo$spawncosts == null) {
            this.lastCharge = 0.0D;
            return true;
         } else {
            double d0 = mobspawninfo$spawncosts.getCharge();
            this.lastCharge = d0;
            double d1 = this.spawnPotential.getPotentialEnergyChange(p_234989_2_, d0);
            return d1 <= mobspawninfo$spawncosts.getEnergyBudget();
         }
      }

      private void afterSpawn(MobEntity p_234990_1_, IChunk p_234990_2_) {
         EntityType<?> entitytype = p_234990_1_.getType();
         BlockPos blockpos = p_234990_1_.blockPosition();
         double d0;
         if (blockpos.equals(this.lastCheckedPos) && entitytype == this.lastCheckedType) {
            d0 = this.lastCharge;
         } else {
            MobSpawnInfo.SpawnCosts mobspawninfo$spawncosts = WorldEntitySpawner.getRoughBiome(blockpos, p_234990_2_).getMobSettings().getMobSpawnCost(entitytype);
            if (mobspawninfo$spawncosts != null) {
               d0 = mobspawninfo$spawncosts.getCharge();
            } else {
               d0 = 0.0D;
            }
         }

         this.spawnPotential.addCharge(blockpos, d0);
         this.mobCategoryCounts.addTo(entitytype.getCategory(), 1);
      }

      @OnlyIn(Dist.CLIENT)
      public int getSpawnableChunkCount() {
         return this.spawnableChunkCount;
      }

      public Object2IntMap<EntityClassification> getMobCategoryCounts() {
         return this.unmodifiableMobCategoryCounts;
      }

      private boolean canSpawnForCategory(EntityClassification p_234991_1_) {
         int i = p_234991_1_.getMaxInstancesPerChunk() * this.spawnableChunkCount / WorldEntitySpawner.MAGIC_NUMBER;
         return this.mobCategoryCounts.getInt(p_234991_1_) < i;
      }
   }

   @FunctionalInterface
   public interface IDensityCheck {
      boolean test(EntityType<?> p_test_1_, BlockPos p_test_2_, IChunk p_test_3_);
   }

   @FunctionalInterface
   public interface IInitialDensityAdder {
      void query(long p_query_1_, Consumer<Chunk> p_query_3_);
   }

   @FunctionalInterface
   public interface IOnSpawnDensityAdder {
      void run(MobEntity p_run_1_, IChunk p_run_2_);
   }
}
