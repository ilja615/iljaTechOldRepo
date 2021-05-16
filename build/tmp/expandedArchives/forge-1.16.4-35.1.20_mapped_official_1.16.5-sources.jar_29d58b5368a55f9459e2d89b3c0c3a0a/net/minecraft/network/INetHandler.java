package net.minecraft.network;

import net.minecraft.util.text.ITextComponent;

public interface INetHandler {
   void onDisconnect(ITextComponent p_147231_1_);

   NetworkManager getConnection();
}
