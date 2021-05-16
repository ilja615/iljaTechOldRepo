package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderer {
   private static final RenderType MAP_BACKGROUND = RenderType.text(new ResourceLocation("textures/map/map_background.png"));
   private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(new ResourceLocation("textures/map/map_background_checkerboard.png"));
   private final Minecraft minecraft;
   private ItemStack mainHandItem = ItemStack.EMPTY;
   private ItemStack offHandItem = ItemStack.EMPTY;
   private float mainHandHeight;
   private float oMainHandHeight;
   private float offHandHeight;
   private float oOffHandHeight;
   private final EntityRendererManager entityRenderDispatcher;
   private final ItemRenderer itemRenderer;

   public FirstPersonRenderer(Minecraft p_i1247_1_) {
      this.minecraft = p_i1247_1_;
      this.entityRenderDispatcher = p_i1247_1_.getEntityRenderDispatcher();
      this.itemRenderer = p_i1247_1_.getItemRenderer();
   }

   public void renderItem(LivingEntity p_228397_1_, ItemStack p_228397_2_, ItemCameraTransforms.TransformType p_228397_3_, boolean p_228397_4_, MatrixStack p_228397_5_, IRenderTypeBuffer p_228397_6_, int p_228397_7_) {
      if (!p_228397_2_.isEmpty()) {
         this.itemRenderer.renderStatic(p_228397_1_, p_228397_2_, p_228397_3_, p_228397_4_, p_228397_5_, p_228397_6_, p_228397_1_.level, p_228397_7_, OverlayTexture.NO_OVERLAY);
      }
   }

   private float calculateMapTilt(float p_178100_1_) {
      float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      return -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
   }

   private void renderMapHand(MatrixStack p_228403_1_, IRenderTypeBuffer p_228403_2_, int p_228403_3_, HandSide p_228403_4_) {
      this.minecraft.getTextureManager().bind(this.minecraft.player.getSkinTextureLocation());
      PlayerRenderer playerrenderer = (PlayerRenderer)this.entityRenderDispatcher.<AbstractClientPlayerEntity>getRenderer(this.minecraft.player);
      p_228403_1_.pushPose();
      float f = p_228403_4_ == HandSide.RIGHT ? 1.0F : -1.0F;
      p_228403_1_.mulPose(Vector3f.YP.rotationDegrees(92.0F));
      p_228403_1_.mulPose(Vector3f.XP.rotationDegrees(45.0F));
      p_228403_1_.mulPose(Vector3f.ZP.rotationDegrees(f * -41.0F));
      p_228403_1_.translate((double)(f * 0.3F), (double)-1.1F, (double)0.45F);
      if (p_228403_4_ == HandSide.RIGHT) {
         playerrenderer.renderRightHand(p_228403_1_, p_228403_2_, p_228403_3_, this.minecraft.player);
      } else {
         playerrenderer.renderLeftHand(p_228403_1_, p_228403_2_, p_228403_3_, this.minecraft.player);
      }

      p_228403_1_.popPose();
   }

   private void renderOneHandedMap(MatrixStack p_228402_1_, IRenderTypeBuffer p_228402_2_, int p_228402_3_, float p_228402_4_, HandSide p_228402_5_, float p_228402_6_, ItemStack p_228402_7_) {
      float f = p_228402_5_ == HandSide.RIGHT ? 1.0F : -1.0F;
      p_228402_1_.translate((double)(f * 0.125F), -0.125D, 0.0D);
      if (!this.minecraft.player.isInvisible()) {
         p_228402_1_.pushPose();
         p_228402_1_.mulPose(Vector3f.ZP.rotationDegrees(f * 10.0F));
         this.renderPlayerArm(p_228402_1_, p_228402_2_, p_228402_3_, p_228402_4_, p_228402_6_, p_228402_5_);
         p_228402_1_.popPose();
      }

      p_228402_1_.pushPose();
      p_228402_1_.translate((double)(f * 0.51F), (double)(-0.08F + p_228402_4_ * -1.2F), -0.75D);
      float f1 = MathHelper.sqrt(p_228402_6_);
      float f2 = MathHelper.sin(f1 * (float)Math.PI);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
      float f5 = -0.3F * MathHelper.sin(p_228402_6_ * (float)Math.PI);
      p_228402_1_.translate((double)(f * f3), (double)(f4 - 0.3F * f2), (double)f5);
      p_228402_1_.mulPose(Vector3f.XP.rotationDegrees(f2 * -45.0F));
      p_228402_1_.mulPose(Vector3f.YP.rotationDegrees(f * f2 * -30.0F));
      this.renderMap(p_228402_1_, p_228402_2_, p_228402_3_, p_228402_7_);
      p_228402_1_.popPose();
   }

   private void renderTwoHandedMap(MatrixStack p_228400_1_, IRenderTypeBuffer p_228400_2_, int p_228400_3_, float p_228400_4_, float p_228400_5_, float p_228400_6_) {
      float f = MathHelper.sqrt(p_228400_6_);
      float f1 = -0.2F * MathHelper.sin(p_228400_6_ * (float)Math.PI);
      float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
      p_228400_1_.translate(0.0D, (double)(-f1 / 2.0F), (double)f2);
      float f3 = this.calculateMapTilt(p_228400_4_);
      p_228400_1_.translate(0.0D, (double)(0.04F + p_228400_5_ * -1.2F + f3 * -0.5F), (double)-0.72F);
      p_228400_1_.mulPose(Vector3f.XP.rotationDegrees(f3 * -85.0F));
      if (!this.minecraft.player.isInvisible()) {
         p_228400_1_.pushPose();
         p_228400_1_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
         this.renderMapHand(p_228400_1_, p_228400_2_, p_228400_3_, HandSide.RIGHT);
         this.renderMapHand(p_228400_1_, p_228400_2_, p_228400_3_, HandSide.LEFT);
         p_228400_1_.popPose();
      }

      float f4 = MathHelper.sin(f * (float)Math.PI);
      p_228400_1_.mulPose(Vector3f.XP.rotationDegrees(f4 * 20.0F));
      p_228400_1_.scale(2.0F, 2.0F, 2.0F);
      this.renderMap(p_228400_1_, p_228400_2_, p_228400_3_, this.mainHandItem);
   }

   private void renderMap(MatrixStack p_228404_1_, IRenderTypeBuffer p_228404_2_, int p_228404_3_, ItemStack p_228404_4_) {
      p_228404_1_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      p_228404_1_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      p_228404_1_.scale(0.38F, 0.38F, 0.38F);
      p_228404_1_.translate(-0.5D, -0.5D, 0.0D);
      p_228404_1_.scale(0.0078125F, 0.0078125F, 0.0078125F);
      MapData mapdata = FilledMapItem.getOrCreateSavedData(p_228404_4_, this.minecraft.level);
      IVertexBuilder ivertexbuilder = p_228404_2_.getBuffer(mapdata == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
      Matrix4f matrix4f = p_228404_1_.last().pose();
      ivertexbuilder.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_228404_3_).endVertex();
      ivertexbuilder.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_228404_3_).endVertex();
      ivertexbuilder.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_228404_3_).endVertex();
      ivertexbuilder.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_228404_3_).endVertex();
      if (mapdata != null) {
         this.minecraft.gameRenderer.getMapRenderer().render(p_228404_1_, p_228404_2_, mapdata, false, p_228404_3_);
      }

   }

   private void renderPlayerArm(MatrixStack p_228401_1_, IRenderTypeBuffer p_228401_2_, int p_228401_3_, float p_228401_4_, float p_228401_5_, HandSide p_228401_6_) {
      boolean flag = p_228401_6_ != HandSide.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = MathHelper.sqrt(p_228401_5_);
      float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
      float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
      float f4 = -0.4F * MathHelper.sin(p_228401_5_ * (float)Math.PI);
      p_228401_1_.translate((double)(f * (f2 + 0.64000005F)), (double)(f3 + -0.6F + p_228401_4_ * -0.6F), (double)(f4 + -0.71999997F));
      p_228401_1_.mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
      float f5 = MathHelper.sin(p_228401_5_ * p_228401_5_ * (float)Math.PI);
      float f6 = MathHelper.sin(f1 * (float)Math.PI);
      p_228401_1_.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
      p_228401_1_.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
      AbstractClientPlayerEntity abstractclientplayerentity = this.minecraft.player;
      this.minecraft.getTextureManager().bind(abstractclientplayerentity.getSkinTextureLocation());
      p_228401_1_.translate((double)(f * -1.0F), (double)3.6F, 3.5D);
      p_228401_1_.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
      p_228401_1_.mulPose(Vector3f.XP.rotationDegrees(200.0F));
      p_228401_1_.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
      p_228401_1_.translate((double)(f * 5.6F), 0.0D, 0.0D);
      PlayerRenderer playerrenderer = (PlayerRenderer)this.entityRenderDispatcher.<AbstractClientPlayerEntity>getRenderer(abstractclientplayerentity);
      if (flag) {
         playerrenderer.renderRightHand(p_228401_1_, p_228401_2_, p_228401_3_, abstractclientplayerentity);
      } else {
         playerrenderer.renderLeftHand(p_228401_1_, p_228401_2_, p_228401_3_, abstractclientplayerentity);
      }

   }

   private void applyEatTransform(MatrixStack p_228398_1_, float p_228398_2_, HandSide p_228398_3_, ItemStack p_228398_4_) {
      float f = (float)this.minecraft.player.getUseItemRemainingTicks() - p_228398_2_ + 1.0F;
      float f1 = f / (float)p_228398_4_.getUseDuration();
      if (f1 < 0.8F) {
         float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
         p_228398_1_.translate(0.0D, (double)f2, 0.0D);
      }

      float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
      int i = p_228398_3_ == HandSide.RIGHT ? 1 : -1;
      p_228398_1_.translate((double)(f3 * 0.6F * (float)i), (double)(f3 * -0.5F), (double)(f3 * 0.0F));
      p_228398_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * f3 * 90.0F));
      p_228398_1_.mulPose(Vector3f.XP.rotationDegrees(f3 * 10.0F));
      p_228398_1_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f3 * 30.0F));
   }

   private void applyItemArmAttackTransform(MatrixStack p_228399_1_, HandSide p_228399_2_, float p_228399_3_) {
      int i = p_228399_2_ == HandSide.RIGHT ? 1 : -1;
      float f = MathHelper.sin(p_228399_3_ * p_228399_3_ * (float)Math.PI);
      p_228399_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
      float f1 = MathHelper.sin(MathHelper.sqrt(p_228399_3_) * (float)Math.PI);
      p_228399_1_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
      p_228399_1_.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
      p_228399_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));
   }

   private void applyItemArmTransform(MatrixStack p_228406_1_, HandSide p_228406_2_, float p_228406_3_) {
      int i = p_228406_2_ == HandSide.RIGHT ? 1 : -1;
      p_228406_1_.translate((double)((float)i * 0.56F), (double)(-0.52F + p_228406_3_ * -0.6F), (double)-0.72F);
   }

   public void renderHandsWithItems(float p_228396_1_, MatrixStack p_228396_2_, IRenderTypeBuffer.Impl p_228396_3_, ClientPlayerEntity p_228396_4_, int p_228396_5_) {
      float f = p_228396_4_.getAttackAnim(p_228396_1_);
      Hand hand = MoreObjects.firstNonNull(p_228396_4_.swingingArm, Hand.MAIN_HAND);
      float f1 = MathHelper.lerp(p_228396_1_, p_228396_4_.xRotO, p_228396_4_.xRot);
      boolean flag = true;
      boolean flag1 = true;
      if (p_228396_4_.isUsingItem()) {
         ItemStack itemstack = p_228396_4_.getUseItem();
         if (itemstack.getItem() instanceof net.minecraft.item.ShootableItem) {
            flag = p_228396_4_.getUsedItemHand() == Hand.MAIN_HAND;
            flag1 = !flag;
         }

         Hand hand1 = p_228396_4_.getUsedItemHand();
         if (hand1 == Hand.MAIN_HAND) {
            ItemStack itemstack1 = p_228396_4_.getOffhandItem();
            if (itemstack1.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack1)) {
               flag1 = false;
            }
         }
      } else {
         ItemStack itemstack2 = p_228396_4_.getMainHandItem();
         ItemStack itemstack3 = p_228396_4_.getOffhandItem();
         if (itemstack2.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack2)) {
            flag1 = !flag;
         }

         if (itemstack3.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack3)) {
            flag = !itemstack2.isEmpty();
            flag1 = !flag;
         }
      }

      float f3 = MathHelper.lerp(p_228396_1_, p_228396_4_.xBobO, p_228396_4_.xBob);
      float f4 = MathHelper.lerp(p_228396_1_, p_228396_4_.yBobO, p_228396_4_.yBob);
      p_228396_2_.mulPose(Vector3f.XP.rotationDegrees((p_228396_4_.getViewXRot(p_228396_1_) - f3) * 0.1F));
      p_228396_2_.mulPose(Vector3f.YP.rotationDegrees((p_228396_4_.getViewYRot(p_228396_1_) - f4) * 0.1F));
      if (flag) {
         float f5 = hand == Hand.MAIN_HAND ? f : 0.0F;
         float f2 = 1.0F - MathHelper.lerp(p_228396_1_, this.oMainHandHeight, this.mainHandHeight);
         if(!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(Hand.MAIN_HAND, p_228396_2_, p_228396_3_, p_228396_5_, p_228396_1_, f1, f5, f2, this.mainHandItem))
         this.renderArmWithItem(p_228396_4_, p_228396_1_, f1, Hand.MAIN_HAND, f5, this.mainHandItem, f2, p_228396_2_, p_228396_3_, p_228396_5_);
      }

      if (flag1) {
         float f6 = hand == Hand.OFF_HAND ? f : 0.0F;
         float f7 = 1.0F - MathHelper.lerp(p_228396_1_, this.oOffHandHeight, this.offHandHeight);
         if(!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(Hand.OFF_HAND, p_228396_2_, p_228396_3_, p_228396_5_, p_228396_1_, f1, f6, f7, this.offHandItem))
         this.renderArmWithItem(p_228396_4_, p_228396_1_, f1, Hand.OFF_HAND, f6, this.offHandItem, f7, p_228396_2_, p_228396_3_, p_228396_5_);
      }

      p_228396_3_.endBatch();
   }

   private void renderArmWithItem(AbstractClientPlayerEntity p_228405_1_, float p_228405_2_, float p_228405_3_, Hand p_228405_4_, float p_228405_5_, ItemStack p_228405_6_, float p_228405_7_, MatrixStack p_228405_8_, IRenderTypeBuffer p_228405_9_, int p_228405_10_) {
      boolean flag = p_228405_4_ == Hand.MAIN_HAND;
      HandSide handside = flag ? p_228405_1_.getMainArm() : p_228405_1_.getMainArm().getOpposite();
      p_228405_8_.pushPose();
      if (p_228405_6_.isEmpty()) {
         if (flag && !p_228405_1_.isInvisible()) {
            this.renderPlayerArm(p_228405_8_, p_228405_9_, p_228405_10_, p_228405_7_, p_228405_5_, handside);
         }
      } else if (p_228405_6_.getItem() instanceof FilledMapItem) {
         if (flag && this.offHandItem.isEmpty()) {
            this.renderTwoHandedMap(p_228405_8_, p_228405_9_, p_228405_10_, p_228405_3_, p_228405_7_, p_228405_5_);
         } else {
            this.renderOneHandedMap(p_228405_8_, p_228405_9_, p_228405_10_, p_228405_7_, handside, p_228405_5_, p_228405_6_);
         }
      } else if (p_228405_6_.getItem() instanceof CrossbowItem) {
         boolean flag1 = CrossbowItem.isCharged(p_228405_6_);
         boolean flag2 = handside == HandSide.RIGHT;
         int i = flag2 ? 1 : -1;
         if (p_228405_1_.isUsingItem() && p_228405_1_.getUseItemRemainingTicks() > 0 && p_228405_1_.getUsedItemHand() == p_228405_4_) {
            this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
            p_228405_8_.translate((double)((float)i * -0.4785682F), (double)-0.094387F, (double)0.05731531F);
            p_228405_8_.mulPose(Vector3f.XP.rotationDegrees(-11.935F));
            p_228405_8_.mulPose(Vector3f.YP.rotationDegrees((float)i * 65.3F));
            p_228405_8_.mulPose(Vector3f.ZP.rotationDegrees((float)i * -9.785F));
            float f9 = (float)p_228405_6_.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - p_228405_2_ + 1.0F);
            float f13 = f9 / (float)CrossbowItem.getChargeDuration(p_228405_6_);
            if (f13 > 1.0F) {
               f13 = 1.0F;
            }

            if (f13 > 0.1F) {
               float f16 = MathHelper.sin((f9 - 0.1F) * 1.3F);
               float f3 = f13 - 0.1F;
               float f4 = f16 * f3;
               p_228405_8_.translate((double)(f4 * 0.0F), (double)(f4 * 0.004F), (double)(f4 * 0.0F));
            }

            p_228405_8_.translate((double)(f13 * 0.0F), (double)(f13 * 0.0F), (double)(f13 * 0.04F));
            p_228405_8_.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
            p_228405_8_.mulPose(Vector3f.YN.rotationDegrees((float)i * 45.0F));
         } else {
            float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_228405_5_) * (float)Math.PI);
            float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_228405_5_) * ((float)Math.PI * 2F));
            float f2 = -0.2F * MathHelper.sin(p_228405_5_ * (float)Math.PI);
            p_228405_8_.translate((double)((float)i * f), (double)f1, (double)f2);
            this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
            this.applyItemArmAttackTransform(p_228405_8_, handside, p_228405_5_);
            if (flag1 && p_228405_5_ < 0.001F) {
               p_228405_8_.translate((double)((float)i * -0.641864F), 0.0D, 0.0D);
               p_228405_8_.mulPose(Vector3f.YP.rotationDegrees((float)i * 10.0F));
            }
         }

         this.renderItem(p_228405_1_, p_228405_6_, flag2 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag2, p_228405_8_, p_228405_9_, p_228405_10_);
      } else {
         boolean flag3 = handside == HandSide.RIGHT;
         if (p_228405_1_.isUsingItem() && p_228405_1_.getUseItemRemainingTicks() > 0 && p_228405_1_.getUsedItemHand() == p_228405_4_) {
            int k = flag3 ? 1 : -1;
            switch(p_228405_6_.getUseAnimation()) {
            case NONE:
               this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
               break;
            case EAT:
            case DRINK:
               this.applyEatTransform(p_228405_8_, p_228405_2_, handside, p_228405_6_);
               this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
               break;
            case BLOCK:
               this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
               break;
            case BOW:
               this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
               p_228405_8_.translate((double)((float)k * -0.2785682F), (double)0.18344387F, (double)0.15731531F);
               p_228405_8_.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
               p_228405_8_.mulPose(Vector3f.YP.rotationDegrees((float)k * 35.3F));
               p_228405_8_.mulPose(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
               float f8 = (float)p_228405_6_.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - p_228405_2_ + 1.0F);
               float f12 = f8 / 20.0F;
               f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
               if (f12 > 1.0F) {
                  f12 = 1.0F;
               }

               if (f12 > 0.1F) {
                  float f15 = MathHelper.sin((f8 - 0.1F) * 1.3F);
                  float f18 = f12 - 0.1F;
                  float f20 = f15 * f18;
                  p_228405_8_.translate((double)(f20 * 0.0F), (double)(f20 * 0.004F), (double)(f20 * 0.0F));
               }

               p_228405_8_.translate((double)(f12 * 0.0F), (double)(f12 * 0.0F), (double)(f12 * 0.04F));
               p_228405_8_.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
               p_228405_8_.mulPose(Vector3f.YN.rotationDegrees((float)k * 45.0F));
               break;
            case SPEAR:
               this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
               p_228405_8_.translate((double)((float)k * -0.5F), (double)0.7F, (double)0.1F);
               p_228405_8_.mulPose(Vector3f.XP.rotationDegrees(-55.0F));
               p_228405_8_.mulPose(Vector3f.YP.rotationDegrees((float)k * 35.3F));
               p_228405_8_.mulPose(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
               float f7 = (float)p_228405_6_.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - p_228405_2_ + 1.0F);
               float f11 = f7 / 10.0F;
               if (f11 > 1.0F) {
                  f11 = 1.0F;
               }

               if (f11 > 0.1F) {
                  float f14 = MathHelper.sin((f7 - 0.1F) * 1.3F);
                  float f17 = f11 - 0.1F;
                  float f19 = f14 * f17;
                  p_228405_8_.translate((double)(f19 * 0.0F), (double)(f19 * 0.004F), (double)(f19 * 0.0F));
               }

               p_228405_8_.translate(0.0D, 0.0D, (double)(f11 * 0.2F));
               p_228405_8_.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
               p_228405_8_.mulPose(Vector3f.YN.rotationDegrees((float)k * 45.0F));
            }
         } else if (p_228405_1_.isAutoSpinAttack()) {
            this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
            int j = flag3 ? 1 : -1;
            p_228405_8_.translate((double)((float)j * -0.4F), (double)0.8F, (double)0.3F);
            p_228405_8_.mulPose(Vector3f.YP.rotationDegrees((float)j * 65.0F));
            p_228405_8_.mulPose(Vector3f.ZP.rotationDegrees((float)j * -85.0F));
         } else {
            float f5 = -0.4F * MathHelper.sin(MathHelper.sqrt(p_228405_5_) * (float)Math.PI);
            float f6 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_228405_5_) * ((float)Math.PI * 2F));
            float f10 = -0.2F * MathHelper.sin(p_228405_5_ * (float)Math.PI);
            int l = flag3 ? 1 : -1;
            p_228405_8_.translate((double)((float)l * f5), (double)f6, (double)f10);
            this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
            this.applyItemArmAttackTransform(p_228405_8_, handside, p_228405_5_);
         }

         this.renderItem(p_228405_1_, p_228405_6_, flag3 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, p_228405_8_, p_228405_9_, p_228405_10_);
      }

      p_228405_8_.popPose();
   }

   public void tick() {
      this.oMainHandHeight = this.mainHandHeight;
      this.oOffHandHeight = this.offHandHeight;
      ClientPlayerEntity clientplayerentity = this.minecraft.player;
      ItemStack itemstack = clientplayerentity.getMainHandItem();
      ItemStack itemstack1 = clientplayerentity.getOffhandItem();
      if (ItemStack.matches(this.mainHandItem, itemstack)) {
         this.mainHandItem = itemstack;
      }

      if (ItemStack.matches(this.offHandItem, itemstack1)) {
         this.offHandItem = itemstack1;
      }

      if (clientplayerentity.isHandsBusy()) {
         this.mainHandHeight = MathHelper.clamp(this.mainHandHeight - 0.4F, 0.0F, 1.0F);
         this.offHandHeight = MathHelper.clamp(this.offHandHeight - 0.4F, 0.0F, 1.0F);
      } else {
         float f = clientplayerentity.getAttackStrengthScale(1.0F);
         boolean requipM = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.mainHandItem, itemstack, clientplayerentity.inventory.selected);
         boolean requipO = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.offHandItem, itemstack1, -1);

         if (!requipM && this.mainHandItem != itemstack)
            this.mainHandItem = itemstack;
         if (!requipO && this.offHandItem != itemstack1)
            this.offHandItem = itemstack1;

         this.mainHandHeight += MathHelper.clamp((!requipM ? f * f * f : 0.0F) - this.mainHandHeight, -0.4F, 0.4F);
         this.offHandHeight += MathHelper.clamp((float)(!requipO ? 1 : 0) - this.offHandHeight, -0.4F, 0.4F);
      }

      if (this.mainHandHeight < 0.1F) {
         this.mainHandItem = itemstack;
      }

      if (this.offHandHeight < 0.1F) {
         this.offHandItem = itemstack1;
      }

   }

   public void itemUsed(Hand p_187460_1_) {
      if (p_187460_1_ == Hand.MAIN_HAND) {
         this.mainHandHeight = 0.0F;
      } else {
         this.offHandHeight = 0.0F;
      }

   }
}
