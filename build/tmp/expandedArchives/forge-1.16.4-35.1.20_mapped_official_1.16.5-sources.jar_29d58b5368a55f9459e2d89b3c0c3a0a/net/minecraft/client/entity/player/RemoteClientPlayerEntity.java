package net.minecraft.client.entity.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemoteClientPlayerEntity extends AbstractClientPlayerEntity {
   public RemoteClientPlayerEntity(ClientWorld p_i50989_1_, GameProfile p_i50989_2_) {
      super(p_i50989_1_, p_i50989_2_);
      this.maxUpStep = 1.0F;
      this.noPhysics = true;
   }

   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getViewScale();
      return p_70112_1_ < d0 * d0;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, p_70097_1_, p_70097_2_);
      return true;
   }

   public void tick() {
      super.tick();
      this.calculateEntityAnimation(this, false);
   }

   public void aiStep() {
      if (this.lerpSteps > 0) {
         double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
         double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
         double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
         this.yRot = (float)((double)this.yRot + MathHelper.wrapDegrees(this.lerpYRot - (double)this.yRot) / (double)this.lerpSteps);
         this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
         --this.lerpSteps;
         this.setPos(d0, d1, d2);
         this.setRot(this.yRot, this.xRot);
      }

      if (this.lerpHeadSteps > 0) {
         this.yHeadRot = (float)((double)this.yHeadRot + MathHelper.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
         --this.lerpHeadSteps;
      }

      this.oBob = this.bob;
      this.updateSwingTime();
      float f1;
      if (this.onGround && !this.isDeadOrDying()) {
         f1 = Math.min(0.1F, MathHelper.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
      } else {
         f1 = 0.0F;
      }

      if (!this.onGround && !this.isDeadOrDying()) {
         float f2 = (float)Math.atan(-this.getDeltaMovement().y * (double)0.2F) * 15.0F;
      } else {
         float f = 0.0F;
      }

      this.bob += (f1 - this.bob) * 0.4F;
      this.level.getProfiler().push("push");
      this.pushEntities();
      this.level.getProfiler().pop();
   }

   protected void updatePlayerPose() {
   }

   public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      Minecraft minecraft = Minecraft.getInstance();
      if (!minecraft.isBlocked(p_145747_2_)) {
         minecraft.gui.getChat().addMessage(p_145747_1_);
      }

   }
}
