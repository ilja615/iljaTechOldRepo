package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelMesher {
   public final Int2ObjectMap<ModelResourceLocation> shapes = new Int2ObjectOpenHashMap<>(256);
   private final Int2ObjectMap<IBakedModel> shapesCache = new Int2ObjectOpenHashMap<>(256);
   private final ModelManager modelManager;

   public ItemModelMesher(ModelManager p_i46250_1_) {
      this.modelManager = p_i46250_1_;
   }

   public TextureAtlasSprite getParticleIcon(IItemProvider p_199934_1_) {
      return this.getParticleIcon(new ItemStack(p_199934_1_));
   }

   public TextureAtlasSprite getParticleIcon(ItemStack p_199309_1_) {
      IBakedModel ibakedmodel = this.getItemModel(p_199309_1_);
      // FORGE: Make sure to call the item overrides
      return ibakedmodel == this.modelManager.getMissingModel() && p_199309_1_.getItem() instanceof BlockItem ? this.modelManager.getBlockModelShaper().getParticleIcon(((BlockItem)p_199309_1_.getItem()).getBlock().defaultBlockState()) : ibakedmodel.getOverrides().resolve(ibakedmodel, p_199309_1_, null, null).getParticleTexture(net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public IBakedModel getItemModel(ItemStack p_178089_1_) {
      IBakedModel ibakedmodel = this.getItemModel(p_178089_1_.getItem());
      return ibakedmodel == null ? this.modelManager.getMissingModel() : ibakedmodel;
   }

   @Nullable
   public IBakedModel getItemModel(Item p_199312_1_) {
      return this.shapesCache.get(getIndex(p_199312_1_));
   }

   private static int getIndex(Item p_199310_0_) {
      return Item.getId(p_199310_0_);
   }

   public void register(Item p_199311_1_, ModelResourceLocation p_199311_2_) {
      this.shapes.put(getIndex(p_199311_1_), p_199311_2_);
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.shapesCache.clear();

      for(Entry<Integer, ModelResourceLocation> entry : this.shapes.entrySet()) {
         this.shapesCache.put(entry.getKey(), this.modelManager.getModel(entry.getValue()));
      }

   }
}
