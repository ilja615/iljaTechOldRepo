package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityMetadataPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private List<EntityDataManager.DataEntry<?>> packedItems;

   public SEntityMetadataPacket() {
   }

   public SEntityMetadataPacket(int p_i46917_1_, EntityDataManager p_i46917_2_, boolean p_i46917_3_) {
      this.id = p_i46917_1_;
      if (p_i46917_3_) {
         this.packedItems = p_i46917_2_.getAll();
         p_i46917_2_.clearDirty();
      } else {
         this.packedItems = p_i46917_2_.packDirty();
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.packedItems = EntityDataManager.unpack(p_148837_1_);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      EntityDataManager.pack(this.packedItems, p_148840_1_);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetEntityData(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<EntityDataManager.DataEntry<?>> getUnpackedData() {
      return this.packedItems;
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }
}
