package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Path {
   private final List<PathPoint> nodes;
   private PathPoint[] openSet = new PathPoint[0];
   private PathPoint[] closedSet = new PathPoint[0];
   @OnlyIn(Dist.CLIENT)
   private Set<FlaggedPathPoint> targetNodes;
   private int nextNodeIndex;
   private final BlockPos target;
   private final float distToTarget;
   private final boolean reached;

   public Path(List<PathPoint> p_i51804_1_, BlockPos p_i51804_2_, boolean p_i51804_3_) {
      this.nodes = p_i51804_1_;
      this.target = p_i51804_2_;
      this.distToTarget = p_i51804_1_.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).distanceManhattan(this.target);
      this.reached = p_i51804_3_;
   }

   public void advance() {
      ++this.nextNodeIndex;
   }

   public boolean notStarted() {
      return this.nextNodeIndex <= 0;
   }

   public boolean isDone() {
      return this.nextNodeIndex >= this.nodes.size();
   }

   @Nullable
   public PathPoint getEndNode() {
      return !this.nodes.isEmpty() ? this.nodes.get(this.nodes.size() - 1) : null;
   }

   public PathPoint getNode(int p_75877_1_) {
      return this.nodes.get(p_75877_1_);
   }

   public void truncateNodes(int p_215747_1_) {
      if (this.nodes.size() > p_215747_1_) {
         this.nodes.subList(p_215747_1_, this.nodes.size()).clear();
      }

   }

   public void replaceNode(int p_186309_1_, PathPoint p_186309_2_) {
      this.nodes.set(p_186309_1_, p_186309_2_);
   }

   public int getNodeCount() {
      return this.nodes.size();
   }

   public int getNextNodeIndex() {
      return this.nextNodeIndex;
   }

   public void setNextNodeIndex(int p_75872_1_) {
      this.nextNodeIndex = p_75872_1_;
   }

   public Vector3d getEntityPosAtNode(Entity p_75881_1_, int p_75881_2_) {
      PathPoint pathpoint = this.nodes.get(p_75881_2_);
      double d0 = (double)pathpoint.x + (double)((int)(p_75881_1_.getBbWidth() + 1.0F)) * 0.5D;
      double d1 = (double)pathpoint.y;
      double d2 = (double)pathpoint.z + (double)((int)(p_75881_1_.getBbWidth() + 1.0F)) * 0.5D;
      return new Vector3d(d0, d1, d2);
   }

   public BlockPos getNodePos(int p_242947_1_) {
      return this.nodes.get(p_242947_1_).asBlockPos();
   }

   public Vector3d getNextEntityPos(Entity p_75878_1_) {
      return this.getEntityPosAtNode(p_75878_1_, this.nextNodeIndex);
   }

   public BlockPos getNextNodePos() {
      return this.nodes.get(this.nextNodeIndex).asBlockPos();
   }

   public PathPoint getNextNode() {
      return this.nodes.get(this.nextNodeIndex);
   }

   @Nullable
   public PathPoint getPreviousNode() {
      return this.nextNodeIndex > 0 ? this.nodes.get(this.nextNodeIndex - 1) : null;
   }

   public boolean sameAs(@Nullable Path p_75876_1_) {
      if (p_75876_1_ == null) {
         return false;
      } else if (p_75876_1_.nodes.size() != this.nodes.size()) {
         return false;
      } else {
         for(int i = 0; i < this.nodes.size(); ++i) {
            PathPoint pathpoint = this.nodes.get(i);
            PathPoint pathpoint1 = p_75876_1_.nodes.get(i);
            if (pathpoint.x != pathpoint1.x || pathpoint.y != pathpoint1.y || pathpoint.z != pathpoint1.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canReach() {
      return this.reached;
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getOpenSet() {
      return this.openSet;
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getClosedSet() {
      return this.closedSet;
   }

   @OnlyIn(Dist.CLIENT)
   public static Path createFromStream(PacketBuffer p_186311_0_) {
      boolean flag = p_186311_0_.readBoolean();
      int i = p_186311_0_.readInt();
      int j = p_186311_0_.readInt();
      Set<FlaggedPathPoint> set = Sets.newHashSet();

      for(int k = 0; k < j; ++k) {
         set.add(FlaggedPathPoint.createFromStream(p_186311_0_));
      }

      BlockPos blockpos = new BlockPos(p_186311_0_.readInt(), p_186311_0_.readInt(), p_186311_0_.readInt());
      List<PathPoint> list = Lists.newArrayList();
      int l = p_186311_0_.readInt();

      for(int i1 = 0; i1 < l; ++i1) {
         list.add(PathPoint.createFromStream(p_186311_0_));
      }

      PathPoint[] apathpoint = new PathPoint[p_186311_0_.readInt()];

      for(int j1 = 0; j1 < apathpoint.length; ++j1) {
         apathpoint[j1] = PathPoint.createFromStream(p_186311_0_);
      }

      PathPoint[] apathpoint1 = new PathPoint[p_186311_0_.readInt()];

      for(int k1 = 0; k1 < apathpoint1.length; ++k1) {
         apathpoint1[k1] = PathPoint.createFromStream(p_186311_0_);
      }

      Path path = new Path(list, blockpos, flag);
      path.openSet = apathpoint;
      path.closedSet = apathpoint1;
      path.targetNodes = set;
      path.nextNodeIndex = i;
      return path;
   }

   public String toString() {
      return "Path(length=" + this.nodes.size() + ")";
   }

   public BlockPos getTarget() {
      return this.target;
   }

   public float getDistToTarget() {
      return this.distToTarget;
   }
}
