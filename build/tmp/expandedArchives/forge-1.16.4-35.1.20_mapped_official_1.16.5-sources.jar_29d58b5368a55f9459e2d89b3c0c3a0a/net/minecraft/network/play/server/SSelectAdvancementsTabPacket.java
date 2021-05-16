package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSelectAdvancementsTabPacket implements IPacket<IClientPlayNetHandler> {
   @Nullable
   private ResourceLocation tab;

   public SSelectAdvancementsTabPacket() {
   }

   public SSelectAdvancementsTabPacket(@Nullable ResourceLocation p_i47596_1_) {
      this.tab = p_i47596_1_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSelectAdvancementsTab(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      if (p_148837_1_.readBoolean()) {
         this.tab = p_148837_1_.readResourceLocation();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.tab != null);
      if (this.tab != null) {
         p_148840_1_.writeResourceLocation(this.tab);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTab() {
      return this.tab;
   }
}
