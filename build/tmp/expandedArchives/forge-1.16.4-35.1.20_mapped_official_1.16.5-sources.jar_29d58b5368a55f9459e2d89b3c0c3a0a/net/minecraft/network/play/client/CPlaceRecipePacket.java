package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlaceRecipePacket implements IPacket<IServerPlayNetHandler> {
   private int containerId;
   private ResourceLocation recipe;
   private boolean shiftDown;

   public CPlaceRecipePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlaceRecipePacket(int p_i47614_1_, IRecipe<?> p_i47614_2_, boolean p_i47614_3_) {
      this.containerId = p_i47614_1_;
      this.recipe = p_i47614_2_.getId();
      this.shiftDown = p_i47614_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
      this.recipe = p_148837_1_.readResourceLocation();
      this.shiftDown = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeResourceLocation(this.recipe);
      p_148840_1_.writeBoolean(this.shiftDown);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlaceRecipe(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isShiftDown() {
      return this.shiftDown;
   }
}
