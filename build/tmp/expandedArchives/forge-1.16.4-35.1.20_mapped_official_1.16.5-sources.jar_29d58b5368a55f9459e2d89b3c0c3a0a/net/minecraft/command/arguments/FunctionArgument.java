package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FunctionArgument implements ArgumentType<FunctionArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_208691_0_) -> {
      return new TranslationTextComponent("arguments.function.tag.unknown", p_208691_0_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((p_208694_0_) -> {
      return new TranslationTextComponent("arguments.function.unknown", p_208694_0_);
   });

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '#') {
         p_parse_1_.skip();
         final ResourceLocation resourcelocation1 = ResourceLocation.read(p_parse_1_);
         return new FunctionArgument.IResult() {
            public Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException {
               ITag<FunctionObject> itag = FunctionArgument.getFunctionTag(p_223252_1_, resourcelocation1);
               return itag.getValues();
            }

            public Pair<ResourceLocation, Either<FunctionObject, ITag<FunctionObject>>> unwrap(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException {
               return Pair.of(resourcelocation1, Either.right(FunctionArgument.getFunctionTag(p_218102_1_, resourcelocation1)));
            }
         };
      } else {
         final ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
         return new FunctionArgument.IResult() {
            public Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(p_223252_1_, resourcelocation));
            }

            public Pair<ResourceLocation, Either<FunctionObject, ITag<FunctionObject>>> unwrap(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException {
               return Pair.of(resourcelocation, Either.left(FunctionArgument.getFunction(p_218102_1_, resourcelocation)));
            }
         };
      }
   }

   private static FunctionObject getFunction(CommandContext<CommandSource> p_218108_0_, ResourceLocation p_218108_1_) throws CommandSyntaxException {
      return p_218108_0_.getSource().getServer().getFunctions().get(p_218108_1_).orElseThrow(() -> {
         return ERROR_UNKNOWN_FUNCTION.create(p_218108_1_.toString());
      });
   }

   private static ITag<FunctionObject> getFunctionTag(CommandContext<CommandSource> p_218111_0_, ResourceLocation p_218111_1_) throws CommandSyntaxException {
      ITag<FunctionObject> itag = p_218111_0_.getSource().getServer().getFunctions().getTag(p_218111_1_);
      if (itag == null) {
         throw ERROR_UNKNOWN_TAG.create(p_218111_1_.toString());
      } else {
         return itag;
      }
   }

   public static Collection<FunctionObject> getFunctions(CommandContext<CommandSource> p_200022_0_, String p_200022_1_) throws CommandSyntaxException {
      return p_200022_0_.getArgument(p_200022_1_, FunctionArgument.IResult.class).create(p_200022_0_);
   }

   public static Pair<ResourceLocation, Either<FunctionObject, ITag<FunctionObject>>> getFunctionOrTag(CommandContext<CommandSource> p_218110_0_, String p_218110_1_) throws CommandSyntaxException {
      return p_218110_0_.getArgument(p_218110_1_, FunctionArgument.IResult.class).unwrap(p_218110_0_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface IResult {
      Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException;

      Pair<ResourceLocation, Either<FunctionObject, ITag<FunctionObject>>> unwrap(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException;
   }
}
