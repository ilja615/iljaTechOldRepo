package net.minecraft.pathfinding;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Region;

public class PathFinder {
   private final PathPoint[] neighbors = new PathPoint[32];
   private final int maxVisitedNodes;
   private final NodeProcessor nodeEvaluator;
   private final PathHeap openSet = new PathHeap();

   public PathFinder(NodeProcessor p_i51280_1_, int p_i51280_2_) {
      this.nodeEvaluator = p_i51280_1_;
      this.maxVisitedNodes = p_i51280_2_;
   }

   @Nullable
   public Path findPath(Region p_227478_1_, MobEntity p_227478_2_, Set<BlockPos> p_227478_3_, float p_227478_4_, int p_227478_5_, float p_227478_6_) {
      this.openSet.clear();
      this.nodeEvaluator.prepare(p_227478_1_, p_227478_2_);
      PathPoint pathpoint = this.nodeEvaluator.getStart();
      Map<FlaggedPathPoint, BlockPos> map = p_227478_3_.stream().collect(Collectors.toMap((p_224782_1_) -> {
         return this.nodeEvaluator.getGoal((double)p_224782_1_.getX(), (double)p_224782_1_.getY(), (double)p_224782_1_.getZ());
      }, Function.identity()));
      Path path = this.findPath(pathpoint, map, p_227478_4_, p_227478_5_, p_227478_6_);
      this.nodeEvaluator.done();
      return path;
   }

   @Nullable
   private Path findPath(PathPoint p_227479_1_, Map<FlaggedPathPoint, BlockPos> p_227479_2_, float p_227479_3_, int p_227479_4_, float p_227479_5_) {
      Set<FlaggedPathPoint> set = p_227479_2_.keySet();
      p_227479_1_.g = 0.0F;
      p_227479_1_.h = this.getBestH(p_227479_1_, set);
      p_227479_1_.f = p_227479_1_.h;
      this.openSet.clear();
      this.openSet.insert(p_227479_1_);
      Set<PathPoint> set1 = ImmutableSet.of();
      int i = 0;
      Set<FlaggedPathPoint> set2 = Sets.newHashSetWithExpectedSize(set.size());
      int j = (int)((float)this.maxVisitedNodes * p_227479_5_);

      while(!this.openSet.isEmpty()) {
         ++i;
         if (i >= j) {
            break;
         }

         PathPoint pathpoint = this.openSet.pop();
         pathpoint.closed = true;

         for(FlaggedPathPoint flaggedpathpoint : set) {
            if (pathpoint.distanceManhattan(flaggedpathpoint) <= (float)p_227479_4_) {
               flaggedpathpoint.setReached();
               set2.add(flaggedpathpoint);
            }
         }

         if (!set2.isEmpty()) {
            break;
         }

         if (!(pathpoint.distanceTo(p_227479_1_) >= p_227479_3_)) {
            int k = this.nodeEvaluator.getNeighbors(this.neighbors, pathpoint);

            for(int l = 0; l < k; ++l) {
               PathPoint pathpoint1 = this.neighbors[l];
               float f = pathpoint.distanceTo(pathpoint1);
               pathpoint1.walkedDistance = pathpoint.walkedDistance + f;
               float f1 = pathpoint.g + f + pathpoint1.costMalus;
               if (pathpoint1.walkedDistance < p_227479_3_ && (!pathpoint1.inOpenSet() || f1 < pathpoint1.g)) {
                  pathpoint1.cameFrom = pathpoint;
                  pathpoint1.g = f1;
                  pathpoint1.h = this.getBestH(pathpoint1, set) * 1.5F;
                  if (pathpoint1.inOpenSet()) {
                     this.openSet.changeCost(pathpoint1, pathpoint1.g + pathpoint1.h);
                  } else {
                     pathpoint1.f = pathpoint1.g + pathpoint1.h;
                     this.openSet.insert(pathpoint1);
                  }
               }
            }
         }
      }

      Optional<Path> optional = !set2.isEmpty() ? set2.stream().map((p_224778_2_) -> {
         return this.reconstructPath(p_224778_2_.getBestNode(), p_227479_2_.get(p_224778_2_), true);
      }).min(Comparator.comparingInt(Path::getNodeCount)) : set.stream().map((p_224777_2_) -> {
         return this.reconstructPath(p_224777_2_.getBestNode(), p_227479_2_.get(p_224777_2_), false);
      }).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
      return !optional.isPresent() ? null : optional.get();
   }

   private float getBestH(PathPoint p_224776_1_, Set<FlaggedPathPoint> p_224776_2_) {
      float f = Float.MAX_VALUE;

      for(FlaggedPathPoint flaggedpathpoint : p_224776_2_) {
         float f1 = p_224776_1_.distanceTo(flaggedpathpoint);
         flaggedpathpoint.updateBest(f1, p_224776_1_);
         f = Math.min(f1, f);
      }

      return f;
   }

   private Path reconstructPath(PathPoint p_224780_1_, BlockPos p_224780_2_, boolean p_224780_3_) {
      List<PathPoint> list = Lists.newArrayList();
      PathPoint pathpoint = p_224780_1_;
      list.add(0, p_224780_1_);

      while(pathpoint.cameFrom != null) {
         pathpoint = pathpoint.cameFrom;
         list.add(0, pathpoint);
      }

      return new Path(list, p_224780_2_, p_224780_3_);
   }
}
