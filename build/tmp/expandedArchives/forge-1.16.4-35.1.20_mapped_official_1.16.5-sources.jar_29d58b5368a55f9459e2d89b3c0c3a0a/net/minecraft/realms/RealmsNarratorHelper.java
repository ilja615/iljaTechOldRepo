package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsNarratorHelper {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

   public static void now(String p_239550_0_) {
      NarratorChatListener narratorchatlistener = NarratorChatListener.INSTANCE;
      narratorchatlistener.clear();
      narratorchatlistener.handle(ChatType.SYSTEM, new StringTextComponent(fixNarrationNewlines(p_239550_0_)), Util.NIL_UUID);
   }

   private static String fixNarrationNewlines(String p_239554_0_) {
      return p_239554_0_.replace("\\n", System.lineSeparator());
   }

   public static void now(String... p_239551_0_) {
      now(Arrays.asList(p_239551_0_));
   }

   public static void now(Iterable<String> p_239549_0_) {
      now(join(p_239549_0_));
   }

   public static String join(Iterable<String> p_239552_0_) {
      return String.join(System.lineSeparator(), p_239552_0_);
   }

   public static void repeatedly(String p_239553_0_) {
      REPEATED_NARRATOR.narrate(fixNarrationNewlines(p_239553_0_));
   }
}
