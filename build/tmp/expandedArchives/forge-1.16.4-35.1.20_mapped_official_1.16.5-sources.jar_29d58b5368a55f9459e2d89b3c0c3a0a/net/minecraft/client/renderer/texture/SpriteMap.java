package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteMap implements AutoCloseable {
   private final Map<ResourceLocation, AtlasTexture> atlases;

   public SpriteMap(Collection<AtlasTexture> p_i226042_1_) {
      this.atlases = p_i226042_1_.stream().collect(Collectors.toMap(AtlasTexture::location, Function.identity()));
   }

   public AtlasTexture getAtlas(ResourceLocation p_229152_1_) {
      return this.atlases.get(p_229152_1_);
   }

   public TextureAtlasSprite getSprite(RenderMaterial p_229151_1_) {
      return this.atlases.get(p_229151_1_.atlasLocation()).getSprite(p_229151_1_.texture());
   }

   public void close() {
      this.atlases.values().forEach(AtlasTexture::clearTextureData);
      this.atlases.clear();
   }
}
