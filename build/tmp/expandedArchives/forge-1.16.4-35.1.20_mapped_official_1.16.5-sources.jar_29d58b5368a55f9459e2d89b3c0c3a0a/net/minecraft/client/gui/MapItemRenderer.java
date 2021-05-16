package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MapItemRenderer implements AutoCloseable {
   private static final ResourceLocation MAP_ICONS_LOCATION = new ResourceLocation("textures/map/map_icons.png");
   private static final RenderType MAP_ICONS = RenderType.text(MAP_ICONS_LOCATION);
   private final TextureManager textureManager;
   private final Map<String, MapItemRenderer.Instance> maps = Maps.newHashMap();

   public MapItemRenderer(TextureManager p_i45009_1_) {
      this.textureManager = p_i45009_1_;
   }

   public void update(MapData p_148246_1_) {
      this.getMapInstance(p_148246_1_).updateTexture();
   }

   public void render(MatrixStack p_228086_1_, IRenderTypeBuffer p_228086_2_, MapData p_228086_3_, boolean p_228086_4_, int p_228086_5_) {
      this.getMapInstance(p_228086_3_).draw(p_228086_1_, p_228086_2_, p_228086_4_, p_228086_5_);
   }

   private MapItemRenderer.Instance getMapInstance(MapData p_148248_1_) {
      MapItemRenderer.Instance mapitemrenderer$instance = this.maps.get(p_148248_1_.getId());
      if (mapitemrenderer$instance == null) {
         mapitemrenderer$instance = new MapItemRenderer.Instance(p_148248_1_);
         this.maps.put(p_148248_1_.getId(), mapitemrenderer$instance);
      }

      return mapitemrenderer$instance;
   }

   @Nullable
   public MapItemRenderer.Instance getMapInstanceIfExists(String p_191205_1_) {
      return this.maps.get(p_191205_1_);
   }

   public void resetData() {
      for(MapItemRenderer.Instance mapitemrenderer$instance : this.maps.values()) {
         mapitemrenderer$instance.close();
      }

      this.maps.clear();
   }

   @Nullable
   public MapData getData(@Nullable MapItemRenderer.Instance p_191207_1_) {
      return p_191207_1_ != null ? p_191207_1_.data : null;
   }

   public void close() {
      this.resetData();
   }

   @OnlyIn(Dist.CLIENT)
   class Instance implements AutoCloseable {
      private final MapData data;
      private final DynamicTexture texture;
      private final RenderType renderType;

      private Instance(MapData p_i45007_2_) {
         this.data = p_i45007_2_;
         this.texture = new DynamicTexture(128, 128, true);
         ResourceLocation resourcelocation = MapItemRenderer.this.textureManager.register("map/" + p_i45007_2_.getId(), this.texture);
         this.renderType = RenderType.text(resourcelocation);
      }

      private void updateTexture() {
         for(int i = 0; i < 128; ++i) {
            for(int j = 0; j < 128; ++j) {
               int k = j + i * 128;
               int l = this.data.colors[k] & 255;
               if (l / 4 == 0) {
                  this.texture.getPixels().setPixelRGBA(j, i, 0);
               } else {
                  this.texture.getPixels().setPixelRGBA(j, i, MaterialColor.MATERIAL_COLORS[l / 4].calculateRGBColor(l & 3));
               }
            }
         }

         this.texture.upload();
      }

      private void draw(MatrixStack p_228089_1_, IRenderTypeBuffer p_228089_2_, boolean p_228089_3_, int p_228089_4_) {
         int i = 0;
         int j = 0;
         float f = 0.0F;
         Matrix4f matrix4f = p_228089_1_.last().pose();
         IVertexBuilder ivertexbuilder = p_228089_2_.getBuffer(this.renderType);
         ivertexbuilder.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_228089_4_).endVertex();
         ivertexbuilder.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_228089_4_).endVertex();
         ivertexbuilder.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_228089_4_).endVertex();
         ivertexbuilder.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_228089_4_).endVertex();
         int k = 0;

         for(MapDecoration mapdecoration : this.data.decorations.values()) {
            if (!p_228089_3_ || mapdecoration.renderOnFrame()) {
               if (mapdecoration.render(k)) { k++; continue; }
               p_228089_1_.pushPose();
               p_228089_1_.translate((double)(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F), (double)(0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F), (double)-0.02F);
               p_228089_1_.mulPose(Vector3f.ZP.rotationDegrees((float)(mapdecoration.getRot() * 360) / 16.0F));
               p_228089_1_.scale(4.0F, 4.0F, 3.0F);
               p_228089_1_.translate(-0.125D, 0.125D, 0.0D);
               byte b0 = mapdecoration.getImage();
               float f1 = (float)(b0 % 16 + 0) / 16.0F;
               float f2 = (float)(b0 / 16 + 0) / 16.0F;
               float f3 = (float)(b0 % 16 + 1) / 16.0F;
               float f4 = (float)(b0 / 16 + 1) / 16.0F;
               Matrix4f matrix4f1 = p_228089_1_.last().pose();
               float f5 = -0.001F;
               IVertexBuilder ivertexbuilder1 = p_228089_2_.getBuffer(MapItemRenderer.MAP_ICONS);
               ivertexbuilder1.vertex(matrix4f1, -1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f2).uv2(p_228089_4_).endVertex();
               ivertexbuilder1.vertex(matrix4f1, 1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f2).uv2(p_228089_4_).endVertex();
               ivertexbuilder1.vertex(matrix4f1, 1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f4).uv2(p_228089_4_).endVertex();
               ivertexbuilder1.vertex(matrix4f1, -1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f4).uv2(p_228089_4_).endVertex();
               p_228089_1_.popPose();
               if (mapdecoration.getName() != null) {
                  FontRenderer fontrenderer = Minecraft.getInstance().font;
                  ITextComponent itextcomponent = mapdecoration.getName();
                  float f6 = (float)fontrenderer.width(itextcomponent);
                  float f7 = MathHelper.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
                  p_228089_1_.pushPose();
                  p_228089_1_.translate((double)(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F - f6 * f7 / 2.0F), (double)(0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F + 4.0F), (double)-0.025F);
                  p_228089_1_.scale(f7, f7, 1.0F);
                  p_228089_1_.translate(0.0D, 0.0D, (double)-0.1F);
                  fontrenderer.drawInBatch(itextcomponent, 0.0F, 0.0F, -1, false, p_228089_1_.last().pose(), p_228089_2_, false, Integer.MIN_VALUE, p_228089_4_);
                  p_228089_1_.popPose();
               }

               ++k;
            }
         }

      }

      public void close() {
         this.texture.close();
      }
   }
}
