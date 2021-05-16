package net.minecraft.world;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ICollisionReader extends IBlockReader {
   WorldBorder getWorldBorder();

   @Nullable
   IBlockReader getChunkForCollisions(int p_225522_1_, int p_225522_2_);

   default boolean isUnobstructed(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return true;
   }

   default boolean isUnobstructed(BlockState p_226663_1_, BlockPos p_226663_2_, ISelectionContext p_226663_3_) {
      VoxelShape voxelshape = p_226663_1_.getCollisionShape(this, p_226663_2_, p_226663_3_);
      return voxelshape.isEmpty() || this.isUnobstructed((Entity)null, voxelshape.move((double)p_226663_2_.getX(), (double)p_226663_2_.getY(), (double)p_226663_2_.getZ()));
   }

   default boolean isUnobstructed(Entity p_226668_1_) {
      return this.isUnobstructed(p_226668_1_, VoxelShapes.create(p_226668_1_.getBoundingBox()));
   }

   default boolean noCollision(AxisAlignedBB p_226664_1_) {
      return this.noCollision((Entity)null, p_226664_1_, (p_234866_0_) -> {
         return true;
      });
   }

   default boolean noCollision(Entity p_226669_1_) {
      return this.noCollision(p_226669_1_, p_226669_1_.getBoundingBox(), (p_234864_0_) -> {
         return true;
      });
   }

   default boolean noCollision(Entity p_226665_1_, AxisAlignedBB p_226665_2_) {
      return this.noCollision(p_226665_1_, p_226665_2_, (p_234863_0_) -> {
         return true;
      });
   }

   default boolean noCollision(@Nullable Entity p_234865_1_, AxisAlignedBB p_234865_2_, Predicate<Entity> p_234865_3_) {
      return this.getCollisions(p_234865_1_, p_234865_2_, p_234865_3_).allMatch(VoxelShape::isEmpty);
   }

   Stream<VoxelShape> getEntityCollisions(@Nullable Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_);

   default Stream<VoxelShape> getCollisions(@Nullable Entity p_234867_1_, AxisAlignedBB p_234867_2_, Predicate<Entity> p_234867_3_) {
      return Stream.concat(this.getBlockCollisions(p_234867_1_, p_234867_2_), this.getEntityCollisions(p_234867_1_, p_234867_2_, p_234867_3_));
   }

   default Stream<VoxelShape> getBlockCollisions(@Nullable Entity p_226666_1_, AxisAlignedBB p_226666_2_) {
      return StreamSupport.stream(new VoxelShapeSpliterator(this, p_226666_1_, p_226666_2_), false);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean noBlockCollision(@Nullable Entity p_242405_1_, AxisAlignedBB p_242405_2_, BiPredicate<BlockState, BlockPos> p_242405_3_) {
      return this.getBlockCollisions(p_242405_1_, p_242405_2_, p_242405_3_).allMatch(VoxelShape::isEmpty);
   }

   default Stream<VoxelShape> getBlockCollisions(@Nullable Entity p_241457_1_, AxisAlignedBB p_241457_2_, BiPredicate<BlockState, BlockPos> p_241457_3_) {
      return StreamSupport.stream(new VoxelShapeSpliterator(this, p_241457_1_, p_241457_2_, p_241457_3_), false);
   }
}
