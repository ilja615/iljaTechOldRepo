package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.JigsawBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.play.client.CJigsawBlockGeneratePacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JigsawScreen extends Screen {
   private static final ITextComponent JOINT_LABEL = new TranslationTextComponent("jigsaw_block.joint_label");
   private static final ITextComponent POOL_LABEL = new TranslationTextComponent("jigsaw_block.pool");
   private static final ITextComponent NAME_LABEL = new TranslationTextComponent("jigsaw_block.name");
   private static final ITextComponent TARGET_LABEL = new TranslationTextComponent("jigsaw_block.target");
   private static final ITextComponent FINAL_STATE_LABEL = new TranslationTextComponent("jigsaw_block.final_state");
   private final JigsawTileEntity jigsawEntity;
   private TextFieldWidget nameEdit;
   private TextFieldWidget targetEdit;
   private TextFieldWidget poolEdit;
   private TextFieldWidget finalStateEdit;
   private int levels;
   private boolean keepJigsaws = true;
   private Button jointButton;
   private Button doneButton;
   private JigsawTileEntity.OrientationType joint;

   public JigsawScreen(JigsawTileEntity p_i51083_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.jigsawEntity = p_i51083_1_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.targetEdit.tick();
      this.poolEdit.tick();
      this.finalStateEdit.tick();
   }

   private void onDone() {
      this.sendToServer();
      this.minecraft.setScreen((Screen)null);
   }

   private void onCancel() {
      this.minecraft.setScreen((Screen)null);
   }

   private void sendToServer() {
      this.minecraft.getConnection().send(new CUpdateJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.nameEdit.getValue()), new ResourceLocation(this.targetEdit.getValue()), new ResourceLocation(this.poolEdit.getValue()), this.finalStateEdit.getValue(), this.joint));
   }

   private void sendGenerate() {
      this.minecraft.getConnection().send(new CJigsawBlockGeneratePacket(this.jigsawEntity.getBlockPos(), this.levels, this.keepJigsaws));
   }

   public void onClose() {
      this.onCancel();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.poolEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 20, 300, 20, new TranslationTextComponent("jigsaw_block.pool"));
      this.poolEdit.setMaxLength(128);
      this.poolEdit.setValue(this.jigsawEntity.getPool().toString());
      this.poolEdit.setResponder((p_238833_1_) -> {
         this.updateValidity();
      });
      this.children.add(this.poolEdit);
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 55, 300, 20, new TranslationTextComponent("jigsaw_block.name"));
      this.nameEdit.setMaxLength(128);
      this.nameEdit.setValue(this.jigsawEntity.getName().toString());
      this.nameEdit.setResponder((p_238830_1_) -> {
         this.updateValidity();
      });
      this.children.add(this.nameEdit);
      this.targetEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 90, 300, 20, new TranslationTextComponent("jigsaw_block.target"));
      this.targetEdit.setMaxLength(128);
      this.targetEdit.setValue(this.jigsawEntity.getTarget().toString());
      this.targetEdit.setResponder((p_214254_1_) -> {
         this.updateValidity();
      });
      this.children.add(this.targetEdit);
      this.finalStateEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 125, 300, 20, new TranslationTextComponent("jigsaw_block.final_state"));
      this.finalStateEdit.setMaxLength(256);
      this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
      this.children.add(this.finalStateEdit);
      this.joint = this.jigsawEntity.getJoint();
      int i = this.font.width(JOINT_LABEL) + 10;
      this.jointButton = this.addButton(new Button(this.width / 2 - 152 + i, 150, 300 - i, 20, this.getJointText(), (p_238834_1_) -> {
         JigsawTileEntity.OrientationType[] ajigsawtileentity$orientationtype = JigsawTileEntity.OrientationType.values();
         int j = (this.joint.ordinal() + 1) % ajigsawtileentity$orientationtype.length;
         this.joint = ajigsawtileentity$orientationtype[j];
         p_238834_1_.setMessage(this.getJointText());
      }));
      boolean flag = JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical();
      this.jointButton.active = flag;
      this.jointButton.visible = flag;
      this.addButton(new AbstractSlider(this.width / 2 - 154, 180, 100, 20, StringTextComponent.EMPTY, 0.0D) {
         {
            this.updateMessage();
         }

         protected void updateMessage() {
            this.setMessage(new TranslationTextComponent("jigsaw_block.levels", JigsawScreen.this.levels));
         }

         protected void applyValue() {
            JigsawScreen.this.levels = MathHelper.floor(MathHelper.clampedLerp(0.0D, 7.0D, this.value));
         }
      });
      this.addButton(new Button(this.width / 2 - 50, 180, 100, 20, new TranslationTextComponent("jigsaw_block.keep_jigsaws"), (p_238832_1_) -> {
         this.keepJigsaws = !this.keepJigsaws;
         p_238832_1_.queueNarration(250);
      }) {
         public ITextComponent getMessage() {
            return DialogTexts.optionStatus(super.getMessage(), JigsawScreen.this.keepJigsaws);
         }
      });
      this.addButton(new Button(this.width / 2 + 54, 180, 100, 20, new TranslationTextComponent("jigsaw_block.generate"), (p_238831_1_) -> {
         this.onDone();
         this.sendGenerate();
      }));
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, DialogTexts.GUI_DONE, (p_238828_1_) -> {
         this.onDone();
      }));
      this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, DialogTexts.GUI_CANCEL, (p_238825_1_) -> {
         this.onCancel();
      }));
      this.setInitialFocus(this.poolEdit);
      this.updateValidity();
   }

   private void updateValidity() {
      this.doneButton.active = ResourceLocation.isValidResourceLocation(this.nameEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.targetEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.poolEdit.getValue());
   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      String s = this.nameEdit.getValue();
      String s1 = this.targetEdit.getValue();
      String s2 = this.poolEdit.getValue();
      String s3 = this.finalStateEdit.getValue();
      int i = this.levels;
      JigsawTileEntity.OrientationType jigsawtileentity$orientationtype = this.joint;
      this.init(p_231152_1_, p_231152_2_, p_231152_3_);
      this.nameEdit.setValue(s);
      this.targetEdit.setValue(s1);
      this.poolEdit.setValue(s2);
      this.finalStateEdit.setValue(s3);
      this.levels = i;
      this.joint = jigsawtileentity$orientationtype;
      this.jointButton.setMessage(this.getJointText());
   }

   private ITextComponent getJointText() {
      return new TranslationTextComponent("jigsaw_block.joint." + this.joint.getSerializedName());
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
         return true;
      } else if (!this.doneButton.active || p_231046_1_ != 257 && p_231046_1_ != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawString(p_230430_1_, this.font, POOL_LABEL, this.width / 2 - 153, 10, 10526880);
      this.poolEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawString(p_230430_1_, this.font, NAME_LABEL, this.width / 2 - 153, 45, 10526880);
      this.nameEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawString(p_230430_1_, this.font, TARGET_LABEL, this.width / 2 - 153, 80, 10526880);
      this.targetEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawString(p_230430_1_, this.font, FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
      this.finalStateEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical()) {
         drawString(p_230430_1_, this.font, JOINT_LABEL, this.width / 2 - 153, 156, 16777215);
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
