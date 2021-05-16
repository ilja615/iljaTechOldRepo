package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRespawnPacket implements IPacket<IClientPlayNetHandler> {
   private DimensionType dimensionType;
   private RegistryKey<World> dimension;
   private long seed;
   private GameType playerGameType;
   private GameType previousPlayerGameType;
   private boolean isDebug;
   private boolean isFlat;
   private boolean keepAllPlayerData;

   public SRespawnPacket() {
   }

   public SRespawnPacket(DimensionType p_i242084_1_, RegistryKey<World> p_i242084_2_, long p_i242084_3_, GameType p_i242084_5_, GameType p_i242084_6_, boolean p_i242084_7_, boolean p_i242084_8_, boolean p_i242084_9_) {
      this.dimensionType = p_i242084_1_;
      this.dimension = p_i242084_2_;
      this.seed = p_i242084_3_;
      this.playerGameType = p_i242084_5_;
      this.previousPlayerGameType = p_i242084_6_;
      this.isDebug = p_i242084_7_;
      this.isFlat = p_i242084_8_;
      this.keepAllPlayerData = p_i242084_9_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRespawn(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.dimensionType = p_148837_1_.readWithCodec(DimensionType.CODEC).get();
      this.dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, p_148837_1_.readResourceLocation());
      this.seed = p_148837_1_.readLong();
      this.playerGameType = GameType.byId(p_148837_1_.readUnsignedByte());
      this.previousPlayerGameType = GameType.byId(p_148837_1_.readUnsignedByte());
      this.isDebug = p_148837_1_.readBoolean();
      this.isFlat = p_148837_1_.readBoolean();
      this.keepAllPlayerData = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeWithCodec(DimensionType.CODEC, () -> {
         return this.dimensionType;
      });
      p_148840_1_.writeResourceLocation(this.dimension.location());
      p_148840_1_.writeLong(this.seed);
      p_148840_1_.writeByte(this.playerGameType.getId());
      p_148840_1_.writeByte(this.previousPlayerGameType.getId());
      p_148840_1_.writeBoolean(this.isDebug);
      p_148840_1_.writeBoolean(this.isFlat);
      p_148840_1_.writeBoolean(this.keepAllPlayerData);
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType getDimensionType() {
      return this.dimensionType;
   }

   @OnlyIn(Dist.CLIENT)
   public RegistryKey<World> getDimension() {
      return this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getPreviousPlayerGameType() {
      return this.previousPlayerGameType;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isDebug() {
      return this.isDebug;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFlat() {
      return this.isFlat;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldKeepAllPlayerData() {
      return this.keepAllPlayerData;
   }
}
