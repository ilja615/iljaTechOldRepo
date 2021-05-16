package net.minecraft.pathfinding;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.Region;
import net.minecraft.world.World;

public abstract class PathNavigator {
   protected final MobEntity mob;
   protected final World level;
   @Nullable
   protected Path path;
   protected double speedModifier;
   protected int tick;
   protected int lastStuckCheck;
   protected Vector3d lastStuckCheckPos = Vector3d.ZERO;
   protected Vector3i timeoutCachedNode = Vector3i.ZERO;
   protected long timeoutTimer;
   protected long lastTimeoutCheck;
   protected double timeoutLimit;
   protected float maxDistanceToWaypoint = 0.5F;
   protected boolean hasDelayedRecomputation;
   protected long timeLastRecompute;
   protected NodeProcessor nodeEvaluator;
   private BlockPos targetPos;
   private int reachRange;
   private float maxVisitedNodesMultiplier = 1.0F;
   private final PathFinder pathFinder;
   private boolean isStuck;

   public PathNavigator(MobEntity p_i1671_1_, World p_i1671_2_) {
      this.mob = p_i1671_1_;
      this.level = p_i1671_2_;
      int i = MathHelper.floor(p_i1671_1_.getAttributeValue(Attributes.FOLLOW_RANGE) * 16.0D);
      this.pathFinder = this.createPathFinder(i);
   }

   public void resetMaxVisitedNodesMultiplier() {
      this.maxVisitedNodesMultiplier = 1.0F;
   }

   public void setMaxVisitedNodesMultiplier(float p_226335_1_) {
      this.maxVisitedNodesMultiplier = p_226335_1_;
   }

   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   protected abstract PathFinder createPathFinder(int p_179679_1_);

   public void setSpeedModifier(double p_75489_1_) {
      this.speedModifier = p_75489_1_;
   }

   public boolean hasDelayedRecomputation() {
      return this.hasDelayedRecomputation;
   }

   public void recomputePath() {
      if (this.level.getGameTime() - this.timeLastRecompute > 20L) {
         if (this.targetPos != null) {
            this.path = null;
            this.path = this.createPath(this.targetPos, this.reachRange);
            this.timeLastRecompute = this.level.getGameTime();
            this.hasDelayedRecomputation = false;
         }
      } else {
         this.hasDelayedRecomputation = true;
      }

   }

   @Nullable
   public final Path createPath(double p_225466_1_, double p_225466_3_, double p_225466_5_, int p_225466_7_) {
      return this.createPath(new BlockPos(p_225466_1_, p_225466_3_, p_225466_5_), p_225466_7_);
   }

   @Nullable
   public Path createPath(Stream<BlockPos> p_225463_1_, int p_225463_2_) {
      return this.createPath(p_225463_1_.collect(Collectors.toSet()), 8, false, p_225463_2_);
   }

   @Nullable
   public Path createPath(Set<BlockPos> p_241390_1_, int p_241390_2_) {
      return this.createPath(p_241390_1_, 8, false, p_241390_2_);
   }

   @Nullable
   public Path createPath(BlockPos p_179680_1_, int p_179680_2_) {
      return this.createPath(ImmutableSet.of(p_179680_1_), 8, false, p_179680_2_);
   }

   @Nullable
   public Path createPath(Entity p_75494_1_, int p_75494_2_) {
      return this.createPath(ImmutableSet.of(p_75494_1_.blockPosition()), 16, true, p_75494_2_);
   }

   @Nullable
   protected Path createPath(Set<BlockPos> p_225464_1_, int p_225464_2_, boolean p_225464_3_, int p_225464_4_) {
      if (p_225464_1_.isEmpty()) {
         return null;
      } else if (this.mob.getY() < 0.0D) {
         return null;
      } else if (!this.canUpdatePath()) {
         return null;
      } else if (this.path != null && !this.path.isDone() && p_225464_1_.contains(this.targetPos)) {
         return this.path;
      } else {
         this.level.getProfiler().push("pathfind");
         float f = (float)this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
         BlockPos blockpos = p_225464_3_ ? this.mob.blockPosition().above() : this.mob.blockPosition();
         int i = (int)(f + (float)p_225464_2_);
         Region region = new Region(this.level, blockpos.offset(-i, -i, -i), blockpos.offset(i, i, i));
         Path path = this.pathFinder.findPath(region, this.mob, p_225464_1_, f, p_225464_4_, this.maxVisitedNodesMultiplier);
         this.level.getProfiler().pop();
         if (path != null && path.getTarget() != null) {
            this.targetPos = path.getTarget();
            this.reachRange = p_225464_4_;
            this.resetStuckTimeout();
         }

         return path;
      }
   }

   public boolean moveTo(double p_75492_1_, double p_75492_3_, double p_75492_5_, double p_75492_7_) {
      return this.moveTo(this.createPath(p_75492_1_, p_75492_3_, p_75492_5_, 1), p_75492_7_);
   }

   public boolean moveTo(Entity p_75497_1_, double p_75497_2_) {
      Path path = this.createPath(p_75497_1_, 1);
      return path != null && this.moveTo(path, p_75497_2_);
   }

   public boolean moveTo(@Nullable Path p_75484_1_, double p_75484_2_) {
      if (p_75484_1_ == null) {
         this.path = null;
         return false;
      } else {
         if (!p_75484_1_.sameAs(this.path)) {
            this.path = p_75484_1_;
         }

         if (this.isDone()) {
            return false;
         } else {
            this.trimPath();
            if (this.path.getNodeCount() <= 0) {
               return false;
            } else {
               this.speedModifier = p_75484_2_;
               Vector3d vector3d = this.getTempMobPos();
               this.lastStuckCheck = this.tick;
               this.lastStuckCheckPos = vector3d;
               return true;
            }
         }
      }
   }

   @Nullable
   public Path getPath() {
      return this.path;
   }

   public void tick() {
      ++this.tick;
      if (this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if (!this.isDone()) {
         if (this.canUpdatePath()) {
            this.followThePath();
         } else if (this.path != null && !this.path.isDone()) {
            Vector3d vector3d = this.getTempMobPos();
            Vector3d vector3d1 = this.path.getNextEntityPos(this.mob);
            if (vector3d.y > vector3d1.y && !this.mob.isOnGround() && MathHelper.floor(vector3d.x) == MathHelper.floor(vector3d1.x) && MathHelper.floor(vector3d.z) == MathHelper.floor(vector3d1.z)) {
               this.path.advance();
            }
         }

         DebugPacketSender.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if (!this.isDone()) {
            Vector3d vector3d2 = this.path.getNextEntityPos(this.mob);
            BlockPos blockpos = new BlockPos(vector3d2);
            this.mob.getMoveControl().setWantedPosition(vector3d2.x, this.level.getBlockState(blockpos.below()).isAir() ? vector3d2.y : WalkNodeProcessor.getFloorLevel(this.level, blockpos), vector3d2.z, this.speedModifier);
         }
      }
   }

   protected void followThePath() {
      Vector3d vector3d = this.getTempMobPos();
      this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
      Vector3i vector3i = this.path.getNextNodePos();
      double d0 = Math.abs(this.mob.getX() - ((double)vector3i.getX() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
      double d1 = Math.abs(this.mob.getY() - (double)vector3i.getY());
      double d2 = Math.abs(this.mob.getZ() - ((double)vector3i.getZ() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
      boolean flag = d0 < (double)this.maxDistanceToWaypoint && d2 < (double)this.maxDistanceToWaypoint && d1 < 1.0D;
      if (flag || this.mob.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
         this.path.advance();
      }

      this.doStuckDetection(vector3d);
   }

   private boolean shouldTargetNextNodeInDirection(Vector3d p_234112_1_) {
      if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
         return false;
      } else {
         Vector3d vector3d = Vector3d.atBottomCenterOf(this.path.getNextNodePos());
         if (!p_234112_1_.closerThan(vector3d, 2.0D)) {
            return false;
         } else {
            Vector3d vector3d1 = Vector3d.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
            Vector3d vector3d2 = vector3d1.subtract(vector3d);
            Vector3d vector3d3 = p_234112_1_.subtract(vector3d);
            return vector3d2.dot(vector3d3) > 0.0D;
         }
      }
   }

   protected void doStuckDetection(Vector3d p_179677_1_) {
      if (this.tick - this.lastStuckCheck > 100) {
         if (p_179677_1_.distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
            this.isStuck = true;
            this.stop();
         } else {
            this.isStuck = false;
         }

         this.lastStuckCheck = this.tick;
         this.lastStuckCheckPos = p_179677_1_;
      }

      if (this.path != null && !this.path.isDone()) {
         Vector3i vector3i = this.path.getNextNodePos();
         if (vector3i.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = vector3i;
            double d0 = p_179677_1_.distanceTo(Vector3d.atBottomCenterOf(this.timeoutCachedNode));
            this.timeoutLimit = this.mob.getSpeed() > 0.0F ? d0 / (double)this.mob.getSpeed() * 1000.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 3.0D) {
            this.timeoutPath();
         }

         this.lastTimeoutCheck = Util.getMillis();
      }

   }

   private void timeoutPath() {
      this.resetStuckTimeout();
      this.stop();
   }

   private void resetStuckTimeout() {
      this.timeoutCachedNode = Vector3i.ZERO;
      this.timeoutTimer = 0L;
      this.timeoutLimit = 0.0D;
      this.isStuck = false;
   }

   public boolean isDone() {
      return this.path == null || this.path.isDone();
   }

   public boolean isInProgress() {
      return !this.isDone();
   }

   public void stop() {
      this.path = null;
   }

   protected abstract Vector3d getTempMobPos();

   protected abstract boolean canUpdatePath();

   protected boolean isInLiquid() {
      return this.mob.isInWaterOrBubble() || this.mob.isInLava();
   }

   protected void trimPath() {
      if (this.path != null) {
         for(int i = 0; i < this.path.getNodeCount(); ++i) {
            PathPoint pathpoint = this.path.getNode(i);
            PathPoint pathpoint1 = i + 1 < this.path.getNodeCount() ? this.path.getNode(i + 1) : null;
            BlockState blockstate = this.level.getBlockState(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z));
            if (blockstate.is(Blocks.CAULDRON)) {
               this.path.replaceNode(i, pathpoint.cloneAndMove(pathpoint.x, pathpoint.y + 1, pathpoint.z));
               if (pathpoint1 != null && pathpoint.y >= pathpoint1.y) {
                  this.path.replaceNode(i + 1, pathpoint.cloneAndMove(pathpoint1.x, pathpoint.y + 1, pathpoint1.z));
               }
            }
         }

      }
   }

   protected abstract boolean canMoveDirectly(Vector3d p_75493_1_, Vector3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_);

   public boolean isStableDestination(BlockPos p_188555_1_) {
      BlockPos blockpos = p_188555_1_.below();
      return this.level.getBlockState(blockpos).isSolidRender(this.level, blockpos);
   }

   public NodeProcessor getNodeEvaluator() {
      return this.nodeEvaluator;
   }

   public void setCanFloat(boolean p_212239_1_) {
      this.nodeEvaluator.setCanFloat(p_212239_1_);
   }

   public boolean canFloat() {
      return this.nodeEvaluator.canFloat();
   }

   public void recomputePath(BlockPos p_220970_1_) {
      if (this.path != null && !this.path.isDone() && this.path.getNodeCount() != 0) {
         PathPoint pathpoint = this.path.getEndNode();
         Vector3d vector3d = new Vector3d(((double)pathpoint.x + this.mob.getX()) / 2.0D, ((double)pathpoint.y + this.mob.getY()) / 2.0D, ((double)pathpoint.z + this.mob.getZ()) / 2.0D);
         if (p_220970_1_.closerThan(vector3d, (double)(this.path.getNodeCount() - this.path.getNextNodeIndex()))) {
            this.recomputePath();
         }

      }
   }

   public boolean isStuck() {
      return this.isStuck;
   }
}
