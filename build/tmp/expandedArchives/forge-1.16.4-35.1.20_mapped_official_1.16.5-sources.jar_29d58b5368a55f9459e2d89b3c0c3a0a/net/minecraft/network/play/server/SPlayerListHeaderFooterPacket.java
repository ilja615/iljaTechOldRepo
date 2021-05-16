package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerListHeaderFooterPacket implements IPacket<IClientPlayNetHandler> {
   private ITextComponent header;
   private ITextComponent footer;

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.header = p_148837_1_.readComponent();
      this.footer = p_148837_1_.readComponent();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeComponent(this.header);
      p_148840_1_.writeComponent(this.footer);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTabListCustomisation(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getHeader() {
      return this.header;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getFooter() {
      return this.footer;
   }
}
