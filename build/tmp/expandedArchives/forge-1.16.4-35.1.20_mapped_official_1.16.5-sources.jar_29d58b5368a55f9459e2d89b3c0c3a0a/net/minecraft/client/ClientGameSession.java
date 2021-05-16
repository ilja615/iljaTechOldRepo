package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientGameSession implements GameSession {
   private final int players;
   private final boolean isRemoteServer;
   private final String difficulty;
   private final String gameMode;
   private final UUID id;

   public ClientGameSession(ClientWorld p_i51152_1_, ClientPlayerEntity p_i51152_2_, ClientPlayNetHandler p_i51152_3_) {
      this.players = p_i51152_3_.getOnlinePlayers().size();
      this.isRemoteServer = !p_i51152_3_.getConnection().isMemoryConnection();
      this.difficulty = p_i51152_1_.getDifficulty().getKey();
      NetworkPlayerInfo networkplayerinfo = p_i51152_3_.getPlayerInfo(p_i51152_2_.getUUID());
      if (networkplayerinfo != null) {
         this.gameMode = networkplayerinfo.getGameMode().getName();
      } else {
         this.gameMode = "unknown";
      }

      this.id = p_i51152_3_.getId();
   }

   public int getPlayerCount() {
      return this.players;
   }

   public boolean isRemoteServer() {
      return this.isRemoteServer;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public String getGameMode() {
      return this.gameMode;
   }

   public UUID getSessionId() {
      return this.id;
   }
}
