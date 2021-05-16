package net.minecraft.client.renderer.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BuiltInModel implements IBakedModel {
   private final ItemCameraTransforms itemTransforms;
   private final ItemOverrideList overrides;
   private final TextureAtlasSprite particleTexture;
   private final boolean usesBlockLight;

   public BuiltInModel(ItemCameraTransforms p_i230058_1_, ItemOverrideList p_i230058_2_, TextureAtlasSprite p_i230058_3_, boolean p_i230058_4_) {
      this.itemTransforms = p_i230058_1_;
      this.overrides = p_i230058_2_;
      this.particleTexture = p_i230058_3_;
      this.usesBlockLight = p_i230058_4_;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
      return Collections.emptyList();
   }

   public boolean useAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   public boolean isCustomRenderer() {
      return true;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleTexture;
   }

   public ItemCameraTransforms getTransforms() {
      return this.itemTransforms;
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }
}
