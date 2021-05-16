package net.minecraft.client.multiplayer;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String name;
   public String ip;
   public ITextComponent status;
   public ITextComponent motd;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public ITextComponent version = new StringTextComponent(SharedConstants.getCurrentVersion().getName());
   public boolean pinged;
   public List<ITextComponent> playerList = Collections.emptyList();
   private ServerData.ServerResourceMode packStatus = ServerData.ServerResourceMode.PROMPT;
   @Nullable
   private String iconB64;
   private boolean lan;
   public net.minecraftforge.fml.client.ExtendedServerListData forgeData = null;

   public ServerData(String p_i46420_1_, String p_i46420_2_, boolean p_i46420_3_) {
      this.name = p_i46420_1_;
      this.ip = p_i46420_2_;
      this.lan = p_i46420_3_;
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("name", this.name);
      compoundnbt.putString("ip", this.ip);
      if (this.iconB64 != null) {
         compoundnbt.putString("icon", this.iconB64);
      }

      if (this.packStatus == ServerData.ServerResourceMode.ENABLED) {
         compoundnbt.putBoolean("acceptTextures", true);
      } else if (this.packStatus == ServerData.ServerResourceMode.DISABLED) {
         compoundnbt.putBoolean("acceptTextures", false);
      }

      return compoundnbt;
   }

   public ServerData.ServerResourceMode getResourcePackStatus() {
      return this.packStatus;
   }

   public void setResourcePackStatus(ServerData.ServerResourceMode p_152584_1_) {
      this.packStatus = p_152584_1_;
   }

   public static ServerData read(CompoundNBT p_78837_0_) {
      ServerData serverdata = new ServerData(p_78837_0_.getString("name"), p_78837_0_.getString("ip"), false);
      if (p_78837_0_.contains("icon", 8)) {
         serverdata.setIconB64(p_78837_0_.getString("icon"));
      }

      if (p_78837_0_.contains("acceptTextures", 1)) {
         if (p_78837_0_.getBoolean("acceptTextures")) {
            serverdata.setResourcePackStatus(ServerData.ServerResourceMode.ENABLED);
         } else {
            serverdata.setResourcePackStatus(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         serverdata.setResourcePackStatus(ServerData.ServerResourceMode.PROMPT);
      }

      return serverdata;
   }

   @Nullable
   public String getIconB64() {
      return this.iconB64;
   }

   public void setIconB64(@Nullable String p_147407_1_) {
      this.iconB64 = p_147407_1_;
   }

   public boolean isLan() {
      return this.lan;
   }

   public void copyFrom(ServerData p_152583_1_) {
      this.ip = p_152583_1_.ip;
      this.name = p_152583_1_.name;
      this.setResourcePackStatus(p_152583_1_.getResourcePackStatus());
      this.iconB64 = p_152583_1_.iconB64;
      this.lan = p_152583_1_.lan;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final ITextComponent name;

      private ServerResourceMode(String p_i1053_3_) {
         this.name = new TranslationTextComponent("addServer.resourcePack." + p_i1053_3_);
      }

      public ITextComponent getName() {
         return this.name;
      }
   }
}
