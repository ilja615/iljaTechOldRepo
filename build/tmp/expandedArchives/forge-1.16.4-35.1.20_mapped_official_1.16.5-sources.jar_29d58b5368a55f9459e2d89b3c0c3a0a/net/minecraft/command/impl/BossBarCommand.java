package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.server.CustomServerBossInfoManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;

public class BossBarCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((p_208783_0_) -> {
      return new TranslationTextComponent("commands.bossbar.create.failed", p_208783_0_);
   });
   private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((p_208782_0_) -> {
      return new TranslationTextComponent("commands.bossbar.unknown", p_208782_0_);
   });
   private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.players.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.name.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.color.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.style.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.value.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.max.unchanged"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.visibility.unchanged.hidden"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.visibility.unchanged.visible"));
   public static final SuggestionProvider<CommandSource> SUGGEST_BOSS_BAR = (p_201404_0_, p_201404_1_) -> {
      return ISuggestionProvider.suggestResource(p_201404_0_.getSource().getServer().getCustomBossEvents().getIds(), p_201404_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_201413_0_) {
      p_201413_0_.register(Commands.literal("bossbar").requires((p_201423_0_) -> {
         return p_201423_0_.hasPermission(2);
      }).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(Commands.argument("name", ComponentArgument.textComponent()).executes((p_201426_0_) -> {
         return createBar(p_201426_0_.getSource(), ResourceLocationArgument.getId(p_201426_0_, "id"), ComponentArgument.getComponent(p_201426_0_, "name"));
      })))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes((p_201429_0_) -> {
         return removeBar(p_201429_0_.getSource(), getBossBar(p_201429_0_));
      }))).then(Commands.literal("list").executes((p_201396_0_) -> {
         return listBars(p_201396_0_.getSource());
      })).then(Commands.literal("set").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.textComponent()).executes((p_201401_0_) -> {
         return setName(p_201401_0_.getSource(), getBossBar(p_201401_0_), ComponentArgument.getComponent(p_201401_0_, "name"));
      }))).then(Commands.literal("color").then(Commands.literal("pink").executes((p_201409_0_) -> {
         return setColor(p_201409_0_.getSource(), getBossBar(p_201409_0_), BossInfo.Color.PINK);
      })).then(Commands.literal("blue").executes((p_201422_0_) -> {
         return setColor(p_201422_0_.getSource(), getBossBar(p_201422_0_), BossInfo.Color.BLUE);
      })).then(Commands.literal("red").executes((p_201417_0_) -> {
         return setColor(p_201417_0_.getSource(), getBossBar(p_201417_0_), BossInfo.Color.RED);
      })).then(Commands.literal("green").executes((p_201424_0_) -> {
         return setColor(p_201424_0_.getSource(), getBossBar(p_201424_0_), BossInfo.Color.GREEN);
      })).then(Commands.literal("yellow").executes((p_201393_0_) -> {
         return setColor(p_201393_0_.getSource(), getBossBar(p_201393_0_), BossInfo.Color.YELLOW);
      })).then(Commands.literal("purple").executes((p_201391_0_) -> {
         return setColor(p_201391_0_.getSource(), getBossBar(p_201391_0_), BossInfo.Color.PURPLE);
      })).then(Commands.literal("white").executes((p_201406_0_) -> {
         return setColor(p_201406_0_.getSource(), getBossBar(p_201406_0_), BossInfo.Color.WHITE);
      }))).then(Commands.literal("style").then(Commands.literal("progress").executes((p_201399_0_) -> {
         return setStyle(p_201399_0_.getSource(), getBossBar(p_201399_0_), BossInfo.Overlay.PROGRESS);
      })).then(Commands.literal("notched_6").executes((p_201419_0_) -> {
         return setStyle(p_201419_0_.getSource(), getBossBar(p_201419_0_), BossInfo.Overlay.NOTCHED_6);
      })).then(Commands.literal("notched_10").executes((p_201412_0_) -> {
         return setStyle(p_201412_0_.getSource(), getBossBar(p_201412_0_), BossInfo.Overlay.NOTCHED_10);
      })).then(Commands.literal("notched_12").executes((p_201421_0_) -> {
         return setStyle(p_201421_0_.getSource(), getBossBar(p_201421_0_), BossInfo.Overlay.NOTCHED_12);
      })).then(Commands.literal("notched_20").executes((p_201403_0_) -> {
         return setStyle(p_201403_0_.getSource(), getBossBar(p_201403_0_), BossInfo.Overlay.NOTCHED_20);
      }))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((p_201408_0_) -> {
         return setValue(p_201408_0_.getSource(), getBossBar(p_201408_0_), IntegerArgumentType.getInteger(p_201408_0_, "value"));
      }))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((p_201395_0_) -> {
         return setMax(p_201395_0_.getSource(), getBossBar(p_201395_0_), IntegerArgumentType.getInteger(p_201395_0_, "max"));
      }))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes((p_201427_0_) -> {
         return setVisible(p_201427_0_.getSource(), getBossBar(p_201427_0_), BoolArgumentType.getBool(p_201427_0_, "visible"));
      }))).then(Commands.literal("players").executes((p_201430_0_) -> {
         return setPlayers(p_201430_0_.getSource(), getBossBar(p_201430_0_), Collections.emptyList());
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_201411_0_) -> {
         return setPlayers(p_201411_0_.getSource(), getBossBar(p_201411_0_), EntityArgument.getOptionalPlayers(p_201411_0_, "targets"));
      }))))).then(Commands.literal("get").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("value").executes((p_201418_0_) -> {
         return getValue(p_201418_0_.getSource(), getBossBar(p_201418_0_));
      })).then(Commands.literal("max").executes((p_201398_0_) -> {
         return getMax(p_201398_0_.getSource(), getBossBar(p_201398_0_));
      })).then(Commands.literal("visible").executes((p_201392_0_) -> {
         return getVisible(p_201392_0_.getSource(), getBossBar(p_201392_0_));
      })).then(Commands.literal("players").executes((p_201388_0_) -> {
         return getPlayers(p_201388_0_.getSource(), getBossBar(p_201388_0_));
      })))));
   }

   private static int getValue(CommandSource p_201414_0_, CustomServerBossInfo p_201414_1_) {
      p_201414_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.get.value", p_201414_1_.getDisplayName(), p_201414_1_.getValue()), true);
      return p_201414_1_.getValue();
   }

   private static int getMax(CommandSource p_201402_0_, CustomServerBossInfo p_201402_1_) {
      p_201402_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.get.max", p_201402_1_.getDisplayName(), p_201402_1_.getMax()), true);
      return p_201402_1_.getMax();
   }

   private static int getVisible(CommandSource p_201389_0_, CustomServerBossInfo p_201389_1_) {
      if (p_201389_1_.isVisible()) {
         p_201389_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.get.visible.visible", p_201389_1_.getDisplayName()), true);
         return 1;
      } else {
         p_201389_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.get.visible.hidden", p_201389_1_.getDisplayName()), true);
         return 0;
      }
   }

   private static int getPlayers(CommandSource p_201425_0_, CustomServerBossInfo p_201425_1_) {
      if (p_201425_1_.getPlayers().isEmpty()) {
         p_201425_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.get.players.none", p_201425_1_.getDisplayName()), true);
      } else {
         p_201425_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.get.players.some", p_201425_1_.getDisplayName(), p_201425_1_.getPlayers().size(), TextComponentUtils.formatList(p_201425_1_.getPlayers(), PlayerEntity::getDisplayName)), true);
      }

      return p_201425_1_.getPlayers().size();
   }

   private static int setVisible(CommandSource p_201410_0_, CustomServerBossInfo p_201410_1_, boolean p_201410_2_) throws CommandSyntaxException {
      if (p_201410_1_.isVisible() == p_201410_2_) {
         if (p_201410_2_) {
            throw ERROR_ALREADY_VISIBLE.create();
         } else {
            throw ERROR_ALREADY_HIDDEN.create();
         }
      } else {
         p_201410_1_.setVisible(p_201410_2_);
         if (p_201410_2_) {
            p_201410_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.visible.success.visible", p_201410_1_.getDisplayName()), true);
         } else {
            p_201410_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.visible.success.hidden", p_201410_1_.getDisplayName()), true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSource p_201397_0_, CustomServerBossInfo p_201397_1_, int p_201397_2_) throws CommandSyntaxException {
      if (p_201397_1_.getValue() == p_201397_2_) {
         throw ERROR_NO_VALUE_CHANGE.create();
      } else {
         p_201397_1_.setValue(p_201397_2_);
         p_201397_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.value.success", p_201397_1_.getDisplayName(), p_201397_2_), true);
         return p_201397_2_;
      }
   }

   private static int setMax(CommandSource p_201394_0_, CustomServerBossInfo p_201394_1_, int p_201394_2_) throws CommandSyntaxException {
      if (p_201394_1_.getMax() == p_201394_2_) {
         throw ERROR_NO_MAX_CHANGE.create();
      } else {
         p_201394_1_.setMax(p_201394_2_);
         p_201394_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.max.success", p_201394_1_.getDisplayName(), p_201394_2_), true);
         return p_201394_2_;
      }
   }

   private static int setColor(CommandSource p_201415_0_, CustomServerBossInfo p_201415_1_, BossInfo.Color p_201415_2_) throws CommandSyntaxException {
      if (p_201415_1_.getColor().equals(p_201415_2_)) {
         throw ERROR_NO_COLOR_CHANGE.create();
      } else {
         p_201415_1_.setColor(p_201415_2_);
         p_201415_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.color.success", p_201415_1_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setStyle(CommandSource p_201390_0_, CustomServerBossInfo p_201390_1_, BossInfo.Overlay p_201390_2_) throws CommandSyntaxException {
      if (p_201390_1_.getOverlay().equals(p_201390_2_)) {
         throw ERROR_NO_STYLE_CHANGE.create();
      } else {
         p_201390_1_.setOverlay(p_201390_2_);
         p_201390_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.style.success", p_201390_1_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setName(CommandSource p_201420_0_, CustomServerBossInfo p_201420_1_, ITextComponent p_201420_2_) throws CommandSyntaxException {
      ITextComponent itextcomponent = TextComponentUtils.updateForEntity(p_201420_0_, p_201420_2_, (Entity)null, 0);
      if (p_201420_1_.getName().equals(itextcomponent)) {
         throw ERROR_NO_NAME_CHANGE.create();
      } else {
         p_201420_1_.setName(itextcomponent);
         p_201420_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.name.success", p_201420_1_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setPlayers(CommandSource p_201405_0_, CustomServerBossInfo p_201405_1_, Collection<ServerPlayerEntity> p_201405_2_) throws CommandSyntaxException {
      boolean flag = p_201405_1_.setPlayers(p_201405_2_);
      if (!flag) {
         throw ERROR_NO_PLAYER_CHANGE.create();
      } else {
         if (p_201405_1_.getPlayers().isEmpty()) {
            p_201405_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.players.success.none", p_201405_1_.getDisplayName()), true);
         } else {
            p_201405_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.set.players.success.some", p_201405_1_.getDisplayName(), p_201405_2_.size(), TextComponentUtils.formatList(p_201405_2_, PlayerEntity::getDisplayName)), true);
         }

         return p_201405_1_.getPlayers().size();
      }
   }

   private static int listBars(CommandSource p_201428_0_) {
      Collection<CustomServerBossInfo> collection = p_201428_0_.getServer().getCustomBossEvents().getEvents();
      if (collection.isEmpty()) {
         p_201428_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.list.bars.none"), false);
      } else {
         p_201428_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.list.bars.some", collection.size(), TextComponentUtils.formatList(collection, CustomServerBossInfo::getDisplayName)), false);
      }

      return collection.size();
   }

   private static int createBar(CommandSource p_201400_0_, ResourceLocation p_201400_1_, ITextComponent p_201400_2_) throws CommandSyntaxException {
      CustomServerBossInfoManager customserverbossinfomanager = p_201400_0_.getServer().getCustomBossEvents();
      if (customserverbossinfomanager.get(p_201400_1_) != null) {
         throw ERROR_ALREADY_EXISTS.create(p_201400_1_.toString());
      } else {
         CustomServerBossInfo customserverbossinfo = customserverbossinfomanager.create(p_201400_1_, TextComponentUtils.updateForEntity(p_201400_0_, p_201400_2_, (Entity)null, 0));
         p_201400_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.create.success", customserverbossinfo.getDisplayName()), true);
         return customserverbossinfomanager.getEvents().size();
      }
   }

   private static int removeBar(CommandSource p_201407_0_, CustomServerBossInfo p_201407_1_) {
      CustomServerBossInfoManager customserverbossinfomanager = p_201407_0_.getServer().getCustomBossEvents();
      p_201407_1_.removeAllPlayers();
      customserverbossinfomanager.remove(p_201407_1_);
      p_201407_0_.sendSuccess(new TranslationTextComponent("commands.bossbar.remove.success", p_201407_1_.getDisplayName()), true);
      return customserverbossinfomanager.getEvents().size();
   }

   public static CustomServerBossInfo getBossBar(CommandContext<CommandSource> p_201416_0_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocationArgument.getId(p_201416_0_, "id");
      CustomServerBossInfo customserverbossinfo = p_201416_0_.getSource().getServer().getCustomBossEvents().get(resourcelocation);
      if (customserverbossinfo == null) {
         throw ERROR_DOESNT_EXIST.create(resourcelocation.toString());
      } else {
         return customserverbossinfo;
      }
   }
}
