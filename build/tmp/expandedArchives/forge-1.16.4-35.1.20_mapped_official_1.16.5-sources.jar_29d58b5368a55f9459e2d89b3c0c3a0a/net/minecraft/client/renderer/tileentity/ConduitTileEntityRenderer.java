package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConduitTileEntityRenderer extends TileEntityRenderer<ConduitTileEntity> {
   public static final RenderMaterial SHELL_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/base"));
   public static final RenderMaterial ACTIVE_SHELL_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/cage"));
   public static final RenderMaterial WIND_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind"));
   public static final RenderMaterial VERTICAL_WIND_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind_vertical"));
   public static final RenderMaterial OPEN_EYE_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/open_eye"));
   public static final RenderMaterial CLOSED_EYE_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/closed_eye"));
   private final ModelRenderer eye = new ModelRenderer(16, 16, 0, 0);
   private final ModelRenderer wind;
   private final ModelRenderer shell;
   private final ModelRenderer cage;

   public ConduitTileEntityRenderer(TileEntityRendererDispatcher p_i226009_1_) {
      super(p_i226009_1_);
      this.eye.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
      this.wind = new ModelRenderer(64, 32, 0, 0);
      this.wind.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      this.shell = new ModelRenderer(32, 16, 0, 0);
      this.shell.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      this.cage = new ModelRenderer(32, 16, 0, 0);
      this.cage.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
   }

   public void render(ConduitTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      float f = (float)p_225616_1_.tickCount + p_225616_2_;
      if (!p_225616_1_.isActive()) {
         float f5 = p_225616_1_.getActiveRotation(0.0F);
         IVertexBuilder ivertexbuilder1 = SHELL_TEXTURE.buffer(p_225616_4_, RenderType::entitySolid);
         p_225616_3_.pushPose();
         p_225616_3_.translate(0.5D, 0.5D, 0.5D);
         p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(f5));
         this.shell.render(p_225616_3_, ivertexbuilder1, p_225616_5_, p_225616_6_);
         p_225616_3_.popPose();
      } else {
         float f1 = p_225616_1_.getActiveRotation(p_225616_2_) * (180F / (float)Math.PI);
         float f2 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
         f2 = f2 * f2 + f2;
         p_225616_3_.pushPose();
         p_225616_3_.translate(0.5D, (double)(0.3F + f2 * 0.2F), 0.5D);
         Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
         vector3f.normalize();
         p_225616_3_.mulPose(new Quaternion(vector3f, f1, true));
         this.cage.render(p_225616_3_, ACTIVE_SHELL_TEXTURE.buffer(p_225616_4_, RenderType::entityCutoutNoCull), p_225616_5_, p_225616_6_);
         p_225616_3_.popPose();
         int i = p_225616_1_.tickCount / 66 % 3;
         p_225616_3_.pushPose();
         p_225616_3_.translate(0.5D, 0.5D, 0.5D);
         if (i == 1) {
            p_225616_3_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         } else if (i == 2) {
            p_225616_3_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
         }

         IVertexBuilder ivertexbuilder = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(p_225616_4_, RenderType::entityCutoutNoCull);
         this.wind.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
         p_225616_3_.popPose();
         p_225616_3_.pushPose();
         p_225616_3_.translate(0.5D, 0.5D, 0.5D);
         p_225616_3_.scale(0.875F, 0.875F, 0.875F);
         p_225616_3_.mulPose(Vector3f.XP.rotationDegrees(180.0F));
         p_225616_3_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         this.wind.render(p_225616_3_, ivertexbuilder, p_225616_5_, p_225616_6_);
         p_225616_3_.popPose();
         ActiveRenderInfo activerenderinfo = this.renderer.camera;
         p_225616_3_.pushPose();
         p_225616_3_.translate(0.5D, (double)(0.3F + f2 * 0.2F), 0.5D);
         p_225616_3_.scale(0.5F, 0.5F, 0.5F);
         float f3 = -activerenderinfo.getYRot();
         p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(f3));
         p_225616_3_.mulPose(Vector3f.XP.rotationDegrees(activerenderinfo.getXRot()));
         p_225616_3_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         float f4 = 1.3333334F;
         p_225616_3_.scale(1.3333334F, 1.3333334F, 1.3333334F);
         this.eye.render(p_225616_3_, (p_225616_1_.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(p_225616_4_, RenderType::entityCutoutNoCull), p_225616_5_, p_225616_6_);
         p_225616_3_.popPose();
      }
   }
}
