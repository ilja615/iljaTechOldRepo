package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;

public class CMarkRecipeSeenPacket implements IPacket<IServerPlayNetHandler> {
   private ResourceLocation recipe;

   public CMarkRecipeSeenPacket() {
   }

   public CMarkRecipeSeenPacket(IRecipe<?> p_i242089_1_) {
      this.recipe = p_i242089_1_.getId();
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.recipe = p_148837_1_.readResourceLocation();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeResourceLocation(this.recipe);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRecipeBookSeenRecipePacket(this);
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }
}
