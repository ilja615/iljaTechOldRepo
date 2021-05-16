package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private int type;
   private ITextComponent title;

   public SOpenWindowPacket() {
   }

   public SOpenWindowPacket(int p_i50769_1_, ContainerType<?> p_i50769_2_, ITextComponent p_i50769_3_) {
      this.containerId = p_i50769_1_;
      this.type = Registry.MENU.getId(p_i50769_2_);
      this.title = p_i50769_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readVarInt();
      this.type = p_148837_1_.readVarInt();
      this.title = p_148837_1_.readComponent();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.containerId);
      p_148840_1_.writeVarInt(this.type);
      p_148840_1_.writeComponent(this.title);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleOpenScreen(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ContainerType<?> getType() {
      return Registry.MENU.byId(this.type);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getTitle() {
      return this.title;
   }
}
