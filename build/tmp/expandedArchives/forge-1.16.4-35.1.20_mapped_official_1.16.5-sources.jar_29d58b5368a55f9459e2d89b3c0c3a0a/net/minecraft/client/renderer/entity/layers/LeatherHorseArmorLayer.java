package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeatherHorseArmorLayer extends LayerRenderer<HorseEntity, HorseModel<HorseEntity>> {
   private final HorseModel<HorseEntity> model = new HorseModel<>(0.1F);

   public LeatherHorseArmorLayer(IEntityRenderer<HorseEntity, HorseModel<HorseEntity>> p_i50937_1_) {
      super(p_i50937_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, HorseEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      ItemStack itemstack = p_225628_4_.getArmor();
      if (itemstack.getItem() instanceof HorseArmorItem) {
         HorseArmorItem horsearmoritem = (HorseArmorItem)itemstack.getItem();
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.prepareMobModel(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_);
         this.model.setupAnim(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
         float f;
         float f1;
         float f2;
         if (horsearmoritem instanceof DyeableHorseArmorItem) {
            int i = ((DyeableHorseArmorItem)horsearmoritem).getColor(itemstack);
            f = (float)(i >> 16 & 255) / 255.0F;
            f1 = (float)(i >> 8 & 255) / 255.0F;
            f2 = (float)(i & 255) / 255.0F;
         } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
         }

         IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.entityCutoutNoCull(horsearmoritem.getTexture()));
         this.model.renderToBuffer(p_225628_1_, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F);
      }
   }
}
