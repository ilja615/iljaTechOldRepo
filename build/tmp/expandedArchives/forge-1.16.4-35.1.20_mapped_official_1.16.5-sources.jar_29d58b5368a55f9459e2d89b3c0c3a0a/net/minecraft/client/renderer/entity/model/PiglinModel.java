package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinAction;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinModel<T extends MobEntity> extends PlayerModel<T> {
   public final ModelRenderer earRight;
   public final ModelRenderer earLeft;
   private final ModelRenderer bodyDefault;
   private final ModelRenderer headDefault;
   private final ModelRenderer leftArmDefault;
   private final ModelRenderer rightArmDefault;

   public PiglinModel(float p_i232336_1_, int p_i232336_2_, int p_i232336_3_) {
      super(p_i232336_1_, false);
      this.texWidth = p_i232336_2_;
      this.texHeight = p_i232336_3_;
      this.body = new ModelRenderer(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i232336_1_);
      this.head = new ModelRenderer(this);
      this.head.texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, p_i232336_1_);
      this.head.texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, p_i232336_1_);
      this.head.texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p_i232336_1_);
      this.head.texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p_i232336_1_);
      this.earRight = new ModelRenderer(this);
      this.earRight.setPos(4.5F, -6.0F, 0.0F);
      this.earRight.texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p_i232336_1_);
      this.head.addChild(this.earRight);
      this.earLeft = new ModelRenderer(this);
      this.earLeft.setPos(-4.5F, -6.0F, 0.0F);
      this.earLeft.texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p_i232336_1_);
      this.head.addChild(this.earLeft);
      this.hat = new ModelRenderer(this);
      this.bodyDefault = this.body.createShallowCopy();
      this.headDefault = this.head.createShallowCopy();
      this.leftArmDefault = this.leftArm.createShallowCopy();
      this.rightArmDefault = this.leftArm.createShallowCopy();
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.body.copyFrom(this.bodyDefault);
      this.head.copyFrom(this.headDefault);
      this.leftArm.copyFrom(this.leftArmDefault);
      this.rightArm.copyFrom(this.rightArmDefault);
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      float f = ((float)Math.PI / 6F);
      float f1 = p_225597_4_ * 0.1F + p_225597_2_ * 0.5F;
      float f2 = 0.08F + p_225597_3_ * 0.4F;
      this.earRight.zRot = (-(float)Math.PI / 6F) - MathHelper.cos(f1 * 1.2F) * f2;
      this.earLeft.zRot = ((float)Math.PI / 6F) + MathHelper.cos(f1) * f2;
      if (p_225597_1_ instanceof AbstractPiglinEntity) {
         AbstractPiglinEntity abstractpiglinentity = (AbstractPiglinEntity)p_225597_1_;
         PiglinAction piglinaction = abstractpiglinentity.getArmPose();
         if (piglinaction == PiglinAction.DANCING) {
            float f3 = p_225597_4_ / 60.0F;
            this.earLeft.zRot = ((float)Math.PI / 6F) + ((float)Math.PI / 180F) * MathHelper.sin(f3 * 30.0F) * 10.0F;
            this.earRight.zRot = (-(float)Math.PI / 6F) - ((float)Math.PI / 180F) * MathHelper.cos(f3 * 30.0F) * 10.0F;
            this.head.x = MathHelper.sin(f3 * 10.0F);
            this.head.y = MathHelper.sin(f3 * 40.0F) + 0.4F;
            this.rightArm.zRot = ((float)Math.PI / 180F) * (70.0F + MathHelper.cos(f3 * 40.0F) * 10.0F);
            this.leftArm.zRot = this.rightArm.zRot * -1.0F;
            this.rightArm.y = MathHelper.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.leftArm.y = MathHelper.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.body.y = MathHelper.sin(f3 * 40.0F) * 0.35F;
         } else if (piglinaction == PiglinAction.ATTACKING_WITH_MELEE_WEAPON && this.attackTime == 0.0F) {
            this.holdWeaponHigh(p_225597_1_);
         } else if (piglinaction == PiglinAction.CROSSBOW_HOLD) {
            ModelHelper.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !p_225597_1_.isLeftHanded());
         } else if (piglinaction == PiglinAction.CROSSBOW_CHARGE) {
            ModelHelper.animateCrossbowCharge(this.rightArm, this.leftArm, p_225597_1_, !p_225597_1_.isLeftHanded());
         } else if (piglinaction == PiglinAction.ADMIRING_ITEM) {
            this.head.xRot = 0.5F;
            this.head.yRot = 0.0F;
            if (p_225597_1_.isLeftHanded()) {
               this.rightArm.yRot = -0.5F;
               this.rightArm.xRot = -0.9F;
            } else {
               this.leftArm.yRot = 0.5F;
               this.leftArm.xRot = -0.9F;
            }
         }
      } else if (p_225597_1_.getType() == EntityType.ZOMBIFIED_PIGLIN) {
         ModelHelper.animateZombieArms(this.leftArm, this.rightArm, p_225597_1_.isAggressive(), this.attackTime, p_225597_4_);
      }

      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      this.hat.copyFrom(this.head);
   }

   protected void setupAttackAnimation(T p_230486_1_, float p_230486_2_) {
      if (this.attackTime > 0.0F && p_230486_1_ instanceof PiglinEntity && ((PiglinEntity)p_230486_1_).getArmPose() == PiglinAction.ATTACKING_WITH_MELEE_WEAPON) {
         ModelHelper.swingWeaponDown(this.rightArm, this.leftArm, p_230486_1_, this.attackTime, p_230486_2_);
      } else {
         super.setupAttackAnimation(p_230486_1_, p_230486_2_);
      }
   }

   private void holdWeaponHigh(T p_239117_1_) {
      if (p_239117_1_.isLeftHanded()) {
         this.leftArm.xRot = -1.8F;
      } else {
         this.rightArm.xRot = -1.8F;
      }

   }
}
