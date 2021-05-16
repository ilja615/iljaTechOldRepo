package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChunkDataPacket implements IPacket<IClientPlayNetHandler> {
   private int x;
   private int z;
   private int availableSections;
   private CompoundNBT heightmaps;
   @Nullable
   private int[] biomes;
   private byte[] buffer;
   private List<CompoundNBT> blockEntitiesTags;
   private boolean fullChunk;

   public SChunkDataPacket() {
   }

   public SChunkDataPacket(Chunk p_i242081_1_, int p_i242081_2_) {
      ChunkPos chunkpos = p_i242081_1_.getPos();
      this.x = chunkpos.x;
      this.z = chunkpos.z;
      this.fullChunk = p_i242081_2_ == 65535;
      this.heightmaps = new CompoundNBT();

      for(Entry<Heightmap.Type, Heightmap> entry : p_i242081_1_.getHeightmaps()) {
         if (entry.getKey().sendToClient()) {
            this.heightmaps.put(entry.getKey().getSerializationKey(), new LongArrayNBT(entry.getValue().getRawData()));
         }
      }

      if (this.fullChunk) {
         this.biomes = p_i242081_1_.getBiomes().writeBiomes();
      }

      this.buffer = new byte[this.calculateChunkSize(p_i242081_1_, p_i242081_2_)];
      this.availableSections = this.extractChunkData(new PacketBuffer(this.getWriteBuffer()), p_i242081_1_, p_i242081_2_);
      this.blockEntitiesTags = Lists.newArrayList();

      for(Entry<BlockPos, TileEntity> entry1 : p_i242081_1_.getBlockEntities().entrySet()) {
         BlockPos blockpos = entry1.getKey();
         TileEntity tileentity = entry1.getValue();
         int i = blockpos.getY() >> 4;
         if (this.isFullChunk() || (p_i242081_2_ & 1 << i) != 0) {
            CompoundNBT compoundnbt = tileentity.getUpdateTag();
            this.blockEntitiesTags.add(compoundnbt);
         }
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readInt();
      this.z = p_148837_1_.readInt();
      this.fullChunk = p_148837_1_.readBoolean();
      this.availableSections = p_148837_1_.readVarInt();
      this.heightmaps = p_148837_1_.readNbt();
      if (this.fullChunk) {
         this.biomes = p_148837_1_.readVarIntArray(BiomeContainer.BIOMES_SIZE);
      }

      int i = p_148837_1_.readVarInt();
      if (i > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[i];
         p_148837_1_.readBytes(this.buffer);
         int j = p_148837_1_.readVarInt();
         this.blockEntitiesTags = Lists.newArrayList();

         for(int k = 0; k < j; ++k) {
            this.blockEntitiesTags.add(p_148837_1_.readNbt());
         }

      }
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.x);
      p_148840_1_.writeInt(this.z);
      p_148840_1_.writeBoolean(this.fullChunk);
      p_148840_1_.writeVarInt(this.availableSections);
      p_148840_1_.writeNbt(this.heightmaps);
      if (this.biomes != null) {
         p_148840_1_.writeVarIntArray(this.biomes);
      }

      p_148840_1_.writeVarInt(this.buffer.length);
      p_148840_1_.writeBytes(this.buffer);
      p_148840_1_.writeVarInt(this.blockEntitiesTags.size());

      for(CompoundNBT compoundnbt : this.blockEntitiesTags) {
         p_148840_1_.writeNbt(compoundnbt);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLevelChunk(this);
   }

   @OnlyIn(Dist.CLIENT)
   public PacketBuffer getReadBuffer() {
      return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer));
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
      bytebuf.writerIndex(0);
      return bytebuf;
   }

   public int extractChunkData(PacketBuffer p_218708_1_, Chunk p_218708_2_, int p_218708_3_) {
      int i = 0;
      ChunkSection[] achunksection = p_218708_2_.getSections();
      int j = 0;

      for(int k = achunksection.length; j < k; ++j) {
         ChunkSection chunksection = achunksection[j];
         if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (p_218708_3_ & 1 << j) != 0) {
            i |= 1 << j;
            chunksection.write(p_218708_1_);
         }
      }

      return i;
   }

   protected int calculateChunkSize(Chunk p_218709_1_, int p_218709_2_) {
      int i = 0;
      ChunkSection[] achunksection = p_218709_1_.getSections();
      int j = 0;

      for(int k = achunksection.length; j < k; ++j) {
         ChunkSection chunksection = achunksection[j];
         if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (p_218709_2_ & 1 << j) != 0) {
            i += chunksection.getSerializedSize();
         }
      }

      return i;
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
   public int getAvailableSections() {
      return this.availableSections;
   }

   public boolean isFullChunk() {
      return this.fullChunk;
   }

   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getHeightmaps() {
      return this.heightmaps;
   }

   @OnlyIn(Dist.CLIENT)
   public List<CompoundNBT> getBlockEntitiesTags() {
      return this.blockEntitiesTags;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public int[] getBiomes() {
      return this.biomes;
   }
}
