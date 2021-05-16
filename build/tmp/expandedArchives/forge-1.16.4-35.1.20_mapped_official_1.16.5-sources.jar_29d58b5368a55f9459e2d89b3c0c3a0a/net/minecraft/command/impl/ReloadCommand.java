package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.IServerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadCommand {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void reloadPacks(Collection<String> p_241062_0_, CommandSource p_241062_1_) {
      p_241062_1_.getServer().reloadResources(p_241062_0_).exceptionally((p_241061_1_) -> {
         LOGGER.warn("Failed to execute reload", p_241061_1_);
         p_241062_1_.sendFailure(new TranslationTextComponent("commands.reload.failure"));
         return null;
      });
   }

   private static Collection<String> discoverNewPacks(ResourcePackList p_241058_0_, IServerConfiguration p_241058_1_, Collection<String> p_241058_2_) {
      p_241058_0_.reload();
      Collection<String> collection = Lists.newArrayList(p_241058_2_);
      Collection<String> collection1 = p_241058_1_.getDataPackConfig().getDisabled();

      for(String s : p_241058_0_.getAvailableIds()) {
         if (!collection1.contains(s) && !collection.contains(s)) {
            collection.add(s);
         }
      }

      return collection;
   }

   public static void register(CommandDispatcher<CommandSource> p_198597_0_) {
      p_198597_0_.register(Commands.literal("reload").requires((p_198599_0_) -> {
         return p_198599_0_.hasPermission(2);
      }).executes((p_198598_0_) -> {
         CommandSource commandsource = p_198598_0_.getSource();
         MinecraftServer minecraftserver = commandsource.getServer();
         ResourcePackList resourcepacklist = minecraftserver.getPackRepository();
         IServerConfiguration iserverconfiguration = minecraftserver.getWorldData();
         Collection<String> collection = resourcepacklist.getSelectedIds();
         Collection<String> collection1 = discoverNewPacks(resourcepacklist, iserverconfiguration, collection);
         commandsource.sendSuccess(new TranslationTextComponent("commands.reload.success"), true);
         reloadPacks(collection1, commandsource);
         return 0;
      }));
   }
}
