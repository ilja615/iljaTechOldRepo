package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements IChatListener {
   public static final ITextComponent NO_TITLE = StringTextComponent.EMPTY;
   private static final Logger LOGGER = LogManager.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public void handle(ChatType p_192576_1_, ITextComponent p_192576_2_, UUID p_192576_3_) {
      NarratorStatus narratorstatus = getStatus();
      if (narratorstatus != NarratorStatus.OFF && this.narrator.active()) {
         if (narratorstatus == NarratorStatus.ALL || narratorstatus == NarratorStatus.CHAT && p_192576_1_ == ChatType.CHAT || narratorstatus == NarratorStatus.SYSTEM && p_192576_1_ == ChatType.SYSTEM) {
            ITextComponent itextcomponent;
            if (p_192576_2_ instanceof TranslationTextComponent && "chat.type.text".equals(((TranslationTextComponent)p_192576_2_).getKey())) {
               itextcomponent = new TranslationTextComponent("chat.type.text.narrate", ((TranslationTextComponent)p_192576_2_).getArgs());
            } else {
               itextcomponent = p_192576_2_;
            }

            this.doSay(p_192576_1_.shouldInterrupt(), itextcomponent.getString());
         }

      }
   }

   public void sayNow(String p_216864_1_) {
      NarratorStatus narratorstatus = getStatus();
      if (this.narrator.active() && narratorstatus != NarratorStatus.OFF && narratorstatus != NarratorStatus.CHAT && !p_216864_1_.isEmpty()) {
         this.narrator.clear();
         this.doSay(true, p_216864_1_);
      }

   }

   private static NarratorStatus getStatus() {
      return Minecraft.getInstance().options.narratorStatus;
   }

   private void doSay(boolean p_216866_1_, String p_216866_2_) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.debug("Narrating: {}", (Object)p_216866_2_.replaceAll("\n", "\\\\n"));
      }

      this.narrator.say(p_216866_2_, p_216866_1_);
   }

   public void updateNarratorStatus(NarratorStatus p_216865_1_) {
      this.clear();
      this.narrator.say((new TranslationTextComponent("options.narrator")).append(" : ").append(p_216865_1_.getName()).getString(), true);
      ToastGui toastgui = Minecraft.getInstance().getToasts();
      if (this.narrator.active()) {
         if (p_216865_1_ == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(toastgui, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.disabled"), (ITextComponent)null);
         } else {
            SystemToast.addOrUpdate(toastgui, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.enabled"), p_216865_1_.getName());
         }
      } else {
         SystemToast.addOrUpdate(toastgui, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.disabled"), new TranslationTextComponent("options.narrator.notavailable"));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if (getStatus() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void destroy() {
      this.narrator.destroy();
   }
}
