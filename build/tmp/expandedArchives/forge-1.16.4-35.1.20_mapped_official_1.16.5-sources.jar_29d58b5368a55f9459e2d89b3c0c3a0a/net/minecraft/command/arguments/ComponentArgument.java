package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ComponentArgument implements ArgumentType<ITextComponent> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType((p_208660_0_) -> {
      return new TranslationTextComponent("argument.component.invalid", p_208660_0_);
   });

   private ComponentArgument() {
   }

   public static ITextComponent getComponent(CommandContext<CommandSource> p_197068_0_, String p_197068_1_) {
      return p_197068_0_.getArgument(p_197068_1_, ITextComponent.class);
   }

   public static ComponentArgument textComponent() {
      return new ComponentArgument();
   }

   public ITextComponent parse(StringReader p_parse_1_) throws CommandSyntaxException {
      try {
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_parse_1_);
         if (itextcomponent == null) {
            throw ERROR_INVALID_JSON.createWithContext(p_parse_1_, "empty");
         } else {
            return itextcomponent;
         }
      } catch (JsonParseException jsonparseexception) {
         String s = jsonparseexception.getCause() != null ? jsonparseexception.getCause().getMessage() : jsonparseexception.getMessage();
         throw ERROR_INVALID_JSON.createWithContext(p_parse_1_, s);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
