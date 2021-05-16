package net.minecraft.network.play.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerListItemPacket implements IPacket<IClientPlayNetHandler> {
   private SPlayerListItemPacket.Action action;
   private final List<SPlayerListItemPacket.AddPlayerData> entries = Lists.newArrayList();

   public SPlayerListItemPacket() {
   }

   public SPlayerListItemPacket(SPlayerListItemPacket.Action p_i46929_1_, ServerPlayerEntity... p_i46929_2_) {
      this.action = p_i46929_1_;

      for(ServerPlayerEntity serverplayerentity : p_i46929_2_) {
         this.entries.add(new SPlayerListItemPacket.AddPlayerData(serverplayerentity.getGameProfile(), serverplayerentity.latency, serverplayerentity.gameMode.getGameModeForPlayer(), serverplayerentity.getTabListDisplayName()));
      }

   }

   public SPlayerListItemPacket(SPlayerListItemPacket.Action p_i46930_1_, Iterable<ServerPlayerEntity> p_i46930_2_) {
      this.action = p_i46930_1_;

      for(ServerPlayerEntity serverplayerentity : p_i46930_2_) {
         this.entries.add(new SPlayerListItemPacket.AddPlayerData(serverplayerentity.getGameProfile(), serverplayerentity.latency, serverplayerentity.gameMode.getGameModeForPlayer(), serverplayerentity.getTabListDisplayName()));
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnum(SPlayerListItemPacket.Action.class);
      int i = p_148837_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         GameProfile gameprofile = null;
         int k = 0;
         GameType gametype = null;
         ITextComponent itextcomponent = null;
         switch(this.action) {
         case ADD_PLAYER:
            gameprofile = new GameProfile(p_148837_1_.readUUID(), p_148837_1_.readUtf(16));
            int l = p_148837_1_.readVarInt();
            int i1 = 0;

            for(; i1 < l; ++i1) {
               String s = p_148837_1_.readUtf(32767);
               String s1 = p_148837_1_.readUtf(32767);
               if (p_148837_1_.readBoolean()) {
                  gameprofile.getProperties().put(s, new Property(s, s1, p_148837_1_.readUtf(32767)));
               } else {
                  gameprofile.getProperties().put(s, new Property(s, s1));
               }
            }

            gametype = GameType.byId(p_148837_1_.readVarInt());
            k = p_148837_1_.readVarInt();
            if (p_148837_1_.readBoolean()) {
               itextcomponent = p_148837_1_.readComponent();
            }
            break;
         case UPDATE_GAME_MODE:
            gameprofile = new GameProfile(p_148837_1_.readUUID(), (String)null);
            gametype = GameType.byId(p_148837_1_.readVarInt());
            break;
         case UPDATE_LATENCY:
            gameprofile = new GameProfile(p_148837_1_.readUUID(), (String)null);
            k = p_148837_1_.readVarInt();
            break;
         case UPDATE_DISPLAY_NAME:
            gameprofile = new GameProfile(p_148837_1_.readUUID(), (String)null);
            if (p_148837_1_.readBoolean()) {
               itextcomponent = p_148837_1_.readComponent();
            }
            break;
         case REMOVE_PLAYER:
            gameprofile = new GameProfile(p_148837_1_.readUUID(), (String)null);
         }

         this.entries.add(new SPlayerListItemPacket.AddPlayerData(gameprofile, k, gametype, itextcomponent));
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.action);
      p_148840_1_.writeVarInt(this.entries.size());

      for(SPlayerListItemPacket.AddPlayerData splayerlistitempacket$addplayerdata : this.entries) {
         switch(this.action) {
         case ADD_PLAYER:
            p_148840_1_.writeUUID(splayerlistitempacket$addplayerdata.getProfile().getId());
            p_148840_1_.writeUtf(splayerlistitempacket$addplayerdata.getProfile().getName());
            p_148840_1_.writeVarInt(splayerlistitempacket$addplayerdata.getProfile().getProperties().size());

            for(Property property : splayerlistitempacket$addplayerdata.getProfile().getProperties().values()) {
               p_148840_1_.writeUtf(property.getName());
               p_148840_1_.writeUtf(property.getValue());
               if (property.hasSignature()) {
                  p_148840_1_.writeBoolean(true);
                  p_148840_1_.writeUtf(property.getSignature());
               } else {
                  p_148840_1_.writeBoolean(false);
               }
            }

            p_148840_1_.writeVarInt(splayerlistitempacket$addplayerdata.getGameMode().getId());
            p_148840_1_.writeVarInt(splayerlistitempacket$addplayerdata.getLatency());
            if (splayerlistitempacket$addplayerdata.getDisplayName() == null) {
               p_148840_1_.writeBoolean(false);
            } else {
               p_148840_1_.writeBoolean(true);
               p_148840_1_.writeComponent(splayerlistitempacket$addplayerdata.getDisplayName());
            }
            break;
         case UPDATE_GAME_MODE:
            p_148840_1_.writeUUID(splayerlistitempacket$addplayerdata.getProfile().getId());
            p_148840_1_.writeVarInt(splayerlistitempacket$addplayerdata.getGameMode().getId());
            break;
         case UPDATE_LATENCY:
            p_148840_1_.writeUUID(splayerlistitempacket$addplayerdata.getProfile().getId());
            p_148840_1_.writeVarInt(splayerlistitempacket$addplayerdata.getLatency());
            break;
         case UPDATE_DISPLAY_NAME:
            p_148840_1_.writeUUID(splayerlistitempacket$addplayerdata.getProfile().getId());
            if (splayerlistitempacket$addplayerdata.getDisplayName() == null) {
               p_148840_1_.writeBoolean(false);
            } else {
               p_148840_1_.writeBoolean(true);
               p_148840_1_.writeComponent(splayerlistitempacket$addplayerdata.getDisplayName());
            }
            break;
         case REMOVE_PLAYER:
            p_148840_1_.writeUUID(splayerlistitempacket$addplayerdata.getProfile().getId());
         }
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerInfo(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<SPlayerListItemPacket.AddPlayerData> getEntries() {
      return this.entries;
   }

   @OnlyIn(Dist.CLIENT)
   public SPlayerListItemPacket.Action getAction() {
      return this.action;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public class AddPlayerData {
      private final int latency;
      private final GameType gameMode;
      private final GameProfile profile;
      private final ITextComponent displayName;

      public AddPlayerData(GameProfile p_i46663_2_, int p_i46663_3_, @Nullable GameType p_i46663_4_, @Nullable ITextComponent p_i46663_5_) {
         this.profile = p_i46663_2_;
         this.latency = p_i46663_3_;
         this.gameMode = p_i46663_4_;
         this.displayName = p_i46663_5_;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getLatency() {
         return this.latency;
      }

      public GameType getGameMode() {
         return this.gameMode;
      }

      @Nullable
      public ITextComponent getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : ITextComponent.Serializer.toJson(this.displayName)).toString();
      }
   }
}
