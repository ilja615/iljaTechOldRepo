package net.minecraft.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.GamemodeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WithNarratorSettingsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyboardListener {
   private final Minecraft minecraft;
   private boolean sendRepeatsToGui;
   private final ClipboardHelper clipboardManager = new ClipboardHelper();
   private long debugCrashKeyTime = -1L;
   private long debugCrashKeyReportedTime = -1L;
   private long debugCrashKeyReportedCount = -1L;
   private boolean handledDebugKey;

   public KeyboardListener(Minecraft p_i47674_1_) {
      this.minecraft = p_i47674_1_;
   }

   private void debugFeedbackTranslated(String p_197964_1_, Object... p_197964_2_) {
      this.minecraft.gui.getChat().addMessage((new StringTextComponent("")).append((new TranslationTextComponent("debug.prefix")).withStyle(new TextFormatting[]{TextFormatting.YELLOW, TextFormatting.BOLD})).append(" ").append(new TranslationTextComponent(p_197964_1_, p_197964_2_)));
   }

   private void debugWarningTranslated(String p_204869_1_, Object... p_204869_2_) {
      this.minecraft.gui.getChat().addMessage((new StringTextComponent("")).append((new TranslationTextComponent("debug.prefix")).withStyle(new TextFormatting[]{TextFormatting.RED, TextFormatting.BOLD})).append(" ").append(new TranslationTextComponent(p_204869_1_, p_204869_2_)));
   }

   private boolean handleDebugKeys(int p_197962_1_) {
      if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
         return true;
      } else {
         switch(p_197962_1_) {
         case 65:
            this.minecraft.levelRenderer.allChanged();
            this.debugFeedbackTranslated("debug.reload_chunks.message");
            return true;
         case 66:
            boolean flag = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
            this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(flag);
            this.debugFeedbackTranslated(flag ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
         case 67:
            if (this.minecraft.player.isReducedDebugInfo()) {
               return false;
            } else {
               ClientPlayNetHandler clientplaynethandler = this.minecraft.player.connection;
               if (clientplaynethandler == null) {
                  return false;
               }

               this.debugFeedbackTranslated("debug.copy_location.message");
               this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.minecraft.player.level.dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), this.minecraft.player.yRot, this.minecraft.player.xRot));
               return true;
            }
         case 68:
            if (this.minecraft.gui != null) {
               this.minecraft.gui.getChat().clearMessages(false);
            }

            return true;
         case 70:
            AbstractOption.RENDER_DISTANCE.set(this.minecraft.options, MathHelper.clamp((double)(this.minecraft.options.renderDistance + (Screen.hasShiftDown() ? -1 : 1)), AbstractOption.RENDER_DISTANCE.getMinValue(), AbstractOption.RENDER_DISTANCE.getMaxValue()));
            this.debugFeedbackTranslated("debug.cycle_renderdistance.message", this.minecraft.options.renderDistance);
            return true;
         case 71:
            boolean flag1 = this.minecraft.debugRenderer.switchRenderChunkborder();
            this.debugFeedbackTranslated(flag1 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
         case 72:
            this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
            this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.minecraft.options.save();
            return true;
         case 73:
            if (!this.minecraft.player.isReducedDebugInfo()) {
               this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
            }

            return true;
         case 78:
            if (!this.minecraft.player.hasPermissions(2)) {
               this.debugFeedbackTranslated("debug.creative_spectator.error");
            } else if (!this.minecraft.player.isSpectator()) {
               this.minecraft.player.chat("/gamemode spectator");
            } else {
               this.minecraft.player.chat("/gamemode " + this.minecraft.gameMode.getPreviousPlayerMode().getName());
            }

            return true;
         case 80:
            this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
            this.minecraft.options.save();
            this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
         case 81:
            this.debugFeedbackTranslated("debug.help.message");
            NewChatGui newchatgui = this.minecraft.gui.getChat();
            newchatgui.addMessage(new TranslationTextComponent("debug.reload_chunks.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.show_hitboxes.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.copy_location.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.clear_chat.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.cycle_renderdistance.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.chunk_boundaries.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.advanced_tooltips.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.inspect.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.creative_spectator.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.pause_focus.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.help.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.reload_resourcepacks.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.pause.help"));
            newchatgui.addMessage(new TranslationTextComponent("debug.gamemodes.help"));
            return true;
         case 84:
            this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
            this.minecraft.reloadResourcePacks();
            return true;
         case 293:
            if (!this.minecraft.player.hasPermissions(2)) {
               this.debugFeedbackTranslated("debug.gamemodes.error");
            } else {
               this.minecraft.setScreen(new GamemodeSelectionScreen());
            }

            return true;
         default:
            return false;
         }
      }
   }

   private void copyRecreateCommand(boolean p_211556_1_, boolean p_211556_2_) {
      RayTraceResult raytraceresult = this.minecraft.hitResult;
      if (raytraceresult != null) {
         switch(raytraceresult.getType()) {
         case BLOCK:
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getBlockPos();
            BlockState blockstate = this.minecraft.player.level.getBlockState(blockpos);
            if (p_211556_1_) {
               if (p_211556_2_) {
                  this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(blockpos, (p_211561_3_) -> {
                     this.copyCreateBlockCommand(blockstate, blockpos, p_211561_3_);
                     this.debugFeedbackTranslated("debug.inspect.server.block");
                  });
               } else {
                  TileEntity tileentity = this.minecraft.player.level.getBlockEntity(blockpos);
                  CompoundNBT compoundnbt1 = tileentity != null ? tileentity.save(new CompoundNBT()) : null;
                  this.copyCreateBlockCommand(blockstate, blockpos, compoundnbt1);
                  this.debugFeedbackTranslated("debug.inspect.client.block");
               }
            } else {
               this.copyCreateBlockCommand(blockstate, blockpos, (CompoundNBT)null);
               this.debugFeedbackTranslated("debug.inspect.client.block");
            }
            break;
         case ENTITY:
            Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(entity.getType());
            if (p_211556_1_) {
               if (p_211556_2_) {
                  this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(entity.getId(), (p_227999_3_) -> {
                     this.copyCreateEntityCommand(resourcelocation, entity.position(), p_227999_3_);
                     this.debugFeedbackTranslated("debug.inspect.server.entity");
                  });
               } else {
                  CompoundNBT compoundnbt = entity.saveWithoutId(new CompoundNBT());
                  this.copyCreateEntityCommand(resourcelocation, entity.position(), compoundnbt);
                  this.debugFeedbackTranslated("debug.inspect.client.entity");
               }
            } else {
               this.copyCreateEntityCommand(resourcelocation, entity.position(), (CompoundNBT)null);
               this.debugFeedbackTranslated("debug.inspect.client.entity");
            }
         }

      }
   }

   private void copyCreateBlockCommand(BlockState p_211558_1_, BlockPos p_211558_2_, @Nullable CompoundNBT p_211558_3_) {
      if (p_211558_3_ != null) {
         p_211558_3_.remove("x");
         p_211558_3_.remove("y");
         p_211558_3_.remove("z");
         p_211558_3_.remove("id");
      }

      StringBuilder stringbuilder = new StringBuilder(BlockStateParser.serialize(p_211558_1_));
      if (p_211558_3_ != null) {
         stringbuilder.append((Object)p_211558_3_);
      }

      String s = String.format(Locale.ROOT, "/setblock %d %d %d %s", p_211558_2_.getX(), p_211558_2_.getY(), p_211558_2_.getZ(), stringbuilder);
      this.setClipboard(s);
   }

   private void copyCreateEntityCommand(ResourceLocation p_211557_1_, Vector3d p_211557_2_, @Nullable CompoundNBT p_211557_3_) {
      String s;
      if (p_211557_3_ != null) {
         p_211557_3_.remove("UUID");
         p_211557_3_.remove("Pos");
         p_211557_3_.remove("Dimension");
         String s1 = p_211557_3_.getPrettyDisplay().getString();
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", p_211557_1_.toString(), p_211557_2_.x, p_211557_2_.y, p_211557_2_.z, s1);
      } else {
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", p_211557_1_.toString(), p_211557_2_.x, p_211557_2_.y, p_211557_2_.z);
      }

      this.setClipboard(s);
   }

   public void keyPress(long p_197961_1_, int p_197961_3_, int p_197961_4_, int p_197961_5_, int p_197961_6_) {
      if (p_197961_1_ == this.minecraft.getWindow().getWindow()) {
         if (this.debugCrashKeyTime > 0L) {
            if (!InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) || !InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
               this.debugCrashKeyTime = -1L;
            }
         } else if (InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) && InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
         }

         INestedGuiEventHandler inestedguieventhandler = this.minecraft.screen;

         if ((!(this.minecraft.screen instanceof ControlsScreen) || ((ControlsScreen)inestedguieventhandler).lastKeySelection <= Util.getMillis() - 20L)) {
            if (p_197961_5_ == 1) {
            if (this.minecraft.options.keyFullscreen.matches(p_197961_3_, p_197961_4_)) {
               this.minecraft.getWindow().toggleFullScreen();
               this.minecraft.options.fullscreen = this.minecraft.getWindow().isFullscreen();
               this.minecraft.options.save();
               return;
            }

            if (this.minecraft.options.keyScreenshot.matches(p_197961_3_, p_197961_4_)) {
               if (Screen.hasControlDown()) {
               }

               ScreenShotHelper.grab(this.minecraft.gameDirectory, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getMainRenderTarget(), (p_212449_1_) -> {
                  this.minecraft.execute(() -> {
                     this.minecraft.gui.getChat().addMessage(p_212449_1_);
                  });
               });
               return;
            }
            } else if (p_197961_5_ == 0 /*GLFW_RELEASE*/ && this.minecraft.screen instanceof ControlsScreen)
               ((ControlsScreen)this.minecraft.screen).selectedKey = null; //Forge: Unset pure modifiers.
         }

         boolean flag = inestedguieventhandler == null || !(inestedguieventhandler.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)inestedguieventhandler.getFocused()).canConsumeInput();
         if (p_197961_5_ != 0 && p_197961_3_ == 66 && Screen.hasControlDown() && flag) {
            AbstractOption.NARRATOR.toggle(this.minecraft.options, 1);
            if (inestedguieventhandler instanceof WithNarratorSettingsScreen) {
               ((WithNarratorSettingsScreen)inestedguieventhandler).updateNarratorButton();
            }
         }

         if (inestedguieventhandler != null) {
            boolean[] aboolean = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if (p_197961_5_ != 1 && (p_197961_5_ != 2 || !this.sendRepeatsToGui)) {
                  if (p_197961_5_ == 0) {
                     aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyReleasedPre(this.minecraft.screen, p_197961_3_, p_197961_4_, p_197961_6_);
                     if (!aboolean[0]) aboolean[0] = inestedguieventhandler.keyReleased(p_197961_3_, p_197961_4_, p_197961_6_);
                     if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyReleasedPost(this.minecraft.screen, p_197961_3_, p_197961_4_, p_197961_6_);
                  }
               } else {
                  aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyPressedPre(this.minecraft.screen, p_197961_3_, p_197961_4_, p_197961_6_);
                  if (!aboolean[0]) aboolean[0] = inestedguieventhandler.keyPressed(p_197961_3_, p_197961_4_, p_197961_6_);
                  if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiKeyPressedPost(this.minecraft.screen, p_197961_3_, p_197961_4_, p_197961_6_);
               }

            }, "keyPressed event handler", inestedguieventhandler.getClass().getCanonicalName());
            if (aboolean[0]) {
               return;
            }
         }

         if (this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            InputMappings.Input inputmappings$input = InputMappings.getKey(p_197961_3_, p_197961_4_);
            if (p_197961_5_ == 0) {
               KeyBinding.set(inputmappings$input, false);
               if (p_197961_3_ == 292) {
                  if (this.handledDebugKey) {
                     this.handledDebugKey = false;
                  } else {
                     this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                     this.minecraft.options.renderDebugCharts = this.minecraft.options.renderDebug && Screen.hasShiftDown();
                     this.minecraft.options.renderFpsChart = this.minecraft.options.renderDebug && Screen.hasAltDown();
                  }
               }
            } else {
               if (p_197961_3_ == 293 && this.minecraft.gameRenderer != null) {
                  this.minecraft.gameRenderer.togglePostEffect();
               }

               boolean flag1 = false;
               if (this.minecraft.screen == null) {
                  if (p_197961_3_ == 256) {
                     boolean flag2 = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
                     this.minecraft.pauseGame(flag2);
                  }

                  flag1 = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292) && this.handleDebugKeys(p_197961_3_);
                  this.handledDebugKey |= flag1;
                  if (p_197961_3_ == 290) {
                     this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                  }
               }

               if (flag1) {
                  KeyBinding.set(inputmappings$input, false);
               } else {
                  KeyBinding.set(inputmappings$input, true);
                  KeyBinding.click(inputmappings$input);
               }

               if (this.minecraft.options.renderDebugCharts && p_197961_3_ >= 48 && p_197961_3_ <= 57) {
                  this.minecraft.debugFpsMeterKeyPress(p_197961_3_ - 48);
               }
            }
         }
         net.minecraftforge.client.ForgeHooksClient.fireKeyInput(p_197961_3_, p_197961_4_, p_197961_5_, p_197961_6_);
      }
   }

   private void charTyped(long p_197963_1_, int p_197963_3_, int p_197963_4_) {
      if (p_197963_1_ == this.minecraft.getWindow().getWindow()) {
         IGuiEventListener iguieventlistener = this.minecraft.screen;
         if (iguieventlistener != null && this.minecraft.getOverlay() == null) {
            if (Character.charCount(p_197963_3_) == 1) {
               Screen.wrapScreenError(() -> {
                  if (net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPre(this.minecraft.screen, (char)p_197963_3_, p_197963_4_)) return;
                  if (iguieventlistener.charTyped((char)p_197963_3_, p_197963_4_)) return;
                  net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPost(this.minecraft.screen, (char)p_197963_3_, p_197963_4_);
               }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
            } else {
               for(char c0 : Character.toChars(p_197963_3_)) {
                  Screen.wrapScreenError(() -> {
                     if (net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPre(this.minecraft.screen, c0, p_197963_4_)) return;
                     if (iguieventlistener.charTyped(c0, p_197963_4_)) return;
                     net.minecraftforge.client.ForgeHooksClient.onGuiCharTypedPost(this.minecraft.screen, c0, p_197963_4_);
                  }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
               }
            }

         }
      }
   }

   public void setSendRepeatsToGui(boolean p_197967_1_) {
      this.sendRepeatsToGui = p_197967_1_;
   }

   public void setup(long p_197968_1_) {
      InputMappings.setupKeyboardCallbacks(p_197968_1_, (p_228001_1_, p_228001_3_, p_228001_4_, p_228001_5_, p_228001_6_) -> {
         this.minecraft.execute(() -> {
            this.keyPress(p_228001_1_, p_228001_3_, p_228001_4_, p_228001_5_, p_228001_6_);
         });
      }, (p_228000_1_, p_228000_3_, p_228000_4_) -> {
         this.minecraft.execute(() -> {
            this.charTyped(p_228000_1_, p_228000_3_, p_228000_4_);
         });
      });
   }

   public String getClipboard() {
      return this.clipboardManager.getClipboard(this.minecraft.getWindow().getWindow(), (p_227998_1_, p_227998_2_) -> {
         if (p_227998_1_ != 65545) {
            this.minecraft.getWindow().defaultErrorCallback(p_227998_1_, p_227998_2_);
         }

      });
   }

   public void setClipboard(String p_197960_1_) {
      this.clipboardManager.setClipboard(this.minecraft.getWindow().getWindow(), p_197960_1_);
   }

   public void tick() {
      if (this.debugCrashKeyTime > 0L) {
         long i = Util.getMillis();
         long j = 10000L - (i - this.debugCrashKeyTime);
         long k = i - this.debugCrashKeyReportedTime;
         if (j < 0L) {
            if (Screen.hasControlDown()) {
               NativeUtil.youJustLostTheGame();
            }

            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
         }

         if (k >= 1000L) {
            if (this.debugCrashKeyReportedCount == 0L) {
               this.debugFeedbackTranslated("debug.crash.message");
            } else {
               this.debugWarningTranslated("debug.crash.warning", MathHelper.ceil((float)j / 1000.0F));
            }

            this.debugCrashKeyReportedTime = i;
            ++this.debugCrashKeyReportedCount;
         }
      }

   }
}
