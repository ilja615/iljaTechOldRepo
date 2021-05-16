package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderState {
   protected final String name;
   private final Runnable setupState;
   private final Runnable clearState;
   protected static final RenderState.TransparencyState NO_TRANSPARENCY = new RenderState.TransparencyState("no_transparency", () -> {
      RenderSystem.disableBlend();
   }, () -> {
   });
   protected static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("additive_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState LIGHTNING_TRANSPARENCY = new RenderState.TransparencyState("lightning_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState GLINT_TRANSPARENCY = new RenderState.TransparencyState("glint_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState CRUMBLING_TRANSPARENCY = new RenderState.TransparencyState("crumbling_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderState.AlphaState NO_ALPHA = new RenderState.AlphaState(0.0F);
   protected static final RenderState.AlphaState DEFAULT_ALPHA = new RenderState.AlphaState(0.003921569F);
   protected static final RenderState.AlphaState MIDWAY_ALPHA = new RenderState.AlphaState(0.5F);
   protected static final RenderState.ShadeModelState FLAT_SHADE = new RenderState.ShadeModelState(false);
   protected static final RenderState.ShadeModelState SMOOTH_SHADE = new RenderState.ShadeModelState(true);
   protected static final RenderState.TextureState BLOCK_SHEET_MIPPED = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS, false, true);
   protected static final RenderState.TextureState BLOCK_SHEET = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS, false, false);
   protected static final RenderState.TextureState NO_TEXTURE = new RenderState.TextureState();
   protected static final RenderState.TexturingState DEFAULT_TEXTURING = new RenderState.TexturingState("default_texturing", () -> {
   }, () -> {
   });
   protected static final RenderState.TexturingState OUTLINE_TEXTURING = new RenderState.TexturingState("outline_texturing", () -> {
      RenderSystem.setupOutline();
   }, () -> {
      RenderSystem.teardownOutline();
   });
   protected static final RenderState.TexturingState GLINT_TEXTURING = new RenderState.TexturingState("glint_texturing", () -> {
      setupGlintTexturing(8.0F);
   }, () -> {
      RenderSystem.matrixMode(5890);
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
   });
   protected static final RenderState.TexturingState ENTITY_GLINT_TEXTURING = new RenderState.TexturingState("entity_glint_texturing", () -> {
      setupGlintTexturing(0.16F);
   }, () -> {
      RenderSystem.matrixMode(5890);
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
   });
   protected static final RenderState.LightmapState LIGHTMAP = new RenderState.LightmapState(true);
   protected static final RenderState.LightmapState NO_LIGHTMAP = new RenderState.LightmapState(false);
   protected static final RenderState.OverlayState OVERLAY = new RenderState.OverlayState(true);
   protected static final RenderState.OverlayState NO_OVERLAY = new RenderState.OverlayState(false);
   protected static final RenderState.DiffuseLightingState DIFFUSE_LIGHTING = new RenderState.DiffuseLightingState(true);
   protected static final RenderState.DiffuseLightingState NO_DIFFUSE_LIGHTING = new RenderState.DiffuseLightingState(false);
   protected static final RenderState.CullState CULL = new RenderState.CullState(true);
   protected static final RenderState.CullState NO_CULL = new RenderState.CullState(false);
   protected static final RenderState.DepthTestState NO_DEPTH_TEST = new RenderState.DepthTestState("always", 519);
   protected static final RenderState.DepthTestState EQUAL_DEPTH_TEST = new RenderState.DepthTestState("==", 514);
   protected static final RenderState.DepthTestState LEQUAL_DEPTH_TEST = new RenderState.DepthTestState("<=", 515);
   protected static final RenderState.WriteMaskState COLOR_DEPTH_WRITE = new RenderState.WriteMaskState(true, true);
   protected static final RenderState.WriteMaskState COLOR_WRITE = new RenderState.WriteMaskState(true, false);
   protected static final RenderState.WriteMaskState DEPTH_WRITE = new RenderState.WriteMaskState(false, true);
   protected static final RenderState.LayerState NO_LAYERING = new RenderState.LayerState("no_layering", () -> {
   }, () -> {
   });
   protected static final RenderState.LayerState POLYGON_OFFSET_LAYERING = new RenderState.LayerState("polygon_offset_layering", () -> {
      RenderSystem.polygonOffset(-1.0F, -10.0F);
      RenderSystem.enablePolygonOffset();
   }, () -> {
      RenderSystem.polygonOffset(0.0F, 0.0F);
      RenderSystem.disablePolygonOffset();
   });
   protected static final RenderState.LayerState VIEW_OFFSET_Z_LAYERING = new RenderState.LayerState("view_offset_z_layering", () -> {
      RenderSystem.pushMatrix();
      RenderSystem.scalef(0.99975586F, 0.99975586F, 0.99975586F);
   }, RenderSystem::popMatrix);
   protected static final RenderState.FogState NO_FOG = new RenderState.FogState("no_fog", () -> {
   }, () -> {
   });
   protected static final RenderState.FogState FOG = new RenderState.FogState("fog", () -> {
      FogRenderer.levelFogColor();
      RenderSystem.enableFog();
   }, () -> {
      RenderSystem.disableFog();
   });
   protected static final RenderState.FogState BLACK_FOG = new RenderState.FogState("black_fog", () -> {
      RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.enableFog();
   }, () -> {
      FogRenderer.levelFogColor();
      RenderSystem.disableFog();
   });
   protected static final RenderState.TargetState MAIN_TARGET = new RenderState.TargetState("main_target", () -> {
   }, () -> {
   });
   protected static final RenderState.TargetState OUTLINE_TARGET = new RenderState.TargetState("outline_target", () -> {
      Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
   }, () -> {
      Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
   });
   protected static final RenderState.TargetState TRANSLUCENT_TARGET = new RenderState.TargetState("translucent_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderState.TargetState PARTICLES_TARGET = new RenderState.TargetState("particles_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderState.TargetState WEATHER_TARGET = new RenderState.TargetState("weather_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getWeatherTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderState.TargetState CLOUDS_TARGET = new RenderState.TargetState("clouds_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getCloudsTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderState.TargetState ITEM_ENTITY_TARGET = new RenderState.TargetState("item_entity_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getItemEntityTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderState.LineState DEFAULT_LINE = new RenderState.LineState(OptionalDouble.of(1.0D));

   public RenderState(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
      this.name = p_i225973_1_;
      this.setupState = p_i225973_2_;
      this.clearState = p_i225973_3_;
   }

   public void setupRenderState() {
      this.setupState.run();
   }

   public void clearRenderState() {
      this.clearState.run();
   }

   public boolean equals(@Nullable Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         RenderState renderstate = (RenderState)p_equals_1_;
         return this.name.equals(renderstate.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String toString() {
      return this.name;
   }

   private static void setupGlintTexturing(float p_228548_0_) {
      RenderSystem.matrixMode(5890);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      long i = Util.getMillis() * 8L;
      float f = (float)(i % 110000L) / 110000.0F;
      float f1 = (float)(i % 30000L) / 30000.0F;
      RenderSystem.translatef(-f, f1, 0.0F);
      RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(p_228548_0_, p_228548_0_, p_228548_0_);
      RenderSystem.matrixMode(5888);
   }

   @OnlyIn(Dist.CLIENT)
   public static class AlphaState extends RenderState {
      private final float cutoff;

      public AlphaState(float p_i225974_1_) {
         super("alpha", () -> {
            if (p_i225974_1_ > 0.0F) {
               RenderSystem.enableAlphaTest();
               RenderSystem.alphaFunc(516, p_i225974_1_);
            } else {
               RenderSystem.disableAlphaTest();
            }

         }, () -> {
            RenderSystem.disableAlphaTest();
            RenderSystem.defaultAlphaFunc();
         });
         this.cutoff = p_i225974_1_;
      }

      public boolean equals(@Nullable Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            if (!super.equals(p_equals_1_)) {
               return false;
            } else {
               return this.cutoff == ((RenderState.AlphaState)p_equals_1_).cutoff;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(super.hashCode(), this.cutoff);
      }

      public String toString() {
         return this.name + '[' + this.cutoff + ']';
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState extends RenderState {
      private final boolean enabled;

      public BooleanState(String p_i225975_1_, Runnable p_i225975_2_, Runnable p_i225975_3_, boolean p_i225975_4_) {
         super(p_i225975_1_, p_i225975_2_, p_i225975_3_);
         this.enabled = p_i225975_4_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.BooleanState renderstate$booleanstate = (RenderState.BooleanState)p_equals_1_;
            return this.enabled == renderstate$booleanstate.enabled;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Boolean.hashCode(this.enabled);
      }

      public String toString() {
         return this.name + '[' + this.enabled + ']';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CullState extends RenderState.BooleanState {
      public CullState(boolean p_i225976_1_) {
         super("cull", () -> {
            if (!p_i225976_1_) {
               RenderSystem.disableCull();
            }

         }, () -> {
            if (!p_i225976_1_) {
               RenderSystem.enableCull();
            }

         }, p_i225976_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DepthTestState extends RenderState {
      private final String functionName;
      private final int function;

      public DepthTestState(String p_i232464_1_, int p_i232464_2_) {
         super("depth_test", () -> {
            if (p_i232464_2_ != 519) {
               RenderSystem.enableDepthTest();
               RenderSystem.depthFunc(p_i232464_2_);
            }

         }, () -> {
            if (p_i232464_2_ != 519) {
               RenderSystem.disableDepthTest();
               RenderSystem.depthFunc(515);
            }

         });
         this.functionName = p_i232464_1_;
         this.function = p_i232464_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.DepthTestState renderstate$depthteststate = (RenderState.DepthTestState)p_equals_1_;
            return this.function == renderstate$depthteststate.function;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Integer.hashCode(this.function);
      }

      public String toString() {
         return this.name + '[' + this.functionName + ']';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DiffuseLightingState extends RenderState.BooleanState {
      public DiffuseLightingState(boolean p_i225978_1_) {
         super("diffuse_lighting", () -> {
            if (p_i225978_1_) {
               RenderHelper.turnBackOn();
            }

         }, () -> {
            if (p_i225978_1_) {
               RenderHelper.turnOff();
            }

         }, p_i225978_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FogState extends RenderState {
      public FogState(String p_i225979_1_, Runnable p_i225979_2_, Runnable p_i225979_3_) {
         super(p_i225979_1_, p_i225979_2_, p_i225979_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LayerState extends RenderState {
      public LayerState(String p_i225980_1_, Runnable p_i225980_2_, Runnable p_i225980_3_) {
         super(p_i225980_1_, p_i225980_2_, p_i225980_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LightmapState extends RenderState.BooleanState {
      public LightmapState(boolean p_i225981_1_) {
         super("lightmap", () -> {
            if (p_i225981_1_) {
               Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            }

         }, () -> {
            if (p_i225981_1_) {
               Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
            }

         }, p_i225981_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LineState extends RenderState {
      private final OptionalDouble width;

      public LineState(OptionalDouble p_i225982_1_) {
         super("line_width", () -> {
            if (!Objects.equals(p_i225982_1_, OptionalDouble.of(1.0D))) {
               if (p_i225982_1_.isPresent()) {
                  RenderSystem.lineWidth((float)p_i225982_1_.getAsDouble());
               } else {
                  RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
               }
            }

         }, () -> {
            if (!Objects.equals(p_i225982_1_, OptionalDouble.of(1.0D))) {
               RenderSystem.lineWidth(1.0F);
            }

         });
         this.width = p_i225982_1_;
      }

      public boolean equals(@Nullable Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            return !super.equals(p_equals_1_) ? false : Objects.equals(this.width, ((RenderState.LineState)p_equals_1_).width);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(super.hashCode(), this.width);
      }

      public String toString() {
         return this.name + '[' + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + ']';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class OffsetTexturingState extends RenderState.TexturingState {
      private final float uOffset;
      private final float vOffset;

      public OffsetTexturingState(float p_i225983_1_, float p_i225983_2_) {
         super("offset_texturing", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(p_i225983_1_, p_i225983_2_, 0.0F);
            RenderSystem.matrixMode(5888);
         }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
         });
         this.uOffset = p_i225983_1_;
         this.vOffset = p_i225983_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.OffsetTexturingState renderstate$offsettexturingstate = (RenderState.OffsetTexturingState)p_equals_1_;
            return Float.compare(renderstate$offsettexturingstate.uOffset, this.uOffset) == 0 && Float.compare(renderstate$offsettexturingstate.vOffset, this.vOffset) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.uOffset, this.vOffset);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OverlayState extends RenderState.BooleanState {
      public OverlayState(boolean p_i225985_1_) {
         super("overlay", () -> {
            if (p_i225985_1_) {
               Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
            }

         }, () -> {
            if (p_i225985_1_) {
               Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
            }

         }, p_i225985_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class PortalTexturingState extends RenderState.TexturingState {
      private final int iteration;

      public PortalTexturingState(int p_i225986_1_) {
         super("portal_texturing", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.5F, 0.5F, 0.0F);
            RenderSystem.scalef(0.5F, 0.5F, 1.0F);
            RenderSystem.translatef(17.0F / (float)p_i225986_1_, (2.0F + (float)p_i225986_1_ / 1.5F) * ((float)(Util.getMillis() % 800000L) / 800000.0F), 0.0F);
            RenderSystem.rotatef(((float)(p_i225986_1_ * p_i225986_1_) * 4321.0F + (float)p_i225986_1_ * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.scalef(4.5F - (float)p_i225986_1_ / 4.0F, 4.5F - (float)p_i225986_1_ / 4.0F, 1.0F);
            RenderSystem.mulTextureByProjModelView();
            RenderSystem.matrixMode(5888);
            RenderSystem.setupEndPortalTexGen();
         }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            RenderSystem.clearTexGen();
         });
         this.iteration = p_i225986_1_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.PortalTexturingState renderstate$portaltexturingstate = (RenderState.PortalTexturingState)p_equals_1_;
            return this.iteration == renderstate$portaltexturingstate.iteration;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Integer.hashCode(this.iteration);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ShadeModelState extends RenderState {
      private final boolean smooth;

      public ShadeModelState(boolean p_i225987_1_) {
         super("shade_model", () -> {
            RenderSystem.shadeModel(p_i225987_1_ ? 7425 : 7424);
         }, () -> {
            RenderSystem.shadeModel(7424);
         });
         this.smooth = p_i225987_1_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.ShadeModelState renderstate$shademodelstate = (RenderState.ShadeModelState)p_equals_1_;
            return this.smooth == renderstate$shademodelstate.smooth;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Boolean.hashCode(this.smooth);
      }

      public String toString() {
         return this.name + '[' + (this.smooth ? "smooth" : "flat") + ']';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TargetState extends RenderState {
      public TargetState(String p_i225984_1_, Runnable p_i225984_2_, Runnable p_i225984_3_) {
         super(p_i225984_1_, p_i225984_2_, p_i225984_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextureState extends RenderState {
      private final Optional<ResourceLocation> texture;
      private final boolean blur;
      private final boolean mipmap;

      public TextureState(ResourceLocation p_i225988_1_, boolean p_i225988_2_, boolean p_i225988_3_) {
         super("texture", () -> {
            RenderSystem.enableTexture();
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            texturemanager.bind(p_i225988_1_);
            texturemanager.getTexture(p_i225988_1_).setFilter(p_i225988_2_, p_i225988_3_);
         }, () -> {
         });
         this.texture = Optional.of(p_i225988_1_);
         this.blur = p_i225988_2_;
         this.mipmap = p_i225988_3_;
      }

      public TextureState() {
         super("texture", () -> {
            RenderSystem.disableTexture();
         }, () -> {
            RenderSystem.enableTexture();
         });
         this.texture = Optional.empty();
         this.blur = false;
         this.mipmap = false;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.TextureState renderstate$texturestate = (RenderState.TextureState)p_equals_1_;
            return this.texture.equals(renderstate$texturestate.texture) && this.blur == renderstate$texturestate.blur && this.mipmap == renderstate$texturestate.mipmap;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.texture.hashCode();
      }

      public String toString() {
         return this.name + '[' + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
      }

      protected Optional<ResourceLocation> texture() {
         return this.texture;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TexturingState extends RenderState {
      public TexturingState(String p_i225989_1_, Runnable p_i225989_2_, Runnable p_i225989_3_) {
         super(p_i225989_1_, p_i225989_2_, p_i225989_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TransparencyState extends RenderState {
      public TransparencyState(String p_i225990_1_, Runnable p_i225990_2_, Runnable p_i225990_3_) {
         super(p_i225990_1_, p_i225990_2_, p_i225990_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WriteMaskState extends RenderState {
      private final boolean writeColor;
      private final boolean writeDepth;

      public WriteMaskState(boolean p_i225991_1_, boolean p_i225991_2_) {
         super("write_mask_state", () -> {
            if (!p_i225991_2_) {
               RenderSystem.depthMask(p_i225991_2_);
            }

            if (!p_i225991_1_) {
               RenderSystem.colorMask(p_i225991_1_, p_i225991_1_, p_i225991_1_, p_i225991_1_);
            }

         }, () -> {
            if (!p_i225991_2_) {
               RenderSystem.depthMask(true);
            }

            if (!p_i225991_1_) {
               RenderSystem.colorMask(true, true, true, true);
            }

         });
         this.writeColor = p_i225991_1_;
         this.writeDepth = p_i225991_2_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderState.WriteMaskState renderstate$writemaskstate = (RenderState.WriteMaskState)p_equals_1_;
            return this.writeColor == renderstate$writemaskstate.writeColor && this.writeDepth == renderstate$writemaskstate.writeDepth;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.writeColor, this.writeDepth);
      }

      public String toString() {
         return this.name + "[writeColor=" + this.writeColor + ", writeDepth=" + this.writeDepth + ']';
      }
   }
}
