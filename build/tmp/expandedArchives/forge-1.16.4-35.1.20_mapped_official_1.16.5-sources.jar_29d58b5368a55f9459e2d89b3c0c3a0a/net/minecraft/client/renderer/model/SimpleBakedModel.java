package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> unculledFaces;
   protected final Map<Direction, List<BakedQuad>> culledFaces;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemCameraTransforms transforms;
   protected final ItemOverrideList overrides;

   public SimpleBakedModel(List<BakedQuad> p_i230059_1_, Map<Direction, List<BakedQuad>> p_i230059_2_, boolean p_i230059_3_, boolean p_i230059_4_, boolean p_i230059_5_, TextureAtlasSprite p_i230059_6_, ItemCameraTransforms p_i230059_7_, ItemOverrideList p_i230059_8_) {
      this.unculledFaces = p_i230059_1_;
      this.culledFaces = p_i230059_2_;
      this.hasAmbientOcclusion = p_i230059_3_;
      this.isGui3d = p_i230059_5_;
      this.usesBlockLight = p_i230059_4_;
      this.particleIcon = p_i230059_6_;
      this.transforms = p_i230059_7_;
      this.overrides = p_i230059_8_;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
      return p_200117_2_ == null ? this.unculledFaces : this.culledFaces.get(p_200117_2_);
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
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

   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public ItemCameraTransforms getTransforms() {
      return this.transforms;
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> unculledFaces = Lists.newArrayList();
      private final Map<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
      private final ItemOverrideList overrides;
      private final boolean hasAmbientOcclusion;
      private TextureAtlasSprite particleIcon;
      private final boolean usesBlockLight;
      private final boolean isGui3d;
      private final ItemCameraTransforms transforms;

      public Builder(net.minecraftforge.client.model.IModelConfiguration model, ItemOverrideList overrides) {
         this(model.useSmoothLighting(), model.isSideLit(), model.isShadedInGui(), model.getCameraTransforms(), overrides);
      }

      public Builder(BlockModel p_i230060_1_, ItemOverrideList p_i230060_2_, boolean p_i230060_3_) {
         this(p_i230060_1_.hasAmbientOcclusion(), p_i230060_1_.getGuiLight().lightLikeBlock(), p_i230060_3_, p_i230060_1_.getTransforms(), p_i230060_2_);
      }

      private Builder(boolean p_i230061_1_, boolean p_i230061_2_, boolean p_i230061_3_, ItemCameraTransforms p_i230061_4_, ItemOverrideList p_i230061_5_) {
         for(Direction direction : Direction.values()) {
            this.culledFaces.put(direction, Lists.newArrayList());
         }

         this.overrides = p_i230061_5_;
         this.hasAmbientOcclusion = p_i230061_1_;
         this.usesBlockLight = p_i230061_2_;
         this.isGui3d = p_i230061_3_;
         this.transforms = p_i230061_4_;
      }

      public SimpleBakedModel.Builder addCulledFace(Direction p_177650_1_, BakedQuad p_177650_2_) {
         this.culledFaces.get(p_177650_1_).add(p_177650_2_);
         return this;
      }

      public SimpleBakedModel.Builder addUnculledFace(BakedQuad p_177648_1_) {
         this.unculledFaces.add(p_177648_1_);
         return this;
      }

      public SimpleBakedModel.Builder particle(TextureAtlasSprite p_177646_1_) {
         this.particleIcon = p_177646_1_;
         return this;
      }

      public IBakedModel build() {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
         }
      }
   }
}
