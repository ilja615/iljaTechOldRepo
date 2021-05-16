package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });
   private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();

   protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> p_207184_1_) {
      p_207184_1_.add(FALLING);
   }

   public Vector3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         blockpos$mutable.setWithOffset(p_215663_2_, direction);
         FluidState fluidstate = p_215663_1_.getFluidState(blockpos$mutable);
         if (this.affectsFlow(fluidstate)) {
            float f = fluidstate.getOwnHeight();
            float f1 = 0.0F;
            if (f == 0.0F) {
               if (!p_215663_1_.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
                  BlockPos blockpos = blockpos$mutable.below();
                  FluidState fluidstate1 = p_215663_1_.getFluidState(blockpos);
                  if (this.affectsFlow(fluidstate1)) {
                     f = fluidstate1.getOwnHeight();
                     if (f > 0.0F) {
                        f1 = p_215663_3_.getOwnHeight() - (f - 0.8888889F);
                     }
                  }
               }
            } else if (f > 0.0F) {
               f1 = p_215663_3_.getOwnHeight() - f;
            }

            if (f1 != 0.0F) {
               d0 += (double)((float)direction.getStepX() * f1);
               d1 += (double)((float)direction.getStepZ() * f1);
            }
         }
      }

      Vector3d vector3d = new Vector3d(d0, 0.0D, d1);
      if (p_215663_3_.getValue(FALLING)) {
         for(Direction direction1 : Direction.Plane.HORIZONTAL) {
            blockpos$mutable.setWithOffset(p_215663_2_, direction1);
            if (this.isSolidFace(p_215663_1_, blockpos$mutable, direction1) || this.isSolidFace(p_215663_1_, blockpos$mutable.above(), direction1)) {
               vector3d = vector3d.normalize().add(0.0D, -6.0D, 0.0D);
               break;
            }
         }
      }

      return vector3d.normalize();
   }

   private boolean affectsFlow(FluidState p_212189_1_) {
      return p_212189_1_.isEmpty() || p_212189_1_.getType().isSame(this);
   }

   protected boolean isSolidFace(IBlockReader p_205573_1_, BlockPos p_205573_2_, Direction p_205573_3_) {
      BlockState blockstate = p_205573_1_.getBlockState(p_205573_2_);
      FluidState fluidstate = p_205573_1_.getFluidState(p_205573_2_);
      if (fluidstate.getType().isSame(this)) {
         return false;
      } else if (p_205573_3_ == Direction.UP) {
         return true;
      } else {
         return blockstate.getMaterial() == Material.ICE ? false : blockstate.isFaceSturdy(p_205573_1_, p_205573_2_, p_205573_3_);
      }
   }

   protected void spread(IWorld p_205575_1_, BlockPos p_205575_2_, FluidState p_205575_3_) {
      if (!p_205575_3_.isEmpty()) {
         BlockState blockstate = p_205575_1_.getBlockState(p_205575_2_);
         BlockPos blockpos = p_205575_2_.below();
         BlockState blockstate1 = p_205575_1_.getBlockState(blockpos);
         FluidState fluidstate = this.getNewLiquid(p_205575_1_, blockpos, blockstate1);
         if (this.canSpreadTo(p_205575_1_, p_205575_2_, blockstate, Direction.DOWN, blockpos, blockstate1, p_205575_1_.getFluidState(blockpos), fluidstate.getType())) {
            this.spreadTo(p_205575_1_, blockpos, blockstate1, Direction.DOWN, fluidstate);
            if (this.sourceNeighborCount(p_205575_1_, p_205575_2_) >= 3) {
               this.spreadToSides(p_205575_1_, p_205575_2_, p_205575_3_, blockstate);
            }
         } else if (p_205575_3_.isSource() || !this.isWaterHole(p_205575_1_, fluidstate.getType(), p_205575_2_, blockstate, blockpos, blockstate1)) {
            this.spreadToSides(p_205575_1_, p_205575_2_, p_205575_3_, blockstate);
         }

      }
   }

   private void spreadToSides(IWorld p_207937_1_, BlockPos p_207937_2_, FluidState p_207937_3_, BlockState p_207937_4_) {
      int i = p_207937_3_.getAmount() - this.getDropOff(p_207937_1_);
      if (p_207937_3_.getValue(FALLING)) {
         i = 7;
      }

      if (i > 0) {
         Map<Direction, FluidState> map = this.getSpread(p_207937_1_, p_207937_2_, p_207937_4_);

         for(Entry<Direction, FluidState> entry : map.entrySet()) {
            Direction direction = entry.getKey();
            FluidState fluidstate = entry.getValue();
            BlockPos blockpos = p_207937_2_.relative(direction);
            BlockState blockstate = p_207937_1_.getBlockState(blockpos);
            if (this.canSpreadTo(p_207937_1_, p_207937_2_, p_207937_4_, direction, blockpos, blockstate, p_207937_1_.getFluidState(blockpos), fluidstate.getType())) {
               this.spreadTo(p_207937_1_, blockpos, blockstate, direction, fluidstate);
            }
         }

      }
   }

   protected FluidState getNewLiquid(IWorldReader p_205576_1_, BlockPos p_205576_2_, BlockState p_205576_3_) {
      int i = 0;
      int j = 0;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_205576_2_.relative(direction);
         BlockState blockstate = p_205576_1_.getBlockState(blockpos);
         FluidState fluidstate = blockstate.getFluidState();
         if (fluidstate.getType().isSame(this) && this.canPassThroughWall(direction, p_205576_1_, p_205576_2_, p_205576_3_, blockpos, blockstate)) {
            if (fluidstate.isSource() && net.minecraftforge.event.ForgeEventFactory.canCreateFluidSource(p_205576_1_, blockpos, blockstate, this.canConvertToSource())) {
               ++j;
            }

            i = Math.max(i, fluidstate.getAmount());
         }
      }

      if (j >= 2) {
         BlockState blockstate1 = p_205576_1_.getBlockState(p_205576_2_.below());
         FluidState fluidstate1 = blockstate1.getFluidState();
         if (blockstate1.getMaterial().isSolid() || this.isSourceBlockOfThisType(fluidstate1)) {
            return this.getSource(false);
         }
      }

      BlockPos blockpos1 = p_205576_2_.above();
      BlockState blockstate2 = p_205576_1_.getBlockState(blockpos1);
      FluidState fluidstate2 = blockstate2.getFluidState();
      if (!fluidstate2.isEmpty() && fluidstate2.getType().isSame(this) && this.canPassThroughWall(Direction.UP, p_205576_1_, p_205576_2_, p_205576_3_, blockpos1, blockstate2)) {
         return this.getFlowing(8, true);
      } else {
         int k = i - this.getDropOff(p_205576_1_);
         return k <= 0 ? Fluids.EMPTY.defaultFluidState() : this.getFlowing(k, false);
      }
   }

   private boolean canPassThroughWall(Direction p_212751_1_, IBlockReader p_212751_2_, BlockPos p_212751_3_, BlockState p_212751_4_, BlockPos p_212751_5_, BlockState p_212751_6_) {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap;
      if (!p_212751_4_.getBlock().hasDynamicShape() && !p_212751_6_.getBlock().hasDynamicShape()) {
         object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
      } else {
         object2bytelinkedopenhashmap = null;
      }

      Block.RenderSideCacheKey block$rendersidecachekey;
      if (object2bytelinkedopenhashmap != null) {
         block$rendersidecachekey = new Block.RenderSideCacheKey(p_212751_4_, p_212751_6_, p_212751_1_);
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         }
      } else {
         block$rendersidecachekey = null;
      }

      VoxelShape voxelshape1 = p_212751_4_.getCollisionShape(p_212751_2_, p_212751_3_);
      VoxelShape voxelshape = p_212751_6_.getCollisionShape(p_212751_2_, p_212751_5_);
      boolean flag = !VoxelShapes.mergedFaceOccludes(voxelshape1, voxelshape, p_212751_1_);
      if (object2bytelinkedopenhashmap != null) {
         if (object2bytelinkedopenhashmap.size() == 200) {
            object2bytelinkedopenhashmap.removeLastByte();
         }

         object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
      }

      return flag;
   }

   public abstract Fluid getFlowing();

   public FluidState getFlowing(int p_207207_1_, boolean p_207207_2_) {
      return this.getFlowing().defaultFluidState().setValue(LEVEL, Integer.valueOf(p_207207_1_)).setValue(FALLING, Boolean.valueOf(p_207207_2_));
   }

   public abstract Fluid getSource();

   public FluidState getSource(boolean p_207204_1_) {
      return this.getSource().defaultFluidState().setValue(FALLING, Boolean.valueOf(p_207204_1_));
   }

   protected abstract boolean canConvertToSource();

   protected void spreadTo(IWorld p_205574_1_, BlockPos p_205574_2_, BlockState p_205574_3_, Direction p_205574_4_, FluidState p_205574_5_) {
      if (p_205574_3_.getBlock() instanceof ILiquidContainer) {
         ((ILiquidContainer)p_205574_3_.getBlock()).placeLiquid(p_205574_1_, p_205574_2_, p_205574_3_, p_205574_5_);
      } else {
         if (!p_205574_3_.isAir()) {
            this.beforeDestroyingBlock(p_205574_1_, p_205574_2_, p_205574_3_);
         }

         p_205574_1_.setBlock(p_205574_2_, p_205574_5_.createLegacyBlock(), 3);
      }

   }

   protected abstract void beforeDestroyingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, BlockState p_205580_3_);

   private static short getCacheKey(BlockPos p_212752_0_, BlockPos p_212752_1_) {
      int i = p_212752_1_.getX() - p_212752_0_.getX();
      int j = p_212752_1_.getZ() - p_212752_0_.getZ();
      return (short)((i + 128 & 255) << 8 | j + 128 & 255);
   }

   protected int getSlopeDistance(IWorldReader p_205571_1_, BlockPos p_205571_2_, int p_205571_3_, Direction p_205571_4_, BlockState p_205571_5_, BlockPos p_205571_6_, Short2ObjectMap<Pair<BlockState, FluidState>> p_205571_7_, Short2BooleanMap p_205571_8_) {
      int i = 1000;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (direction != p_205571_4_) {
            BlockPos blockpos = p_205571_2_.relative(direction);
            short short1 = getCacheKey(p_205571_6_, blockpos);
            Pair<BlockState, FluidState> pair = p_205571_7_.computeIfAbsent(short1, (p_212748_2_) -> {
               BlockState blockstate1 = p_205571_1_.getBlockState(blockpos);
               return Pair.of(blockstate1, blockstate1.getFluidState());
            });
            BlockState blockstate = pair.getFirst();
            FluidState fluidstate = pair.getSecond();
            if (this.canPassThrough(p_205571_1_, this.getFlowing(), p_205571_2_, p_205571_5_, direction, blockpos, blockstate, fluidstate)) {
               boolean flag = p_205571_8_.computeIfAbsent(short1, (p_212749_4_) -> {
                  BlockPos blockpos1 = blockpos.below();
                  BlockState blockstate1 = p_205571_1_.getBlockState(blockpos1);
                  return this.isWaterHole(p_205571_1_, this.getFlowing(), blockpos, blockstate, blockpos1, blockstate1);
               });
               if (flag) {
                  return p_205571_3_;
               }

               if (p_205571_3_ < this.getSlopeFindDistance(p_205571_1_)) {
                  int j = this.getSlopeDistance(p_205571_1_, blockpos, p_205571_3_ + 1, direction.getOpposite(), blockstate, p_205571_6_, p_205571_7_, p_205571_8_);
                  if (j < i) {
                     i = j;
                  }
               }
            }
         }
      }

      return i;
   }

   private boolean isWaterHole(IBlockReader p_211759_1_, Fluid p_211759_2_, BlockPos p_211759_3_, BlockState p_211759_4_, BlockPos p_211759_5_, BlockState p_211759_6_) {
      if (!this.canPassThroughWall(Direction.DOWN, p_211759_1_, p_211759_3_, p_211759_4_, p_211759_5_, p_211759_6_)) {
         return false;
      } else {
         return p_211759_6_.getFluidState().getType().isSame(this) ? true : this.canHoldFluid(p_211759_1_, p_211759_5_, p_211759_6_, p_211759_2_);
      }
   }

   private boolean canPassThrough(IBlockReader p_211760_1_, Fluid p_211760_2_, BlockPos p_211760_3_, BlockState p_211760_4_, Direction p_211760_5_, BlockPos p_211760_6_, BlockState p_211760_7_, FluidState p_211760_8_) {
      return !this.isSourceBlockOfThisType(p_211760_8_) && this.canPassThroughWall(p_211760_5_, p_211760_1_, p_211760_3_, p_211760_4_, p_211760_6_, p_211760_7_) && this.canHoldFluid(p_211760_1_, p_211760_6_, p_211760_7_, p_211760_2_);
   }

   private boolean isSourceBlockOfThisType(FluidState p_211758_1_) {
      return p_211758_1_.getType().isSame(this) && p_211758_1_.isSource();
   }

   protected abstract int getSlopeFindDistance(IWorldReader p_185698_1_);

   private int sourceNeighborCount(IWorldReader p_207936_1_, BlockPos p_207936_2_) {
      int i = 0;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_207936_2_.relative(direction);
         FluidState fluidstate = p_207936_1_.getFluidState(blockpos);
         if (this.isSourceBlockOfThisType(fluidstate)) {
            ++i;
         }
      }

      return i;
   }

   protected Map<Direction, FluidState> getSpread(IWorldReader p_205572_1_, BlockPos p_205572_2_, BlockState p_205572_3_) {
      int i = 1000;
      Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
      Short2ObjectMap<Pair<BlockState, FluidState>> short2objectmap = new Short2ObjectOpenHashMap<>();
      Short2BooleanMap short2booleanmap = new Short2BooleanOpenHashMap();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_205572_2_.relative(direction);
         short short1 = getCacheKey(p_205572_2_, blockpos);
         Pair<BlockState, FluidState> pair = short2objectmap.computeIfAbsent(short1, (p_212755_2_) -> {
            BlockState blockstate1 = p_205572_1_.getBlockState(blockpos);
            return Pair.of(blockstate1, blockstate1.getFluidState());
         });
         BlockState blockstate = pair.getFirst();
         FluidState fluidstate = pair.getSecond();
         FluidState fluidstate1 = this.getNewLiquid(p_205572_1_, blockpos, blockstate);
         if (this.canPassThrough(p_205572_1_, fluidstate1.getType(), p_205572_2_, p_205572_3_, direction, blockpos, blockstate, fluidstate)) {
            BlockPos blockpos1 = blockpos.below();
            boolean flag = short2booleanmap.computeIfAbsent(short1, (p_212753_5_) -> {
               BlockState blockstate1 = p_205572_1_.getBlockState(blockpos1);
               return this.isWaterHole(p_205572_1_, this.getFlowing(), blockpos, blockstate, blockpos1, blockstate1);
            });
            int j;
            if (flag) {
               j = 0;
            } else {
               j = this.getSlopeDistance(p_205572_1_, blockpos, 1, direction.getOpposite(), blockstate, p_205572_2_, short2objectmap, short2booleanmap);
            }

            if (j < i) {
               map.clear();
            }

            if (j <= i) {
               map.put(direction, fluidstate1);
               i = j;
            }
         }
      }

      return map;
   }

   private boolean canHoldFluid(IBlockReader p_211761_1_, BlockPos p_211761_2_, BlockState p_211761_3_, Fluid p_211761_4_) {
      Block block = p_211761_3_.getBlock();
      if (block instanceof ILiquidContainer) {
         return ((ILiquidContainer)block).canPlaceLiquid(p_211761_1_, p_211761_2_, p_211761_3_, p_211761_4_);
      } else if (!(block instanceof DoorBlock) && !block.is(BlockTags.SIGNS) && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN) {
         Material material = p_211761_3_.getMaterial();
         if (material != Material.PORTAL && material != Material.STRUCTURAL_AIR && material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT) {
            return !material.blocksMotion();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canSpreadTo(IBlockReader p_205570_1_, BlockPos p_205570_2_, BlockState p_205570_3_, Direction p_205570_4_, BlockPos p_205570_5_, BlockState p_205570_6_, FluidState p_205570_7_, Fluid p_205570_8_) {
      return p_205570_7_.canBeReplacedWith(p_205570_1_, p_205570_5_, p_205570_8_, p_205570_4_) && this.canPassThroughWall(p_205570_4_, p_205570_1_, p_205570_2_, p_205570_3_, p_205570_5_, p_205570_6_) && this.canHoldFluid(p_205570_1_, p_205570_5_, p_205570_6_, p_205570_8_);
   }

   protected abstract int getDropOff(IWorldReader p_204528_1_);

   protected int getSpreadDelay(World p_215667_1_, BlockPos p_215667_2_, FluidState p_215667_3_, FluidState p_215667_4_) {
      return this.getTickDelay(p_215667_1_);
   }

   public void tick(World p_207191_1_, BlockPos p_207191_2_, FluidState p_207191_3_) {
      if (!p_207191_3_.isSource()) {
         FluidState fluidstate = this.getNewLiquid(p_207191_1_, p_207191_2_, p_207191_1_.getBlockState(p_207191_2_));
         int i = this.getSpreadDelay(p_207191_1_, p_207191_2_, p_207191_3_, fluidstate);
         if (fluidstate.isEmpty()) {
            p_207191_3_ = fluidstate;
            p_207191_1_.setBlock(p_207191_2_, Blocks.AIR.defaultBlockState(), 3);
         } else if (!fluidstate.equals(p_207191_3_)) {
            p_207191_3_ = fluidstate;
            BlockState blockstate = fluidstate.createLegacyBlock();
            p_207191_1_.setBlock(p_207191_2_, blockstate, 2);
            p_207191_1_.getLiquidTicks().scheduleTick(p_207191_2_, fluidstate.getType(), i);
            p_207191_1_.updateNeighborsAt(p_207191_2_, blockstate.getBlock());
         }
      }

      this.spread(p_207191_1_, p_207191_2_, p_207191_3_);
   }

   protected static int getLegacyLevel(FluidState p_207205_0_) {
      return p_207205_0_.isSource() ? 0 : 8 - Math.min(p_207205_0_.getAmount(), 8) + (p_207205_0_.getValue(FALLING) ? 8 : 0);
   }

   private static boolean hasSameAbove(FluidState p_215666_0_, IBlockReader p_215666_1_, BlockPos p_215666_2_) {
      return p_215666_0_.getType().isSame(p_215666_1_.getFluidState(p_215666_2_.above()).getType());
   }

   public float getHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
      return hasSameAbove(p_215662_1_, p_215662_2_, p_215662_3_) ? 1.0F : p_215662_1_.getOwnHeight();
   }

   public float getOwnHeight(FluidState p_223407_1_) {
      return (float)p_223407_1_.getAmount() / 9.0F;
   }

   public VoxelShape getShape(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
      return p_215664_1_.getAmount() == 9 && hasSameAbove(p_215664_1_, p_215664_2_, p_215664_3_) ? VoxelShapes.block() : this.shapes.computeIfAbsent(p_215664_1_, (p_215668_2_) -> {
         return VoxelShapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)p_215668_2_.getHeight(p_215664_2_, p_215664_3_), 1.0D);
      });
   }
}
