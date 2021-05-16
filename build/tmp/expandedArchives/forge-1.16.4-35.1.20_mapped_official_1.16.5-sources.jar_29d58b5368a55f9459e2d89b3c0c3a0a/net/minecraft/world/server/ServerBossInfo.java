package net.minecraft.world.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

public class ServerBossInfo extends BossInfo {
   private final Set<ServerPlayerEntity> players = Sets.newHashSet();
   private final Set<ServerPlayerEntity> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
   private boolean visible = true;

   public ServerBossInfo(ITextComponent p_i46839_1_, BossInfo.Color p_i46839_2_, BossInfo.Overlay p_i46839_3_) {
      super(MathHelper.createInsecureUUID(), p_i46839_1_, p_i46839_2_, p_i46839_3_);
   }

   public void setPercent(float p_186735_1_) {
      if (p_186735_1_ != this.percent) {
         super.setPercent(p_186735_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
      }

   }

   public void setColor(BossInfo.Color p_186745_1_) {
      if (p_186745_1_ != this.color) {
         super.setColor(p_186745_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
      }

   }

   public void setOverlay(BossInfo.Overlay p_186746_1_) {
      if (p_186746_1_ != this.overlay) {
         super.setOverlay(p_186746_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
      }

   }

   public BossInfo setDarkenScreen(boolean p_186741_1_) {
      if (p_186741_1_ != this.darkenScreen) {
         super.setDarkenScreen(p_186741_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo setPlayBossMusic(boolean p_186742_1_) {
      if (p_186742_1_ != this.playBossMusic) {
         super.setPlayBossMusic(p_186742_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo setCreateWorldFog(boolean p_186743_1_) {
      if (p_186743_1_ != this.createWorldFog) {
         super.setCreateWorldFog(p_186743_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public void setName(ITextComponent p_186739_1_) {
      if (!Objects.equal(p_186739_1_, this.name)) {
         super.setName(p_186739_1_);
         this.broadcast(SUpdateBossInfoPacket.Operation.UPDATE_NAME);
      }

   }

   private void broadcast(SUpdateBossInfoPacket.Operation p_186759_1_) {
      if (this.visible) {
         SUpdateBossInfoPacket supdatebossinfopacket = new SUpdateBossInfoPacket(p_186759_1_, this);

         for(ServerPlayerEntity serverplayerentity : this.players) {
            serverplayerentity.connection.send(supdatebossinfopacket);
         }
      }

   }

   public void addPlayer(ServerPlayerEntity p_186760_1_) {
      if (this.players.add(p_186760_1_) && this.visible) {
         p_186760_1_.connection.send(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, this));
      }

   }

   public void removePlayer(ServerPlayerEntity p_186761_1_) {
      if (this.players.remove(p_186761_1_) && this.visible) {
         p_186761_1_.connection.send(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.REMOVE, this));
      }

   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(this.players)) {
            this.removePlayer(serverplayerentity);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_186758_1_) {
      if (p_186758_1_ != this.visible) {
         this.visible = p_186758_1_;

         for(ServerPlayerEntity serverplayerentity : this.players) {
            serverplayerentity.connection.send(new SUpdateBossInfoPacket(p_186758_1_ ? SUpdateBossInfoPacket.Operation.ADD : SUpdateBossInfoPacket.Operation.REMOVE, this));
         }
      }

   }

   public Collection<ServerPlayerEntity> getPlayers() {
      return this.unmodifiablePlayers;
   }
}
