package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderType extends RenderState {
   private static final RenderType SOLID = create("solid", DefaultVertexFormats.BLOCK, 7, 2097152, true, false, RenderType.State.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
   private static final RenderType CUTOUT_MIPPED = create("cutout_mipped", DefaultVertexFormats.BLOCK, 7, 131072, true, false, RenderType.State.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setAlphaState(MIDWAY_ALPHA).createCompositeState(true));
   private static final RenderType CUTOUT = create("cutout", DefaultVertexFormats.BLOCK, 7, 131072, true, false, RenderType.State.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).setAlphaState(MIDWAY_ALPHA).createCompositeState(true));
   private static final RenderType TRANSLUCENT = create("translucent", DefaultVertexFormats.BLOCK, 7, 262144, true, true, translucentState());
   private static final RenderType TRANSLUCENT_MOVING_BLOCK = create("translucent_moving_block", DefaultVertexFormats.BLOCK, 7, 262144, false, true, translucentMovingBlockState());
   private static final RenderType TRANSLUCENT_NO_CRUMBLING = create("translucent_no_crumbling", DefaultVertexFormats.BLOCK, 7, 262144, false, true, translucentState());
   private static final RenderType LEASH = create("leash", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, RenderType.State.builder().setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(false));
   private static final RenderType WATER_MASK = create("water_mask", DefaultVertexFormats.POSITION, 7, 256, RenderType.State.builder().setTextureState(NO_TEXTURE).setWriteMaskState(DEPTH_WRITE).createCompositeState(false));
   private static final RenderType ARMOR_GLINT = create("armor_glint", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
   private static final RenderType ARMOR_ENTITY_GLINT = create("armor_entity_glint", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
   private static final RenderType GLINT_TRANSLUCENT = create("glint_translucent", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
   private static final RenderType GLINT = create("glint", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
   private static final RenderType GLINT_DIRECT = create("glint_direct", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
   private static final RenderType ENTITY_GLINT = create("entity_glint", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
   private static final RenderType ENTITY_GLINT_DIRECT = create("entity_glint_direct", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
   private static final RenderType LIGHTNING = create("lightning", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.builder().setWriteMaskState(COLOR_DEPTH_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(WEATHER_TARGET).setShadeModelState(SMOOTH_SHADE).createCompositeState(false));
   private static final RenderType TRIPWIRE = create("tripwire", DefaultVertexFormats.BLOCK, 7, 262144, true, true, tripwireState());
   public static final RenderType.Type LINES = create("lines", DefaultVertexFormats.POSITION_COLOR, 1, 256, RenderType.State.builder().setLineState(new RenderState.LineState(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
   private final VertexFormat format;
   private final int mode;
   private final int bufferSize;
   private final boolean affectsCrumbling;
   private final boolean sortOnUpload;
   private final Optional<RenderType> asOptional;

   public static RenderType solid() {
      return SOLID;
   }

   public static RenderType cutoutMipped() {
      return CUTOUT_MIPPED;
   }

   public static RenderType cutout() {
      return CUTOUT;
   }

   private static RenderType.State translucentState() {
      return RenderType.State.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).createCompositeState(true);
   }

   public static RenderType translucent() {
      return TRANSLUCENT;
   }

   private static RenderType.State translucentMovingBlockState() {
      return RenderType.State.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true);
   }

   public static RenderType translucentMovingBlock() {
      return TRANSLUCENT_MOVING_BLOCK;
   }

   public static RenderType translucentNoCrumbling() {
      return TRANSLUCENT_NO_CRUMBLING;
   }

   public static RenderType armorCutoutNoCull(ResourceLocation p_239263_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_239263_0_, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
      return create("armor_cutout_no_cull", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
   }

   public static RenderType entitySolid(ResourceLocation p_228634_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228634_0_, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_solid", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
   }

   public static RenderType entityCutout(ResourceLocation p_228638_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228638_0_, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_cutout", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation p_230167_0_, boolean p_230167_1_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_230167_0_, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(p_230167_1_);
      return create("entity_cutout_no_cull", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation p_228640_0_) {
      return entityCutoutNoCull(p_228640_0_, true);
   }

   public static RenderType entityCutoutNoCullZOffset(ResourceLocation p_239266_0_, boolean p_239266_1_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_239266_0_, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(p_239266_1_);
      return create("entity_cutout_no_cull_z_offset", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
   }

   public static RenderType entityCutoutNoCullZOffset(ResourceLocation p_239267_0_) {
      return entityCutoutNoCullZOffset(p_239267_0_, true);
   }

   public static RenderType itemEntityTranslucentCull(ResourceLocation p_239268_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_239268_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderState.COLOR_DEPTH_WRITE).createCompositeState(true);
      return create("item_entity_translucent_cull", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
   }

   public static RenderType entityTranslucentCull(ResourceLocation p_228642_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228642_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_translucent_cull", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
   }

   public static RenderType entityTranslucent(ResourceLocation p_230168_0_, boolean p_230168_1_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_230168_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(p_230168_1_);
      return create("entity_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
   }

   public static RenderType entityTranslucent(ResourceLocation p_228644_0_) {
      return entityTranslucent(p_228644_0_, true);
   }

   public static RenderType entitySmoothCutout(ResourceLocation p_228646_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228646_0_, false, false)).setAlphaState(MIDWAY_ALPHA).setDiffuseLightingState(DIFFUSE_LIGHTING).setShadeModelState(SMOOTH_SHADE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true);
      return create("entity_smooth_cutout", DefaultVertexFormats.NEW_ENTITY, 7, 256, rendertype$state);
   }

   public static RenderType beaconBeam(ResourceLocation p_228637_0_, boolean p_228637_1_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228637_0_, false, false)).setTransparencyState(p_228637_1_ ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).setWriteMaskState(p_228637_1_ ? COLOR_WRITE : COLOR_DEPTH_WRITE).setFogState(NO_FOG).createCompositeState(false);
      return create("beacon_beam", DefaultVertexFormats.BLOCK, 7, 256, false, true, rendertype$state);
   }

   public static RenderType entityDecal(ResourceLocation p_228648_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228648_0_, false, false)).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
      return create("entity_decal", DefaultVertexFormats.NEW_ENTITY, 7, 256, rendertype$state);
   }

   public static RenderType entityNoOutline(ResourceLocation p_228650_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228650_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
      return create("entity_no_outline", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, true, rendertype$state);
   }

   public static RenderType entityShadow(ResourceLocation p_239272_0_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_239272_0_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
      return create("entity_shadow", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, false, rendertype$state);
   }

   public static RenderType dragonExplosionAlpha(ResourceLocation p_239264_0_, float p_239264_1_) {
      RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(p_239264_0_, false, false)).setAlphaState(new RenderState.AlphaState(p_239264_1_)).setCullState(NO_CULL).createCompositeState(true);
      return create("entity_alpha", DefaultVertexFormats.NEW_ENTITY, 7, 256, rendertype$state);
   }

   public static RenderType eyes(ResourceLocation p_228652_0_) {
      RenderState.TextureState renderstate$texturestate = new RenderState.TextureState(p_228652_0_, false, false);
      return create("eyes", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, true, RenderType.State.builder().setTextureState(renderstate$texturestate).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setFogState(BLACK_FOG).createCompositeState(false));
   }

   public static RenderType energySwirl(ResourceLocation p_228636_0_, float p_228636_1_, float p_228636_2_) {
      return create("energy_swirl", DefaultVertexFormats.NEW_ENTITY, 7, 256, false, true, RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228636_0_, false, false)).setTexturingState(new RenderState.OffsetTexturingState(p_228636_1_, p_228636_2_)).setFogState(BLACK_FOG).setTransparencyState(ADDITIVE_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return WATER_MASK;
   }

   public static RenderType outline(ResourceLocation p_228654_0_) {
      return outline(p_228654_0_, NO_CULL);
   }

   public static RenderType outline(ResourceLocation p_239265_0_, RenderState.CullState p_239265_1_) {
      return create("outline", DefaultVertexFormats.POSITION_COLOR_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(p_239265_0_, false, false)).setCullState(p_239265_1_).setDepthTestState(NO_DEPTH_TEST).setAlphaState(DEFAULT_ALPHA).setTexturingState(OUTLINE_TEXTURING).setFogState(NO_FOG).setOutputState(OUTLINE_TARGET).createCompositeState(RenderType.OutlineState.IS_OUTLINE));
   }

   public static RenderType armorGlint() {
      return ARMOR_GLINT;
   }

   public static RenderType armorEntityGlint() {
      return ARMOR_ENTITY_GLINT;
   }

   public static RenderType glintTranslucent() {
      return GLINT_TRANSLUCENT;
   }

   public static RenderType glint() {
      return GLINT;
   }

   public static RenderType glintDirect() {
      return GLINT_DIRECT;
   }

   public static RenderType entityGlint() {
      return ENTITY_GLINT;
   }

   public static RenderType entityGlintDirect() {
      return ENTITY_GLINT_DIRECT;
   }

   public static RenderType crumbling(ResourceLocation p_228656_0_) {
      RenderState.TextureState renderstate$texturestate = new RenderState.TextureState(p_228656_0_, false, false);
      return create("crumbling", DefaultVertexFormats.BLOCK, 7, 256, false, true, RenderType.State.builder().setTextureState(renderstate$texturestate).setAlphaState(DEFAULT_ALPHA).setTransparencyState(CRUMBLING_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
   }

   public static RenderType text(ResourceLocation p_228658_0_) {
      return create("text", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228658_0_, false, false)).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
   }

   public static RenderType textSeeThrough(ResourceLocation p_228660_0_) {
      return create("text_see_through", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, RenderType.State.builder().setTextureState(new RenderState.TextureState(p_228660_0_, false, false)).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
   }

   public static RenderType lightning() {
      return LIGHTNING;
   }

   private static RenderType.State tripwireState() {
      return RenderType.State.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(true);
   }

   public static RenderType tripwire() {
      return TRIPWIRE;
   }

   public static RenderType endPortal(int p_228630_0_) {
      RenderState.TransparencyState renderstate$transparencystate;
      RenderState.TextureState renderstate$texturestate;
      if (p_228630_0_ <= 1) {
         renderstate$transparencystate = TRANSLUCENT_TRANSPARENCY;
         renderstate$texturestate = new RenderState.TextureState(EndPortalTileEntityRenderer.END_SKY_LOCATION, false, false);
      } else {
         renderstate$transparencystate = ADDITIVE_TRANSPARENCY;
         renderstate$texturestate = new RenderState.TextureState(EndPortalTileEntityRenderer.END_PORTAL_LOCATION, false, false);
      }

      return create("end_portal", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.builder().setTransparencyState(renderstate$transparencystate).setTextureState(renderstate$texturestate).setTexturingState(new RenderState.PortalTexturingState(p_228630_0_)).setFogState(BLACK_FOG).createCompositeState(false));
   }

   public static RenderType lines() {
      return LINES;
   }

   public RenderType(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
      super(p_i225992_1_, p_i225992_7_, p_i225992_8_);
      this.format = p_i225992_2_;
      this.mode = p_i225992_3_;
      this.bufferSize = p_i225992_4_;
      this.affectsCrumbling = p_i225992_5_;
      this.sortOnUpload = p_i225992_6_;
      this.asOptional = Optional.of(this);
   }

   public static RenderType.Type create(String p_228632_0_, VertexFormat p_228632_1_, int p_228632_2_, int p_228632_3_, RenderType.State p_228632_4_) {
      return create(p_228632_0_, p_228632_1_, p_228632_2_, p_228632_3_, false, false, p_228632_4_);
   }

   public static RenderType.Type create(String p_228633_0_, VertexFormat p_228633_1_, int p_228633_2_, int p_228633_3_, boolean p_228633_4_, boolean p_228633_5_, RenderType.State p_228633_6_) {
      return RenderType.Type.memoize(p_228633_0_, p_228633_1_, p_228633_2_, p_228633_3_, p_228633_4_, p_228633_5_, p_228633_6_);
   }

   public void end(BufferBuilder p_228631_1_, int p_228631_2_, int p_228631_3_, int p_228631_4_) {
      if (p_228631_1_.building()) {
         if (this.sortOnUpload) {
            p_228631_1_.sortQuads((float)p_228631_2_, (float)p_228631_3_, (float)p_228631_4_);
         }

         p_228631_1_.end();
         this.setupRenderState();
         WorldVertexBufferUploader.end(p_228631_1_);
         this.clearRenderState();
      }
   }

   public String toString() {
      return this.name;
   }

   public static List<RenderType> chunkBufferLayers() {
      return ImmutableList.of(solid(), cutoutMipped(), cutout(), translucent(), tripwire());
   }

   public int bufferSize() {
      return this.bufferSize;
   }

   public VertexFormat format() {
      return this.format;
   }

   public int mode() {
      return this.mode;
   }

   public Optional<RenderType> outline() {
      return Optional.empty();
   }

   public boolean isOutline() {
      return false;
   }

   public boolean affectsCrumbling() {
      return this.affectsCrumbling;
   }

   public Optional<RenderType> asOptional() {
      return this.asOptional;
   }

   @OnlyIn(Dist.CLIENT)
   static enum OutlineState {
      NONE("none"),
      IS_OUTLINE("is_outline"),
      AFFECTS_OUTLINE("affects_outline");

      private final String name;

      private OutlineState(String p_i232465_3_) {
         this.name = p_i232465_3_;
      }

      public String toString() {
         return this.name;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class State {
      private final RenderState.TextureState textureState;
      private final RenderState.TransparencyState transparencyState;
      private final RenderState.DiffuseLightingState diffuseLightingState;
      private final RenderState.ShadeModelState shadeModelState;
      private final RenderState.AlphaState alphaState;
      private final RenderState.DepthTestState depthTestState;
      private final RenderState.CullState cullState;
      private final RenderState.LightmapState lightmapState;
      private final RenderState.OverlayState overlayState;
      private final RenderState.FogState fogState;
      private final RenderState.LayerState layeringState;
      private final RenderState.TargetState outputState;
      private final RenderState.TexturingState texturingState;
      private final RenderState.WriteMaskState writeMaskState;
      private final RenderState.LineState lineState;
      private final RenderType.OutlineState outlineProperty;
      private final ImmutableList<RenderState> states;

      private State(RenderState.TextureState p_i230053_1_, RenderState.TransparencyState p_i230053_2_, RenderState.DiffuseLightingState p_i230053_3_, RenderState.ShadeModelState p_i230053_4_, RenderState.AlphaState p_i230053_5_, RenderState.DepthTestState p_i230053_6_, RenderState.CullState p_i230053_7_, RenderState.LightmapState p_i230053_8_, RenderState.OverlayState p_i230053_9_, RenderState.FogState p_i230053_10_, RenderState.LayerState p_i230053_11_, RenderState.TargetState p_i230053_12_, RenderState.TexturingState p_i230053_13_, RenderState.WriteMaskState p_i230053_14_, RenderState.LineState p_i230053_15_, RenderType.OutlineState p_i230053_16_) {
         this.textureState = p_i230053_1_;
         this.transparencyState = p_i230053_2_;
         this.diffuseLightingState = p_i230053_3_;
         this.shadeModelState = p_i230053_4_;
         this.alphaState = p_i230053_5_;
         this.depthTestState = p_i230053_6_;
         this.cullState = p_i230053_7_;
         this.lightmapState = p_i230053_8_;
         this.overlayState = p_i230053_9_;
         this.fogState = p_i230053_10_;
         this.layeringState = p_i230053_11_;
         this.outputState = p_i230053_12_;
         this.texturingState = p_i230053_13_;
         this.writeMaskState = p_i230053_14_;
         this.lineState = p_i230053_15_;
         this.outlineProperty = p_i230053_16_;
         this.states = ImmutableList.of(this.textureState, this.transparencyState, this.diffuseLightingState, this.shadeModelState, this.alphaState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.fogState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.lineState);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            RenderType.State rendertype$state = (RenderType.State)p_equals_1_;
            return this.outlineProperty == rendertype$state.outlineProperty && this.states.equals(rendertype$state.states);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.states, this.outlineProperty);
      }

      public String toString() {
         return "CompositeState[" + this.states + ", outlineProperty=" + this.outlineProperty + ']';
      }

      public static RenderType.State.Builder builder() {
         return new RenderType.State.Builder();
      }

      @OnlyIn(Dist.CLIENT)
      public static class Builder {
         private RenderState.TextureState textureState = RenderState.NO_TEXTURE;
         private RenderState.TransparencyState transparencyState = RenderState.NO_TRANSPARENCY;
         private RenderState.DiffuseLightingState diffuseLightingState = RenderState.NO_DIFFUSE_LIGHTING;
         private RenderState.ShadeModelState shadeModelState = RenderState.FLAT_SHADE;
         private RenderState.AlphaState alphaState = RenderState.NO_ALPHA;
         private RenderState.DepthTestState depthTestState = RenderState.LEQUAL_DEPTH_TEST;
         private RenderState.CullState cullState = RenderState.CULL;
         private RenderState.LightmapState lightmapState = RenderState.NO_LIGHTMAP;
         private RenderState.OverlayState overlayState = RenderState.NO_OVERLAY;
         private RenderState.FogState fogState = RenderState.FOG;
         private RenderState.LayerState layeringState = RenderState.NO_LAYERING;
         private RenderState.TargetState outputState = RenderState.MAIN_TARGET;
         private RenderState.TexturingState texturingState = RenderState.DEFAULT_TEXTURING;
         private RenderState.WriteMaskState writeMaskState = RenderState.COLOR_DEPTH_WRITE;
         private RenderState.LineState lineState = RenderState.DEFAULT_LINE;

         private Builder() {
         }

         public RenderType.State.Builder setTextureState(RenderState.TextureState p_228724_1_) {
            this.textureState = p_228724_1_;
            return this;
         }

         public RenderType.State.Builder setTransparencyState(RenderState.TransparencyState p_228726_1_) {
            this.transparencyState = p_228726_1_;
            return this;
         }

         public RenderType.State.Builder setDiffuseLightingState(RenderState.DiffuseLightingState p_228716_1_) {
            this.diffuseLightingState = p_228716_1_;
            return this;
         }

         public RenderType.State.Builder setShadeModelState(RenderState.ShadeModelState p_228723_1_) {
            this.shadeModelState = p_228723_1_;
            return this;
         }

         public RenderType.State.Builder setAlphaState(RenderState.AlphaState p_228713_1_) {
            this.alphaState = p_228713_1_;
            return this;
         }

         public RenderType.State.Builder setDepthTestState(RenderState.DepthTestState p_228715_1_) {
            this.depthTestState = p_228715_1_;
            return this;
         }

         public RenderType.State.Builder setCullState(RenderState.CullState p_228714_1_) {
            this.cullState = p_228714_1_;
            return this;
         }

         public RenderType.State.Builder setLightmapState(RenderState.LightmapState p_228719_1_) {
            this.lightmapState = p_228719_1_;
            return this;
         }

         public RenderType.State.Builder setOverlayState(RenderState.OverlayState p_228722_1_) {
            this.overlayState = p_228722_1_;
            return this;
         }

         public RenderType.State.Builder setFogState(RenderState.FogState p_228717_1_) {
            this.fogState = p_228717_1_;
            return this;
         }

         public RenderType.State.Builder setLayeringState(RenderState.LayerState p_228718_1_) {
            this.layeringState = p_228718_1_;
            return this;
         }

         public RenderType.State.Builder setOutputState(RenderState.TargetState p_228721_1_) {
            this.outputState = p_228721_1_;
            return this;
         }

         public RenderType.State.Builder setTexturingState(RenderState.TexturingState p_228725_1_) {
            this.texturingState = p_228725_1_;
            return this;
         }

         public RenderType.State.Builder setWriteMaskState(RenderState.WriteMaskState p_228727_1_) {
            this.writeMaskState = p_228727_1_;
            return this;
         }

         public RenderType.State.Builder setLineState(RenderState.LineState p_228720_1_) {
            this.lineState = p_228720_1_;
            return this;
         }

         public RenderType.State createCompositeState(boolean p_228728_1_) {
            return this.createCompositeState(p_228728_1_ ? RenderType.OutlineState.AFFECTS_OUTLINE : RenderType.OutlineState.NONE);
         }

         public RenderType.State createCompositeState(RenderType.OutlineState p_230173_1_) {
            return new RenderType.State(this.textureState, this.transparencyState, this.diffuseLightingState, this.shadeModelState, this.alphaState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.fogState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.lineState, p_230173_1_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class Type extends RenderType {
      private static final ObjectOpenCustomHashSet<RenderType.Type> INSTANCES = new ObjectOpenCustomHashSet<>(RenderType.Type.EqualityStrategy.INSTANCE);
      private final RenderType.State state;
      private final int hashCode;
      private final Optional<RenderType> outline;
      private final boolean isOutline;

      private Type(String p_i225993_1_, VertexFormat p_i225993_2_, int p_i225993_3_, int p_i225993_4_, boolean p_i225993_5_, boolean p_i225993_6_, RenderType.State p_i225993_7_) {
         super(p_i225993_1_, p_i225993_2_, p_i225993_3_, p_i225993_4_, p_i225993_5_, p_i225993_6_, () -> {
            p_i225993_7_.states.forEach(RenderState::setupRenderState);
         }, () -> {
            p_i225993_7_.states.forEach(RenderState::clearRenderState);
         });
         this.state = p_i225993_7_;
         this.outline = p_i225993_7_.outlineProperty == RenderType.OutlineState.AFFECTS_OUTLINE ? p_i225993_7_.textureState.texture().map((p_239275_1_) -> {
            return outline(p_239275_1_, p_i225993_7_.cullState);
         }) : Optional.empty();
         this.isOutline = p_i225993_7_.outlineProperty == RenderType.OutlineState.IS_OUTLINE;
         this.hashCode = Objects.hash(super.hashCode(), p_i225993_7_);
      }

      private static RenderType.Type memoize(String p_228676_0_, VertexFormat p_228676_1_, int p_228676_2_, int p_228676_3_, boolean p_228676_4_, boolean p_228676_5_, RenderType.State p_228676_6_) {
         return INSTANCES.addOrGet(new RenderType.Type(p_228676_0_, p_228676_1_, p_228676_2_, p_228676_3_, p_228676_4_, p_228676_5_, p_228676_6_));
      }

      public Optional<RenderType> outline() {
         return this.outline;
      }

      public boolean isOutline() {
         return this.isOutline;
      }

      public boolean equals(@Nullable Object p_equals_1_) {
         return this == p_equals_1_;
      }

      public int hashCode() {
         return this.hashCode;
      }

      public String toString() {
         return "RenderType[" + this.state + ']';
      }

      @OnlyIn(Dist.CLIENT)
      static enum EqualityStrategy implements Strategy<RenderType.Type> {
         INSTANCE;

         public int hashCode(@Nullable RenderType.Type p_hashCode_1_) {
            return p_hashCode_1_ == null ? 0 : p_hashCode_1_.hashCode;
         }

         public boolean equals(@Nullable RenderType.Type p_equals_1_, @Nullable RenderType.Type p_equals_2_) {
            if (p_equals_1_ == p_equals_2_) {
               return true;
            } else {
               return p_equals_1_ != null && p_equals_2_ != null ? Objects.equals(p_equals_1_.state, p_equals_2_.state) : false;
            }
         }
      }
   }
}
