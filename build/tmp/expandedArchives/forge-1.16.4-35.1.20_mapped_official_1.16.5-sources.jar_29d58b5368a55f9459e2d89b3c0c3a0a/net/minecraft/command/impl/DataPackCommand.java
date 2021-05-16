package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class DataPackCommand {
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((p_208808_0_) -> {
      return new TranslationTextComponent("commands.datapack.unknown", p_208808_0_);
   });
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((p_208818_0_) -> {
      return new TranslationTextComponent("commands.datapack.enable.failed", p_208818_0_);
   });
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((p_208815_0_) -> {
      return new TranslationTextComponent("commands.datapack.disable.failed", p_208815_0_);
   });
   private static final SuggestionProvider<CommandSource> SELECTED_PACKS = (p_198305_0_, p_198305_1_) -> {
      return ISuggestionProvider.suggest(p_198305_0_.getSource().getServer().getPackRepository().getSelectedIds().stream().map(StringArgumentType::escapeIfRequired), p_198305_1_);
   };
   private static final SuggestionProvider<CommandSource> UNSELECTED_PACKS = (p_241030_0_, p_241030_1_) -> {
      ResourcePackList resourcepacklist = p_241030_0_.getSource().getServer().getPackRepository();
      Collection<String> collection = resourcepacklist.getSelectedIds();
      return ISuggestionProvider.suggest(resourcepacklist.getAvailableIds().stream().filter((p_241033_1_) -> {
         return !collection.contains(p_241033_1_);
      }).map(StringArgumentType::escapeIfRequired), p_241030_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198299_0_) {
      p_198299_0_.register(Commands.literal("datapack").requires((p_198301_0_) -> {
         return p_198301_0_.hasPermission(2);
      }).then(Commands.literal("enable").then(Commands.argument("name", StringArgumentType.string()).suggests(UNSELECTED_PACKS).executes((p_198292_0_) -> {
         return enablePack(p_198292_0_.getSource(), getPack(p_198292_0_, "name", true), (p_198289_0_, p_198289_1_) -> {
            p_198289_1_.getDefaultPosition().insert(p_198289_0_, p_198289_1_, (p_198304_0_) -> {
               return p_198304_0_;
            }, false);
         });
      }).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((p_198307_0_) -> {
         return enablePack(p_198307_0_.getSource(), getPack(p_198307_0_, "name", true), (p_198308_1_, p_198308_2_) -> {
            p_198308_1_.add(p_198308_1_.indexOf(getPack(p_198307_0_, "existing", false)) + 1, p_198308_2_);
         });
      }))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((p_198311_0_) -> {
         return enablePack(p_198311_0_.getSource(), getPack(p_198311_0_, "name", true), (p_198302_1_, p_198302_2_) -> {
            p_198302_1_.add(p_198302_1_.indexOf(getPack(p_198311_0_, "existing", false)), p_198302_2_);
         });
      }))).then(Commands.literal("last").executes((p_198298_0_) -> {
         return enablePack(p_198298_0_.getSource(), getPack(p_198298_0_, "name", true), List::add);
      })).then(Commands.literal("first").executes((p_198300_0_) -> {
         return enablePack(p_198300_0_.getSource(), getPack(p_198300_0_, "name", true), (p_241034_0_, p_241034_1_) -> {
            p_241034_0_.add(0, p_241034_1_);
         });
      })))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((p_198295_0_) -> {
         return disablePack(p_198295_0_.getSource(), getPack(p_198295_0_, "name", false));
      }))).then(Commands.literal("list").executes((p_198290_0_) -> {
         return listPacks(p_198290_0_.getSource());
      }).then(Commands.literal("available").executes((p_198288_0_) -> {
         return listAvailablePacks(p_198288_0_.getSource());
      })).then(Commands.literal("enabled").executes((p_198309_0_) -> {
         return listEnabledPacks(p_198309_0_.getSource());
      }))));
   }

   private static int enablePack(CommandSource p_198297_0_, ResourcePackInfo p_198297_1_, DataPackCommand.IHandler p_198297_2_) throws CommandSyntaxException {
      ResourcePackList resourcepacklist = p_198297_0_.getServer().getPackRepository();
      List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getSelectedPacks());
      p_198297_2_.apply(list, p_198297_1_);
      p_198297_0_.sendSuccess(new TranslationTextComponent("commands.datapack.modify.enable", p_198297_1_.getChatLink(true)), true);
      ReloadCommand.reloadPacks(list.stream().map(ResourcePackInfo::getId).collect(Collectors.toList()), p_198297_0_);
      return list.size();
   }

   private static int disablePack(CommandSource p_198312_0_, ResourcePackInfo p_198312_1_) {
      ResourcePackList resourcepacklist = p_198312_0_.getServer().getPackRepository();
      List<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getSelectedPacks());
      list.remove(p_198312_1_);
      p_198312_0_.sendSuccess(new TranslationTextComponent("commands.datapack.modify.disable", p_198312_1_.getChatLink(true)), true);
      ReloadCommand.reloadPacks(list.stream().map(ResourcePackInfo::getId).collect(Collectors.toList()), p_198312_0_);
      return list.size();
   }

   private static int listPacks(CommandSource p_198313_0_) {
      return listEnabledPacks(p_198313_0_) + listAvailablePacks(p_198313_0_);
   }

   private static int listAvailablePacks(CommandSource p_198314_0_) {
      ResourcePackList resourcepacklist = p_198314_0_.getServer().getPackRepository();
      resourcepacklist.reload();
      Collection<? extends ResourcePackInfo> collection = resourcepacklist.getSelectedPacks();
      Collection<? extends ResourcePackInfo> collection1 = resourcepacklist.getAvailablePacks();
      List<ResourcePackInfo> list = collection1.stream().filter((p_241032_1_) -> {
         return !collection.contains(p_241032_1_);
      }).collect(Collectors.toList());
      if (list.isEmpty()) {
         p_198314_0_.sendSuccess(new TranslationTextComponent("commands.datapack.list.available.none"), false);
      } else {
         p_198314_0_.sendSuccess(new TranslationTextComponent("commands.datapack.list.available.success", list.size(), TextComponentUtils.formatList(list, (p_198293_0_) -> {
            return p_198293_0_.getChatLink(false);
         })), false);
      }

      return list.size();
   }

   private static int listEnabledPacks(CommandSource p_198315_0_) {
      ResourcePackList resourcepacklist = p_198315_0_.getServer().getPackRepository();
      resourcepacklist.reload();
      Collection<? extends ResourcePackInfo> collection = resourcepacklist.getSelectedPacks();
      if (collection.isEmpty()) {
         p_198315_0_.sendSuccess(new TranslationTextComponent("commands.datapack.list.enabled.none"), false);
      } else {
         p_198315_0_.sendSuccess(new TranslationTextComponent("commands.datapack.list.enabled.success", collection.size(), TextComponentUtils.formatList(collection, (p_198306_0_) -> {
            return p_198306_0_.getChatLink(true);
         })), false);
      }

      return collection.size();
   }

   private static ResourcePackInfo getPack(CommandContext<CommandSource> p_198303_0_, String p_198303_1_, boolean p_198303_2_) throws CommandSyntaxException {
      String s = StringArgumentType.getString(p_198303_0_, p_198303_1_);
      ResourcePackList resourcepacklist = p_198303_0_.getSource().getServer().getPackRepository();
      ResourcePackInfo resourcepackinfo = resourcepacklist.getPack(s);
      if (resourcepackinfo == null) {
         throw ERROR_UNKNOWN_PACK.create(s);
      } else {
         boolean flag = resourcepacklist.getSelectedPacks().contains(resourcepackinfo);
         if (p_198303_2_ && flag) {
            throw ERROR_PACK_ALREADY_ENABLED.create(s);
         } else if (!p_198303_2_ && !flag) {
            throw ERROR_PACK_ALREADY_DISABLED.create(s);
         } else {
            return resourcepackinfo;
         }
      }
   }

   interface IHandler {
      void apply(List<ResourcePackInfo> p_apply_1_, ResourcePackInfo p_apply_2_) throws CommandSyntaxException;
   }
}
