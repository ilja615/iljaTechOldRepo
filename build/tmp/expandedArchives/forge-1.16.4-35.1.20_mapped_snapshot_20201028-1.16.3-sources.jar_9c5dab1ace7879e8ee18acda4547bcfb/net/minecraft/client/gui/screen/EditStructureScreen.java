package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditStructureScreen extends Screen {
   private static final ITextComponent field_243355_a = new TranslationTextComponent("structure_block.structure_name");
   private static final ITextComponent field_243356_b = new TranslationTextComponent("structure_block.position");
   private static final ITextComponent field_243357_c = new TranslationTextComponent("structure_block.size");
   private static final ITextComponent field_243358_p = new TranslationTextComponent("structure_block.integrity");
   private static final ITextComponent field_243359_q = new TranslationTextComponent("structure_block.custom_data");
   private static final ITextComponent field_243360_r = new TranslationTextComponent("structure_block.include_entities");
   private static final ITextComponent field_243361_s = new TranslationTextComponent("structure_block.detect_size");
   private static final ITextComponent field_243362_t = new TranslationTextComponent("structure_block.show_air");
   private static final ITextComponent field_243363_u = new TranslationTextComponent("structure_block.show_boundingbox");
   private final StructureBlockTileEntity tileStructure;
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private StructureMode mode = StructureMode.DATA;
   private boolean ignoreEntities;
   private boolean showAir;
   private boolean showBoundingBox;
   private TextFieldWidget nameEdit;
   private TextFieldWidget posXEdit;
   private TextFieldWidget posYEdit;
   private TextFieldWidget posZEdit;
   private TextFieldWidget sizeXEdit;
   private TextFieldWidget sizeYEdit;
   private TextFieldWidget sizeZEdit;
   private TextFieldWidget integrityEdit;
   private TextFieldWidget seedEdit;
   private TextFieldWidget dataEdit;
   private Button doneButton;
   private Button cancelButton;
   private Button saveButton;
   private Button loadButton;
   private Button rotateZeroDegreesButton;
   private Button rotateNinetyDegreesButton;
   private Button rotate180DegreesButton;
   private Button rotate270DegressButton;
   private Button modeButton;
   private Button detectSizeButton;
   private Button showEntitiesButton;
   private Button mirrorButton;
   private Button showAirButton;
   private Button showBoundingBoxButton;
   private final DecimalFormat decimalFormat = new DecimalFormat("0.0###");

   public EditStructureScreen(StructureBlockTileEntity p_i47142_1_) {
      super(new TranslationTextComponent(Blocks.STRUCTURE_BLOCK.getTranslationKey()));
      this.tileStructure = p_i47142_1_;
      this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public void tick() {
      this.nameEdit.tick();
      this.posXEdit.tick();
      this.posYEdit.tick();
      this.posZEdit.tick();
      this.sizeXEdit.tick();
      this.sizeYEdit.tick();
      this.sizeZEdit.tick();
      this.integrityEdit.tick();
      this.seedEdit.tick();
      this.dataEdit.tick();
   }

   private void func_195275_h() {
      if (this.func_210143_a(StructureBlockTileEntity.UpdateCommand.UPDATE_DATA)) {
         this.minecraft.displayGuiScreen((Screen)null);
      }

   }

   private void func_195272_i() {
      this.tileStructure.setMirror(this.mirror);
      this.tileStructure.setRotation(this.rotation);
      this.tileStructure.setMode(this.mode);
      this.tileStructure.setIgnoresEntities(this.ignoreEntities);
      this.tileStructure.setShowAir(this.showAir);
      this.tileStructure.setShowBoundingBox(this.showBoundingBox);
      this.minecraft.displayGuiScreen((Screen)null);
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, DialogTexts.GUI_DONE, (p_214274_1_) -> {
         this.func_195275_h();
      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, DialogTexts.GUI_CANCEL, (p_214275_1_) -> {
         this.func_195272_i();
      }));
      this.saveButton = this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, new TranslationTextComponent("structure_block.button.save"), (p_214276_1_) -> {
         if (this.tileStructure.getMode() == StructureMode.SAVE) {
            this.func_210143_a(StructureBlockTileEntity.UpdateCommand.SAVE_AREA);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.loadButton = this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, new TranslationTextComponent("structure_block.button.load"), (p_214277_1_) -> {
         if (this.tileStructure.getMode() == StructureMode.LOAD) {
            this.func_210143_a(StructureBlockTileEntity.UpdateCommand.LOAD_AREA);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.modeButton = this.addButton(new Button(this.width / 2 - 4 - 150, 185, 50, 20, new StringTextComponent("MODE"), (p_214280_1_) -> {
         this.tileStructure.nextMode();
         this.updateMode();
      }));
      this.detectSizeButton = this.addButton(new Button(this.width / 2 + 4 + 100, 120, 50, 20, new TranslationTextComponent("structure_block.button.detect_size"), (p_214278_1_) -> {
         if (this.tileStructure.getMode() == StructureMode.SAVE) {
            this.func_210143_a(StructureBlockTileEntity.UpdateCommand.SCAN_AREA);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.showEntitiesButton = this.addButton(new Button(this.width / 2 + 4 + 100, 160, 50, 20, new StringTextComponent("ENTITIES"), (p_214282_1_) -> {
         this.tileStructure.setIgnoresEntities(!this.tileStructure.ignoresEntities());
         this.updateEntitiesButton();
      }));
      this.mirrorButton = this.addButton(new Button(this.width / 2 - 20, 185, 40, 20, new StringTextComponent("MIRROR"), (p_214281_1_) -> {
         switch(this.tileStructure.getMirror()) {
         case NONE:
            this.tileStructure.setMirror(Mirror.LEFT_RIGHT);
            break;
         case LEFT_RIGHT:
            this.tileStructure.setMirror(Mirror.FRONT_BACK);
            break;
         case FRONT_BACK:
            this.tileStructure.setMirror(Mirror.NONE);
         }

         this.updateMirrorButton();
      }));
      this.showAirButton = this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, new StringTextComponent("SHOWAIR"), (p_214269_1_) -> {
         this.tileStructure.setShowAir(!this.tileStructure.showsAir());
         this.updateToggleAirButton();
      }));
      this.showBoundingBoxButton = this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, new StringTextComponent("SHOWBB"), (p_214270_1_) -> {
         this.tileStructure.setShowBoundingBox(!this.tileStructure.showsBoundingBox());
         this.updateToggleBoundingBox();
      }));
      this.rotateZeroDegreesButton = this.addButton(new Button(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, new StringTextComponent("0"), (p_214268_1_) -> {
         this.tileStructure.setRotation(Rotation.NONE);
         this.updateDirectionButtons();
      }));
      this.rotateNinetyDegreesButton = this.addButton(new Button(this.width / 2 - 1 - 40 - 20, 185, 40, 20, new StringTextComponent("90"), (p_214273_1_) -> {
         this.tileStructure.setRotation(Rotation.CLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.rotate180DegreesButton = this.addButton(new Button(this.width / 2 + 1 + 20, 185, 40, 20, new StringTextComponent("180"), (p_214272_1_) -> {
         this.tileStructure.setRotation(Rotation.CLOCKWISE_180);
         this.updateDirectionButtons();
      }));
      this.rotate270DegressButton = this.addButton(new Button(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, new StringTextComponent("270"), (p_214271_1_) -> {
         this.tileStructure.setRotation(Rotation.COUNTERCLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, new TranslationTextComponent("structure_block.structure_name")) {
         public boolean charTyped(char codePoint, int modifiers) {
            return !EditStructureScreen.this.isValidCharacterForName(this.getText(), codePoint, this.getCursorPosition()) ? false : super.charTyped(codePoint, modifiers);
         }
      };
      this.nameEdit.setMaxStringLength(64);
      this.nameEdit.setText(this.tileStructure.getName());
      this.children.add(this.nameEdit);
      BlockPos blockpos = this.tileStructure.getPosition();
      this.posXEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 80, 20, new TranslationTextComponent("structure_block.position.x"));
      this.posXEdit.setMaxStringLength(15);
      this.posXEdit.setText(Integer.toString(blockpos.getX()));
      this.children.add(this.posXEdit);
      this.posYEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 80, 80, 20, new TranslationTextComponent("structure_block.position.y"));
      this.posYEdit.setMaxStringLength(15);
      this.posYEdit.setText(Integer.toString(blockpos.getY()));
      this.children.add(this.posYEdit);
      this.posZEdit = new TextFieldWidget(this.font, this.width / 2 + 8, 80, 80, 20, new TranslationTextComponent("structure_block.position.z"));
      this.posZEdit.setMaxStringLength(15);
      this.posZEdit.setText(Integer.toString(blockpos.getZ()));
      this.children.add(this.posZEdit);
      BlockPos blockpos1 = this.tileStructure.getStructureSize();
      this.sizeXEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, new TranslationTextComponent("structure_block.size.x"));
      this.sizeXEdit.setMaxStringLength(15);
      this.sizeXEdit.setText(Integer.toString(blockpos1.getX()));
      this.children.add(this.sizeXEdit);
      this.sizeYEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, new TranslationTextComponent("structure_block.size.y"));
      this.sizeYEdit.setMaxStringLength(15);
      this.sizeYEdit.setText(Integer.toString(blockpos1.getY()));
      this.children.add(this.sizeYEdit);
      this.sizeZEdit = new TextFieldWidget(this.font, this.width / 2 + 8, 120, 80, 20, new TranslationTextComponent("structure_block.size.z"));
      this.sizeZEdit.setMaxStringLength(15);
      this.sizeZEdit.setText(Integer.toString(blockpos1.getZ()));
      this.children.add(this.sizeZEdit);
      this.integrityEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, new TranslationTextComponent("structure_block.integrity.integrity"));
      this.integrityEdit.setMaxStringLength(15);
      this.integrityEdit.setText(this.decimalFormat.format((double)this.tileStructure.getIntegrity()));
      this.children.add(this.integrityEdit);
      this.seedEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, new TranslationTextComponent("structure_block.integrity.seed"));
      this.seedEdit.setMaxStringLength(31);
      this.seedEdit.setText(Long.toString(this.tileStructure.getSeed()));
      this.children.add(this.seedEdit);
      this.dataEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 240, 20, new TranslationTextComponent("structure_block.custom_data"));
      this.dataEdit.setMaxStringLength(128);
      this.dataEdit.setText(this.tileStructure.getMetadata());
      this.children.add(this.dataEdit);
      this.mirror = this.tileStructure.getMirror();
      this.updateMirrorButton();
      this.rotation = this.tileStructure.getRotation();
      this.updateDirectionButtons();
      this.mode = this.tileStructure.getMode();
      this.updateMode();
      this.ignoreEntities = this.tileStructure.ignoresEntities();
      this.updateEntitiesButton();
      this.showAir = this.tileStructure.showsAir();
      this.updateToggleAirButton();
      this.showBoundingBox = this.tileStructure.showsBoundingBox();
      this.updateToggleBoundingBox();
      this.setFocusedDefault(this.nameEdit);
   }

   public void resize(Minecraft minecraft, int width, int height) {
      String s = this.nameEdit.getText();
      String s1 = this.posXEdit.getText();
      String s2 = this.posYEdit.getText();
      String s3 = this.posZEdit.getText();
      String s4 = this.sizeXEdit.getText();
      String s5 = this.sizeYEdit.getText();
      String s6 = this.sizeZEdit.getText();
      String s7 = this.integrityEdit.getText();
      String s8 = this.seedEdit.getText();
      String s9 = this.dataEdit.getText();
      this.init(minecraft, width, height);
      this.nameEdit.setText(s);
      this.posXEdit.setText(s1);
      this.posYEdit.setText(s2);
      this.posZEdit.setText(s3);
      this.sizeXEdit.setText(s4);
      this.sizeYEdit.setText(s5);
      this.sizeZEdit.setText(s6);
      this.integrityEdit.setText(s7);
      this.seedEdit.setText(s8);
      this.dataEdit.setText(s9);
   }

   public void onClose() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   private void updateEntitiesButton() {
      this.showEntitiesButton.setMessage(DialogTexts.optionsEnabled(!this.tileStructure.ignoresEntities()));
   }

   private void updateToggleAirButton() {
      this.showAirButton.setMessage(DialogTexts.optionsEnabled(this.tileStructure.showsAir()));
   }

   private void updateToggleBoundingBox() {
      this.showBoundingBoxButton.setMessage(DialogTexts.optionsEnabled(this.tileStructure.showsBoundingBox()));
   }

   private void updateMirrorButton() {
      Mirror mirror = this.tileStructure.getMirror();
      switch(mirror) {
      case NONE:
         this.mirrorButton.setMessage(new StringTextComponent("|"));
         break;
      case LEFT_RIGHT:
         this.mirrorButton.setMessage(new StringTextComponent("< >"));
         break;
      case FRONT_BACK:
         this.mirrorButton.setMessage(new StringTextComponent("^ v"));
      }

   }

   private void updateDirectionButtons() {
      this.rotateZeroDegreesButton.active = true;
      this.rotateNinetyDegreesButton.active = true;
      this.rotate180DegreesButton.active = true;
      this.rotate270DegressButton.active = true;
      switch(this.tileStructure.getRotation()) {
      case NONE:
         this.rotateZeroDegreesButton.active = false;
         break;
      case CLOCKWISE_180:
         this.rotate180DegreesButton.active = false;
         break;
      case COUNTERCLOCKWISE_90:
         this.rotate270DegressButton.active = false;
         break;
      case CLOCKWISE_90:
         this.rotateNinetyDegreesButton.active = false;
      }

   }

   private void updateMode() {
      this.nameEdit.setVisible(false);
      this.posXEdit.setVisible(false);
      this.posYEdit.setVisible(false);
      this.posZEdit.setVisible(false);
      this.sizeXEdit.setVisible(false);
      this.sizeYEdit.setVisible(false);
      this.sizeZEdit.setVisible(false);
      this.integrityEdit.setVisible(false);
      this.seedEdit.setVisible(false);
      this.dataEdit.setVisible(false);
      this.saveButton.visible = false;
      this.loadButton.visible = false;
      this.detectSizeButton.visible = false;
      this.showEntitiesButton.visible = false;
      this.mirrorButton.visible = false;
      this.rotateZeroDegreesButton.visible = false;
      this.rotateNinetyDegreesButton.visible = false;
      this.rotate180DegreesButton.visible = false;
      this.rotate270DegressButton.visible = false;
      this.showAirButton.visible = false;
      this.showBoundingBoxButton.visible = false;
      switch(this.tileStructure.getMode()) {
      case SAVE:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.sizeXEdit.setVisible(true);
         this.sizeYEdit.setVisible(true);
         this.sizeZEdit.setVisible(true);
         this.saveButton.visible = true;
         this.detectSizeButton.visible = true;
         this.showEntitiesButton.visible = true;
         this.showAirButton.visible = true;
         break;
      case LOAD:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.integrityEdit.setVisible(true);
         this.seedEdit.setVisible(true);
         this.loadButton.visible = true;
         this.showEntitiesButton.visible = true;
         this.mirrorButton.visible = true;
         this.rotateZeroDegreesButton.visible = true;
         this.rotateNinetyDegreesButton.visible = true;
         this.rotate180DegreesButton.visible = true;
         this.rotate270DegressButton.visible = true;
         this.showBoundingBoxButton.visible = true;
         this.updateDirectionButtons();
         break;
      case CORNER:
         this.nameEdit.setVisible(true);
         break;
      case DATA:
         this.dataEdit.setVisible(true);
      }

      this.modeButton.setMessage(new TranslationTextComponent("structure_block.mode." + this.tileStructure.getMode().getString()));
   }

   private boolean func_210143_a(StructureBlockTileEntity.UpdateCommand p_210143_1_) {
      BlockPos blockpos = new BlockPos(this.parseCoordinate(this.posXEdit.getText()), this.parseCoordinate(this.posYEdit.getText()), this.parseCoordinate(this.posZEdit.getText()));
      BlockPos blockpos1 = new BlockPos(this.parseCoordinate(this.sizeXEdit.getText()), this.parseCoordinate(this.sizeYEdit.getText()), this.parseCoordinate(this.sizeZEdit.getText()));
      float f = this.parseIntegrity(this.integrityEdit.getText());
      long i = this.parseSeed(this.seedEdit.getText());
      this.minecraft.getConnection().sendPacket(new CUpdateStructureBlockPacket(this.tileStructure.getPos(), p_210143_1_, this.tileStructure.getMode(), this.nameEdit.getText(), blockpos, blockpos1, this.tileStructure.getMirror(), this.tileStructure.getRotation(), this.dataEdit.getText(), this.tileStructure.ignoresEntities(), this.tileStructure.showsAir(), this.tileStructure.showsBoundingBox(), f, i));
      return true;
   }

   private long parseSeed(String p_189821_1_) {
      try {
         return Long.valueOf(p_189821_1_);
      } catch (NumberFormatException numberformatexception) {
         return 0L;
      }
   }

   private float parseIntegrity(String p_189819_1_) {
      try {
         return Float.valueOf(p_189819_1_);
      } catch (NumberFormatException numberformatexception) {
         return 1.0F;
      }
   }

   private int parseCoordinate(String p_189817_1_) {
      try {
         return Integer.parseInt(p_189817_1_);
      } catch (NumberFormatException numberformatexception) {
         return 0;
      }
   }

   public void closeScreen() {
      this.func_195272_i();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (keyCode != 257 && keyCode != 335) {
         return false;
      } else {
         this.func_195275_h();
         return true;
      }
   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      StructureMode structuremode = this.tileStructure.getMode();
      drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 10, 16777215);
      if (structuremode != StructureMode.DATA) {
         drawString(matrixStack, this.font, field_243355_a, this.width / 2 - 153, 30, 10526880);
         this.nameEdit.render(matrixStack, mouseX, mouseY, partialTicks);
      }

      if (structuremode == StructureMode.LOAD || structuremode == StructureMode.SAVE) {
         drawString(matrixStack, this.font, field_243356_b, this.width / 2 - 153, 70, 10526880);
         this.posXEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         this.posYEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         this.posZEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         drawString(matrixStack, this.font, field_243360_r, this.width / 2 + 154 - this.font.getStringPropertyWidth(field_243360_r), 150, 10526880);
      }

      if (structuremode == StructureMode.SAVE) {
         drawString(matrixStack, this.font, field_243357_c, this.width / 2 - 153, 110, 10526880);
         this.sizeXEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         this.sizeYEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         this.sizeZEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         drawString(matrixStack, this.font, field_243361_s, this.width / 2 + 154 - this.font.getStringPropertyWidth(field_243361_s), 110, 10526880);
         drawString(matrixStack, this.font, field_243362_t, this.width / 2 + 154 - this.font.getStringPropertyWidth(field_243362_t), 70, 10526880);
      }

      if (structuremode == StructureMode.LOAD) {
         drawString(matrixStack, this.font, field_243358_p, this.width / 2 - 153, 110, 10526880);
         this.integrityEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         this.seedEdit.render(matrixStack, mouseX, mouseY, partialTicks);
         drawString(matrixStack, this.font, field_243363_u, this.width / 2 + 154 - this.font.getStringPropertyWidth(field_243363_u), 70, 10526880);
      }

      if (structuremode == StructureMode.DATA) {
         drawString(matrixStack, this.font, field_243359_q, this.width / 2 - 153, 110, 10526880);
         this.dataEdit.render(matrixStack, mouseX, mouseY, partialTicks);
      }

      drawString(matrixStack, this.font, structuremode.func_242703_b(), this.width / 2 - 153, 174, 10526880);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
