package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vector3i {
   public static final Codec<BlockPos> CODEC = Codec.INT_STREAM.comapFlatMap((p_239586_0_) -> {
      return Util.fixedSize(p_239586_0_, 3).map((p_239587_0_) -> {
         return new BlockPos(p_239587_0_[0], p_239587_0_[1], p_239587_0_[2]);
      });
   }, (p_239582_0_) -> {
      return IntStream.of(p_239582_0_.getX(), p_239582_0_.getY(), p_239582_0_.getZ());
   }).stable();
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int PACKED_X_LENGTH = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
   private static final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
   private static final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
   private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
   private static final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
   private static final int Z_OFFSET = PACKED_Y_LENGTH;
   private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;

   public BlockPos(int p_i46030_1_, int p_i46030_2_, int p_i46030_3_) {
      super(p_i46030_1_, p_i46030_2_, p_i46030_3_);
   }

   public BlockPos(double p_i46031_1_, double p_i46031_3_, double p_i46031_5_) {
      super(p_i46031_1_, p_i46031_3_, p_i46031_5_);
   }

   public BlockPos(Vector3d p_i47100_1_) {
      this(p_i47100_1_.x, p_i47100_1_.y, p_i47100_1_.z);
   }

   public BlockPos(IPosition p_i50799_1_) {
      this(p_i50799_1_.x(), p_i50799_1_.y(), p_i50799_1_.z());
   }

   public BlockPos(Vector3i p_i46034_1_) {
      this(p_i46034_1_.getX(), p_i46034_1_.getY(), p_i46034_1_.getZ());
   }

   public static long offset(long p_218289_0_, Direction p_218289_2_) {
      return offset(p_218289_0_, p_218289_2_.getStepX(), p_218289_2_.getStepY(), p_218289_2_.getStepZ());
   }

   public static long offset(long p_218291_0_, int p_218291_2_, int p_218291_3_, int p_218291_4_) {
      return asLong(getX(p_218291_0_) + p_218291_2_, getY(p_218291_0_) + p_218291_3_, getZ(p_218291_0_) + p_218291_4_);
   }

   public static int getX(long p_218290_0_) {
      return (int)(p_218290_0_ << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
   }

   public static int getY(long p_218274_0_) {
      return (int)(p_218274_0_ << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
   }

   public static int getZ(long p_218282_0_) {
      return (int)(p_218282_0_ << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
   }

   public static BlockPos of(long p_218283_0_) {
      return new BlockPos(getX(p_218283_0_), getY(p_218283_0_), getZ(p_218283_0_));
   }

   public long asLong() {
      return asLong(this.getX(), this.getY(), this.getZ());
   }

   public static long asLong(int p_218276_0_, int p_218276_1_, int p_218276_2_) {
      long i = 0L;
      i = i | ((long)p_218276_0_ & PACKED_X_MASK) << X_OFFSET;
      i = i | ((long)p_218276_1_ & PACKED_Y_MASK) << 0;
      return i | ((long)p_218276_2_ & PACKED_Z_MASK) << Z_OFFSET;
   }

   public static long getFlatIndex(long p_218288_0_) {
      return p_218288_0_ & -16L;
   }

   public BlockPos offset(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
      return p_177963_1_ == 0.0D && p_177963_3_ == 0.0D && p_177963_5_ == 0.0D ? this : new BlockPos((double)this.getX() + p_177963_1_, (double)this.getY() + p_177963_3_, (double)this.getZ() + p_177963_5_);
   }

   public BlockPos offset(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
      return p_177982_1_ == 0 && p_177982_2_ == 0 && p_177982_3_ == 0 ? this : new BlockPos(this.getX() + p_177982_1_, this.getY() + p_177982_2_, this.getZ() + p_177982_3_);
   }

   public BlockPos offset(Vector3i p_177971_1_) {
      return this.offset(p_177971_1_.getX(), p_177971_1_.getY(), p_177971_1_.getZ());
   }

   public BlockPos subtract(Vector3i p_177973_1_) {
      return this.offset(-p_177973_1_.getX(), -p_177973_1_.getY(), -p_177973_1_.getZ());
   }

   public BlockPos above() {
      return this.relative(Direction.UP);
   }

   public BlockPos above(int p_177981_1_) {
      return this.relative(Direction.UP, p_177981_1_);
   }

   public BlockPos below() {
      return this.relative(Direction.DOWN);
   }

   public BlockPos below(int p_177979_1_) {
      return this.relative(Direction.DOWN, p_177979_1_);
   }

   public BlockPos north() {
      return this.relative(Direction.NORTH);
   }

   public BlockPos north(int p_177964_1_) {
      return this.relative(Direction.NORTH, p_177964_1_);
   }

   public BlockPos south() {
      return this.relative(Direction.SOUTH);
   }

   public BlockPos south(int p_177970_1_) {
      return this.relative(Direction.SOUTH, p_177970_1_);
   }

   public BlockPos west() {
      return this.relative(Direction.WEST);
   }

   public BlockPos west(int p_177985_1_) {
      return this.relative(Direction.WEST, p_177985_1_);
   }

   public BlockPos east() {
      return this.relative(Direction.EAST);
   }

   public BlockPos east(int p_177965_1_) {
      return this.relative(Direction.EAST, p_177965_1_);
   }

   public BlockPos relative(Direction p_177972_1_) {
      return new BlockPos(this.getX() + p_177972_1_.getStepX(), this.getY() + p_177972_1_.getStepY(), this.getZ() + p_177972_1_.getStepZ());
   }

   public BlockPos relative(Direction p_177967_1_, int p_177967_2_) {
      return p_177967_2_ == 0 ? this : new BlockPos(this.getX() + p_177967_1_.getStepX() * p_177967_2_, this.getY() + p_177967_1_.getStepY() * p_177967_2_, this.getZ() + p_177967_1_.getStepZ() * p_177967_2_);
   }

   public BlockPos relative(Direction.Axis p_241872_1_, int p_241872_2_) {
      if (p_241872_2_ == 0) {
         return this;
      } else {
         int i = p_241872_1_ == Direction.Axis.X ? p_241872_2_ : 0;
         int j = p_241872_1_ == Direction.Axis.Y ? p_241872_2_ : 0;
         int k = p_241872_1_ == Direction.Axis.Z ? p_241872_2_ : 0;
         return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
      }
   }

   public BlockPos rotate(Rotation p_190942_1_) {
      switch(p_190942_1_) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos cross(Vector3i p_177955_1_) {
      return new BlockPos(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public BlockPos immutable() {
      return this;
   }

   public BlockPos.Mutable mutable() {
      return new BlockPos.Mutable(this.getX(), this.getY(), this.getZ());
   }

   public static Iterable<BlockPos> randomBetweenClosed(Random p_239585_0_, int p_239585_1_, int p_239585_2_, int p_239585_3_, int p_239585_4_, int p_239585_5_, int p_239585_6_, int p_239585_7_) {
      int i = p_239585_5_ - p_239585_2_ + 1;
      int j = p_239585_6_ - p_239585_3_ + 1;
      int k = p_239585_7_ - p_239585_4_ + 1;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final BlockPos.Mutable nextPos = new BlockPos.Mutable();
            int counter = p_239585_1_;

            protected BlockPos computeNext() {
               if (this.counter <= 0) {
                  return this.endOfData();
               } else {
                  BlockPos blockpos = this.nextPos.set(p_239585_2_ + p_239585_0_.nextInt(i), p_239585_3_ + p_239585_0_.nextInt(j), p_239585_4_ + p_239585_0_.nextInt(k));
                  --this.counter;
                  return blockpos;
               }
            }
         };
      };
   }

   public static Iterable<BlockPos> withinManhattan(BlockPos p_239583_0_, int p_239583_1_, int p_239583_2_, int p_239583_3_) {
      int i = p_239583_1_ + p_239583_2_ + p_239583_3_;
      int j = p_239583_0_.getX();
      int k = p_239583_0_.getY();
      int l = p_239583_0_.getZ();
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final BlockPos.Mutable cursor = new BlockPos.Mutable();
            private int currentDepth;
            private int maxX;
            private int maxY;
            private int x;
            private int y;
            private boolean zMirror;

            protected BlockPos computeNext() {
               if (this.zMirror) {
                  this.zMirror = false;
                  this.cursor.setZ(l - (this.cursor.getZ() - l));
                  return this.cursor;
               } else {
                  BlockPos blockpos;
                  for(blockpos = null; blockpos == null; ++this.y) {
                     if (this.y > this.maxY) {
                        ++this.x;
                        if (this.x > this.maxX) {
                           ++this.currentDepth;
                           if (this.currentDepth > i) {
                              return this.endOfData();
                           }

                           this.maxX = Math.min(p_239583_1_, this.currentDepth);
                           this.x = -this.maxX;
                        }

                        this.maxY = Math.min(p_239583_2_, this.currentDepth - Math.abs(this.x));
                        this.y = -this.maxY;
                     }

                     int i1 = this.x;
                     int j1 = this.y;
                     int k1 = this.currentDepth - Math.abs(i1) - Math.abs(j1);
                     if (k1 <= p_239583_3_) {
                        this.zMirror = k1 != 0;
                        blockpos = this.cursor.set(j + i1, k + j1, l + k1);
                     }
                  }

                  return blockpos;
               }
            }
         };
      };
   }

   public static Optional<BlockPos> findClosestMatch(BlockPos p_239584_0_, int p_239584_1_, int p_239584_2_, Predicate<BlockPos> p_239584_3_) {
      return withinManhattanStream(p_239584_0_, p_239584_1_, p_239584_2_, p_239584_1_).filter(p_239584_3_).findFirst();
   }

   public static Stream<BlockPos> withinManhattanStream(BlockPos p_239588_0_, int p_239588_1_, int p_239588_2_, int p_239588_3_) {
      return StreamSupport.stream(withinManhattan(p_239588_0_, p_239588_1_, p_239588_2_, p_239588_3_).spliterator(), false);
   }

   public static Iterable<BlockPos> betweenClosed(BlockPos p_218278_0_, BlockPos p_218278_1_) {
      return betweenClosed(Math.min(p_218278_0_.getX(), p_218278_1_.getX()), Math.min(p_218278_0_.getY(), p_218278_1_.getY()), Math.min(p_218278_0_.getZ(), p_218278_1_.getZ()), Math.max(p_218278_0_.getX(), p_218278_1_.getX()), Math.max(p_218278_0_.getY(), p_218278_1_.getY()), Math.max(p_218278_0_.getZ(), p_218278_1_.getZ()));
   }

   public static Stream<BlockPos> betweenClosedStream(BlockPos p_218281_0_, BlockPos p_218281_1_) {
      return StreamSupport.stream(betweenClosed(p_218281_0_, p_218281_1_).spliterator(), false);
   }

   public static Stream<BlockPos> betweenClosedStream(MutableBoundingBox p_229383_0_) {
      return betweenClosedStream(Math.min(p_229383_0_.x0, p_229383_0_.x1), Math.min(p_229383_0_.y0, p_229383_0_.y1), Math.min(p_229383_0_.z0, p_229383_0_.z1), Math.max(p_229383_0_.x0, p_229383_0_.x1), Math.max(p_229383_0_.y0, p_229383_0_.y1), Math.max(p_229383_0_.z0, p_229383_0_.z1));
   }

   public static Stream<BlockPos> betweenClosedStream(AxisAlignedBB p_239581_0_) {
      return betweenClosedStream(MathHelper.floor(p_239581_0_.minX), MathHelper.floor(p_239581_0_.minY), MathHelper.floor(p_239581_0_.minZ), MathHelper.floor(p_239581_0_.maxX), MathHelper.floor(p_239581_0_.maxY), MathHelper.floor(p_239581_0_.maxZ));
   }

   public static Stream<BlockPos> betweenClosedStream(int p_218287_0_, int p_218287_1_, int p_218287_2_, int p_218287_3_, int p_218287_4_, int p_218287_5_) {
      return StreamSupport.stream(betweenClosed(p_218287_0_, p_218287_1_, p_218287_2_, p_218287_3_, p_218287_4_, p_218287_5_).spliterator(), false);
   }

   public static Iterable<BlockPos> betweenClosed(int p_191531_0_, int p_191531_1_, int p_191531_2_, int p_191531_3_, int p_191531_4_, int p_191531_5_) {
      int i = p_191531_3_ - p_191531_0_ + 1;
      int j = p_191531_4_ - p_191531_1_ + 1;
      int k = p_191531_5_ - p_191531_2_ + 1;
      int l = i * j * k;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final BlockPos.Mutable cursor = new BlockPos.Mutable();
            private int index;

            protected BlockPos computeNext() {
               if (this.index == l) {
                  return this.endOfData();
               } else {
                  int i1 = this.index % i;
                  int j1 = this.index / i;
                  int k1 = j1 % j;
                  int l1 = j1 / j;
                  ++this.index;
                  return this.cursor.set(p_191531_0_ + i1, p_191531_1_ + k1, p_191531_2_ + l1);
               }
            }
         };
      };
   }

   public static Iterable<BlockPos.Mutable> spiralAround(BlockPos p_243514_0_, int p_243514_1_, Direction p_243514_2_, Direction p_243514_3_) {
      Validate.validState(p_243514_2_.getAxis() != p_243514_3_.getAxis(), "The two directions cannot be on the same axis");
      return () -> {
         return new AbstractIterator<BlockPos.Mutable>() {
            private final Direction[] directions = new Direction[]{p_243514_2_, p_243514_3_, p_243514_2_.getOpposite(), p_243514_3_.getOpposite()};
            private final BlockPos.Mutable cursor = p_243514_0_.mutable().move(p_243514_3_);
            private final int legs = 4 * p_243514_1_;
            private int leg = -1;
            private int legSize;
            private int legIndex;
            private int lastX = this.cursor.getX();
            private int lastY = this.cursor.getY();
            private int lastZ = this.cursor.getZ();

            protected BlockPos.Mutable computeNext() {
               this.cursor.set(this.lastX, this.lastY, this.lastZ).move(this.directions[(this.leg + 4) % 4]);
               this.lastX = this.cursor.getX();
               this.lastY = this.cursor.getY();
               this.lastZ = this.cursor.getZ();
               if (this.legIndex >= this.legSize) {
                  if (this.leg >= this.legs) {
                     return this.endOfData();
                  }

                  ++this.leg;
                  this.legIndex = 0;
                  this.legSize = this.leg / 2 + 1;
               }

               ++this.legIndex;
               return this.cursor;
            }
         };
      };
   }

   public static class Mutable extends BlockPos {
      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(int p_i46024_1_, int p_i46024_2_, int p_i46024_3_) {
         super(p_i46024_1_, p_i46024_2_, p_i46024_3_);
      }

      public Mutable(double p_i50824_1_, double p_i50824_3_, double p_i50824_5_) {
         this(MathHelper.floor(p_i50824_1_), MathHelper.floor(p_i50824_3_), MathHelper.floor(p_i50824_5_));
      }

      public BlockPos offset(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
         return super.offset(p_177963_1_, p_177963_3_, p_177963_5_).immutable();
      }

      public BlockPos offset(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
         return super.offset(p_177982_1_, p_177982_2_, p_177982_3_).immutable();
      }

      public BlockPos relative(Direction p_177967_1_, int p_177967_2_) {
         return super.relative(p_177967_1_, p_177967_2_).immutable();
      }

      public BlockPos relative(Direction.Axis p_241872_1_, int p_241872_2_) {
         return super.relative(p_241872_1_, p_241872_2_).immutable();
      }

      public BlockPos rotate(Rotation p_190942_1_) {
         return super.rotate(p_190942_1_).immutable();
      }

      public BlockPos.Mutable set(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         this.setX(p_181079_1_);
         this.setY(p_181079_2_);
         this.setZ(p_181079_3_);
         return this;
      }

      public BlockPos.Mutable set(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return this.set(MathHelper.floor(p_189532_1_), MathHelper.floor(p_189532_3_), MathHelper.floor(p_189532_5_));
      }

      public BlockPos.Mutable set(Vector3i p_189533_1_) {
         return this.set(p_189533_1_.getX(), p_189533_1_.getY(), p_189533_1_.getZ());
      }

      public BlockPos.Mutable set(long p_218294_1_) {
         return this.set(getX(p_218294_1_), getY(p_218294_1_), getZ(p_218294_1_));
      }

      public BlockPos.Mutable set(AxisRotation p_218295_1_, int p_218295_2_, int p_218295_3_, int p_218295_4_) {
         return this.set(p_218295_1_.cycle(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.X), p_218295_1_.cycle(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Y), p_218295_1_.cycle(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Z));
      }

      public BlockPos.Mutable setWithOffset(Vector3i p_239622_1_, Direction p_239622_2_) {
         return this.set(p_239622_1_.getX() + p_239622_2_.getStepX(), p_239622_1_.getY() + p_239622_2_.getStepY(), p_239622_1_.getZ() + p_239622_2_.getStepZ());
      }

      public BlockPos.Mutable setWithOffset(Vector3i p_239621_1_, int p_239621_2_, int p_239621_3_, int p_239621_4_) {
         return this.set(p_239621_1_.getX() + p_239621_2_, p_239621_1_.getY() + p_239621_3_, p_239621_1_.getZ() + p_239621_4_);
      }

      public BlockPos.Mutable move(Direction p_189536_1_) {
         return this.move(p_189536_1_, 1);
      }

      public BlockPos.Mutable move(Direction p_189534_1_, int p_189534_2_) {
         return this.set(this.getX() + p_189534_1_.getStepX() * p_189534_2_, this.getY() + p_189534_1_.getStepY() * p_189534_2_, this.getZ() + p_189534_1_.getStepZ() * p_189534_2_);
      }

      public BlockPos.Mutable move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return this.set(this.getX() + p_196234_1_, this.getY() + p_196234_2_, this.getZ() + p_196234_3_);
      }

      public BlockPos.Mutable move(Vector3i p_243531_1_) {
         return this.set(this.getX() + p_243531_1_.getX(), this.getY() + p_243531_1_.getY(), this.getZ() + p_243531_1_.getZ());
      }

      public BlockPos.Mutable clamp(Direction.Axis p_239620_1_, int p_239620_2_, int p_239620_3_) {
         switch(p_239620_1_) {
         case X:
            return this.set(MathHelper.clamp(this.getX(), p_239620_2_, p_239620_3_), this.getY(), this.getZ());
         case Y:
            return this.set(this.getX(), MathHelper.clamp(this.getY(), p_239620_2_, p_239620_3_), this.getZ());
         case Z:
            return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), p_239620_2_, p_239620_3_));
         default:
            throw new IllegalStateException("Unable to clamp axis " + p_239620_1_);
         }
      }

      public void setX(int p_223471_1_) {
         super.setX(p_223471_1_);
      }

      public void setY(int p_185336_1_) {
         super.setY(p_185336_1_);
      }

      public void setZ(int p_223472_1_) {
         super.setZ(p_223472_1_);
      }

      public BlockPos immutable() {
         return new BlockPos(this);
      }
   }
}
