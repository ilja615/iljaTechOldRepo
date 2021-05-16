package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static ITextComponent getMessage(CommandContext<CommandSource> p_197124_0_, String p_197124_1_) throws CommandSyntaxException {
      return p_197124_0_.getArgument(p_197124_1_, MessageArgument.Message.class).toComponent(p_197124_0_.getSource(), p_197124_0_.getSource().hasPermission(2));
   }

   public MessageArgument.Message parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(p_parse_1_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Message {
      private final String text;
      private final MessageArgument.Part[] parts;

      public Message(String p_i48021_1_, MessageArgument.Part[] p_i48021_2_) {
         this.text = p_i48021_1_;
         this.parts = p_i48021_2_;
      }

      public ITextComponent toComponent(CommandSource p_201312_1_, boolean p_201312_2_) throws CommandSyntaxException {
         if (this.parts.length != 0 && p_201312_2_) {
            IFormattableTextComponent iformattabletextcomponent = new StringTextComponent(this.text.substring(0, this.parts[0].getStart()));
            int i = this.parts[0].getStart();

            for(MessageArgument.Part messageargument$part : this.parts) {
               ITextComponent itextcomponent = messageargument$part.toComponent(p_201312_1_);
               if (i < messageargument$part.getStart()) {
                  iformattabletextcomponent.append(this.text.substring(i, messageargument$part.getStart()));
               }

               if (itextcomponent != null) {
                  iformattabletextcomponent.append(itextcomponent);
               }

               i = messageargument$part.getEnd();
            }

            if (i < this.text.length()) {
               iformattabletextcomponent.append(this.text.substring(i, this.text.length()));
            }

            return iformattabletextcomponent;
         } else {
            return new StringTextComponent(this.text);
         }
      }

      public static MessageArgument.Message parseText(StringReader p_197113_0_, boolean p_197113_1_) throws CommandSyntaxException {
         String s = p_197113_0_.getString().substring(p_197113_0_.getCursor(), p_197113_0_.getTotalLength());
         if (!p_197113_1_) {
            p_197113_0_.setCursor(p_197113_0_.getTotalLength());
            return new MessageArgument.Message(s, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> list = Lists.newArrayList();
            int i = p_197113_0_.getCursor();

            while(true) {
               int j;
               EntitySelector entityselector;
               while(true) {
                  if (!p_197113_0_.canRead()) {
                     return new MessageArgument.Message(s, list.toArray(new MessageArgument.Part[list.size()]));
                  }

                  if (p_197113_0_.peek() == '@') {
                     j = p_197113_0_.getCursor();

                     try {
                        EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_197113_0_);
                        entityselector = entityselectorparser.parse();
                        break;
                     } catch (CommandSyntaxException commandsyntaxexception) {
                        if (commandsyntaxexception.getType() != EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE && commandsyntaxexception.getType() != EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                           throw commandsyntaxexception;
                        }

                        p_197113_0_.setCursor(j + 1);
                     }
                  } else {
                     p_197113_0_.skip();
                  }
               }

               list.add(new MessageArgument.Part(j - i, p_197113_0_.getCursor() - i, entityselector));
            }
         }
      }
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int p_i48020_1_, int p_i48020_2_, EntitySelector p_i48020_3_) {
         this.start = p_i48020_1_;
         this.end = p_i48020_2_;
         this.selector = p_i48020_3_;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      @Nullable
      public ITextComponent toComponent(CommandSource p_197116_1_) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.findEntities(p_197116_1_));
      }
   }
}
