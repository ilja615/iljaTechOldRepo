package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRenderTypeBuffer {
   static IRenderTypeBuffer.Impl immediate(BufferBuilder p_228455_0_) {
      return immediateWithBuffers(ImmutableMap.of(), p_228455_0_);
   }

   static IRenderTypeBuffer.Impl immediateWithBuffers(Map<RenderType, BufferBuilder> p_228456_0_, BufferBuilder p_228456_1_) {
      return new IRenderTypeBuffer.Impl(p_228456_1_, p_228456_0_);
   }

   IVertexBuilder getBuffer(RenderType p_getBuffer_1_);

   @OnlyIn(Dist.CLIENT)
   public static class Impl implements IRenderTypeBuffer {
      protected final BufferBuilder builder;
      protected final Map<RenderType, BufferBuilder> fixedBuffers;
      protected Optional<RenderType> lastState = Optional.empty();
      protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();

      protected Impl(BufferBuilder p_i225969_1_, Map<RenderType, BufferBuilder> p_i225969_2_) {
         this.builder = p_i225969_1_;
         this.fixedBuffers = p_i225969_2_;
      }

      public IVertexBuilder getBuffer(RenderType p_getBuffer_1_) {
         Optional<RenderType> optional = p_getBuffer_1_.asOptional();
         BufferBuilder bufferbuilder = this.getBuilderRaw(p_getBuffer_1_);
         if (!Objects.equals(this.lastState, optional)) {
            if (this.lastState.isPresent()) {
               RenderType rendertype = this.lastState.get();
               if (!this.fixedBuffers.containsKey(rendertype)) {
                  this.endBatch(rendertype);
               }
            }

            if (this.startedBuffers.add(bufferbuilder)) {
               bufferbuilder.begin(p_getBuffer_1_.mode(), p_getBuffer_1_.format());
            }

            this.lastState = optional;
         }

         return bufferbuilder;
      }

      private BufferBuilder getBuilderRaw(RenderType p_228463_1_) {
         return this.fixedBuffers.getOrDefault(p_228463_1_, this.builder);
      }

      public void endBatch() {
         this.lastState.ifPresent((p_228464_1_) -> {
            IVertexBuilder ivertexbuilder = this.getBuffer(p_228464_1_);
            if (ivertexbuilder == this.builder) {
               this.endBatch(p_228464_1_);
            }

         });

         for(RenderType rendertype : this.fixedBuffers.keySet()) {
            this.endBatch(rendertype);
         }

      }

      public void endBatch(RenderType p_228462_1_) {
         BufferBuilder bufferbuilder = this.getBuilderRaw(p_228462_1_);
         boolean flag = Objects.equals(this.lastState, p_228462_1_.asOptional());
         if (flag || bufferbuilder != this.builder) {
            if (this.startedBuffers.remove(bufferbuilder)) {
               p_228462_1_.end(bufferbuilder, 0, 0, 0);
               if (flag) {
                  this.lastState = Optional.empty();
               }

            }
         }
      }
   }
}
