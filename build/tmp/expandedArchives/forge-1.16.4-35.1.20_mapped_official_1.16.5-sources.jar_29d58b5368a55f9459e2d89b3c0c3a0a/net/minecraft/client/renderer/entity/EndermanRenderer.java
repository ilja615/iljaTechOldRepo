package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.EndermanEyesLayer;
import net.minecraft.client.renderer.entity.layers.HeldBlockLayer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanRenderer extends MobRenderer<EndermanEntity, EndermanModel<EndermanEntity>> {
   private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random random = new Random();

   public EndermanRenderer(EntityRendererManager p_i46182_1_) {
      super(p_i46182_1_, new EndermanModel<>(0.0F), 0.5F);
      this.addLayer(new EndermanEyesLayer<>(this));
      this.addLayer(new HeldBlockLayer(this));
   }

   public void render(EndermanEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      BlockState blockstate = p_225623_1_.getCarriedBlock();
      EndermanModel<EndermanEntity> endermanmodel = this.getModel();
      endermanmodel.carrying = blockstate != null;
      endermanmodel.creepy = p_225623_1_.isCreepy();
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public Vector3d getRenderOffset(EndermanEntity p_225627_1_, float p_225627_2_) {
      if (p_225627_1_.isCreepy()) {
         double d0 = 0.02D;
         return new Vector3d(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
      } else {
         return super.getRenderOffset(p_225627_1_, p_225627_2_);
      }
   }

   public ResourceLocation getTextureLocation(EndermanEntity p_110775_1_) {
      return ENDERMAN_LOCATION;
   }
}
