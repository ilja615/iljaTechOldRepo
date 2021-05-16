package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkeletonSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   public WitherSkeletonSkullBlock(AbstractBlock.Properties p_i48293_1_) {
      super(SkullBlock.Types.WITHER_SKELETON, p_i48293_1_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
      if (tileentity instanceof SkullTileEntity) {
         checkSpawn(p_180633_1_, p_180633_2_, (SkullTileEntity)tileentity);
      }

   }

   public static void checkSpawn(World p_196298_0_, BlockPos p_196298_1_, SkullTileEntity p_196298_2_) {
      if (!p_196298_0_.isClientSide) {
         BlockState blockstate = p_196298_2_.getBlockState();
         boolean flag = blockstate.is(Blocks.WITHER_SKELETON_SKULL) || blockstate.is(Blocks.WITHER_SKELETON_WALL_SKULL);
         if (flag && p_196298_1_.getY() >= 0 && p_196298_0_.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern blockpattern = getOrCreateWitherFull();
            BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.find(p_196298_0_, p_196298_1_);
            if (blockpattern$patternhelper != null) {
               for(int i = 0; i < blockpattern.getWidth(); ++i) {
                  for(int j = 0; j < blockpattern.getHeight(); ++j) {
                     CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.getBlock(i, j, 0);
                     p_196298_0_.setBlock(cachedblockinfo.getPos(), Blocks.AIR.defaultBlockState(), 2);
                     p_196298_0_.levelEvent(2001, cachedblockinfo.getPos(), Block.getId(cachedblockinfo.getState()));
                  }
               }

               WitherEntity witherentity = EntityType.WITHER.create(p_196298_0_);
               BlockPos blockpos = blockpattern$patternhelper.getBlock(1, 2, 0).getPos();
               witherentity.moveTo((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.55D, (double)blockpos.getZ() + 0.5D, blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
               witherentity.yBodyRot = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
               witherentity.makeInvulnerable();

               for(ServerPlayerEntity serverplayerentity : p_196298_0_.getEntitiesOfClass(ServerPlayerEntity.class, witherentity.getBoundingBox().inflate(50.0D))) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, witherentity);
               }

               p_196298_0_.addFreshEntity(witherentity);

               for(int k = 0; k < blockpattern.getWidth(); ++k) {
                  for(int l = 0; l < blockpattern.getHeight(); ++l) {
                     p_196298_0_.blockUpdated(blockpattern$patternhelper.getBlock(k, l, 0).getPos(), Blocks.AIR);
                  }
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(World p_196299_0_, BlockPos p_196299_1_, ItemStack p_196299_2_) {
      if (p_196299_2_.getItem() == Items.WITHER_SKELETON_SKULL && p_196299_1_.getY() >= 2 && p_196299_0_.getDifficulty() != Difficulty.PEACEFUL && !p_196299_0_.isClientSide) {
         return getOrCreateWitherBase().find(p_196299_0_, p_196299_1_) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (p_235639_0_) -> {
            return p_235639_0_.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
         }).where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', (p_235638_0_) -> {
            return p_235638_0_.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
         }).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternBase;
   }
}
