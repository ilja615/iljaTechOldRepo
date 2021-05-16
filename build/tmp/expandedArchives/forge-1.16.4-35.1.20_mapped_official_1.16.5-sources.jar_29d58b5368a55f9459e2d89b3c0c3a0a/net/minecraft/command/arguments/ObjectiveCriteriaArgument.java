package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ObjectiveCriteriaArgument implements ArgumentType<ScoreCriteria> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((p_208672_0_) -> {
      return new TranslationTextComponent("argument.criteria.invalid", p_208672_0_);
   });

   private ObjectiveCriteriaArgument() {
   }

   public static ObjectiveCriteriaArgument criteria() {
      return new ObjectiveCriteriaArgument();
   }

   public static ScoreCriteria getCriteria(CommandContext<CommandSource> p_197161_0_, String p_197161_1_) {
      return p_197161_0_.getArgument(p_197161_1_, ScoreCriteria.class);
   }

   public ScoreCriteria parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         p_parse_1_.skip();
      }

      String s = p_parse_1_.getString().substring(i, p_parse_1_.getCursor());
      return ScoreCriteria.byName(s).orElseThrow(() -> {
         p_parse_1_.setCursor(i);
         return ERROR_INVALID_VALUE.create(s);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      List<String> list = Lists.newArrayList(ScoreCriteria.CRITERIA_BY_NAME.keySet());

      for(StatType<?> stattype : Registry.STAT_TYPE) {
         for(Object object : stattype.getRegistry()) {
            String s = this.getName(stattype, object);
            list.add(s);
         }
      }

      return ISuggestionProvider.suggest(list, p_listSuggestions_2_);
   }

   public <T> String getName(StatType<T> p_199815_1_, Object p_199815_2_) {
      return Stat.buildName(p_199815_1_, (T)p_199815_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
