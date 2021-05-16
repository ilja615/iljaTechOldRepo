package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTypeBuffers {
   private final RegionRenderCacheBuilder fixedBufferPack = new RegionRenderCacheBuilder();
   private final SortedMap<RenderType, BufferBuilder> fixedBuffers = Util.make(new Object2ObjectLinkedOpenHashMap<>(), (p_228485_1_) -> {
      p_228485_1_.put(Atlases.solidBlockSheet(), this.fixedBufferPack.builder(RenderType.solid()));
      p_228485_1_.put(Atlases.cutoutBlockSheet(), this.fixedBufferPack.builder(RenderType.cutout()));
      p_228485_1_.put(Atlases.bannerSheet(), this.fixedBufferPack.builder(RenderType.cutoutMipped()));
      p_228485_1_.put(Atlases.translucentCullBlockSheet(), this.fixedBufferPack.builder(RenderType.translucent()));
      put(p_228485_1_, Atlases.shieldSheet());
      put(p_228485_1_, Atlases.bedSheet());
      put(p_228485_1_, Atlases.shulkerBoxSheet());
      put(p_228485_1_, Atlases.signSheet());
      put(p_228485_1_, Atlases.chestSheet());
      put(p_228485_1_, RenderType.translucentNoCrumbling());
      put(p_228485_1_, RenderType.armorGlint());
      put(p_228485_1_, RenderType.armorEntityGlint());
      put(p_228485_1_, RenderType.glint());
      put(p_228485_1_, RenderType.glintDirect());
      put(p_228485_1_, RenderType.glintTranslucent());
      put(p_228485_1_, RenderType.entityGlint());
      put(p_228485_1_, RenderType.entityGlintDirect());
      put(p_228485_1_, RenderType.waterMask());
      ModelBakery.DESTROY_TYPES.forEach((p_228488_1_) -> {
         put(p_228485_1_, p_228488_1_);
      });
   });
   private final IRenderTypeBuffer.Impl bufferSource = IRenderTypeBuffer.immediateWithBuffers(this.fixedBuffers, new BufferBuilder(256));
   private final IRenderTypeBuffer.Impl crumblingBufferSource = IRenderTypeBuffer.immediate(new BufferBuilder(256));
   private final OutlineLayerBuffer outlineBufferSource = new OutlineLayerBuffer(this.bufferSource);

   private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> p_228486_0_, RenderType p_228486_1_) {
      p_228486_0_.put(p_228486_1_, new BufferBuilder(p_228486_1_.bufferSize()));
   }

   public RegionRenderCacheBuilder fixedBufferPack() {
      return this.fixedBufferPack;
   }

   public IRenderTypeBuffer.Impl bufferSource() {
      return this.bufferSource;
   }

   public IRenderTypeBuffer.Impl crumblingBufferSource() {
      return this.crumblingBufferSource;
   }

   public OutlineLayerBuffer outlineBufferSource() {
      return this.outlineBufferSource;
   }
}
