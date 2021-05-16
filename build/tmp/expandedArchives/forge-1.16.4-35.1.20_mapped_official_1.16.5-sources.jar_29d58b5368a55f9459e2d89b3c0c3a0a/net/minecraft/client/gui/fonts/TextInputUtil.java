package net.minecraft.client.gui.fonts;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextInputUtil {
   private final Supplier<String> getMessageFn;
   private final Consumer<String> setMessageFn;
   private final Supplier<String> getClipboardFn;
   private final Consumer<String> setClipboardFn;
   private final Predicate<String> stringValidator;
   private int cursorPos;
   private int selectionPos;

   public TextInputUtil(Supplier<String> p_i232265_1_, Consumer<String> p_i232265_2_, Supplier<String> p_i232265_3_, Consumer<String> p_i232265_4_, Predicate<String> p_i232265_5_) {
      this.getMessageFn = p_i232265_1_;
      this.setMessageFn = p_i232265_2_;
      this.getClipboardFn = p_i232265_3_;
      this.setClipboardFn = p_i232265_4_;
      this.stringValidator = p_i232265_5_;
      this.setCursorToEnd();
   }

   public static Supplier<String> createClipboardGetter(Minecraft p_238570_0_) {
      return () -> {
         return getClipboardContents(p_238570_0_);
      };
   }

   public static String getClipboardContents(Minecraft p_238576_0_) {
      return TextFormatting.stripFormatting(p_238576_0_.keyboardHandler.getClipboard().replaceAll("\\r", ""));
   }

   public static Consumer<String> createClipboardSetter(Minecraft p_238582_0_) {
      return (p_238577_1_) -> {
         setClipboardContents(p_238582_0_, p_238577_1_);
      };
   }

   public static void setClipboardContents(Minecraft p_238571_0_, String p_238571_1_) {
      p_238571_0_.keyboardHandler.setClipboard(p_238571_1_);
   }

   public boolean charTyped(char p_216894_1_) {
      if (SharedConstants.isAllowedChatCharacter(p_216894_1_)) {
         this.insertText(this.getMessageFn.get(), Character.toString(p_216894_1_));
      }

      return true;
   }

   public boolean keyPressed(int p_216897_1_) {
      if (Screen.isSelectAll(p_216897_1_)) {
         this.selectAll();
         return true;
      } else if (Screen.isCopy(p_216897_1_)) {
         this.copy();
         return true;
      } else if (Screen.isPaste(p_216897_1_)) {
         this.paste();
         return true;
      } else if (Screen.isCut(p_216897_1_)) {
         this.cut();
         return true;
      } else if (p_216897_1_ == 259) {
         this.removeCharsFromCursor(-1);
         return true;
      } else {
         if (p_216897_1_ == 261) {
            this.removeCharsFromCursor(1);
         } else {
            if (p_216897_1_ == 263) {
               if (Screen.hasControlDown()) {
                  this.moveByWords(-1, Screen.hasShiftDown());
               } else {
                  this.moveByChars(-1, Screen.hasShiftDown());
               }

               return true;
            }

            if (p_216897_1_ == 262) {
               if (Screen.hasControlDown()) {
                  this.moveByWords(1, Screen.hasShiftDown());
               } else {
                  this.moveByChars(1, Screen.hasShiftDown());
               }

               return true;
            }

            if (p_216897_1_ == 268) {
               this.setCursorToStart(Screen.hasShiftDown());
               return true;
            }

            if (p_216897_1_ == 269) {
               this.setCursorToEnd(Screen.hasShiftDown());
               return true;
            }
         }

         return false;
      }
   }

   private int clampToMsgLength(int p_238589_1_) {
      return MathHelper.clamp(p_238589_1_, 0, this.getMessageFn.get().length());
   }

   private void insertText(String p_238572_1_, String p_238572_2_) {
      if (this.selectionPos != this.cursorPos) {
         p_238572_1_ = this.deleteSelection(p_238572_1_);
      }

      this.cursorPos = MathHelper.clamp(this.cursorPos, 0, p_238572_1_.length());
      String s = (new StringBuilder(p_238572_1_)).insert(this.cursorPos, p_238572_2_).toString();
      if (this.stringValidator.test(s)) {
         this.setMessageFn.accept(s);
         this.selectionPos = this.cursorPos = Math.min(s.length(), this.cursorPos + p_238572_2_.length());
      }

   }

   public void insertText(String p_216892_1_) {
      this.insertText(this.getMessageFn.get(), p_216892_1_);
   }

   private void resetSelectionIfNeeded(boolean p_238573_1_) {
      if (!p_238573_1_) {
         this.selectionPos = this.cursorPos;
      }

   }

   public void moveByChars(int p_238569_1_, boolean p_238569_2_) {
      this.cursorPos = Util.offsetByCodepoints(this.getMessageFn.get(), this.cursorPos, p_238569_1_);
      this.resetSelectionIfNeeded(p_238569_2_);
   }

   public void moveByWords(int p_238575_1_, boolean p_238575_2_) {
      this.cursorPos = CharacterManager.getWordPosition(this.getMessageFn.get(), p_238575_1_, this.cursorPos, true);
      this.resetSelectionIfNeeded(p_238575_2_);
   }

   public void removeCharsFromCursor(int p_238586_1_) {
      String s = this.getMessageFn.get();
      if (!s.isEmpty()) {
         String s1;
         if (this.selectionPos != this.cursorPos) {
            s1 = this.deleteSelection(s);
         } else {
            int i = Util.offsetByCodepoints(s, this.cursorPos, p_238586_1_);
            int j = Math.min(i, this.cursorPos);
            int k = Math.max(i, this.cursorPos);
            s1 = (new StringBuilder(s)).delete(j, k).toString();
            if (p_238586_1_ < 0) {
               this.selectionPos = this.cursorPos = j;
            }
         }

         this.setMessageFn.accept(s1);
      }

   }

   public void cut() {
      String s = this.getMessageFn.get();
      this.setClipboardFn.accept(this.getSelected(s));
      this.setMessageFn.accept(this.deleteSelection(s));
   }

   public void paste() {
      this.insertText(this.getMessageFn.get(), this.getClipboardFn.get());
      this.selectionPos = this.cursorPos;
   }

   public void copy() {
      this.setClipboardFn.accept(this.getSelected(this.getMessageFn.get()));
   }

   public void selectAll() {
      this.selectionPos = 0;
      this.cursorPos = this.getMessageFn.get().length();
   }

   private String getSelected(String p_238578_1_) {
      int i = Math.min(this.cursorPos, this.selectionPos);
      int j = Math.max(this.cursorPos, this.selectionPos);
      return p_238578_1_.substring(i, j);
   }

   private String deleteSelection(String p_238583_1_) {
      if (this.selectionPos == this.cursorPos) {
         return p_238583_1_;
      } else {
         int i = Math.min(this.cursorPos, this.selectionPos);
         int j = Math.max(this.cursorPos, this.selectionPos);
         String s = p_238583_1_.substring(0, i) + p_238583_1_.substring(j);
         this.selectionPos = this.cursorPos = i;
         return s;
      }
   }

   private void setCursorToStart(boolean p_238579_1_) {
      this.cursorPos = 0;
      this.resetSelectionIfNeeded(p_238579_1_);
   }

   public void setCursorToEnd() {
      this.setCursorToEnd(false);
   }

   private void setCursorToEnd(boolean p_238584_1_) {
      this.cursorPos = this.getMessageFn.get().length();
      this.resetSelectionIfNeeded(p_238584_1_);
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public void setCursorPos(int p_238581_1_, boolean p_238581_2_) {
      this.cursorPos = this.clampToMsgLength(p_238581_1_);
      this.resetSelectionIfNeeded(p_238581_2_);
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }

   public void setSelectionRange(int p_238568_1_, int p_238568_2_) {
      int i = this.getMessageFn.get().length();
      this.cursorPos = MathHelper.clamp(p_238568_1_, 0, i);
      this.selectionPos = MathHelper.clamp(p_238568_2_, 0, i);
   }

   public boolean isSelecting() {
      return this.cursorPos != this.selectionPos;
   }
}
