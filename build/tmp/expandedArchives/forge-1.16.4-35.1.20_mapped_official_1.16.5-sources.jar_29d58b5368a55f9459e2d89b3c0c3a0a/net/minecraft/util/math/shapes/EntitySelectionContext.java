package net.minecraft.util.math.shapes;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class EntitySelectionContext implements ISelectionContext {
   protected static final ISelectionContext EMPTY = new EntitySelectionContext(false, -Double.MAX_VALUE, Items.AIR, (p_237495_0_) -> {
      return false;
   }) {
      public boolean isAbove(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
         return p_216378_3_;
      }
   };
   private final boolean descending;
   private final double entityBottom;
   private final Item heldItem;
   private final Predicate<Fluid> canStandOnFluid;

   protected EntitySelectionContext(boolean p_i232177_1_, double p_i232177_2_, Item p_i232177_4_, Predicate<Fluid> p_i232177_5_) {
       this(null, p_i232177_1_, p_i232177_2_, p_i232177_4_, p_i232177_5_);
   }

   protected EntitySelectionContext(@javax.annotation.Nullable Entity entity, boolean p_i232177_1_, double p_i232177_2_, Item p_i232177_4_, Predicate<Fluid> p_i232177_5_) {
      this.entity = entity;
      this.descending = p_i232177_1_;
      this.entityBottom = p_i232177_2_;
      this.heldItem = p_i232177_4_;
      this.canStandOnFluid = p_i232177_5_;
   }

   @Deprecated
   protected EntitySelectionContext(Entity p_i51182_1_) {
      this(p_i51182_1_, p_i51182_1_.isDescending(), p_i51182_1_.getY(), p_i51182_1_ instanceof LivingEntity ? ((LivingEntity)p_i51182_1_).getMainHandItem().getItem() : Items.AIR, p_i51182_1_ instanceof LivingEntity ? ((LivingEntity)p_i51182_1_)::canStandOnFluid : (p_237494_0_) -> {
         return false;
      });
   }

   public boolean isHoldingItem(Item p_216375_1_) {
      return this.heldItem == p_216375_1_;
   }

   public boolean canStandOnFluid(FluidState p_230426_1_, FlowingFluid p_230426_2_) {
      return this.canStandOnFluid.test(p_230426_2_) && !p_230426_1_.getType().isSame(p_230426_2_);
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
      return this.entityBottom > (double)p_216378_2_.getY() + p_216378_1_.max(Direction.Axis.Y) - (double)1.0E-5F;
   }

   private final @javax.annotation.Nullable Entity entity;

   @Override
   public @javax.annotation.Nullable Entity getEntity() {
      return entity;
   }
}
