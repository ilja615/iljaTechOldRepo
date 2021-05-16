package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GamemodeSelectionScreen extends Screen {
   private static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
   private static final int ALL_SLOTS_WIDTH = GamemodeSelectionScreen.Mode.values().length * 30 - 5;
   private static final ITextComponent SELECT_KEY = new TranslationTextComponent("debug.gamemodes.select_next", (new TranslationTextComponent("debug.gamemodes.press_f4")).withStyle(TextFormatting.AQUA));
   private final Optional<GamemodeSelectionScreen.Mode> previousHovered;
   private Optional<GamemodeSelectionScreen.Mode> currentlyHovered = Optional.empty();
   private int firstMouseX;
   private int firstMouseY;
   private boolean setFirstMousePos;
   private final List<GamemodeSelectionScreen.SelectorWidget> slots = Lists.newArrayList();

   public GamemodeSelectionScreen() {
      super(NarratorChatListener.NO_TITLE);
      this.previousHovered = GamemodeSelectionScreen.Mode.getFromGameType(this.getDefaultSelected());
   }

   private GameType getDefaultSelected() {
      GameType gametype = Minecraft.getInstance().gameMode.getPlayerMode();
      GameType gametype1 = Minecraft.getInstance().gameMode.getPreviousPlayerMode();
      if (gametype1 == GameType.NOT_SET) {
         if (gametype == GameType.CREATIVE) {
            gametype1 = GameType.SURVIVAL;
         } else {
            gametype1 = GameType.CREATIVE;
         }
      }

      return gametype1;
   }

   protected void init() {
      super.init();
      this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GamemodeSelectionScreen.Mode.getFromGameType(this.minecraft.gameMode.getPlayerMode());

      for(int i = 0; i < GamemodeSelectionScreen.Mode.VALUES.length; ++i) {
         GamemodeSelectionScreen.Mode gamemodeselectionscreen$mode = GamemodeSelectionScreen.Mode.VALUES[i];
         this.slots.add(new GamemodeSelectionScreen.SelectorWidget(gamemodeselectionscreen$mode, this.width / 2 - ALL_SLOTS_WIDTH / 2 + i * 30, this.height / 2 - 30));
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (!this.checkToClose()) {
         p_230430_1_.pushPose();
         RenderSystem.enableBlend();
         this.minecraft.getTextureManager().bind(GAMEMODE_SWITCHER_LOCATION);
         int i = this.width / 2 - 62;
         int j = this.height / 2 - 30 - 27;
         blit(p_230430_1_, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
         p_230430_1_.popPose();
         super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         this.currentlyHovered.ifPresent((p_238712_2_) -> {
            drawCenteredString(p_230430_1_, this.font, p_238712_2_.getName(), this.width / 2, this.height / 2 - 30 - 20, -1);
         });
         drawCenteredString(p_230430_1_, this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
         if (!this.setFirstMousePos) {
            this.firstMouseX = p_230430_2_;
            this.firstMouseY = p_230430_3_;
            this.setFirstMousePos = true;
         }

         boolean flag = this.firstMouseX == p_230430_2_ && this.firstMouseY == p_230430_3_;

         for(GamemodeSelectionScreen.SelectorWidget gamemodeselectionscreen$selectorwidget : this.slots) {
            gamemodeselectionscreen$selectorwidget.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            this.currentlyHovered.ifPresent((p_238714_1_) -> {
               gamemodeselectionscreen$selectorwidget.setSelected(p_238714_1_ == gamemodeselectionscreen$selectorwidget.icon);
            });
            if (!flag && gamemodeselectionscreen$selectorwidget.isHovered()) {
               this.currentlyHovered = Optional.of(gamemodeselectionscreen$selectorwidget.icon);
            }
         }

      }
   }

   private void switchToHoveredGameMode() {
      switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
   }

   private static void switchToHoveredGameMode(Minecraft p_238713_0_, Optional<GamemodeSelectionScreen.Mode> p_238713_1_) {
      if (p_238713_0_.gameMode != null && p_238713_0_.player != null && p_238713_1_.isPresent()) {
         Optional<GamemodeSelectionScreen.Mode> optional = GamemodeSelectionScreen.Mode.getFromGameType(p_238713_0_.gameMode.getPlayerMode());
         GamemodeSelectionScreen.Mode gamemodeselectionscreen$mode = p_238713_1_.get();
         if (optional.isPresent() && p_238713_0_.player.hasPermissions(2) && gamemodeselectionscreen$mode != optional.get()) {
            p_238713_0_.player.chat(gamemodeselectionscreen$mode.getCommand());
         }

      }
   }

   private boolean checkToClose() {
      if (!InputMappings.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
         this.switchToHoveredGameMode();
         this.minecraft.setScreen((Screen)null);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 293 && this.currentlyHovered.isPresent()) {
         this.setFirstMousePos = false;
         this.currentlyHovered = this.currentlyHovered.get().getNext();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   static enum Mode {
      CREATIVE(new TranslationTextComponent("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(new TranslationTextComponent("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(new TranslationTextComponent("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
      SPECTATOR(new TranslationTextComponent("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

      protected static final GamemodeSelectionScreen.Mode[] VALUES = values();
      final ITextComponent name;
      final String command;
      final ItemStack renderStack;

      private Mode(ITextComponent p_i232285_3_, String p_i232285_4_, ItemStack p_i232285_5_) {
         this.name = p_i232285_3_;
         this.command = p_i232285_4_;
         this.renderStack = p_i232285_5_;
      }

      private void drawIcon(ItemRenderer p_238729_1_, int p_238729_2_, int p_238729_3_) {
         p_238729_1_.renderAndDecorateItem(this.renderStack, p_238729_2_, p_238729_3_);
      }

      private ITextComponent getName() {
         return this.name;
      }

      private String getCommand() {
         return this.command;
      }

      private Optional<GamemodeSelectionScreen.Mode> getNext() {
         switch(this) {
         case CREATIVE:
            return Optional.of(SURVIVAL);
         case SURVIVAL:
            return Optional.of(ADVENTURE);
         case ADVENTURE:
            return Optional.of(SPECTATOR);
         default:
            return Optional.of(CREATIVE);
         }
      }

      private static Optional<GamemodeSelectionScreen.Mode> getFromGameType(GameType p_238731_0_) {
         switch(p_238731_0_) {
         case SPECTATOR:
            return Optional.of(SPECTATOR);
         case SURVIVAL:
            return Optional.of(SURVIVAL);
         case CREATIVE:
            return Optional.of(CREATIVE);
         case ADVENTURE:
            return Optional.of(ADVENTURE);
         default:
            return Optional.empty();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class SelectorWidget extends Widget {
      private final GamemodeSelectionScreen.Mode icon;
      private boolean isSelected;

      public SelectorWidget(GamemodeSelectionScreen.Mode p_i232286_2_, int p_i232286_3_, int p_i232286_4_) {
         super(p_i232286_3_, p_i232286_4_, 25, 25, p_i232286_2_.getName());
         this.icon = p_i232286_2_;
      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         Minecraft minecraft = Minecraft.getInstance();
         this.drawSlot(p_230431_1_, minecraft.getTextureManager());
         this.icon.drawIcon(GamemodeSelectionScreen.this.itemRenderer, this.x + 5, this.y + 5);
         if (this.isSelected) {
            this.drawSelection(p_230431_1_, minecraft.getTextureManager());
         }

      }

      public boolean isHovered() {
         return super.isHovered() || this.isSelected;
      }

      public void setSelected(boolean p_238741_1_) {
         this.isSelected = p_238741_1_;
         this.narrate();
      }

      private void drawSlot(MatrixStack p_238738_1_, TextureManager p_238738_2_) {
         p_238738_2_.bind(GamemodeSelectionScreen.GAMEMODE_SWITCHER_LOCATION);
         p_238738_1_.pushPose();
         p_238738_1_.translate((double)this.x, (double)this.y, 0.0D);
         blit(p_238738_1_, 0, 0, 0.0F, 75.0F, 25, 25, 128, 128);
         p_238738_1_.popPose();
      }

      private void drawSelection(MatrixStack p_238740_1_, TextureManager p_238740_2_) {
         p_238740_2_.bind(GamemodeSelectionScreen.GAMEMODE_SWITCHER_LOCATION);
         p_238740_1_.pushPose();
         p_238740_1_.translate((double)this.x, (double)this.y, 0.0D);
         blit(p_238740_1_, 0, 0, 25.0F, 75.0F, 25, 25, 128, 128);
         p_238740_1_.popPose();
      }
   }
}
