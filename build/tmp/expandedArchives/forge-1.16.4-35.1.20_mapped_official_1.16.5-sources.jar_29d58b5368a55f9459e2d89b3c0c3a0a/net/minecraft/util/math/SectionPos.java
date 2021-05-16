package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

public class SectionPos extends Vector3i {
   private SectionPos(int p_i50794_1_, int p_i50794_2_, int p_i50794_3_) {
      super(p_i50794_1_, p_i50794_2_, p_i50794_3_);
   }

   public static SectionPos of(int p_218154_0_, int p_218154_1_, int p_218154_2_) {
      return new SectionPos(p_218154_0_, p_218154_1_, p_218154_2_);
   }

   public static SectionPos of(BlockPos p_218167_0_) {
      return new SectionPos(blockToSectionCoord(p_218167_0_.getX()), blockToSectionCoord(p_218167_0_.getY()), blockToSectionCoord(p_218167_0_.getZ()));
   }

   public static SectionPos of(ChunkPos p_218156_0_, int p_218156_1_) {
      return new SectionPos(p_218156_0_.x, p_218156_1_, p_218156_0_.z);
   }

   public static SectionPos of(Entity p_218157_0_) {
      return new SectionPos(blockToSectionCoord(MathHelper.floor(p_218157_0_.getX())), blockToSectionCoord(MathHelper.floor(p_218157_0_.getY())), blockToSectionCoord(MathHelper.floor(p_218157_0_.getZ())));
   }

   public static SectionPos of(long p_218170_0_) {
      return new SectionPos(x(p_218170_0_), y(p_218170_0_), z(p_218170_0_));
   }

   public static long offset(long p_218172_0_, Direction p_218172_2_) {
      return offset(p_218172_0_, p_218172_2_.getStepX(), p_218172_2_.getStepY(), p_218172_2_.getStepZ());
   }

   public static long offset(long p_218174_0_, int p_218174_2_, int p_218174_3_, int p_218174_4_) {
      return asLong(x(p_218174_0_) + p_218174_2_, y(p_218174_0_) + p_218174_3_, z(p_218174_0_) + p_218174_4_);
   }

   public static int blockToSectionCoord(int p_218159_0_) {
      return p_218159_0_ >> 4;
   }

   public static int sectionRelative(int p_218171_0_) {
      return p_218171_0_ & 15;
   }

   public static short sectionRelativePos(BlockPos p_218150_0_) {
      int i = sectionRelative(p_218150_0_.getX());
      int j = sectionRelative(p_218150_0_.getY());
      int k = sectionRelative(p_218150_0_.getZ());
      return (short)(i << 8 | k << 4 | j << 0);
   }

   public static int sectionRelativeX(short p_243641_0_) {
      return p_243641_0_ >>> 8 & 15;
   }

   public static int sectionRelativeY(short p_243642_0_) {
      return p_243642_0_ >>> 0 & 15;
   }

   public static int sectionRelativeZ(short p_243643_0_) {
      return p_243643_0_ >>> 4 & 15;
   }

   public int relativeToBlockX(short p_243644_1_) {
      return this.minBlockX() + sectionRelativeX(p_243644_1_);
   }

   public int relativeToBlockY(short p_243645_1_) {
      return this.minBlockY() + sectionRelativeY(p_243645_1_);
   }

   public int relativeToBlockZ(short p_243646_1_) {
      return this.minBlockZ() + sectionRelativeZ(p_243646_1_);
   }

   public BlockPos relativeToBlockPos(short p_243647_1_) {
      return new BlockPos(this.relativeToBlockX(p_243647_1_), this.relativeToBlockY(p_243647_1_), this.relativeToBlockZ(p_243647_1_));
   }

   public static int sectionToBlockCoord(int p_218142_0_) {
      return p_218142_0_ << 4;
   }

   public static int x(long p_218173_0_) {
      return (int)(p_218173_0_ << 0 >> 42);
   }

   public static int y(long p_218144_0_) {
      return (int)(p_218144_0_ << 44 >> 44);
   }

   public static int z(long p_218153_0_) {
      return (int)(p_218153_0_ << 22 >> 42);
   }

   public int x() {
      return this.getX();
   }

   public int y() {
      return this.getY();
   }

   public int z() {
      return this.getZ();
   }

   public int minBlockX() {
      return this.x() << 4;
   }

   public int minBlockY() {
      return this.y() << 4;
   }

   public int minBlockZ() {
      return this.z() << 4;
   }

   public int maxBlockX() {
      return (this.x() << 4) + 15;
   }

   public int maxBlockY() {
      return (this.y() << 4) + 15;
   }

   public int maxBlockZ() {
      return (this.z() << 4) + 15;
   }

   public static long blockToSection(long p_218162_0_) {
      return asLong(blockToSectionCoord(BlockPos.getX(p_218162_0_)), blockToSectionCoord(BlockPos.getY(p_218162_0_)), blockToSectionCoord(BlockPos.getZ(p_218162_0_)));
   }

   public static long getZeroNode(long p_218169_0_) {
      return p_218169_0_ & -1048576L;
   }

   public BlockPos origin() {
      return new BlockPos(sectionToBlockCoord(this.x()), sectionToBlockCoord(this.y()), sectionToBlockCoord(this.z()));
   }

   public BlockPos center() {
      int i = 8;
      return this.origin().offset(8, 8, 8);
   }

   public ChunkPos chunk() {
      return new ChunkPos(this.x(), this.z());
   }

   public static long asLong(int p_218166_0_, int p_218166_1_, int p_218166_2_) {
      long i = 0L;
      i = i | ((long)p_218166_0_ & 4194303L) << 42;
      i = i | ((long)p_218166_1_ & 1048575L) << 0;
      return i | ((long)p_218166_2_ & 4194303L) << 20;
   }

   public long asLong() {
      return asLong(this.x(), this.y(), this.z());
   }

   public Stream<BlockPos> blocksInside() {
      return BlockPos.betweenClosedStream(this.minBlockX(), this.minBlockY(), this.minBlockZ(), this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
   }

   public static Stream<SectionPos> cube(SectionPos p_218158_0_, int p_218158_1_) {
      int i = p_218158_0_.x();
      int j = p_218158_0_.y();
      int k = p_218158_0_.z();
      return betweenClosedStream(i - p_218158_1_, j - p_218158_1_, k - p_218158_1_, i + p_218158_1_, j + p_218158_1_, k + p_218158_1_);
   }

   public static Stream<SectionPos> aroundChunk(ChunkPos p_229421_0_, int p_229421_1_) {
      int i = p_229421_0_.x;
      int j = p_229421_0_.z;
      return betweenClosedStream(i - p_229421_1_, 0, j - p_229421_1_, i + p_229421_1_, 15, j + p_229421_1_);
   }

   public static Stream<SectionPos> betweenClosedStream(final int p_218168_0_, final int p_218168_1_, final int p_218168_2_, final int p_218168_3_, final int p_218168_4_, final int p_218168_5_) {
      return StreamSupport.stream(new AbstractSpliterator<SectionPos>((long)((p_218168_3_ - p_218168_0_ + 1) * (p_218168_4_ - p_218168_1_ + 1) * (p_218168_5_ - p_218168_2_ + 1)), 64) {
         final CubeCoordinateIterator cursor = new CubeCoordinateIterator(p_218168_0_, p_218168_1_, p_218168_2_, p_218168_3_, p_218168_4_, p_218168_5_);

         public boolean tryAdvance(Consumer<? super SectionPos> p_tryAdvance_1_) {
            if (this.cursor.advance()) {
               p_tryAdvance_1_.accept(new SectionPos(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }
}
