package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tessellator {
   private final BufferBuilder builder;
   private static final Tessellator INSTANCE = new Tessellator();

   public static Tessellator getInstance() {
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      return INSTANCE;
   }

   public Tessellator(int p_i1250_1_) {
      this.builder = new BufferBuilder(p_i1250_1_);
   }

   public Tessellator() {
      this(2097152);
   }

   public void end() {
      this.builder.end();
      WorldVertexBufferUploader.end(this.builder);
   }

   public BufferBuilder getBuilder() {
      return this.builder;
   }
}
