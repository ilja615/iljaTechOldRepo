package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SStopSoundPacket implements IPacket<IClientPlayNetHandler> {
   private ResourceLocation name;
   private SoundCategory source;

   public SStopSoundPacket() {
   }

   public SStopSoundPacket(@Nullable ResourceLocation p_i47929_1_, @Nullable SoundCategory p_i47929_2_) {
      this.name = p_i47929_1_;
      this.source = p_i47929_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      int i = p_148837_1_.readByte();
      if ((i & 1) > 0) {
         this.source = p_148837_1_.readEnum(SoundCategory.class);
      }

      if ((i & 2) > 0) {
         this.name = p_148837_1_.readResourceLocation();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      if (this.source != null) {
         if (this.name != null) {
            p_148840_1_.writeByte(3);
            p_148840_1_.writeEnum(this.source);
            p_148840_1_.writeResourceLocation(this.name);
         } else {
            p_148840_1_.writeByte(1);
            p_148840_1_.writeEnum(this.source);
         }
      } else if (this.name != null) {
         p_148840_1_.writeByte(2);
         p_148840_1_.writeResourceLocation(this.name);
      } else {
         p_148840_1_.writeByte(0);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public SoundCategory getSource() {
      return this.source;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleStopSoundEvent(this);
   }
}
