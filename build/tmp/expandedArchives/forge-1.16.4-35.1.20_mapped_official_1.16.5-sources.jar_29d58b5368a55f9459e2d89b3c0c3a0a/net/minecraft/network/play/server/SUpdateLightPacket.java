package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateLightPacket implements IPacket<IClientPlayNetHandler> {
   private int x;
   private int z;
   private int skyYMask;
   private int blockYMask;
   private int emptySkyYMask;
   private int emptyBlockYMask;
   private List<byte[]> skyUpdates;
   private List<byte[]> blockUpdates;
   private boolean trustEdges;

   public SUpdateLightPacket() {
   }

   public SUpdateLightPacket(ChunkPos p_i50774_1_, WorldLightManager p_i50774_2_, boolean p_i50774_3_) {
      this.x = p_i50774_1_.x;
      this.z = p_i50774_1_.z;
      this.trustEdges = p_i50774_3_;
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int i = 0; i < 18; ++i) {
         NibbleArray nibblearray = p_i50774_2_.getLayerListener(LightType.SKY).getDataLayerData(SectionPos.of(p_i50774_1_, -1 + i));
         NibbleArray nibblearray1 = p_i50774_2_.getLayerListener(LightType.BLOCK).getDataLayerData(SectionPos.of(p_i50774_1_, -1 + i));
         if (nibblearray != null) {
            if (nibblearray.isEmpty()) {
               this.emptySkyYMask |= 1 << i;
            } else {
               this.skyYMask |= 1 << i;
               this.skyUpdates.add((byte[])nibblearray.getData().clone());
            }
         }

         if (nibblearray1 != null) {
            if (nibblearray1.isEmpty()) {
               this.emptyBlockYMask |= 1 << i;
            } else {
               this.blockYMask |= 1 << i;
               this.blockUpdates.add((byte[])nibblearray1.getData().clone());
            }
         }
      }

   }

   public SUpdateLightPacket(ChunkPos p_i50775_1_, WorldLightManager p_i50775_2_, int p_i50775_3_, int p_i50775_4_, boolean p_i50775_5_) {
      this.x = p_i50775_1_.x;
      this.z = p_i50775_1_.z;
      this.trustEdges = p_i50775_5_;
      this.skyYMask = p_i50775_3_;
      this.blockYMask = p_i50775_4_;
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int i = 0; i < 18; ++i) {
         if ((this.skyYMask & 1 << i) != 0) {
            NibbleArray nibblearray = p_i50775_2_.getLayerListener(LightType.SKY).getDataLayerData(SectionPos.of(p_i50775_1_, -1 + i));
            if (nibblearray != null && !nibblearray.isEmpty()) {
               this.skyUpdates.add((byte[])nibblearray.getData().clone());
            } else {
               this.skyYMask &= ~(1 << i);
               if (nibblearray != null) {
                  this.emptySkyYMask |= 1 << i;
               }
            }
         }

         if ((this.blockYMask & 1 << i) != 0) {
            NibbleArray nibblearray1 = p_i50775_2_.getLayerListener(LightType.BLOCK).getDataLayerData(SectionPos.of(p_i50775_1_, -1 + i));
            if (nibblearray1 != null && !nibblearray1.isEmpty()) {
               this.blockUpdates.add((byte[])nibblearray1.getData().clone());
            } else {
               this.blockYMask &= ~(1 << i);
               if (nibblearray1 != null) {
                  this.emptyBlockYMask |= 1 << i;
               }
            }
         }
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readVarInt();
      this.z = p_148837_1_.readVarInt();
      this.trustEdges = p_148837_1_.readBoolean();
      this.skyYMask = p_148837_1_.readVarInt();
      this.blockYMask = p_148837_1_.readVarInt();
      this.emptySkyYMask = p_148837_1_.readVarInt();
      this.emptyBlockYMask = p_148837_1_.readVarInt();
      this.skyUpdates = Lists.newArrayList();

      for(int i = 0; i < 18; ++i) {
         if ((this.skyYMask & 1 << i) != 0) {
            this.skyUpdates.add(p_148837_1_.readByteArray(2048));
         }
      }

      this.blockUpdates = Lists.newArrayList();

      for(int j = 0; j < 18; ++j) {
         if ((this.blockYMask & 1 << j) != 0) {
            this.blockUpdates.add(p_148837_1_.readByteArray(2048));
         }
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.x);
      p_148840_1_.writeVarInt(this.z);
      p_148840_1_.writeBoolean(this.trustEdges);
      p_148840_1_.writeVarInt(this.skyYMask);
      p_148840_1_.writeVarInt(this.blockYMask);
      p_148840_1_.writeVarInt(this.emptySkyYMask);
      p_148840_1_.writeVarInt(this.emptyBlockYMask);

      for(byte[] abyte : this.skyUpdates) {
         p_148840_1_.writeByteArray(abyte);
      }

      for(byte[] abyte1 : this.blockUpdates) {
         p_148840_1_.writeByteArray(abyte1);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLightUpdatePacked(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyYMask() {
      return this.skyYMask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEmptySkyYMask() {
      return this.emptySkyYMask;
   }

   @OnlyIn(Dist.CLIENT)
   public List<byte[]> getSkyUpdates() {
      return this.skyUpdates;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBlockYMask() {
      return this.blockYMask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEmptyBlockYMask() {
      return this.emptyBlockYMask;
   }

   @OnlyIn(Dist.CLIENT)
   public List<byte[]> getBlockUpdates() {
      return this.blockUpdates;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getTrustEdges() {
      return this.trustEdges;
   }
}
