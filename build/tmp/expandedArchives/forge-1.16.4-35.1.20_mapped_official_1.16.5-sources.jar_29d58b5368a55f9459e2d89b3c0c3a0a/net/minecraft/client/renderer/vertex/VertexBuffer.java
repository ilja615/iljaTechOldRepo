package net.minecraft.client.renderer.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBuffer implements AutoCloseable {
   private int id;
   private final VertexFormat format;
   private int vertexCount;

   public VertexBuffer(VertexFormat p_i46098_1_) {
      this.format = p_i46098_1_;
      RenderSystem.glGenBuffers((p_227876_1_) -> {
         this.id = p_227876_1_;
      });
   }

   public void bind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return this.id;
      });
   }

   public void upload(BufferBuilder p_227875_1_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.upload_(p_227875_1_);
         });
      } else {
         this.upload_(p_227875_1_);
      }

   }

   public CompletableFuture<Void> uploadLater(BufferBuilder p_227878_1_) {
      if (!RenderSystem.isOnRenderThread()) {
         return CompletableFuture.runAsync(() -> {
            this.upload_(p_227878_1_);
         }, (p_227877_0_) -> {
            RenderSystem.recordRenderCall(p_227877_0_::run);
         });
      } else {
         this.upload_(p_227878_1_);
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private void upload_(BufferBuilder p_227880_1_) {
      Pair<BufferBuilder.DrawState, ByteBuffer> pair = p_227880_1_.popNextBuffer();
      if (this.id != -1) {
         ByteBuffer bytebuffer = pair.getSecond();
         this.vertexCount = bytebuffer.remaining() / this.format.getVertexSize();
         this.bind();
         RenderSystem.glBufferData(34962, bytebuffer, 35044);
         unbind();
      }
   }

   public void draw(Matrix4f p_227874_1_, int p_227874_2_) {
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(p_227874_1_);
      RenderSystem.drawArrays(p_227874_2_, 0, this.vertexCount);
      RenderSystem.popMatrix();
   }

   public static void unbind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return 0;
      });
   }

   public void close() {
      if (this.id >= 0) {
         RenderSystem.glDeleteBuffers(this.id);
         this.id = -1;
      }

   }
}
