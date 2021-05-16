package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.event.HoverEvent;

public class TextComponentUtils {
   public static IFormattableTextComponent mergeStyles(IFormattableTextComponent p_240648_0_, Style p_240648_1_) {
      if (p_240648_1_.isEmpty()) {
         return p_240648_0_;
      } else {
         Style style = p_240648_0_.getStyle();
         if (style.isEmpty()) {
            return p_240648_0_.setStyle(p_240648_1_);
         } else {
            return style.equals(p_240648_1_) ? p_240648_0_ : p_240648_0_.setStyle(style.applyTo(p_240648_1_));
         }
      }
   }

   public static IFormattableTextComponent updateForEntity(@Nullable CommandSource p_240645_0_, ITextComponent p_240645_1_, @Nullable Entity p_240645_2_, int p_240645_3_) throws CommandSyntaxException {
      if (p_240645_3_ > 100) {
         return p_240645_1_.copy();
      } else {
         IFormattableTextComponent iformattabletextcomponent = p_240645_1_ instanceof ITargetedTextComponent ? ((ITargetedTextComponent)p_240645_1_).resolve(p_240645_0_, p_240645_2_, p_240645_3_ + 1) : p_240645_1_.plainCopy();

         for(ITextComponent itextcomponent : p_240645_1_.getSiblings()) {
            iformattabletextcomponent.append(updateForEntity(p_240645_0_, itextcomponent, p_240645_2_, p_240645_3_ + 1));
         }

         return iformattabletextcomponent.withStyle(resolveStyle(p_240645_0_, p_240645_1_.getStyle(), p_240645_2_, p_240645_3_));
      }
   }

   private static Style resolveStyle(@Nullable CommandSource p_240646_0_, Style p_240646_1_, @Nullable Entity p_240646_2_, int p_240646_3_) throws CommandSyntaxException {
      HoverEvent hoverevent = p_240646_1_.getHoverEvent();
      if (hoverevent != null) {
         ITextComponent itextcomponent = hoverevent.getValue(HoverEvent.Action.SHOW_TEXT);
         if (itextcomponent != null) {
            HoverEvent hoverevent1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, updateForEntity(p_240646_0_, itextcomponent, p_240646_2_, p_240646_3_ + 1));
            return p_240646_1_.withHoverEvent(hoverevent1);
         }
      }

      return p_240646_1_;
   }

   public static ITextComponent getDisplayName(GameProfile p_197679_0_) {
      if (p_197679_0_.getName() != null) {
         return new StringTextComponent(p_197679_0_.getName());
      } else {
         return p_197679_0_.getId() != null ? new StringTextComponent(p_197679_0_.getId().toString()) : new StringTextComponent("(unknown)");
      }
   }

   public static ITextComponent formatList(Collection<String> p_197678_0_) {
      return formatAndSortList(p_197678_0_, (p_197681_0_) -> {
         return (new StringTextComponent(p_197681_0_)).withStyle(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent formatAndSortList(Collection<T> p_197675_0_, Function<T, ITextComponent> p_197675_1_) {
      if (p_197675_0_.isEmpty()) {
         return StringTextComponent.EMPTY;
      } else if (p_197675_0_.size() == 1) {
         return p_197675_1_.apply(p_197675_0_.iterator().next());
      } else {
         List<T> list = Lists.newArrayList(p_197675_0_);
         list.sort(Comparable::compareTo);
         return formatList(list, p_197675_1_);
      }
   }

   public static <T> IFormattableTextComponent formatList(Collection<T> p_240649_0_, Function<T, ITextComponent> p_240649_1_) {
      if (p_240649_0_.isEmpty()) {
         return new StringTextComponent("");
      } else if (p_240649_0_.size() == 1) {
         return p_240649_1_.apply(p_240649_0_.iterator().next()).copy();
      } else {
         IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("");
         boolean flag = true;

         for(T t : p_240649_0_) {
            if (!flag) {
               iformattabletextcomponent.append((new StringTextComponent(", ")).withStyle(TextFormatting.GRAY));
            }

            iformattabletextcomponent.append(p_240649_1_.apply(t));
            flag = false;
         }

         return iformattabletextcomponent;
      }
   }

   public static IFormattableTextComponent wrapInSquareBrackets(ITextComponent p_240647_0_) {
      return new TranslationTextComponent("chat.square_brackets", p_240647_0_);
   }

   public static ITextComponent fromMessage(Message p_202465_0_) {
      return (ITextComponent)(p_202465_0_ instanceof ITextComponent ? (ITextComponent)p_202465_0_ : new StringTextComponent(p_202465_0_.getString()));
   }
}
