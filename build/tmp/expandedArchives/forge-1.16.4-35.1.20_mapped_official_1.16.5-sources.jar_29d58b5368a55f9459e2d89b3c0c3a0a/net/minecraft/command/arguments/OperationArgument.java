package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class OperationArgument implements ArgumentType<OperationArgument.IOperation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.operation.invalid"));
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.operation.div0"));

   public static OperationArgument operation() {
      return new OperationArgument();
   }

   public static OperationArgument.IOperation getOperation(CommandContext<CommandSource> p_197179_0_, String p_197179_1_) throws CommandSyntaxException {
      return p_197179_0_.getArgument(p_197179_1_, OperationArgument.IOperation.class);
   }

   public OperationArgument.IOperation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (!p_parse_1_.canRead()) {
         throw ERROR_INVALID_OPERATION.create();
      } else {
         int i = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         return getOperation(p_parse_1_.getString().substring(i, p_parse_1_.getCursor()));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static OperationArgument.IOperation getOperation(String p_197177_0_) throws CommandSyntaxException {
      return (p_197177_0_.equals("><") ? (p_197175_0_, p_197175_1_) -> {
         int i = p_197175_0_.getScore();
         p_197175_0_.setScore(p_197175_1_.getScore());
         p_197175_1_.setScore(i);
      } : getSimpleOperation(p_197177_0_));
   }

   private static OperationArgument.IIntOperation getSimpleOperation(String p_197182_0_) throws CommandSyntaxException {
      switch(p_197182_0_) {
      case "=":
         return (p_197174_0_, p_197174_1_) -> {
            return p_197174_1_;
         };
      case "+=":
         return (p_197176_0_, p_197176_1_) -> {
            return p_197176_0_ + p_197176_1_;
         };
      case "-=":
         return (p_197183_0_, p_197183_1_) -> {
            return p_197183_0_ - p_197183_1_;
         };
      case "*=":
         return (p_197173_0_, p_197173_1_) -> {
            return p_197173_0_ * p_197173_1_;
         };
      case "/=":
         return (p_197178_0_, p_197178_1_) -> {
            if (p_197178_1_ == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return MathHelper.intFloorDiv(p_197178_0_, p_197178_1_);
            }
         };
      case "%=":
         return (p_197181_0_, p_197181_1_) -> {
            if (p_197181_1_ == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return MathHelper.positiveModulo(p_197181_0_, p_197181_1_);
            }
         };
      case "<":
         return Math::min;
      case ">":
         return Math::max;
      default:
         throw ERROR_INVALID_OPERATION.create();
      }
   }

   @FunctionalInterface
   interface IIntOperation extends OperationArgument.IOperation {
      int apply(int p_apply_1_, int p_apply_2_) throws CommandSyntaxException;

      default void apply(Score p_apply_1_, Score p_apply_2_) throws CommandSyntaxException {
         p_apply_1_.setScore(this.apply(p_apply_1_.getScore(), p_apply_2_.getScore()));
      }
   }

   @FunctionalInterface
   public interface IOperation {
      void apply(Score p_apply_1_, Score p_apply_2_) throws CommandSyntaxException;
   }
}
