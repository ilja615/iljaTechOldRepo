package net.minecraft.pathfinding;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PathPoint {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int heapIdx = -1;
   public float g;
   public float h;
   public float f;
   public PathPoint cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public PathNodeType type = PathNodeType.BLOCKED;

   public PathPoint(int p_i2135_1_, int p_i2135_2_, int p_i2135_3_) {
      this.x = p_i2135_1_;
      this.y = p_i2135_2_;
      this.z = p_i2135_3_;
      this.hash = createHash(p_i2135_1_, p_i2135_2_, p_i2135_3_);
   }

   public PathPoint cloneAndMove(int p_186283_1_, int p_186283_2_, int p_186283_3_) {
      PathPoint pathpoint = new PathPoint(p_186283_1_, p_186283_2_, p_186283_3_);
      pathpoint.heapIdx = this.heapIdx;
      pathpoint.g = this.g;
      pathpoint.h = this.h;
      pathpoint.f = this.f;
      pathpoint.cameFrom = this.cameFrom;
      pathpoint.closed = this.closed;
      pathpoint.walkedDistance = this.walkedDistance;
      pathpoint.costMalus = this.costMalus;
      pathpoint.type = this.type;
      return pathpoint;
   }

   public static int createHash(int p_75830_0_, int p_75830_1_, int p_75830_2_) {
      return p_75830_1_ & 255 | (p_75830_0_ & 32767) << 8 | (p_75830_2_ & 32767) << 24 | (p_75830_0_ < 0 ? Integer.MIN_VALUE : 0) | (p_75830_2_ < 0 ? '\u8000' : 0);
   }

   public float distanceTo(PathPoint p_75829_1_) {
      float f = (float)(p_75829_1_.x - this.x);
      float f1 = (float)(p_75829_1_.y - this.y);
      float f2 = (float)(p_75829_1_.z - this.z);
      return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public float distanceToSqr(PathPoint p_75832_1_) {
      float f = (float)(p_75832_1_.x - this.x);
      float f1 = (float)(p_75832_1_.y - this.y);
      float f2 = (float)(p_75832_1_.z - this.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   public float distanceManhattan(PathPoint p_224757_1_) {
      float f = (float)Math.abs(p_224757_1_.x - this.x);
      float f1 = (float)Math.abs(p_224757_1_.y - this.y);
      float f2 = (float)Math.abs(p_224757_1_.z - this.z);
      return f + f1 + f2;
   }

   public float distanceManhattan(BlockPos p_224758_1_) {
      float f = (float)Math.abs(p_224758_1_.getX() - this.x);
      float f1 = (float)Math.abs(p_224758_1_.getY() - this.y);
      float f2 = (float)Math.abs(p_224758_1_.getZ() - this.z);
      return f + f1 + f2;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof PathPoint)) {
         return false;
      } else {
         PathPoint pathpoint = (PathPoint)p_equals_1_;
         return this.hash == pathpoint.hash && this.x == pathpoint.x && this.y == pathpoint.y && this.z == pathpoint.z;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }

   @OnlyIn(Dist.CLIENT)
   public static PathPoint createFromStream(PacketBuffer p_186282_0_) {
      PathPoint pathpoint = new PathPoint(p_186282_0_.readInt(), p_186282_0_.readInt(), p_186282_0_.readInt());
      pathpoint.walkedDistance = p_186282_0_.readFloat();
      pathpoint.costMalus = p_186282_0_.readFloat();
      pathpoint.closed = p_186282_0_.readBoolean();
      pathpoint.type = PathNodeType.values()[p_186282_0_.readInt()];
      pathpoint.f = p_186282_0_.readFloat();
      return pathpoint;
   }
}
