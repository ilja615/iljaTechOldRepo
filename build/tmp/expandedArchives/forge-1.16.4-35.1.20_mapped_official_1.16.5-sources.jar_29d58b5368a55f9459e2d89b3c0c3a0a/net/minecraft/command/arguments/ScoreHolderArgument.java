package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.INameProvider> {
   public static final SuggestionProvider<CommandSource> SUGGEST_SCORE_HOLDERS = (p_201323_0_, p_201323_1_) -> {
      StringReader stringreader = new StringReader(p_201323_1_.getInput());
      stringreader.setCursor(p_201323_1_.getStart());
      EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader);

      try {
         entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return entityselectorparser.fillSuggestions(p_201323_1_, (p_201949_1_) -> {
         ISuggestionProvider.suggest(p_201323_0_.getSource().getOnlinePlayerNames(), p_201949_1_);
      });
   };
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(new TranslationTextComponent("argument.scoreHolder.empty"));
   private final boolean multiple;

   public ScoreHolderArgument(boolean p_i47968_1_) {
      this.multiple = p_i47968_1_;
   }

   public static String getName(CommandContext<CommandSource> p_197211_0_, String p_197211_1_) throws CommandSyntaxException {
      return getNames(p_197211_0_, p_197211_1_).iterator().next();
   }

   public static Collection<String> getNames(CommandContext<CommandSource> p_197213_0_, String p_197213_1_) throws CommandSyntaxException {
      return getNames(p_197213_0_, p_197213_1_, Collections::emptyList);
   }

   public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandSource> p_211707_0_, String p_211707_1_) throws CommandSyntaxException {
      return getNames(p_211707_0_, p_211707_1_, p_211707_0_.getSource().getServer().getScoreboard()::getTrackedPlayers);
   }

   public static Collection<String> getNames(CommandContext<CommandSource> p_197210_0_, String p_197210_1_, Supplier<Collection<String>> p_197210_2_) throws CommandSyntaxException {
      Collection<String> collection = p_197210_0_.getArgument(p_197210_1_, ScoreHolderArgument.INameProvider.class).getNames(p_197210_0_.getSource(), p_197210_2_);
      if (collection.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else {
         return collection;
      }
   }

   public static ScoreHolderArgument scoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument scoreHolders() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.INameProvider parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '@') {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_parse_1_);
         EntitySelector entityselector = entityselectorparser.parse();
         if (!this.multiple && entityselector.getMaxResults() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
         } else {
            return new ScoreHolderArgument.NameProvider(entityselector);
         }
      } else {
         int i = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         String s = p_parse_1_.getString().substring(i, p_parse_1_.getCursor());
         if (s.equals("*")) {
            return (p_197208_0_, p_197208_1_) -> {
               Collection<String> collection1 = p_197208_1_.get();
               if (collection1.isEmpty()) {
                  throw ERROR_NO_RESULTS.create();
               } else {
                  return collection1;
               }
            };
         } else {
            Collection<String> collection = Collections.singleton(s);
            return (p_197212_1_, p_197212_2_) -> {
               return collection;
            };
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface INameProvider {
      Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException;
   }

   public static class NameProvider implements ScoreHolderArgument.INameProvider {
      private final EntitySelector selector;

      public NameProvider(EntitySelector p_i47977_1_) {
         this.selector = p_i47977_1_;
      }

      public Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException {
         List<? extends Entity> list = this.selector.findEntities(p_getNames_1_);
         if (list.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
         } else {
            List<String> list1 = Lists.newArrayList();

            for(Entity entity : list) {
               list1.add(entity.getScoreboardName());
            }

            return list1;
         }
      }
   }

   public static class Serializer implements IArgumentSerializer<ScoreHolderArgument> {
      public void serializeToNetwork(ScoreHolderArgument p_197072_1_, PacketBuffer p_197072_2_) {
         byte b0 = 0;
         if (p_197072_1_.multiple) {
            b0 = (byte)(b0 | 1);
         }

         p_197072_2_.writeByte(b0);
      }

      public ScoreHolderArgument deserializeFromNetwork(PacketBuffer p_197071_1_) {
         byte b0 = p_197071_1_.readByte();
         boolean flag = (b0 & 1) != 0;
         return new ScoreHolderArgument(flag);
      }

      public void serializeToJson(ScoreHolderArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.multiple ? "multiple" : "single");
      }
   }
}
