package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerModel<T extends LivingEntity> extends BipedModel<T> {
   private List<ModelRenderer> cubes = Lists.newArrayList();
   public final ModelRenderer leftSleeve;
   public final ModelRenderer rightSleeve;
   public final ModelRenderer leftPants;
   public final ModelRenderer rightPants;
   public final ModelRenderer jacket;
   private final ModelRenderer cloak;
   private final ModelRenderer ear;
   private final boolean slim;

   public PlayerModel(float p_i46304_1_, boolean p_i46304_2_) {
      super(RenderType::entityTranslucent, p_i46304_1_, 0.0F, 64, 64);
      this.slim = p_i46304_2_;
      this.ear = new ModelRenderer(this, 24, 0);
      this.ear.addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, p_i46304_1_);
      this.cloak = new ModelRenderer(this, 0, 0);
      this.cloak.setTexSize(64, 32);
      this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, p_i46304_1_);
      if (p_i46304_2_) {
         this.leftArm = new ModelRenderer(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p_i46304_1_);
         this.leftArm.setPos(5.0F, 2.5F, 0.0F);
         this.rightArm = new ModelRenderer(this, 40, 16);
         this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p_i46304_1_);
         this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
         this.leftSleeve = new ModelRenderer(this, 48, 48);
         this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
         this.leftSleeve.setPos(5.0F, 2.5F, 0.0F);
         this.rightSleeve = new ModelRenderer(this, 40, 32);
         this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
         this.rightSleeve.setPos(-5.0F, 2.5F, 10.0F);
      } else {
         this.leftArm = new ModelRenderer(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i46304_1_);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.leftSleeve = new ModelRenderer(this, 48, 48);
         this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
         this.leftSleeve.setPos(5.0F, 2.0F, 0.0F);
         this.rightSleeve = new ModelRenderer(this, 40, 32);
         this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
         this.rightSleeve.setPos(-5.0F, 2.0F, 10.0F);
      }

      this.leftLeg = new ModelRenderer(this, 16, 48);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i46304_1_);
      this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
      this.leftPants = new ModelRenderer(this, 0, 48);
      this.leftPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
      this.leftPants.setPos(1.9F, 12.0F, 0.0F);
      this.rightPants = new ModelRenderer(this, 0, 32);
      this.rightPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
      this.rightPants.setPos(-1.9F, 12.0F, 0.0F);
      this.jacket = new ModelRenderer(this, 16, 32);
      this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i46304_1_ + 0.25F);
      this.jacket.setPos(0.0F, 0.0F, 0.0F);
   }

   protected Iterable<ModelRenderer> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
   }

   public void renderEars(MatrixStack p_228287_1_, IVertexBuilder p_228287_2_, int p_228287_3_, int p_228287_4_) {
      this.ear.copyFrom(this.head);
      this.ear.x = 0.0F;
      this.ear.y = 0.0F;
      this.ear.render(p_228287_1_, p_228287_2_, p_228287_3_, p_228287_4_);
   }

   public void renderCloak(MatrixStack p_228289_1_, IVertexBuilder p_228289_2_, int p_228289_3_, int p_228289_4_) {
      this.cloak.render(p_228289_1_, p_228289_2_, p_228289_3_, p_228289_4_);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      if (p_225597_1_.getItemBySlot(EquipmentSlotType.CHEST).isEmpty()) {
         if (p_225597_1_.isCrouching()) {
            this.cloak.z = 1.4F;
            this.cloak.y = 1.85F;
         } else {
            this.cloak.z = 0.0F;
            this.cloak.y = 0.0F;
         }
      } else if (p_225597_1_.isCrouching()) {
         this.cloak.z = 0.3F;
         this.cloak.y = 0.8F;
      } else {
         this.cloak.z = -1.1F;
         this.cloak.y = -0.85F;
      }

   }

   public void setAllVisible(boolean p_178719_1_) {
      super.setAllVisible(p_178719_1_);
      this.leftSleeve.visible = p_178719_1_;
      this.rightSleeve.visible = p_178719_1_;
      this.leftPants.visible = p_178719_1_;
      this.rightPants.visible = p_178719_1_;
      this.jacket.visible = p_178719_1_;
      this.cloak.visible = p_178719_1_;
      this.ear.visible = p_178719_1_;
   }

   public void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_) {
      ModelRenderer modelrenderer = this.getArm(p_225599_1_);
      if (this.slim) {
         float f = 0.5F * (float)(p_225599_1_ == HandSide.RIGHT ? 1 : -1);
         modelrenderer.x += f;
         modelrenderer.translateAndRotate(p_225599_2_);
         modelrenderer.x -= f;
      } else {
         modelrenderer.translateAndRotate(p_225599_2_);
      }

   }

   public ModelRenderer getRandomModelPart(Random p_228288_1_) {
      return this.cubes.get(p_228288_1_.nextInt(this.cubes.size()));
   }

   public void accept(ModelRenderer p_accept_1_) {
      if (this.cubes == null) {
         this.cubes = Lists.newArrayList();
      }

      this.cubes.add(p_accept_1_);
   }
}
