package net.minecraft.util.palette;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction8;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData();
   private static final Direction8[] DIRECTIONS = Direction8.values();
   private final EnumSet<Direction8> sides = EnumSet.noneOf(Direction8.class);
   private final int[][] index = new int[16][];
   private static final Map<Block, UpgradeData.IBlockFixer> MAP = new IdentityHashMap<>();
   private static final Set<UpgradeData.IBlockFixer> CHUNKY_FIXERS = Sets.newHashSet();

   private UpgradeData() {
   }

   public UpgradeData(CompoundNBT p_i47714_1_) {
      this();
      if (p_i47714_1_.contains("Indices", 10)) {
         CompoundNBT compoundnbt = p_i47714_1_.getCompound("Indices");

         for(int i = 0; i < this.index.length; ++i) {
            String s = String.valueOf(i);
            if (compoundnbt.contains(s, 11)) {
               this.index[i] = compoundnbt.getIntArray(s);
            }
         }
      }

      int j = p_i47714_1_.getInt("Sides");

      for(Direction8 direction8 : Direction8.values()) {
         if ((j & 1 << direction8.ordinal()) != 0) {
            this.sides.add(direction8);
         }
      }

   }

   public void upgrade(Chunk p_196990_1_) {
      this.upgradeInside(p_196990_1_);

      for(Direction8 direction8 : DIRECTIONS) {
         upgradeSides(p_196990_1_, direction8);
      }

      World world = p_196990_1_.getLevel();
      CHUNKY_FIXERS.forEach((p_208829_1_) -> {
         p_208829_1_.processChunk(world);
      });
   }

   private static void upgradeSides(Chunk p_196991_0_, Direction8 p_196991_1_) {
      World world = p_196991_0_.getLevel();
      if (p_196991_0_.getUpgradeData().sides.remove(p_196991_1_)) {
         Set<Direction> set = p_196991_1_.getDirections();
         int i = 0;
         int j = 15;
         boolean flag = set.contains(Direction.EAST);
         boolean flag1 = set.contains(Direction.WEST);
         boolean flag2 = set.contains(Direction.SOUTH);
         boolean flag3 = set.contains(Direction.NORTH);
         boolean flag4 = set.size() == 1;
         ChunkPos chunkpos = p_196991_0_.getPos();
         int k = chunkpos.getMinBlockX() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 1);
         int l = chunkpos.getMinBlockX() + (!flag4 || !flag3 && !flag2 ? (flag1 ? 0 : 15) : 14);
         int i1 = chunkpos.getMinBlockZ() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 1);
         int j1 = chunkpos.getMinBlockZ() + (!flag4 || !flag && !flag1 ? (flag3 ? 0 : 15) : 14);
         Direction[] adirection = Direction.values();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(BlockPos blockpos : BlockPos.betweenClosed(k, 0, i1, l, world.getMaxBuildHeight() - 1, j1)) {
            BlockState blockstate = world.getBlockState(blockpos);
            BlockState blockstate1 = blockstate;

            for(Direction direction : adirection) {
               blockpos$mutable.setWithOffset(blockpos, direction);
               blockstate1 = updateState(blockstate1, direction, world, blockpos, blockpos$mutable);
            }

            Block.updateOrDestroy(blockstate, blockstate1, world, blockpos, 18);
         }

      }
   }

   private static BlockState updateState(BlockState p_196987_0_, Direction p_196987_1_, IWorld p_196987_2_, BlockPos p_196987_3_, BlockPos p_196987_4_) {
      return MAP.getOrDefault(p_196987_0_.getBlock(), UpgradeData.BlockFixers.DEFAULT).updateShape(p_196987_0_, p_196987_1_, p_196987_2_.getBlockState(p_196987_4_), p_196987_2_, p_196987_3_, p_196987_4_);
   }

   private void upgradeInside(Chunk p_196989_1_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
      ChunkPos chunkpos = p_196989_1_.getPos();
      IWorld iworld = p_196989_1_.getLevel();

      for(int i = 0; i < 16; ++i) {
         ChunkSection chunksection = p_196989_1_.getSections()[i];
         int[] aint = this.index[i];
         this.index[i] = null;
         if (chunksection != null && aint != null && aint.length > 0) {
            Direction[] adirection = Direction.values();
            PalettedContainer<BlockState> palettedcontainer = chunksection.getStates();

            for(int j : aint) {
               int k = j & 15;
               int l = j >> 8 & 15;
               int i1 = j >> 4 & 15;
               blockpos$mutable.set(chunkpos.getMinBlockX() + k, (i << 4) + l, chunkpos.getMinBlockZ() + i1);
               BlockState blockstate = palettedcontainer.get(j);
               BlockState blockstate1 = blockstate;

               for(Direction direction : adirection) {
                  blockpos$mutable1.setWithOffset(blockpos$mutable, direction);
                  if (blockpos$mutable.getX() >> 4 == chunkpos.x && blockpos$mutable.getZ() >> 4 == chunkpos.z) {
                     blockstate1 = updateState(blockstate1, direction, iworld, blockpos$mutable, blockpos$mutable1);
                  }
               }

               Block.updateOrDestroy(blockstate, blockstate1, iworld, blockpos$mutable, 18);
            }
         }
      }

      for(int j1 = 0; j1 < this.index.length; ++j1) {
         if (this.index[j1] != null) {
            LOGGER.warn("Discarding update data for section {} for chunk ({} {})", j1, chunkpos.x, chunkpos.z);
         }

         this.index[j1] = null;
      }

   }

   public boolean isEmpty() {
      for(int[] aint : this.index) {
         if (aint != null) {
            return false;
         }
      }

      return this.sides.isEmpty();
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();

      for(int i = 0; i < this.index.length; ++i) {
         String s = String.valueOf(i);
         if (this.index[i] != null && this.index[i].length != 0) {
            compoundnbt1.putIntArray(s, this.index[i]);
         }
      }

      if (!compoundnbt1.isEmpty()) {
         compoundnbt.put("Indices", compoundnbt1);
      }

      int j = 0;

      for(Direction8 direction8 : this.sides) {
         j |= 1 << direction8.ordinal();
      }

      compoundnbt.putByte("Sides", (byte)j);
      return compoundnbt;
   }

   static enum BlockFixers implements UpgradeData.IBlockFixer {
      BLACKLIST(Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN) {
         public BlockState updateShape(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_;
         }
      },
      DEFAULT {
         public BlockState updateShape(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            return p_196982_1_.updateShape(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
         }
      },
      CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
         public BlockState updateShape(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_3_.is(p_196982_1_.getBlock()) && p_196982_2_.getAxis().isHorizontal() && p_196982_1_.getValue(ChestBlock.TYPE) == ChestType.SINGLE && p_196982_3_.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction direction = p_196982_1_.getValue(ChestBlock.FACING);
               if (p_196982_2_.getAxis() != direction.getAxis() && direction == p_196982_3_.getValue(ChestBlock.FACING)) {
                  ChestType chesttype = p_196982_2_ == direction.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                  p_196982_4_.setBlock(p_196982_6_, p_196982_3_.setValue(ChestBlock.TYPE, chesttype.getOpposite()), 18);
                  if (direction == Direction.NORTH || direction == Direction.EAST) {
                     TileEntity tileentity = p_196982_4_.getBlockEntity(p_196982_5_);
                     TileEntity tileentity1 = p_196982_4_.getBlockEntity(p_196982_6_);
                     if (tileentity instanceof ChestTileEntity && tileentity1 instanceof ChestTileEntity) {
                        ChestTileEntity.swapContents((ChestTileEntity)tileentity, (ChestTileEntity)tileentity1);
                     }
                  }

                  return p_196982_1_.setValue(ChestBlock.TYPE, chesttype);
               }
            }

            return p_196982_1_;
         }
      },
      LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
         private final ThreadLocal<List<ObjectSet<BlockPos>>> queue = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState updateShape(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            BlockState blockstate = p_196982_1_.updateShape(p_196982_2_, p_196982_4_.getBlockState(p_196982_6_), p_196982_4_, p_196982_5_, p_196982_6_);
            if (p_196982_1_ != blockstate) {
               int i = blockstate.getValue(BlockStateProperties.DISTANCE);
               List<ObjectSet<BlockPos>> list = this.queue.get();
               if (list.isEmpty()) {
                  for(int j = 0; j < 7; ++j) {
                     list.add(new ObjectOpenHashSet<>());
                  }
               }

               list.get(i).add(p_196982_5_.immutable());
            }

            return p_196982_1_;
         }

         public void processChunk(IWorld p_208826_1_) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            List<ObjectSet<BlockPos>> list = this.queue.get();

            for(int i = 2; i < list.size(); ++i) {
               int j = i - 1;
               ObjectSet<BlockPos> objectset = list.get(j);
               ObjectSet<BlockPos> objectset1 = list.get(i);

               for(BlockPos blockpos : objectset) {
                  BlockState blockstate = p_208826_1_.getBlockState(blockpos);
                  if (blockstate.getValue(BlockStateProperties.DISTANCE) >= j) {
                     p_208826_1_.setBlock(blockpos, blockstate.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(j)), 18);
                     if (i != 7) {
                        for(Direction direction : DIRECTIONS) {
                           blockpos$mutable.setWithOffset(blockpos, direction);
                           BlockState blockstate1 = p_208826_1_.getBlockState(blockpos$mutable);
                           if (blockstate1.hasProperty(BlockStateProperties.DISTANCE) && blockstate.getValue(BlockStateProperties.DISTANCE) > i) {
                              objectset1.add(blockpos$mutable.immutable());
                           }
                        }
                     }
                  }
               }
            }

            list.clear();
         }
      },
      STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
         public BlockState updateShape(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_) {
            if (p_196982_1_.getValue(StemBlock.AGE) == 7) {
               StemGrownBlock stemgrownblock = ((StemBlock)p_196982_1_.getBlock()).getFruit();
               if (p_196982_3_.is(stemgrownblock)) {
                  return stemgrownblock.getAttachedStem().defaultBlockState().setValue(HorizontalBlock.FACING, p_196982_2_);
               }
            }

            return p_196982_1_;
         }
      };

      public static final Direction[] DIRECTIONS = Direction.values();

      private BlockFixers(Block... p_i47847_3_) {
         this(false, p_i47847_3_);
      }

      private BlockFixers(boolean p_i49366_3_, Block... p_i49366_4_) {
         for(Block block : p_i49366_4_) {
            UpgradeData.MAP.put(block, this);
         }

         if (p_i49366_3_) {
            UpgradeData.CHUNKY_FIXERS.add(this);
         }

      }
   }

   public interface IBlockFixer {
      BlockState updateShape(BlockState p_196982_1_, Direction p_196982_2_, BlockState p_196982_3_, IWorld p_196982_4_, BlockPos p_196982_5_, BlockPos p_196982_6_);

      default void processChunk(IWorld p_208826_1_) {
      }
   }
}
