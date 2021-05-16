package net.minecraft.pathfinding;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlaggedPathPoint extends PathPoint {
   private float bestHeuristic = Float.MAX_VALUE;
   private PathPoint bestNode;
   private boolean reached;

   public FlaggedPathPoint(PathPoint p_i51802_1_) {
      super(p_i51802_1_.x, p_i51802_1_.y, p_i51802_1_.z);
   }

   @OnlyIn(Dist.CLIENT)
   public FlaggedPathPoint(int p_i51803_1_, int p_i51803_2_, int p_i51803_3_) {
      super(p_i51803_1_, p_i51803_2_, p_i51803_3_);
   }

   public void updateBest(float p_224761_1_, PathPoint p_224761_2_) {
      if (p_224761_1_ < this.bestHeuristic) {
         this.bestHeuristic = p_224761_1_;
         this.bestNode = p_224761_2_;
      }

   }

   public PathPoint getBestNode() {
      return this.bestNode;
   }

   public void setReached() {
      this.reached = true;
   }

   @OnlyIn(Dist.CLIENT)
   public static FlaggedPathPoint createFromStream(PacketBuffer p_224760_0_) {
      FlaggedPathPoint flaggedpathpoint = new FlaggedPathPoint(p_224760_0_.readInt(), p_224760_0_.readInt(), p_224760_0_.readInt());
      flaggedpathpoint.walkedDistance = p_224760_0_.readFloat();
      flaggedpathpoint.costMalus = p_224760_0_.readFloat();
      flaggedpathpoint.closed = p_224760_0_.readBoolean();
      flaggedpathpoint.type = PathNodeType.values()[p_224760_0_.readInt()];
      flaggedpathpoint.f = p_224760_0_.readFloat();
      return flaggedpathpoint;
   }
}
