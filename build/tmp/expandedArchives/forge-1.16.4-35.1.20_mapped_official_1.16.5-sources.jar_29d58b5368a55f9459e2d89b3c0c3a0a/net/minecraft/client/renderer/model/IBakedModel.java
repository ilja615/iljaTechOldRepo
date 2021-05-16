package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel extends net.minecraftforge.client.extensions.IForgeBakedModel {
   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getQuads(IBlockState, EnumFacing, Random, net.minecraftforge.client.model.data.IModelData)}*/
   @Deprecated
   List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_);

   boolean useAmbientOcclusion();

   boolean isGui3d();

   boolean usesBlockLight();

   boolean isCustomRenderer();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getParticleTexture(net.minecraftforge.client.model.data.IModelData)}*/
   @Deprecated
   TextureAtlasSprite getParticleIcon();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#handlePerspective(net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType, com.mojang.blaze3d.matrix.MatrixStack)} instead */
   @Deprecated
   default ItemCameraTransforms getTransforms() { return ItemCameraTransforms.NO_TRANSFORMS; }

   ItemOverrideList getOverrides();
}
