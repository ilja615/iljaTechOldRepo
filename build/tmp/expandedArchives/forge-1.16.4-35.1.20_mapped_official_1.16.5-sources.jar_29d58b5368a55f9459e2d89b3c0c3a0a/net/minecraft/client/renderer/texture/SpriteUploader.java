package net.minecraft.client.renderer.texture;

import java.util.stream.Stream;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable {
   private final AtlasTexture textureAtlas;
   private final String prefix;

   public SpriteUploader(TextureManager p_i50905_1_, ResourceLocation p_i50905_2_, String p_i50905_3_) {
      this.prefix = p_i50905_3_;
      this.textureAtlas = new AtlasTexture(p_i50905_2_);
      p_i50905_1_.register(this.textureAtlas.location(), this.textureAtlas);
   }

   protected abstract Stream<ResourceLocation> getResourcesToLoad();

   protected TextureAtlasSprite getSprite(ResourceLocation p_215282_1_) {
      return this.textureAtlas.getSprite(this.resolveLocation(p_215282_1_));
   }

   private ResourceLocation resolveLocation(ResourceLocation p_229299_1_) {
      return new ResourceLocation(p_229299_1_.getNamespace(), this.prefix + "/" + p_229299_1_.getPath());
   }

   protected AtlasTexture.SheetData prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      p_212854_2_.startTick();
      p_212854_2_.push("stitching");
      AtlasTexture.SheetData atlastexture$sheetdata = this.textureAtlas.prepareToStitch(p_212854_1_, this.getResourcesToLoad().map(this::resolveLocation), p_212854_2_, 0);
      p_212854_2_.pop();
      p_212854_2_.endTick();
      return atlastexture$sheetdata;
   }

   protected void apply(AtlasTexture.SheetData p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_3_.startTick();
      p_212853_3_.push("upload");
      this.textureAtlas.reload(p_212853_1_);
      p_212853_3_.pop();
      p_212853_3_.endTick();
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }
}
