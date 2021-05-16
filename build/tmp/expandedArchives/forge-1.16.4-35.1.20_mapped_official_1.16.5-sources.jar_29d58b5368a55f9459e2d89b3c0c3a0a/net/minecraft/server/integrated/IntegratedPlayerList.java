package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.PlayerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private CompoundNBT playerData;

   public IntegratedPlayerList(IntegratedServer p_i232493_1_, DynamicRegistries.Impl p_i232493_2_, PlayerData p_i232493_3_) {
      super(p_i232493_1_, p_i232493_2_, p_i232493_3_, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayerEntity p_72391_1_) {
      if (p_72391_1_.getName().getString().equals(this.getServer().getSingleplayerName())) {
         this.playerData = p_72391_1_.saveWithoutId(new CompoundNBT());
      }

      super.save(p_72391_1_);
   }

   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      return (ITextComponent)(p_206258_2_.getName().equalsIgnoreCase(this.getServer().getSingleplayerName()) && this.getPlayerByName(p_206258_2_.getName()) != null ? new TranslationTextComponent("multiplayer.disconnect.name_taken") : super.canPlayerLogin(p_206258_1_, p_206258_2_));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public CompoundNBT getSingleplayerData() {
      return this.playerData;
   }
}
