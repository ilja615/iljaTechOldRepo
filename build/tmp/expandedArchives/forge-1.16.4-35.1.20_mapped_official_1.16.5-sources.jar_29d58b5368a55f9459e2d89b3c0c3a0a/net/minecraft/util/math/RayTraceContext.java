package net.minecraft.util.math;

import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class RayTraceContext {
   private final Vector3d from;
   private final Vector3d to;
   private final RayTraceContext.BlockMode block;
   private final RayTraceContext.FluidMode fluid;
   private final ISelectionContext collisionContext;

   public RayTraceContext(Vector3d p_i50009_1_, Vector3d p_i50009_2_, RayTraceContext.BlockMode p_i50009_3_, RayTraceContext.FluidMode p_i50009_4_, @javax.annotation.Nullable Entity p_i50009_5_) {
      this.from = p_i50009_1_;
      this.to = p_i50009_2_;
      this.block = p_i50009_3_;
      this.fluid = p_i50009_4_;
      this.collisionContext = p_i50009_5_ == null ? ISelectionContext.empty() : ISelectionContext.of(p_i50009_5_);
   }

   public Vector3d getTo() {
      return this.to;
   }

   public Vector3d getFrom() {
      return this.from;
   }

   public VoxelShape getBlockShape(BlockState p_222251_1_, IBlockReader p_222251_2_, BlockPos p_222251_3_) {
      return this.block.get(p_222251_1_, p_222251_2_, p_222251_3_, this.collisionContext);
   }

   public VoxelShape getFluidShape(FluidState p_222252_1_, IBlockReader p_222252_2_, BlockPos p_222252_3_) {
      return this.fluid.canPick(p_222252_1_) ? p_222252_1_.getShape(p_222252_2_, p_222252_3_) : VoxelShapes.empty();
   }

   public static enum BlockMode implements RayTraceContext.IVoxelProvider {
      COLLIDER(AbstractBlock.AbstractBlockState::getCollisionShape),
      OUTLINE(AbstractBlock.AbstractBlockState::getShape),
      VISUAL(AbstractBlock.AbstractBlockState::getVisualShape);

      private final RayTraceContext.IVoxelProvider shapeGetter;

      private BlockMode(RayTraceContext.IVoxelProvider p_i49926_3_) {
         this.shapeGetter = p_i49926_3_;
      }

      public VoxelShape get(BlockState p_get_1_, IBlockReader p_get_2_, BlockPos p_get_3_, ISelectionContext p_get_4_) {
         return this.shapeGetter.get(p_get_1_, p_get_2_, p_get_3_, p_get_4_);
      }
   }

   public static enum FluidMode {
      NONE((p_222247_0_) -> {
         return false;
      }),
      SOURCE_ONLY(FluidState::isSource),
      ANY((p_222246_0_) -> {
         return !p_222246_0_.isEmpty();
      });

      private final Predicate<FluidState> canPick;

      private FluidMode(Predicate<FluidState> p_i49923_3_) {
         this.canPick = p_i49923_3_;
      }

      public boolean canPick(FluidState p_222248_1_) {
         return this.canPick.test(p_222248_1_);
      }
   }

   public interface IVoxelProvider {
      VoxelShape get(BlockState p_get_1_, IBlockReader p_get_2_, BlockPos p_get_3_, ISelectionContext p_get_4_);
   }
}
