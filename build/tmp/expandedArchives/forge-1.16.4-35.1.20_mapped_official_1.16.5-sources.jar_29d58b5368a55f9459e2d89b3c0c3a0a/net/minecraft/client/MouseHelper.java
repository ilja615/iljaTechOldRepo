package net.minecraft.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWDropCallback;

@OnlyIn(Dist.CLIENT)
public class MouseHelper {
   private final Minecraft minecraft;
   private boolean isLeftPressed;
   private boolean isMiddlePressed;
   private boolean isRightPressed;
   private double xpos;
   private double ypos;
   private int fakeRightMouse;
   private int activeButton = -1;
   private boolean ignoreFirstMove = true;
   private int clickDepth;
   private double mousePressedTime;
   private final MouseSmoother smoothTurnX = new MouseSmoother();
   private final MouseSmoother smoothTurnY = new MouseSmoother();
   private double accumulatedDX;
   private double accumulatedDY;
   private double accumulatedScroll;
   private double lastMouseEventTime = Double.MIN_VALUE;
   private boolean mouseGrabbed;

   public MouseHelper(Minecraft p_i47672_1_) {
      this.minecraft = p_i47672_1_;
   }

   private void onPress(long p_198023_1_, int p_198023_3_, int p_198023_4_, int p_198023_5_) {
      if (p_198023_1_ == this.minecraft.getWindow().getWindow()) {
         boolean flag = p_198023_4_ == 1;
         if (Minecraft.ON_OSX && p_198023_3_ == 0) {
            if (flag) {
               if ((p_198023_5_ & 2) == 2) {
                  p_198023_3_ = 1;
                  ++this.fakeRightMouse;
               }
            } else if (this.fakeRightMouse > 0) {
               p_198023_3_ = 1;
               --this.fakeRightMouse;
            }
         }

         int i = p_198023_3_;
         if (flag) {
            if (this.minecraft.options.touchscreen && this.clickDepth++ > 0) {
               return;
            }

            this.activeButton = i;
            this.mousePressedTime = NativeUtil.getTime();
         } else if (this.activeButton != -1) {
            if (this.minecraft.options.touchscreen && --this.clickDepth > 0) {
               return;
            }

            this.activeButton = -1;
         }

         if (net.minecraftforge.client.ForgeHooksClient.onRawMouseClicked(p_198023_3_, p_198023_4_, p_198023_5_)) return;
         boolean[] aboolean = new boolean[]{false};
         if (this.minecraft.overlay == null) {
            if (this.minecraft.screen == null) {
               if (!this.mouseGrabbed && flag) {
                  this.grabMouse();
               }
            } else {
               double d0 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double d1 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               if (flag) {
                  Screen.wrapScreenError(() -> {
                     aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseClickedPre(this.minecraft.screen, d0, d1, i);
                     if (!aboolean[0]) aboolean[0] = this.minecraft.screen.mouseClicked(d0, d1, i);
                     if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseClickedPost(this.minecraft.screen, d0, d1, i);
                  }, "mouseClicked event handler", this.minecraft.screen.getClass().getCanonicalName());
               } else {
                  Screen.wrapScreenError(() -> {
                     aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseReleasedPre(this.minecraft.screen, d0, d1, i);
                     if (!aboolean[0]) aboolean[0] = this.minecraft.screen.mouseReleased(d0, d1, i);
                     if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseReleasedPost(this.minecraft.screen, d0, d1, i);
                  }, "mouseReleased event handler", this.minecraft.screen.getClass().getCanonicalName());
               }
            }
         }

         if (!aboolean[0] && (this.minecraft.screen == null || this.minecraft.screen.passEvents) && this.minecraft.overlay == null) {
            if (i == 0) {
               this.isLeftPressed = flag;
            } else if (i == 2) {
               this.isMiddlePressed = flag;
            } else if (i == 1) {
               this.isRightPressed = flag;
            }

            KeyBinding.set(InputMappings.Type.MOUSE.getOrCreate(i), flag);
            if (flag) {
               if (this.minecraft.player.isSpectator() && i == 2) {
                  this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
               } else {
                  KeyBinding.click(InputMappings.Type.MOUSE.getOrCreate(i));
               }
            }
         }
         net.minecraftforge.client.ForgeHooksClient.fireMouseInput(p_198023_3_, p_198023_4_, p_198023_5_);
      }
   }

   private void onScroll(long p_198020_1_, double p_198020_3_, double p_198020_5_) {
      if (p_198020_1_ == Minecraft.getInstance().getWindow().getWindow()) {
         double d0 = (this.minecraft.options.discreteMouseScroll ? Math.signum(p_198020_5_) : p_198020_5_) * this.minecraft.options.mouseWheelSensitivity;
         if (this.minecraft.overlay == null) {
            if (this.minecraft.screen != null) {
               double d1 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double d2 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               if (net.minecraftforge.client.ForgeHooksClient.onGuiMouseScrollPre(this, this.minecraft.screen, d0)) return;
               if (this.minecraft.screen.mouseScrolled(d1, d2, d0)) return;
               net.minecraftforge.client.ForgeHooksClient.onGuiMouseScrollPost(this, this.minecraft.screen, d0);
            } else if (this.minecraft.player != null) {
               if (this.accumulatedScroll != 0.0D && Math.signum(d0) != Math.signum(this.accumulatedScroll)) {
                  this.accumulatedScroll = 0.0D;
               }

               this.accumulatedScroll += d0;
               float f1 = (float)((int)this.accumulatedScroll);
               if (f1 == 0.0F) {
                  return;
               }

               this.accumulatedScroll -= (double)f1;
               if (net.minecraftforge.client.ForgeHooksClient.onMouseScroll(this, d0)) return;
               if (this.minecraft.player.isSpectator()) {
                  if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                     this.minecraft.gui.getSpectatorGui().onMouseScrolled((double)(-f1));
                  } else {
                     float f = MathHelper.clamp(this.minecraft.player.abilities.getFlyingSpeed() + f1 * 0.005F, 0.0F, 0.2F);
                     this.minecraft.player.abilities.setFlyingSpeed(f);
                  }
               } else {
                  this.minecraft.player.inventory.swapPaint((double)f1);
               }
            }
         }
      }

   }

   private void onDrop(long p_238228_1_, List<Path> p_238228_3_) {
      if (this.minecraft.screen != null) {
         this.minecraft.screen.onFilesDrop(p_238228_3_);
      }

   }

   public void setup(long p_198029_1_) {
      InputMappings.setupMouseCallbacks(p_198029_1_, (p_228032_1_, p_228032_3_, p_228032_5_) -> {
         this.minecraft.execute(() -> {
            this.onMove(p_228032_1_, p_228032_3_, p_228032_5_);
         });
      }, (p_228028_1_, p_228028_3_, p_228028_4_, p_228028_5_) -> {
         this.minecraft.execute(() -> {
            this.onPress(p_228028_1_, p_228028_3_, p_228028_4_, p_228028_5_);
         });
      }, (p_228029_1_, p_228029_3_, p_228029_5_) -> {
         this.minecraft.execute(() -> {
            this.onScroll(p_228029_1_, p_228029_3_, p_228029_5_);
         });
      }, (p_238227_1_, p_238227_3_, p_238227_4_) -> {
         Path[] apath = new Path[p_238227_3_];

         for(int i = 0; i < p_238227_3_; ++i) {
            apath[i] = Paths.get(GLFWDropCallback.getName(p_238227_4_, i));
         }

         this.minecraft.execute(() -> {
            this.onDrop(p_238227_1_, Arrays.asList(apath));
         });
      });
   }

   private void onMove(long p_198022_1_, double p_198022_3_, double p_198022_5_) {
      if (p_198022_1_ == Minecraft.getInstance().getWindow().getWindow()) {
         if (this.ignoreFirstMove) {
            this.xpos = p_198022_3_;
            this.ypos = p_198022_5_;
            this.ignoreFirstMove = false;
         }

         IGuiEventListener iguieventlistener = this.minecraft.screen;
         if (iguieventlistener != null && this.minecraft.overlay == null) {
            double d0 = p_198022_3_ * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double d1 = p_198022_5_ * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
            Screen.wrapScreenError(() -> {
               iguieventlistener.mouseMoved(d0, d1);
            }, "mouseMoved event handler", iguieventlistener.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.mousePressedTime > 0.0D) {
               double d2 = (p_198022_3_ - this.xpos) * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double d3 = (p_198022_5_ - this.ypos) * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               Screen.wrapScreenError(() -> {
                  if (net.minecraftforge.client.ForgeHooksClient.onGuiMouseDragPre(this.minecraft.screen, d0, d1, this.activeButton, d2, d3)) return;
                  if (iguieventlistener.mouseDragged(d0, d1, this.activeButton, d2, d3)) return;
                  net.minecraftforge.client.ForgeHooksClient.onGuiMouseDragPost(this.minecraft.screen, d0, d1, this.activeButton, d2, d3);
               }, "mouseDragged event handler", iguieventlistener.getClass().getCanonicalName());
            }
         }

         this.minecraft.getProfiler().push("mouse");
         if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            this.accumulatedDX += p_198022_3_ - this.xpos;
            this.accumulatedDY += p_198022_5_ - this.ypos;
         }

         this.turnPlayer();
         this.xpos = p_198022_3_;
         this.ypos = p_198022_5_;
         this.minecraft.getProfiler().pop();
      }
   }

   public void turnPlayer() {
      double d0 = NativeUtil.getTime();
      double d1 = d0 - this.lastMouseEventTime;
      this.lastMouseEventTime = d0;
      if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
         double d4 = this.minecraft.options.sensitivity * (double)0.6F + (double)0.2F;
         double d5 = d4 * d4 * d4 * 8.0D;
         double d2;
         double d3;
         if (this.minecraft.options.smoothCamera) {
            double d6 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d5, d1 * d5);
            double d7 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d5, d1 * d5);
            d2 = d6;
            d3 = d7;
         } else {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            d2 = this.accumulatedDX * d5;
            d3 = this.accumulatedDY * d5;
         }

         this.accumulatedDX = 0.0D;
         this.accumulatedDY = 0.0D;
         int i = 1;
         if (this.minecraft.options.invertYMouse) {
            i = -1;
         }

         this.minecraft.getTutorial().onMouse(d2, d3);
         if (this.minecraft.player != null) {
            this.minecraft.player.turn(d2, d3 * (double)i);
         }

      } else {
         this.accumulatedDX = 0.0D;
         this.accumulatedDY = 0.0D;
      }
   }

   public boolean isLeftPressed() {
      return this.isLeftPressed;
   }

   public boolean isRightPressed() {
      return this.isRightPressed;
   }

   public boolean isMiddleDown() {
      return this.isMiddlePressed;
   }

   public double xpos() {
      return this.xpos;
   }

   public double ypos() {
      return this.ypos;
   }

   public double getXVelocity() {
      return this.accumulatedDX;
   }

   public double getYVelocity() {
      return this.accumulatedDY;
   }

   public void setIgnoreFirstMove() {
      this.ignoreFirstMove = true;
   }

   public boolean isMouseGrabbed() {
      return this.mouseGrabbed;
   }

   public void grabMouse() {
      if (this.minecraft.isWindowActive()) {
         if (!this.mouseGrabbed) {
            if (!Minecraft.ON_OSX) {
               KeyBinding.setAll();
            }

            this.mouseGrabbed = true;
            this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
            this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
            InputMappings.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
            this.minecraft.setScreen((Screen)null);
            this.minecraft.missTime = 10000;
            this.ignoreFirstMove = true;
         }
      }
   }

   public void releaseMouse() {
      if (this.mouseGrabbed) {
         this.mouseGrabbed = false;
         this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
         this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
         InputMappings.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
      }
   }

   public void cursorEntered() {
      this.ignoreFirstMove = true;
   }
}
