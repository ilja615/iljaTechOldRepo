package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class PlayerGenerationTracker {
   private final Object2BooleanMap<ServerPlayerEntity> players = new Object2BooleanOpenHashMap<>();

   public Stream<ServerPlayerEntity> getPlayers(long p_219444_1_) {
      return this.players.keySet().stream();
   }

   public void addPlayer(long p_219442_1_, ServerPlayerEntity p_219442_3_, boolean p_219442_4_) {
      this.players.put(p_219442_3_, p_219442_4_);
   }

   public void removePlayer(long p_219443_1_, ServerPlayerEntity p_219443_3_) {
      this.players.removeBoolean(p_219443_3_);
   }

   public void ignorePlayer(ServerPlayerEntity p_219446_1_) {
      this.players.replace(p_219446_1_, true);
   }

   public void unIgnorePlayer(ServerPlayerEntity p_219447_1_) {
      this.players.replace(p_219447_1_, false);
   }

   public boolean ignoredOrUnknown(ServerPlayerEntity p_219448_1_) {
      return this.players.getOrDefault(p_219448_1_, true);
   }

   public boolean ignored(ServerPlayerEntity p_225419_1_) {
      return this.players.getBoolean(p_225419_1_);
   }

   public void updatePlayer(long p_219445_1_, long p_219445_3_, ServerPlayerEntity p_219445_5_) {
   }
}
