package net.minecraft.pathfinding;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public abstract class NodeProcessor {
   protected Region level;
   protected MobEntity mob;
   protected final Int2ObjectMap<PathPoint> nodes = new Int2ObjectOpenHashMap<>();
   protected int entityWidth;
   protected int entityHeight;
   protected int entityDepth;
   protected boolean canPassDoors;
   protected boolean canOpenDoors;
   protected boolean canFloat;

   public void prepare(Region p_225578_1_, MobEntity p_225578_2_) {
      this.level = p_225578_1_;
      this.mob = p_225578_2_;
      this.nodes.clear();
      this.entityWidth = MathHelper.floor(p_225578_2_.getBbWidth() + 1.0F);
      this.entityHeight = MathHelper.floor(p_225578_2_.getBbHeight() + 1.0F);
      this.entityDepth = MathHelper.floor(p_225578_2_.getBbWidth() + 1.0F);
   }

   public void done() {
      this.level = null;
      this.mob = null;
   }

   protected PathPoint getNode(BlockPos p_237223_1_) {
      return this.getNode(p_237223_1_.getX(), p_237223_1_.getY(), p_237223_1_.getZ());
   }

   protected PathPoint getNode(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      return this.nodes.computeIfAbsent(PathPoint.createHash(p_176159_1_, p_176159_2_, p_176159_3_), (p_215743_3_) -> {
         return new PathPoint(p_176159_1_, p_176159_2_, p_176159_3_);
      });
   }

   public abstract PathPoint getStart();

   public abstract FlaggedPathPoint getGoal(double p_224768_1_, double p_224768_3_, double p_224768_5_);

   public abstract int getNeighbors(PathPoint[] p_222859_1_, PathPoint p_222859_2_);

   public abstract PathNodeType getBlockPathType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, MobEntity p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_);

   public abstract PathNodeType getBlockPathType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_);

   public void setCanPassDoors(boolean p_186317_1_) {
      this.canPassDoors = p_186317_1_;
   }

   public void setCanOpenDoors(boolean p_186321_1_) {
      this.canOpenDoors = p_186321_1_;
   }

   public void setCanFloat(boolean p_186316_1_) {
      this.canFloat = p_186316_1_;
   }

   public boolean canPassDoors() {
      return this.canPassDoors;
   }

   public boolean canOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean canFloat() {
      return this.canFloat;
   }
}
