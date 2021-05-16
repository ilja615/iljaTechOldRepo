package net.minecraft.client.gui.screen;

import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditMinecartCommandBlockScreen extends AbstractCommandBlockScreen {
   private final CommandBlockLogic commandBlock;

   public EditMinecartCommandBlockScreen(CommandBlockLogic p_i46595_1_) {
      this.commandBlock = p_i46595_1_;
   }

   public CommandBlockLogic getCommandBlock() {
      return this.commandBlock;
   }

   int getPreviousY() {
      return 150;
   }

   protected void init() {
      super.init();
      this.trackOutput = this.getCommandBlock().isTrackOutput();
      this.updateCommandOutput();
      this.commandEdit.setValue(this.getCommandBlock().getCommand());
   }

   protected void populateAndSendPacket(CommandBlockLogic p_195235_1_) {
      if (p_195235_1_ instanceof CommandBlockMinecartEntity.MinecartCommandLogic) {
         CommandBlockMinecartEntity.MinecartCommandLogic commandblockminecartentity$minecartcommandlogic = (CommandBlockMinecartEntity.MinecartCommandLogic)p_195235_1_;
         this.minecraft.getConnection().send(new CUpdateMinecartCommandBlockPacket(commandblockminecartentity$minecartcommandlogic.getMinecart().getId(), this.commandEdit.getValue(), p_195235_1_.isTrackOutput()));
      }

   }
}
