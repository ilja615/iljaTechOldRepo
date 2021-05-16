package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.minecraft.realms.IPersistentSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerInfo extends ValueObject implements IPersistentSerializable {
   @SerializedName("name")
   private String name;
   @SerializedName("uuid")
   private String uuid;
   @SerializedName("operator")
   private boolean operator;
   @SerializedName("accepted")
   private boolean accepted;
   @SerializedName("online")
   private boolean online;

   public String getName() {
      return this.name;
   }

   public void setName(String p_230758_1_) {
      this.name = p_230758_1_;
   }

   public String getUuid() {
      return this.uuid;
   }

   public void setUuid(String p_230761_1_) {
      this.uuid = p_230761_1_;
   }

   public boolean isOperator() {
      return this.operator;
   }

   public void setOperator(boolean p_230759_1_) {
      this.operator = p_230759_1_;
   }

   public boolean getAccepted() {
      return this.accepted;
   }

   public void setAccepted(boolean p_230762_1_) {
      this.accepted = p_230762_1_;
   }

   public boolean getOnline() {
      return this.online;
   }

   public void setOnline(boolean p_230764_1_) {
      this.online = p_230764_1_;
   }
}
