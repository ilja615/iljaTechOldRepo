package net.minecraft.world;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

public class Teleporter implements net.minecraftforge.common.util.ITeleporter {
   protected final ServerWorld level;

   public Teleporter(ServerWorld p_i1963_1_) {
      this.level = p_i1963_1_;
   }

   public Optional<TeleportationRepositioner.Result> findPortalAround(BlockPos p_242957_1_, boolean p_242957_2_) {
      PointOfInterestManager pointofinterestmanager = this.level.getPoiManager();
      int i = p_242957_2_ ? 16 : 128;
      pointofinterestmanager.ensureLoadedAndValid(this.level, p_242957_1_, i);
      Optional<PointOfInterest> optional = pointofinterestmanager.getInSquare((p_242952_0_) -> {
         return p_242952_0_ == PointOfInterestType.NETHER_PORTAL;
      }, p_242957_1_, i, PointOfInterestManager.Status.ANY).sorted(Comparator.<PointOfInterest>comparingDouble((p_242954_1_) -> {
         return p_242954_1_.getPos().distSqr(p_242957_1_);
      }).thenComparingInt((p_242959_0_) -> {
         return p_242959_0_.getPos().getY();
      })).filter((p_242958_1_) -> {
         return this.level.getBlockState(p_242958_1_.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS);
      }).findFirst();
      return optional.map((p_242951_1_) -> {
         BlockPos blockpos = p_242951_1_.getPos();
         this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
         BlockState blockstate = this.level.getBlockState(blockpos);
         return TeleportationRepositioner.getLargestRectangleAround(blockpos, blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, (p_242953_2_) -> {
            return this.level.getBlockState(p_242953_2_) == blockstate;
         });
      });
   }

   public Optional<TeleportationRepositioner.Result> createPortal(BlockPos p_242956_1_, Direction.Axis p_242956_2_) {
      Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, p_242956_2_);
      double d0 = -1.0D;
      BlockPos blockpos = null;
      double d1 = -1.0D;
      BlockPos blockpos1 = null;
      WorldBorder worldborder = this.level.getWorldBorder();
      int i = this.level.getHeight() - 1;
      BlockPos.Mutable blockpos$mutable = p_242956_1_.mutable();

      for(BlockPos.Mutable blockpos$mutable1 : BlockPos.spiralAround(p_242956_1_, 16, Direction.EAST, Direction.SOUTH)) {
         int j = Math.min(i, this.level.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutable1.getX(), blockpos$mutable1.getZ()));
         int k = 1;
         if (worldborder.isWithinBounds(blockpos$mutable1) && worldborder.isWithinBounds(blockpos$mutable1.move(direction, 1))) {
            blockpos$mutable1.move(direction.getOpposite(), 1);

            for(int l = j; l >= 0; --l) {
               blockpos$mutable1.setY(l);
               if (this.level.isEmptyBlock(blockpos$mutable1)) {
                  int i1;
                  for(i1 = l; l > 0 && this.level.isEmptyBlock(blockpos$mutable1.move(Direction.DOWN)); --l) {
                  }

                  if (l + 4 <= i) {
                     int j1 = i1 - l;
                     if (j1 <= 0 || j1 >= 3) {
                        blockpos$mutable1.setY(l);
                        if (this.canHostFrame(blockpos$mutable1, blockpos$mutable, direction, 0)) {
                           double d2 = p_242956_1_.distSqr(blockpos$mutable1);
                           if (this.canHostFrame(blockpos$mutable1, blockpos$mutable, direction, -1) && this.canHostFrame(blockpos$mutable1, blockpos$mutable, direction, 1) && (d0 == -1.0D || d0 > d2)) {
                              d0 = d2;
                              blockpos = blockpos$mutable1.immutable();
                           }

                           if (d0 == -1.0D && (d1 == -1.0D || d1 > d2)) {
                              d1 = d2;
                              blockpos1 = blockpos$mutable1.immutable();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (d0 == -1.0D && d1 != -1.0D) {
         blockpos = blockpos1;
         d0 = d1;
      }

      if (d0 == -1.0D) {
         blockpos = (new BlockPos(p_242956_1_.getX(), MathHelper.clamp(p_242956_1_.getY(), 70, this.level.getHeight() - 10), p_242956_1_.getZ())).immutable();
         Direction direction1 = direction.getClockWise();
         if (!worldborder.isWithinBounds(blockpos)) {
            return Optional.empty();
         }

         for(int l1 = -1; l1 < 2; ++l1) {
            for(int k2 = 0; k2 < 2; ++k2) {
               for(int i3 = -1; i3 < 3; ++i3) {
                  BlockState blockstate1 = i3 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                  blockpos$mutable.setWithOffset(blockpos, k2 * direction.getStepX() + l1 * direction1.getStepX(), i3, k2 * direction.getStepZ() + l1 * direction1.getStepZ());
                  this.level.setBlockAndUpdate(blockpos$mutable, blockstate1);
               }
            }
         }
      }

      for(int k1 = -1; k1 < 3; ++k1) {
         for(int i2 = -1; i2 < 4; ++i2) {
            if (k1 == -1 || k1 == 2 || i2 == -1 || i2 == 3) {
               blockpos$mutable.setWithOffset(blockpos, k1 * direction.getStepX(), i2, k1 * direction.getStepZ());
               this.level.setBlock(blockpos$mutable, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
         }
      }

      BlockState blockstate = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, p_242956_2_);

      for(int j2 = 0; j2 < 2; ++j2) {
         for(int l2 = 0; l2 < 3; ++l2) {
            blockpos$mutable.setWithOffset(blockpos, j2 * direction.getStepX(), l2, j2 * direction.getStepZ());
            this.level.setBlock(blockpos$mutable, blockstate, 18);
         }
      }

      return Optional.of(new TeleportationRepositioner.Result(blockpos.immutable(), 2, 3));
   }

   private boolean canHostFrame(BlockPos p_242955_1_, BlockPos.Mutable p_242955_2_, Direction p_242955_3_, int p_242955_4_) {
      Direction direction = p_242955_3_.getClockWise();

      for(int i = -1; i < 3; ++i) {
         for(int j = -1; j < 4; ++j) {
            p_242955_2_.setWithOffset(p_242955_1_, p_242955_3_.getStepX() * i + direction.getStepX() * p_242955_4_, j, p_242955_3_.getStepZ() * i + direction.getStepZ() * p_242955_4_);
            if (j < 0 && !this.level.getBlockState(p_242955_2_).getMaterial().isSolid()) {
               return false;
            }

            if (j >= 0 && !this.level.isEmptyBlock(p_242955_2_)) {
               return false;
            }
         }
      }

      return true;
   }
}
