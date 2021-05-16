package net.minecraft.entity.item.minecart;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
   public MinecartEntity(EntityType<?> p_i50126_1_, World p_i50126_2_) {
      super(p_i50126_1_, p_i50126_2_);
   }

   public MinecartEntity(World p_i1723_1_, double p_i1723_2_, double p_i1723_4_, double p_i1723_6_) {
      super(EntityType.MINECART, p_i1723_1_, p_i1723_2_, p_i1723_4_, p_i1723_6_);
   }

   public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      ActionResultType ret = super.interact(p_184230_1_, p_184230_2_);
      if (ret.consumesAction()) return ret;
      if (p_184230_1_.isSecondaryUseActive()) {
         return ActionResultType.PASS;
      } else if (this.isVehicle()) {
         return ActionResultType.PASS;
      } else if (!this.level.isClientSide) {
         return p_184230_1_.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_) {
         if (this.isVehicle()) {
            this.ejectPassengers();
         }

         if (this.getHurtTime() == 0) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(50.0F);
            this.markHurt();
         }
      }

   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.RIDEABLE;
   }
}
