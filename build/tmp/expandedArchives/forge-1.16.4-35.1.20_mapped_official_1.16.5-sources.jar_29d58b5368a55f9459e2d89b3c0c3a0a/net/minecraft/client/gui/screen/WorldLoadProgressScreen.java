package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldLoadProgressScreen extends Screen {
   private final TrackingChunkStatusListener progressListener;
   private long lastNarration = -1L;
   private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap<>(), (p_213039_0_) -> {
      p_213039_0_.defaultReturnValue(0);
      p_213039_0_.put(ChunkStatus.EMPTY, 5526612);
      p_213039_0_.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      p_213039_0_.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      p_213039_0_.put(ChunkStatus.BIOMES, 8434258);
      p_213039_0_.put(ChunkStatus.NOISE, 13750737);
      p_213039_0_.put(ChunkStatus.SURFACE, 7497737);
      p_213039_0_.put(ChunkStatus.CARVERS, 7169628);
      p_213039_0_.put(ChunkStatus.LIQUID_CARVERS, 3159410);
      p_213039_0_.put(ChunkStatus.FEATURES, 2213376);
      p_213039_0_.put(ChunkStatus.LIGHT, 13421772);
      p_213039_0_.put(ChunkStatus.SPAWN, 15884384);
      p_213039_0_.put(ChunkStatus.HEIGHTMAPS, 15658734);
      p_213039_0_.put(ChunkStatus.FULL, 16777215);
   });

   public WorldLoadProgressScreen(TrackingChunkStatusListener p_i51113_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.progressListener = p_i51113_1_;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void removed() {
      NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.loading.done")).getString());
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      String s = MathHelper.clamp(this.progressListener.getProgress(), 0, 100) + "%";
      long i = Util.getMillis();
      if (i - this.lastNarration > 2000L) {
         this.lastNarration = i;
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.loading", s)).getString());
      }

      int j = this.width / 2;
      int k = this.height / 2;
      int l = 30;
      renderChunks(p_230430_1_, this.progressListener, j, k + 30, 2, 0);
      drawCenteredString(p_230430_1_, this.font, s, j, k - 9 / 2 - 30, 16777215);
   }

   public static void renderChunks(MatrixStack p_238625_0_, TrackingChunkStatusListener p_238625_1_, int p_238625_2_, int p_238625_3_, int p_238625_4_, int p_238625_5_) {
      int i = p_238625_4_ + p_238625_5_;
      int j = p_238625_1_.getFullDiameter();
      int k = j * i - p_238625_5_;
      int l = p_238625_1_.getDiameter();
      int i1 = l * i - p_238625_5_;
      int j1 = p_238625_2_ - i1 / 2;
      int k1 = p_238625_3_ - i1 / 2;
      int l1 = k / 2 + 1;
      int i2 = -16772609;
      if (p_238625_5_ != 0) {
         fill(p_238625_0_, p_238625_2_ - l1, p_238625_3_ - l1, p_238625_2_ - l1 + 1, p_238625_3_ + l1, -16772609);
         fill(p_238625_0_, p_238625_2_ + l1 - 1, p_238625_3_ - l1, p_238625_2_ + l1, p_238625_3_ + l1, -16772609);
         fill(p_238625_0_, p_238625_2_ - l1, p_238625_3_ - l1, p_238625_2_ + l1, p_238625_3_ - l1 + 1, -16772609);
         fill(p_238625_0_, p_238625_2_ - l1, p_238625_3_ + l1 - 1, p_238625_2_ + l1, p_238625_3_ + l1, -16772609);
      }

      for(int j2 = 0; j2 < l; ++j2) {
         for(int k2 = 0; k2 < l; ++k2) {
            ChunkStatus chunkstatus = p_238625_1_.getStatus(j2, k2);
            int l2 = j1 + j2 * i;
            int i3 = k1 + k2 * i;
            fill(p_238625_0_, l2, i3, l2 + p_238625_4_, i3 + p_238625_4_, COLORS.getInt(chunkstatus) | -16777216);
         }
      }

   }
}
