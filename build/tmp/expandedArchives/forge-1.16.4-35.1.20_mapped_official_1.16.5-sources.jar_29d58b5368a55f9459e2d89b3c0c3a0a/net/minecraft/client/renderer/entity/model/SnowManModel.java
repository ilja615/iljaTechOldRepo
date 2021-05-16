package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowManModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer piece1;
   private final ModelRenderer piece2;
   private final ModelRenderer head;
   private final ModelRenderer arm1;
   private final ModelRenderer arm2;

   public SnowManModel() {
      float f = 4.0F;
      float f1 = 0.0F;
      this.head = (new ModelRenderer(this, 0, 0)).setTexSize(64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.5F);
      this.head.setPos(0.0F, 4.0F, 0.0F);
      this.arm1 = (new ModelRenderer(this, 32, 0)).setTexSize(64, 64);
      this.arm1.addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
      this.arm1.setPos(0.0F, 6.0F, 0.0F);
      this.arm2 = (new ModelRenderer(this, 32, 0)).setTexSize(64, 64);
      this.arm2.addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
      this.arm2.setPos(0.0F, 6.0F, 0.0F);
      this.piece1 = (new ModelRenderer(this, 0, 16)).setTexSize(64, 64);
      this.piece1.addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, -0.5F);
      this.piece1.setPos(0.0F, 13.0F, 0.0F);
      this.piece2 = (new ModelRenderer(this, 0, 36)).setTexSize(64, 64);
      this.piece2.addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, -0.5F);
      this.piece2.setPos(0.0F, 24.0F, 0.0F);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      this.piece1.yRot = p_225597_5_ * ((float)Math.PI / 180F) * 0.25F;
      float f = MathHelper.sin(this.piece1.yRot);
      float f1 = MathHelper.cos(this.piece1.yRot);
      this.arm1.zRot = 1.0F;
      this.arm2.zRot = -1.0F;
      this.arm1.yRot = 0.0F + this.piece1.yRot;
      this.arm2.yRot = (float)Math.PI + this.piece1.yRot;
      this.arm1.x = f1 * 5.0F;
      this.arm1.z = -f * 5.0F;
      this.arm2.x = -f1 * 5.0F;
      this.arm2.z = f * 5.0F;
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.piece1, this.piece2, this.head, this.arm1, this.arm2);
   }

   public ModelRenderer getHead() {
      return this.head;
   }
}
