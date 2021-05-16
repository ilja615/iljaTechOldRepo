package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CClientSettingsPacket implements IPacket<IServerPlayNetHandler> {
   private String language;
   private int viewDistance;
   private ChatVisibility chatVisibility;
   private boolean chatColors;
   private int modelCustomisation;
   private HandSide mainHand;

   public CClientSettingsPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CClientSettingsPacket(String p_i50761_1_, int p_i50761_2_, ChatVisibility p_i50761_3_, boolean p_i50761_4_, int p_i50761_5_, HandSide p_i50761_6_) {
      this.language = p_i50761_1_;
      this.viewDistance = p_i50761_2_;
      this.chatVisibility = p_i50761_3_;
      this.chatColors = p_i50761_4_;
      this.modelCustomisation = p_i50761_5_;
      this.mainHand = p_i50761_6_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.language = p_148837_1_.readUtf(16);
      this.viewDistance = p_148837_1_.readByte();
      this.chatVisibility = p_148837_1_.readEnum(ChatVisibility.class);
      this.chatColors = p_148837_1_.readBoolean();
      this.modelCustomisation = p_148837_1_.readUnsignedByte();
      this.mainHand = p_148837_1_.readEnum(HandSide.class);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.language);
      p_148840_1_.writeByte(this.viewDistance);
      p_148840_1_.writeEnum(this.chatVisibility);
      p_148840_1_.writeBoolean(this.chatColors);
      p_148840_1_.writeByte(this.modelCustomisation);
      p_148840_1_.writeEnum(this.mainHand);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleClientInformation(this);
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean getChatColors() {
      return this.chatColors;
   }

   public int getModelCustomisation() {
      return this.modelCustomisation;
   }

   public HandSide getMainHand() {
      return this.mainHand;
   }

   public String getLanguage() {
      return this.language;
   }
}
