package net.minecraft.client.renderer.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.fluid.FluidState;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelManager extends ReloadListener<ModelBakery> implements AutoCloseable {
   private Map<ResourceLocation, IBakedModel> bakedRegistry = new java.util.HashMap<>();
   @Nullable
   private SpriteMap atlases;
   private final BlockModelShapes blockModelShaper;
   private final TextureManager textureManager;
   private final BlockColors blockColors;
   private int maxMipmapLevels;
   private IBakedModel missingModel;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(TextureManager p_i226057_1_, BlockColors p_i226057_2_, int p_i226057_3_) {
      this.textureManager = p_i226057_1_;
      this.blockColors = p_i226057_2_;
      this.maxMipmapLevels = p_i226057_3_;
      this.blockModelShaper = new BlockModelShapes(this);
   }

   public IBakedModel getModel(ResourceLocation modelLocation) {
      return this.bakedRegistry.getOrDefault(modelLocation, this.missingModel);
   }

   public IBakedModel getModel(ModelResourceLocation p_174953_1_) {
      return this.bakedRegistry.getOrDefault(p_174953_1_, this.missingModel);
   }

   public IBakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShapes getBlockModelShaper() {
      return this.blockModelShaper;
   }

   protected ModelBakery prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      p_212854_2_.startTick();
      net.minecraftforge.client.model.ModelLoader modelbakery = new net.minecraftforge.client.model.ModelLoader(p_212854_1_, this.blockColors, p_212854_2_, this.maxMipmapLevels);
      p_212854_2_.endTick();
      return modelbakery;
   }

   protected void apply(ModelBakery p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_3_.startTick();
      p_212853_3_.push("upload");
      if (this.atlases != null) {
         this.atlases.close();
      }

      this.atlases = p_212853_1_.uploadTextures(this.textureManager, p_212853_3_);
      this.bakedRegistry = p_212853_1_.getBakedTopLevelModels();
      this.modelGroups = p_212853_1_.getModelGroups();
      this.missingModel = this.bakedRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
      net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.bakedRegistry, (net.minecraftforge.client.model.ModelLoader) p_212853_1_);
      p_212853_3_.popPush("cache");
      this.blockModelShaper.rebuildCache();
      p_212853_3_.pop();
      p_212853_3_.endTick();
   }

   public boolean requiresRender(BlockState p_224742_1_, BlockState p_224742_2_) {
      if (p_224742_1_ == p_224742_2_) {
         return false;
      } else {
         int i = this.modelGroups.getInt(p_224742_1_);
         if (i != -1) {
            int j = this.modelGroups.getInt(p_224742_2_);
            if (i == j) {
               FluidState fluidstate = p_224742_1_.getFluidState();
               FluidState fluidstate1 = p_224742_2_.getFluidState();
               return fluidstate != fluidstate1;
            }
         }

         return true;
      }
   }

   public AtlasTexture getAtlas(ResourceLocation p_229356_1_) {
      if (this.atlases == null) throw new RuntimeException("getAtlasTexture called too early!");
      return this.atlases.getAtlas(p_229356_1_);
   }

   public void close() {
      if (this.atlases != null) {
         this.atlases.close();
      }

   }

   public void updateMaxMipLevel(int p_229355_1_) {
      this.maxMipmapLevels = p_229355_1_;
   }

   // TODO
   //@Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}
