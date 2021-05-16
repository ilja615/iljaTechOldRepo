package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishBigModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer cube;
   private final ModelRenderer blueFin0;
   private final ModelRenderer blueFin1;
   private final ModelRenderer topFrontFin;
   private final ModelRenderer topMidFin;
   private final ModelRenderer topBackFin;
   private final ModelRenderer sideFrontFin0;
   private final ModelRenderer sideFrontFin1;
   private final ModelRenderer bottomFrontFin;
   private final ModelRenderer bottomBackFin;
   private final ModelRenderer bottomMidFin;
   private final ModelRenderer sideBackFin0;
   private final ModelRenderer sideBackFin1;

   public PufferFishBigModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      int i = 22;
      this.cube = new ModelRenderer(this, 0, 0);
      this.cube.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.cube.setPos(0.0F, 22.0F, 0.0F);
      this.blueFin0 = new ModelRenderer(this, 24, 0);
      this.blueFin0.addBox(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F);
      this.blueFin0.setPos(-4.0F, 15.0F, -2.0F);
      this.blueFin1 = new ModelRenderer(this, 24, 3);
      this.blueFin1.addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F);
      this.blueFin1.setPos(4.0F, 15.0F, -2.0F);
      this.topFrontFin = new ModelRenderer(this, 15, 17);
      this.topFrontFin.addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.topFrontFin.setPos(0.0F, 14.0F, -4.0F);
      this.topFrontFin.xRot = ((float)Math.PI / 4F);
      this.topMidFin = new ModelRenderer(this, 14, 16);
      this.topMidFin.addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F);
      this.topMidFin.setPos(0.0F, 14.0F, 0.0F);
      this.topBackFin = new ModelRenderer(this, 23, 18);
      this.topBackFin.addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.topBackFin.setPos(0.0F, 14.0F, 4.0F);
      this.topBackFin.xRot = (-(float)Math.PI / 4F);
      this.sideFrontFin0 = new ModelRenderer(this, 5, 17);
      this.sideFrontFin0.addBox(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.sideFrontFin0.setPos(-4.0F, 22.0F, -4.0F);
      this.sideFrontFin0.yRot = (-(float)Math.PI / 4F);
      this.sideFrontFin1 = new ModelRenderer(this, 1, 17);
      this.sideFrontFin1.addBox(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.sideFrontFin1.setPos(4.0F, 22.0F, -4.0F);
      this.sideFrontFin1.yRot = ((float)Math.PI / 4F);
      this.bottomFrontFin = new ModelRenderer(this, 15, 20);
      this.bottomFrontFin.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.bottomFrontFin.setPos(0.0F, 22.0F, -4.0F);
      this.bottomFrontFin.xRot = (-(float)Math.PI / 4F);
      this.bottomMidFin = new ModelRenderer(this, 15, 20);
      this.bottomMidFin.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.bottomMidFin.setPos(0.0F, 22.0F, 0.0F);
      this.bottomBackFin = new ModelRenderer(this, 15, 20);
      this.bottomBackFin.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F);
      this.bottomBackFin.setPos(0.0F, 22.0F, 4.0F);
      this.bottomBackFin.xRot = ((float)Math.PI / 4F);
      this.sideBackFin0 = new ModelRenderer(this, 9, 17);
      this.sideBackFin0.addBox(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.sideBackFin0.setPos(-4.0F, 22.0F, 4.0F);
      this.sideBackFin0.yRot = ((float)Math.PI / 4F);
      this.sideBackFin1 = new ModelRenderer(this, 9, 17);
      this.sideBackFin1.addBox(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F);
      this.sideBackFin1.setPos(4.0F, 22.0F, 4.0F);
      this.sideBackFin1.yRot = (-(float)Math.PI / 4F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.cube, this.blueFin0, this.blueFin1, this.topFrontFin, this.topMidFin, this.topBackFin, this.sideFrontFin0, this.sideFrontFin1, this.bottomFrontFin, this.bottomMidFin, this.bottomBackFin, this.sideBackFin0, this.sideBackFin1);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.blueFin0.zRot = -0.2F + 0.4F * MathHelper.sin(p_225597_4_ * 0.2F);
      this.blueFin1.zRot = 0.2F - 0.4F * MathHelper.sin(p_225597_4_ * 0.2F);
   }
}
