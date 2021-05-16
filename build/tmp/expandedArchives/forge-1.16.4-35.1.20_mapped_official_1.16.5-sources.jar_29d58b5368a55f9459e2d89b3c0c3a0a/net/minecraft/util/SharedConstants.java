package net.minecraft.util;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.time.Duration;
import net.minecraft.command.TranslatableExceptionProvider;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION = Level.DISABLED;
   public static final long MAXIMUM_TICK_TIME_NANOS = Duration.ofMillis(300L).toNanos();
   public static boolean CHECK_DATA_FIXER_SCHEMA = true;
   public static boolean IS_RUNNING_IN_IDE;
   public static final char[] ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
   private static GameVersion CURRENT_VERSION;

   public static boolean isAllowedChatCharacter(char p_71566_0_) {
      return p_71566_0_ != 167 && p_71566_0_ >= ' ' && p_71566_0_ != 127;
   }

   public static String filterText(String p_71565_0_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(char c0 : p_71565_0_.toCharArray()) {
         if (isAllowedChatCharacter(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   public static GameVersion getCurrentVersion() {
      if (CURRENT_VERSION == null) {
         CURRENT_VERSION = MinecraftVersion.tryDetectVersion();
      }

      return CURRENT_VERSION;
   }

   public static int getProtocolVersion() {
      return 754;
   }

   static {
      if (System.getProperty("io.netty.leakDetection.level") == null) // Forge: allow level to be manually specified
      ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
   }
}
