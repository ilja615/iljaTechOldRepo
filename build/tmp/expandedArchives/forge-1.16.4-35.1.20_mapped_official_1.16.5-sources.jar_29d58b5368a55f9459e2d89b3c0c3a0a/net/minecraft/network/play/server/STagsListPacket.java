package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STagsListPacket implements IPacket<IClientPlayNetHandler> {
   private ITagCollectionSupplier tags;

   public STagsListPacket() {
   }

   public STagsListPacket(ITagCollectionSupplier p_i242087_1_) {
      this.tags = p_i242087_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.tags = ITagCollectionSupplier.deserializeFromNetwork(p_148837_1_);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      this.tags.serializeToNetwork(p_148840_1_);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateTags(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITagCollectionSupplier getTags() {
      return this.tags;
   }
}
