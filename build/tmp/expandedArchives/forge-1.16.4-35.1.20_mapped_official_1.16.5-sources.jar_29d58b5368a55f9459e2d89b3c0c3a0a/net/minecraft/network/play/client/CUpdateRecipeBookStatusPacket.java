package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateRecipeBookStatusPacket implements IPacket<IServerPlayNetHandler> {
   private RecipeBookCategory bookType;
   private boolean isOpen;
   private boolean isFiltering;

   public CUpdateRecipeBookStatusPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateRecipeBookStatusPacket(RecipeBookCategory p_i242088_1_, boolean p_i242088_2_, boolean p_i242088_3_) {
      this.bookType = p_i242088_1_;
      this.isOpen = p_i242088_2_;
      this.isFiltering = p_i242088_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.bookType = p_148837_1_.readEnum(RecipeBookCategory.class);
      this.isOpen = p_148837_1_.readBoolean();
      this.isFiltering = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.bookType);
      p_148840_1_.writeBoolean(this.isOpen);
      p_148840_1_.writeBoolean(this.isFiltering);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRecipeBookChangeSettingsPacket(this);
   }

   public RecipeBookCategory getBookType() {
      return this.bookType;
   }

   public boolean isOpen() {
      return this.isOpen;
   }

   public boolean isFiltering() {
      return this.isFiltering;
   }
}
