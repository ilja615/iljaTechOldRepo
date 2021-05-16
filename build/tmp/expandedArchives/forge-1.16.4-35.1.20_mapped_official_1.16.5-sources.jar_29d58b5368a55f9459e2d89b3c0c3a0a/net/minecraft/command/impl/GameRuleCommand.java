package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher<CommandSource> p_198487_0_) {
      final LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamerule").requires((p_198491_0_) -> {
         return p_198491_0_.hasPermission(2);
      });
      GameRules.visitGameRuleTypes(new GameRules.IRuleEntryVisitor() {
         public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_) {
            literalargumentbuilder.then(Commands.literal(p_223481_1_.getId()).executes((p_223483_1_) -> {
               return GameRuleCommand.queryRule(p_223483_1_.getSource(), p_223481_1_);
            }).then(p_223481_2_.createArgument("value").executes((p_223482_1_) -> {
               return GameRuleCommand.setRule(p_223482_1_, p_223481_1_);
            })));
         }
      });
      p_198487_0_.register(literalargumentbuilder);
   }

   private static <T extends GameRules.RuleValue<T>> int setRule(CommandContext<CommandSource> p_223485_0_, GameRules.RuleKey<T> p_223485_1_) {
      CommandSource commandsource = p_223485_0_.getSource();
      T t = commandsource.getServer().getGameRules().getRule(p_223485_1_);
      t.setFromArgument(p_223485_0_, "value");
      commandsource.sendSuccess(new TranslationTextComponent("commands.gamerule.set", p_223485_1_.getId(), t.toString()), true);
      return t.getCommandResult();
   }

   private static <T extends GameRules.RuleValue<T>> int queryRule(CommandSource p_223486_0_, GameRules.RuleKey<T> p_223486_1_) {
      T t = p_223486_0_.getServer().getGameRules().getRule(p_223486_1_);
      p_223486_0_.sendSuccess(new TranslationTextComponent("commands.gamerule.query", p_223486_1_.getId(), t.toString()), false);
      return t.getCommandResult();
   }
}
