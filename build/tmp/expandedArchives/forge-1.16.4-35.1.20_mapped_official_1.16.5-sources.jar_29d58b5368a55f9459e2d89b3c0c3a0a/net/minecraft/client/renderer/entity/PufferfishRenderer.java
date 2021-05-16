package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PufferFishBigModel;
import net.minecraft.client.renderer.entity.model.PufferFishMediumModel;
import net.minecraft.client.renderer.entity.model.PufferFishSmallModel;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferfishRenderer extends MobRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
   private static final ResourceLocation PUFFER_LOCATION = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int puffStateO;
   private final PufferFishSmallModel<PufferfishEntity> small = new PufferFishSmallModel<>();
   private final PufferFishMediumModel<PufferfishEntity> mid = new PufferFishMediumModel<>();
   private final PufferFishBigModel<PufferfishEntity> big = new PufferFishBigModel<>();

   public PufferfishRenderer(EntityRendererManager p_i48863_1_) {
      super(p_i48863_1_, new PufferFishBigModel<>(), 0.2F);
      this.puffStateO = 3;
   }

   public ResourceLocation getTextureLocation(PufferfishEntity p_110775_1_) {
      return PUFFER_LOCATION;
   }

   public void render(PufferfishEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      int i = p_225623_1_.getPuffState();
      if (i != this.puffStateO) {
         if (i == 0) {
            this.model = this.small;
         } else if (i == 1) {
            this.model = this.mid;
         } else {
            this.model = this.big;
         }
      }

      this.puffStateO = i;
      this.shadowRadius = 0.1F + 0.1F * (float)i;
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   protected void setupRotations(PufferfishEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      p_225621_2_.translate(0.0D, (double)(MathHelper.cos(p_225621_3_ * 0.05F) * 0.08F), 0.0D);
      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
   }
}
