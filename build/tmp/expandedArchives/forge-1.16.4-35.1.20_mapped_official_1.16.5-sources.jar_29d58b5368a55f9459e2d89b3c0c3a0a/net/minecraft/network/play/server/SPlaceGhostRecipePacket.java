package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlaceGhostRecipePacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private ResourceLocation recipe;

   public SPlaceGhostRecipePacket() {
   }

   public SPlaceGhostRecipePacket(int p_i47615_1_, IRecipe<?> p_i47615_2_) {
      this.containerId = p_i47615_1_;
      this.recipe = p_i47615_2_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
      this.recipe = p_148837_1_.readResourceLocation();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeResourceLocation(this.recipe);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlaceRecipe(this);
   }
}
