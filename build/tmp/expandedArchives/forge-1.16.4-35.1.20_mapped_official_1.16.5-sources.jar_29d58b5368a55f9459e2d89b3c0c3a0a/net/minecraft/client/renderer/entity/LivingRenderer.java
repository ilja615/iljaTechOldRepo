package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LivingRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected M model;
   protected final List<LayerRenderer<T, M>> layers = Lists.newArrayList();

   public LivingRenderer(EntityRendererManager p_i50965_1_, M p_i50965_2_, float p_i50965_3_) {
      super(p_i50965_1_);
      this.model = p_i50965_2_;
      this.shadowRadius = p_i50965_3_;
   }

   public final boolean addLayer(LayerRenderer<T, M> p_177094_1_) {
      return this.layers.add(p_177094_1_);
   }

   public M getModel() {
      return this.model;
   }

   public void render(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<T, M>(p_225623_1_, this, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_))) return;
      p_225623_4_.pushPose();
      this.model.attackTime = this.getAttackAnim(p_225623_1_, p_225623_3_);

      boolean shouldSit = p_225623_1_.isPassenger() && (p_225623_1_.getVehicle() != null && p_225623_1_.getVehicle().shouldRiderSit());
      this.model.riding = shouldSit;
      this.model.young = p_225623_1_.isBaby();
      float f = MathHelper.rotLerp(p_225623_3_, p_225623_1_.yBodyRotO, p_225623_1_.yBodyRot);
      float f1 = MathHelper.rotLerp(p_225623_3_, p_225623_1_.yHeadRotO, p_225623_1_.yHeadRot);
      float f2 = f1 - f;
      if (shouldSit && p_225623_1_.getVehicle() instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_225623_1_.getVehicle();
         f = MathHelper.rotLerp(p_225623_3_, livingentity.yBodyRotO, livingentity.yBodyRot);
         f2 = f1 - f;
         float f3 = MathHelper.wrapDegrees(f2);
         if (f3 < -85.0F) {
            f3 = -85.0F;
         }

         if (f3 >= 85.0F) {
            f3 = 85.0F;
         }

         f = f1 - f3;
         if (f3 * f3 > 2500.0F) {
            f += f3 * 0.2F;
         }

         f2 = f1 - f;
      }

      float f6 = MathHelper.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.xRot);
      if (p_225623_1_.getPose() == Pose.SLEEPING) {
         Direction direction = p_225623_1_.getBedOrientation();
         if (direction != null) {
            float f4 = p_225623_1_.getEyeHeight(Pose.STANDING) - 0.1F;
            p_225623_4_.translate((double)((float)(-direction.getStepX()) * f4), 0.0D, (double)((float)(-direction.getStepZ()) * f4));
         }
      }

      float f7 = this.getBob(p_225623_1_, p_225623_3_);
      this.setupRotations(p_225623_1_, p_225623_4_, f7, f, p_225623_3_);
      p_225623_4_.scale(-1.0F, -1.0F, 1.0F);
      this.scale(p_225623_1_, p_225623_4_, p_225623_3_);
      p_225623_4_.translate(0.0D, (double)-1.501F, 0.0D);
      float f8 = 0.0F;
      float f5 = 0.0F;
      if (!shouldSit && p_225623_1_.isAlive()) {
         f8 = MathHelper.lerp(p_225623_3_, p_225623_1_.animationSpeedOld, p_225623_1_.animationSpeed);
         f5 = p_225623_1_.animationPosition - p_225623_1_.animationSpeed * (1.0F - p_225623_3_);
         if (p_225623_1_.isBaby()) {
            f5 *= 3.0F;
         }

         if (f8 > 1.0F) {
            f8 = 1.0F;
         }
      }

      this.model.prepareMobModel(p_225623_1_, f5, f8, p_225623_3_);
      this.model.setupAnim(p_225623_1_, f5, f8, f7, f2, f6);
      Minecraft minecraft = Minecraft.getInstance();
      boolean flag = this.isBodyVisible(p_225623_1_);
      boolean flag1 = !flag && !p_225623_1_.isInvisibleTo(minecraft.player);
      boolean flag2 = minecraft.shouldEntityAppearGlowing(p_225623_1_);
      RenderType rendertype = this.getRenderType(p_225623_1_, flag, flag1, flag2);
      if (rendertype != null) {
         IVertexBuilder ivertexbuilder = p_225623_5_.getBuffer(rendertype);
         int i = getOverlayCoords(p_225623_1_, this.getWhiteOverlayProgress(p_225623_1_, p_225623_3_));
         this.model.renderToBuffer(p_225623_4_, ivertexbuilder, p_225623_6_, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
      }

      if (!p_225623_1_.isSpectator()) {
         for(LayerRenderer<T, M> layerrenderer : this.layers) {
            layerrenderer.render(p_225623_4_, p_225623_5_, p_225623_6_, p_225623_1_, f5, f8, p_225623_3_, f7, f2, f6);
         }
      }

      p_225623_4_.popPose();
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<T, M>(p_225623_1_, this, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_));
   }

   @Nullable
   protected RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
      ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
      if (p_230496_3_) {
         return RenderType.itemEntityTranslucentCull(resourcelocation);
      } else if (p_230496_2_) {
         return this.model.renderType(resourcelocation);
      } else {
         return p_230496_4_ ? RenderType.outline(resourcelocation) : null;
      }
   }

   public static int getOverlayCoords(LivingEntity p_229117_0_, float p_229117_1_) {
      return OverlayTexture.pack(OverlayTexture.u(p_229117_1_), OverlayTexture.v(p_229117_0_.hurtTime > 0 || p_229117_0_.deathTime > 0));
   }

   protected boolean isBodyVisible(T p_225622_1_) {
      return !p_225622_1_.isInvisible();
   }

   private static float sleepDirectionToRotation(Direction p_217765_0_) {
      switch(p_217765_0_) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected boolean isShaking(T p_230495_1_) {
      return false;
   }

   protected void setupRotations(T p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      if (this.isShaking(p_225621_1_)) {
         p_225621_4_ += (float)(Math.cos((double)p_225621_1_.tickCount * 3.25D) * Math.PI * (double)0.4F);
      }

      Pose pose = p_225621_1_.getPose();
      if (pose != Pose.SLEEPING) {
         p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_225621_4_));
      }

      if (p_225621_1_.deathTime > 0) {
         float f = ((float)p_225621_1_.deathTime + p_225621_5_ - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(p_225621_1_)));
      } else if (p_225621_1_.isAutoSpinAttack()) {
         p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(-90.0F - p_225621_1_.xRot));
         p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(((float)p_225621_1_.tickCount + p_225621_5_) * -75.0F));
      } else if (pose == Pose.SLEEPING) {
         Direction direction = p_225621_1_.getBedOrientation();
         float f1 = direction != null ? sleepDirectionToRotation(direction) : p_225621_4_;
         p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(f1));
         p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(p_225621_1_)));
         p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(270.0F));
      } else if (p_225621_1_.hasCustomName() || p_225621_1_ instanceof PlayerEntity) {
         String s = TextFormatting.stripFormatting(p_225621_1_.getName().getString());
         if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(p_225621_1_ instanceof PlayerEntity) || ((PlayerEntity)p_225621_1_).isModelPartShown(PlayerModelPart.CAPE))) {
            p_225621_2_.translate(0.0D, (double)(p_225621_1_.getBbHeight() + 0.1F), 0.0D);
            p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         }
      }

   }

   protected float getAttackAnim(T p_77040_1_, float p_77040_2_) {
      return p_77040_1_.getAttackAnim(p_77040_2_);
   }

   protected float getBob(T p_77044_1_, float p_77044_2_) {
      return (float)p_77044_1_.tickCount + p_77044_2_;
   }

   protected float getFlipDegrees(T p_77037_1_) {
      return 90.0F;
   }

   protected float getWhiteOverlayProgress(T p_225625_1_, float p_225625_2_) {
      return 0.0F;
   }

   protected void scale(T p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
   }

   protected boolean shouldShowName(T p_177070_1_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_177070_1_);
      float f = p_177070_1_.isDiscrete() ? 32.0F : 64.0F;
      if (d0 >= (double)(f * f)) {
         return false;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         ClientPlayerEntity clientplayerentity = minecraft.player;
         boolean flag = !p_177070_1_.isInvisibleTo(clientplayerentity);
         if (p_177070_1_ != clientplayerentity) {
            Team team = p_177070_1_.getTeam();
            Team team1 = clientplayerentity.getTeam();
            if (team != null) {
               Team.Visible team$visible = team.getNameTagVisibility();
               switch(team$visible) {
               case ALWAYS:
                  return flag;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return team1 == null ? flag : team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
               case HIDE_FOR_OWN_TEAM:
                  return team1 == null ? flag : !team.isAlliedTo(team1) && flag;
               default:
                  return true;
               }
            }
         }

         return Minecraft.renderNames() && p_177070_1_ != minecraft.getCameraEntity() && flag && !p_177070_1_.isVehicle();
      }
   }
}
