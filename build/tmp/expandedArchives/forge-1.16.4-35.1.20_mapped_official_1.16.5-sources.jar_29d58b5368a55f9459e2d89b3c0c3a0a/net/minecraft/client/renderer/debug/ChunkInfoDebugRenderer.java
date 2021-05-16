package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkInfoDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = Double.MIN_VALUE;
   private final int radius = 12;
   @Nullable
   private ChunkInfoDebugRenderer.Entry data;

   public ChunkInfoDebugRenderer(Minecraft p_i50978_1_) {
      this.minecraft = p_i50978_1_;
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      double d0 = (double)Util.getNanos();
      if (d0 - this.lastUpdateTime > 3.0E9D) {
         this.lastUpdateTime = d0;
         IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
         if (integratedserver != null) {
            this.data = new ChunkInfoDebugRenderer.Entry(integratedserver, p_225619_3_, p_225619_7_);
         } else {
            this.data = null;
         }
      }

      if (this.data != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(2.0F);
         RenderSystem.disableTexture();
         RenderSystem.depthMask(false);
         Map<ChunkPos, String> map = this.data.serverData.getNow((Map<ChunkPos, String>)null);
         double d1 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85D;

         for(Map.Entry<ChunkPos, String> entry : this.data.clientData.entrySet()) {
            ChunkPos chunkpos = entry.getKey();
            String s = entry.getValue();
            if (map != null) {
               s = s + (String)map.get(chunkpos);
            }

            String[] astring = s.split("\n");
            int i = 0;

            for(String s1 : astring) {
               DebugRenderer.renderFloatingText(s1, (double)((chunkpos.x << 4) + 8), d1 + (double)i, (double)((chunkpos.z << 4) + 8), -1, 0.15F);
               i -= 2;
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableTexture();
         RenderSystem.disableBlend();
      }

   }

   @OnlyIn(Dist.CLIENT)
   final class Entry {
      private final Map<ChunkPos, String> clientData;
      private final CompletableFuture<Map<ChunkPos, String>> serverData;

      private Entry(IntegratedServer p_i226030_2_, double p_i226030_3_, double p_i226030_5_) {
         ClientWorld clientworld = ChunkInfoDebugRenderer.this.minecraft.level;
         RegistryKey<World> registrykey = clientworld.dimension();
         int i = (int)p_i226030_3_ >> 4;
         int j = (int)p_i226030_5_ >> 4;
         Builder<ChunkPos, String> builder = ImmutableMap.builder();
         ClientChunkProvider clientchunkprovider = clientworld.getChunkSource();

         for(int k = i - 12; k <= i + 12; ++k) {
            for(int l = j - 12; l <= j + 12; ++l) {
               ChunkPos chunkpos = new ChunkPos(k, l);
               String s = "";
               Chunk chunk = clientchunkprovider.getChunk(k, l, false);
               s = s + "Client: ";
               if (chunk == null) {
                  s = s + "0n/a\n";
               } else {
                  s = s + (chunk.isEmpty() ? " E" : "");
                  s = s + "\n";
               }

               builder.put(chunkpos, s);
            }
         }

         this.clientData = builder.build();
         this.serverData = p_i226030_2_.submit(() -> {
            ServerWorld serverworld = p_i226030_2_.getLevel(registrykey);
            if (serverworld == null) {
               return ImmutableMap.of();
            } else {
               Builder<ChunkPos, String> builder1 = ImmutableMap.builder();
               ServerChunkProvider serverchunkprovider = serverworld.getChunkSource();

               for(int i1 = i - 12; i1 <= i + 12; ++i1) {
                  for(int j1 = j - 12; j1 <= j + 12; ++j1) {
                     ChunkPos chunkpos1 = new ChunkPos(i1, j1);
                     builder1.put(chunkpos1, "Server: " + serverchunkprovider.getChunkDebugData(chunkpos1));
                  }
               }

               return builder1.build();
            }
         });
      }
   }
}
