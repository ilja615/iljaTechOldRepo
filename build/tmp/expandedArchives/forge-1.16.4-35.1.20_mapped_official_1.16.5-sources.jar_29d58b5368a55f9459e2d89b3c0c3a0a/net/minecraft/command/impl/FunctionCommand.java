package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.util.text.TranslationTextComponent;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSource> SUGGEST_FUNCTION = (p_198477_0_, p_198477_1_) -> {
      FunctionManager functionmanager = p_198477_0_.getSource().getServer().getFunctions();
      ISuggestionProvider.suggestResource(functionmanager.getTagNames(), p_198477_1_, "#");
      return ISuggestionProvider.suggestResource(functionmanager.getFunctionNames(), p_198477_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198476_0_) {
      p_198476_0_.register(Commands.literal("function").requires((p_198480_0_) -> {
         return p_198480_0_.hasPermission(2);
      }).then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes((p_198479_0_) -> {
         return runFunction(p_198479_0_.getSource(), FunctionArgument.getFunctions(p_198479_0_, "name"));
      })));
   }

   private static int runFunction(CommandSource p_200025_0_, Collection<FunctionObject> p_200025_1_) {
      int i = 0;

      for(FunctionObject functionobject : p_200025_1_) {
         i += p_200025_0_.getServer().getFunctions().execute(functionobject, p_200025_0_.withSuppressedOutput().withMaximumPermission(2));
      }

      if (p_200025_1_.size() == 1) {
         p_200025_0_.sendSuccess(new TranslationTextComponent("commands.function.success.single", i, p_200025_1_.iterator().next().getId()), true);
      } else {
         p_200025_0_.sendSuccess(new TranslationTextComponent("commands.function.success.multiple", i, p_200025_1_.size()), true);
      }

      return i;
   }
}
