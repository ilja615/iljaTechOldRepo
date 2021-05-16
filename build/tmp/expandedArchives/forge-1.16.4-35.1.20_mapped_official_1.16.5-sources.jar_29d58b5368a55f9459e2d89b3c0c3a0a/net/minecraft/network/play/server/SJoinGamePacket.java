package net.minecraft.network.play.server;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SJoinGamePacket implements IPacket<IClientPlayNetHandler> {
   private int playerId;
   private long seed;
   private boolean hardcore;
   private GameType gameType;
   private GameType previousGameType;
   private Set<RegistryKey<World>> levels;
   private DynamicRegistries.Impl registryHolder;
   private DimensionType dimensionType;
   private RegistryKey<World> dimension;
   private int maxPlayers;
   private int chunkRadius;
   private boolean reducedDebugInfo;
   private boolean showDeathScreen;
   private boolean isDebug;
   private boolean isFlat;

   public SJoinGamePacket() {
   }

   public SJoinGamePacket(int p_i242082_1_, GameType p_i242082_2_, GameType p_i242082_3_, long p_i242082_4_, boolean p_i242082_6_, Set<RegistryKey<World>> p_i242082_7_, DynamicRegistries.Impl p_i242082_8_, DimensionType p_i242082_9_, RegistryKey<World> p_i242082_10_, int p_i242082_11_, int p_i242082_12_, boolean p_i242082_13_, boolean p_i242082_14_, boolean p_i242082_15_, boolean p_i242082_16_) {
      this.playerId = p_i242082_1_;
      this.levels = p_i242082_7_;
      this.registryHolder = p_i242082_8_;
      this.dimensionType = p_i242082_9_;
      this.dimension = p_i242082_10_;
      this.seed = p_i242082_4_;
      this.gameType = p_i242082_2_;
      this.previousGameType = p_i242082_3_;
      this.maxPlayers = p_i242082_11_;
      this.hardcore = p_i242082_6_;
      this.chunkRadius = p_i242082_12_;
      this.reducedDebugInfo = p_i242082_13_;
      this.showDeathScreen = p_i242082_14_;
      this.isDebug = p_i242082_15_;
      this.isFlat = p_i242082_16_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.playerId = p_148837_1_.readInt();
      this.hardcore = p_148837_1_.readBoolean();
      this.gameType = GameType.byId(p_148837_1_.readByte());
      this.previousGameType = GameType.byId(p_148837_1_.readByte());
      int i = p_148837_1_.readVarInt();
      this.levels = Sets.newHashSet();

      for(int j = 0; j < i; ++j) {
         this.levels.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, p_148837_1_.readResourceLocation()));
      }

      this.registryHolder = p_148837_1_.readWithCodec(DynamicRegistries.Impl.NETWORK_CODEC);
      this.dimensionType = p_148837_1_.readWithCodec(DimensionType.CODEC).get();
      this.dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, p_148837_1_.readResourceLocation());
      this.seed = p_148837_1_.readLong();
      this.maxPlayers = p_148837_1_.readVarInt();
      this.chunkRadius = p_148837_1_.readVarInt();
      this.reducedDebugInfo = p_148837_1_.readBoolean();
      this.showDeathScreen = p_148837_1_.readBoolean();
      this.isDebug = p_148837_1_.readBoolean();
      this.isFlat = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.playerId);
      p_148840_1_.writeBoolean(this.hardcore);
      p_148840_1_.writeByte(this.gameType.getId());
      p_148840_1_.writeByte(this.previousGameType.getId());
      p_148840_1_.writeVarInt(this.levels.size());

      for(RegistryKey<World> registrykey : this.levels) {
         p_148840_1_.writeResourceLocation(registrykey.location());
      }

      p_148840_1_.writeWithCodec(DynamicRegistries.Impl.NETWORK_CODEC, this.registryHolder);
      p_148840_1_.writeWithCodec(DimensionType.CODEC, () -> {
         return this.dimensionType;
      });
      p_148840_1_.writeResourceLocation(this.dimension.location());
      p_148840_1_.writeLong(this.seed);
      p_148840_1_.writeVarInt(this.maxPlayers);
      p_148840_1_.writeVarInt(this.chunkRadius);
      p_148840_1_.writeBoolean(this.reducedDebugInfo);
      p_148840_1_.writeBoolean(this.showDeathScreen);
      p_148840_1_.writeBoolean(this.isDebug);
      p_148840_1_.writeBoolean(this.isFlat);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLogin(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPlayerId() {
      return this.playerId;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHardcore() {
      return this.hardcore;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getPreviousGameType() {
      return this.previousGameType;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<RegistryKey<World>> levels() {
      return this.levels;
   }

   @OnlyIn(Dist.CLIENT)
   public DynamicRegistries registryAccess() {
      return this.registryHolder;
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
   public int getChunkRadius() {
      return this.chunkRadius;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldShowDeathScreen() {
      return this.showDeathScreen;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isDebug() {
      return this.isDebug;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFlat() {
      return this.isFlat;
   }
}
