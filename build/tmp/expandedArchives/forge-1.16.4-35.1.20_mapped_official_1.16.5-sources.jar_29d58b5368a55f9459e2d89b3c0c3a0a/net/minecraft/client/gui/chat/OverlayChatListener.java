package net.minecraft.client.gui.chat;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayChatListener implements IChatListener {
   private final Minecraft minecraft;

   public OverlayChatListener(Minecraft p_i47394_1_) {
      this.minecraft = p_i47394_1_;
   }

   public void handle(ChatType p_192576_1_, ITextComponent p_192576_2_, UUID p_192576_3_) {
      this.minecraft.gui.setOverlayMessage(p_192576_2_, false);
   }
}
