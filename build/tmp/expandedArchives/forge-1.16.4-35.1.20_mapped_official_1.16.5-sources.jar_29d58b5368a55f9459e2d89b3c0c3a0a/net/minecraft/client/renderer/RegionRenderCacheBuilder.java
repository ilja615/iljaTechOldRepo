package net.minecraft.client.renderer;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionRenderCacheBuilder {
   private final Map<RenderType, BufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((p_228369_0_) -> {
      return p_228369_0_;
   }, (p_228368_0_) -> {
      return new BufferBuilder(p_228368_0_.bufferSize());
   }));

   public BufferBuilder builder(RenderType p_228366_1_) {
      return this.builders.get(p_228366_1_);
   }

   public void clearAll() {
      this.builders.values().forEach(BufferBuilder::clear);
   }

   public void discardAll() {
      this.builders.values().forEach(BufferBuilder::discard);
   }
}
