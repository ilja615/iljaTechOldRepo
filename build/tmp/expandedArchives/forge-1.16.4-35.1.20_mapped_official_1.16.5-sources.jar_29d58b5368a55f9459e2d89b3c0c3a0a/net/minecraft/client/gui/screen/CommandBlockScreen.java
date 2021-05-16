package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandBlockScreen extends AbstractCommandBlockScreen {
   private final CommandBlockTileEntity autoCommandBlock;
   private Button modeButton;
   private Button conditionalButton;
   private Button autoexecButton;
   private CommandBlockTileEntity.Mode mode = CommandBlockTileEntity.Mode.REDSTONE;
   private boolean conditional;
   private boolean autoexec;

   public CommandBlockScreen(CommandBlockTileEntity p_i46596_1_) {
      this.autoCommandBlock = p_i46596_1_;
   }

   CommandBlockLogic getCommandBlock() {
      return this.autoCommandBlock.getCommandBlock();
   }

   int getPreviousY() {
      return 135;
   }

   protected void init() {
      super.init();
      this.modeButton = this.addButton(new Button(this.width / 2 - 50 - 100 - 4, 165, 100, 20, new TranslationTextComponent("advMode.mode.sequence"), (p_214191_1_) -> {
         this.nextMode();
         this.updateMode();
      }));
      this.conditionalButton = this.addButton(new Button(this.width / 2 - 50, 165, 100, 20, new TranslationTextComponent("advMode.mode.unconditional"), (p_214190_1_) -> {
         this.conditional = !this.conditional;
         this.updateConditional();
      }));
      this.autoexecButton = this.addButton(new Button(this.width / 2 + 50 + 4, 165, 100, 20, new TranslationTextComponent("advMode.mode.redstoneTriggered"), (p_214189_1_) -> {
         this.autoexec = !this.autoexec;
         this.updateAutoexec();
      }));
      this.doneButton.active = false;
      this.outputButton.active = false;
      this.modeButton.active = false;
      this.conditionalButton.active = false;
      this.autoexecButton.active = false;
   }

   public void updateGui() {
      CommandBlockLogic commandblocklogic = this.autoCommandBlock.getCommandBlock();
      this.commandEdit.setValue(commandblocklogic.getCommand());
      this.trackOutput = commandblocklogic.isTrackOutput();
      this.mode = this.autoCommandBlock.getMode();
      this.conditional = this.autoCommandBlock.isConditional();
      this.autoexec = this.autoCommandBlock.isAutomatic();
      this.updateCommandOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoexec();
      this.doneButton.active = true;
      this.outputButton.active = true;
      this.modeButton.active = true;
      this.conditionalButton.active = true;
      this.autoexecButton.active = true;
   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      super.resize(p_231152_1_, p_231152_2_, p_231152_3_);
      this.updateCommandOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoexec();
      this.doneButton.active = true;
      this.outputButton.active = true;
      this.modeButton.active = true;
      this.conditionalButton.active = true;
      this.autoexecButton.active = true;
   }

   protected void populateAndSendPacket(CommandBlockLogic p_195235_1_) {
      this.minecraft.getConnection().send(new CUpdateCommandBlockPacket(new BlockPos(p_195235_1_.getPosition()), this.commandEdit.getValue(), this.mode, p_195235_1_.isTrackOutput(), this.conditional, this.autoexec));
   }

   private void updateMode() {
      switch(this.mode) {
      case SEQUENCE:
         this.modeButton.setMessage(new TranslationTextComponent("advMode.mode.sequence"));
         break;
      case AUTO:
         this.modeButton.setMessage(new TranslationTextComponent("advMode.mode.auto"));
         break;
      case REDSTONE:
         this.modeButton.setMessage(new TranslationTextComponent("advMode.mode.redstone"));
      }

   }

   private void nextMode() {
      switch(this.mode) {
      case SEQUENCE:
         this.mode = CommandBlockTileEntity.Mode.AUTO;
         break;
      case AUTO:
         this.mode = CommandBlockTileEntity.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.mode = CommandBlockTileEntity.Mode.SEQUENCE;
      }

   }

   private void updateConditional() {
      if (this.conditional) {
         this.conditionalButton.setMessage(new TranslationTextComponent("advMode.mode.conditional"));
      } else {
         this.conditionalButton.setMessage(new TranslationTextComponent("advMode.mode.unconditional"));
      }

   }

   private void updateAutoexec() {
      if (this.autoexec) {
         this.autoexecButton.setMessage(new TranslationTextComponent("advMode.mode.autoexec.bat"));
      } else {
         this.autoexecButton.setMessage(new TranslationTextComponent("advMode.mode.redstoneTriggered"));
      }

   }
}
