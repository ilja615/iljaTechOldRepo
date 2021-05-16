package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class MultipartBakedModel implements net.minecraftforge.client.model.data.IDynamicBakedModel {
   private final List<Pair<Predicate<BlockState>, IBakedModel>> selectors;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemCameraTransforms transforms;
   protected final ItemOverrideList overrides;
   private final Map<BlockState, BitSet> selectorCache = new Object2ObjectOpenCustomHashMap<>(Util.identityStrategy());
   private final IBakedModel defaultModel;

   public MultipartBakedModel(List<Pair<Predicate<BlockState>, IBakedModel>> p_i48273_1_) {
      this.selectors = p_i48273_1_;
      IBakedModel ibakedmodel = p_i48273_1_.iterator().next().getRight();
      this.defaultModel = ibakedmodel;
      this.hasAmbientOcclusion = ibakedmodel.useAmbientOcclusion();
      this.isGui3d = ibakedmodel.isGui3d();
      this.usesBlockLight = ibakedmodel.usesBlockLight();
      this.particleIcon = ibakedmodel.getParticleIcon();
      this.transforms = ibakedmodel.getTransforms();
      this.overrides = ibakedmodel.getOverrides();
   }

   // FORGE: Implement our overloads (here and below) so child models can have custom logic 
   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_, net.minecraftforge.client.model.data.IModelData modelData) {
      if (p_200117_1_ == null) {
         return Collections.emptyList();
      } else {
         BitSet bitset = this.selectorCache.get(p_200117_1_);
         if (bitset == null) {
            bitset = new BitSet();

            for(int i = 0; i < this.selectors.size(); ++i) {
               Pair<Predicate<BlockState>, IBakedModel> pair = this.selectors.get(i);
               if (pair.getLeft().test(p_200117_1_)) {
                  bitset.set(i);
               }
            }

            this.selectorCache.put(p_200117_1_, bitset);
         }

         List<BakedQuad> list = Lists.newArrayList();
         long k = p_200117_3_.nextLong();

         for(int j = 0; j < bitset.length(); ++j) {
            if (bitset.get(j)) {
               list.addAll(this.selectors.get(j).getRight().getQuads(p_200117_1_, p_200117_2_, new Random(k), modelData));
            }
         }

         return list;
      }
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean isAmbientOcclusion(BlockState state) {
      return this.defaultModel.isAmbientOcclusion(state);
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   @Deprecated
   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public TextureAtlasSprite getParticleTexture(net.minecraftforge.client.model.data.IModelData modelData) {
      return this.defaultModel.getParticleTexture(modelData);
   }

   @Deprecated
   public ItemCameraTransforms getTransforms() {
      return this.transforms;
   }

   public IBakedModel handlePerspective(net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType transformType, com.mojang.blaze3d.matrix.MatrixStack matrixStack) {
      return this.defaultModel.handlePerspective(transformType, matrixStack);
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<Pair<Predicate<BlockState>, IBakedModel>> selectors = Lists.newArrayList();

      public void add(Predicate<BlockState> p_188648_1_, IBakedModel p_188648_2_) {
         this.selectors.add(Pair.of(p_188648_1_, p_188648_2_));
      }

      public IBakedModel build() {
         return new MultipartBakedModel(this.selectors);
      }
   }
}
