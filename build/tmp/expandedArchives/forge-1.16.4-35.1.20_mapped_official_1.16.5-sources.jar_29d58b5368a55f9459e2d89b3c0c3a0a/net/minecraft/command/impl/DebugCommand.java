package net.minecraft.command.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.alreadyRunning"));
   @Nullable
   private static final FileSystemProvider ZIP_FS_PROVIDER = FileSystemProvider.installedProviders().stream().filter((p_225386_0_) -> {
      return p_225386_0_.getScheme().equalsIgnoreCase("jar");
   }).findFirst().orElse((FileSystemProvider)null);

   public static void register(CommandDispatcher<CommandSource> p_198330_0_) {
      p_198330_0_.register(Commands.literal("debug").requires((p_198332_0_) -> {
         return p_198332_0_.hasPermission(3);
      }).then(Commands.literal("start").executes((p_198329_0_) -> {
         return start(p_198329_0_.getSource());
      })).then(Commands.literal("stop").executes((p_198333_0_) -> {
         return stop(p_198333_0_.getSource());
      })).then(Commands.literal("report").executes((p_225388_0_) -> {
         return report(p_225388_0_.getSource());
      })));
   }

   private static int start(CommandSource p_198335_0_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_198335_0_.getServer();
      if (minecraftserver.isProfiling()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         minecraftserver.startProfiling();
         p_198335_0_.sendSuccess(new TranslationTextComponent("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
         return 0;
      }
   }

   private static int stop(CommandSource p_198336_0_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_198336_0_.getServer();
      if (!minecraftserver.isProfiling()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         IProfileResult iprofileresult = minecraftserver.finishProfiling();
         File file1 = new File(minecraftserver.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         iprofileresult.saveResults(file1);
         float f = (float)iprofileresult.getNanoDuration() / 1.0E9F;
         float f1 = (float)iprofileresult.getTickDuration() / f;
         p_198336_0_.sendSuccess(new TranslationTextComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), iprofileresult.getTickDuration(), String.format("%.2f", f1)), true);
         return MathHelper.floor(f1);
      }
   }

   private static int report(CommandSource p_225389_0_) {
      MinecraftServer minecraftserver = p_225389_0_.getServer();
      String s = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

      try {
         Path path1 = minecraftserver.getFile("debug").toPath();
         Files.createDirectories(path1);
         if (!SharedConstants.IS_RUNNING_IN_IDE && ZIP_FS_PROVIDER != null) {
            Path path2 = path1.resolve(s + ".zip");

            try (FileSystem filesystem = ZIP_FS_PROVIDER.newFileSystem(path2, ImmutableMap.of("create", "true"))) {
               minecraftserver.saveDebugReport(filesystem.getPath("/"));
            }
         } else {
            Path path = path1.resolve(s);
            minecraftserver.saveDebugReport(path);
         }

         p_225389_0_.sendSuccess(new TranslationTextComponent("commands.debug.reportSaved", s), false);
         return 1;
      } catch (IOException ioexception) {
         LOGGER.error("Failed to save debug dump", (Throwable)ioexception);
         p_225389_0_.sendFailure(new TranslationTextComponent("commands.debug.reportFailed"));
         return 0;
      }
   }
}
