package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelShapes {
   private final Map<BlockState, IBakedModel> modelByStateCache = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShapes(ModelManager p_i46245_1_) {
      this.modelManager = p_i46245_1_;
   }

   @Deprecated
   public TextureAtlasSprite getParticleIcon(BlockState p_178122_1_) {
      return this.getBlockModel(p_178122_1_).getParticleTexture(net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public TextureAtlasSprite getTexture(BlockState state, net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos) {
      net.minecraftforge.client.model.data.IModelData data = net.minecraftforge.client.model.ModelDataManager.getModelData(world, pos);
      return this.getBlockModel(state).getParticleTexture(data == null ? net.minecraftforge.client.model.data.EmptyModelData.INSTANCE : data);
   }

   public IBakedModel getBlockModel(BlockState p_178125_1_) {
      IBakedModel ibakedmodel = this.modelByStateCache.get(p_178125_1_);
      if (ibakedmodel == null) {
         ibakedmodel = this.modelManager.getMissingModel();
      }

      return ibakedmodel;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.modelByStateCache.clear();

      for(Block block : Registry.BLOCK) {
         block.getStateDefinition().getPossibleStates().forEach((p_209551_1_) -> {
            IBakedModel ibakedmodel = this.modelByStateCache.put(p_209551_1_, this.modelManager.getModel(stateToModelLocation(p_209551_1_)));
         });
      }

   }

   public static ModelResourceLocation stateToModelLocation(BlockState p_209554_0_) {
      return stateToModelLocation(Registry.BLOCK.getKey(p_209554_0_.getBlock()), p_209554_0_);
   }

   public static ModelResourceLocation stateToModelLocation(ResourceLocation p_209553_0_, BlockState p_209553_1_) {
      return new ModelResourceLocation(p_209553_0_, statePropertiesToString(p_209553_1_.getValues()));
   }

   public static String statePropertiesToString(Map<Property<?>, Comparable<?>> p_209552_0_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Entry<Property<?>, Comparable<?>> entry : p_209552_0_.entrySet()) {
         if (stringbuilder.length() != 0) {
            stringbuilder.append(',');
         }

         Property<?> property = entry.getKey();
         stringbuilder.append(property.getName());
         stringbuilder.append('=');
         stringbuilder.append(getValue(property, entry.getValue()));
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> String getValue(Property<T> p_209555_0_, Comparable<?> p_209555_1_) {
      return p_209555_0_.getName((T)p_209555_1_);
   }
}
