package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChatPacket implements IPacket<IClientPlayNetHandler> {
   private ITextComponent message;
   private ChatType type;
   private UUID sender;

   public SChatPacket() {
   }

   public SChatPacket(ITextComponent p_i232578_1_, ChatType p_i232578_2_, UUID p_i232578_3_) {
      this.message = p_i232578_1_;
      this.type = p_i232578_2_;
      this.sender = p_i232578_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.message = p_148837_1_.readComponent();
      this.type = ChatType.getForIndex(p_148837_1_.readByte());
      this.sender = p_148837_1_.readUUID();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeComponent(this.message);
      p_148840_1_.writeByte(this.type.getIndex());
      p_148840_1_.writeUUID(this.sender);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleChat(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getMessage() {
      return this.message;
   }

   public boolean isSystem() {
      return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
   }

   public ChatType getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getSender() {
      return this.sender;
   }

   public boolean isSkippable() {
      return true;
   }
}
