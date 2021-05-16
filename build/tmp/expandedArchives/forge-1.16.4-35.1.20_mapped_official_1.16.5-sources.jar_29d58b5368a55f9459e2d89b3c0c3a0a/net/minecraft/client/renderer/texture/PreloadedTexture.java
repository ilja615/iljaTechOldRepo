package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PreloadedTexture extends SimpleTexture {
   @Nullable
   private CompletableFuture<SimpleTexture.TextureData> future;

   public PreloadedTexture(IResourceManager p_i50911_1_, ResourceLocation p_i50911_2_, Executor p_i50911_3_) {
      super(p_i50911_2_);
      this.future = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureData.load(p_i50911_1_, p_i50911_2_);
      }, p_i50911_3_);
   }

   protected SimpleTexture.TextureData getTextureImage(IResourceManager p_215246_1_) {
      if (this.future != null) {
         SimpleTexture.TextureData simpletexture$texturedata = this.future.join();
         this.future = null;
         return simpletexture$texturedata;
      } else {
         return SimpleTexture.TextureData.load(p_215246_1_, this.location);
      }
   }

   public CompletableFuture<Void> getFuture() {
      return this.future == null ? CompletableFuture.completedFuture((Void)null) : this.future.thenApply((p_215247_0_) -> {
         return null;
      });
   }

   public void reset(TextureManager p_215244_1_, IResourceManager p_215244_2_, ResourceLocation p_215244_3_, Executor p_215244_4_) {
      this.future = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureData.load(p_215244_2_, this.location);
      }, Util.backgroundExecutor());
      this.future.thenRunAsync(() -> {
         p_215244_1_.register(this.location, this);
      }, executor(p_215244_4_));
   }

   private static Executor executor(Executor p_229205_0_) {
      return (p_229206_1_) -> {
         p_229205_0_.execute(() -> {
            RenderSystem.recordRenderCall(p_229206_1_::run);
         });
      };
   }
}
