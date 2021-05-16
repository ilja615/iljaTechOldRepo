package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_208699_0_) -> {
      return new TranslationTextComponent("arguments.item.tag.unknown", p_208699_0_);
   });

   public static ItemPredicateArgument itemPredicate() {
      return new ItemPredicateArgument();
   }

   public ItemPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ItemParser itemparser = (new ItemParser(p_parse_1_, true)).parse();
      if (itemparser.getItem() != null) {
         ItemPredicateArgument.ItemPredicate itempredicateargument$itempredicate = new ItemPredicateArgument.ItemPredicate(itemparser.getItem(), itemparser.getNbt());
         return (p_199848_1_) -> {
            return itempredicateargument$itempredicate;
         };
      } else {
         ResourceLocation resourcelocation = itemparser.getTag();
         return (p_199845_2_) -> {
            ITag<Item> itag = p_199845_2_.getSource().getServer().getTags().getItems().getTag(resourcelocation);
            if (itag == null) {
               throw ERROR_UNKNOWN_TAG.create(resourcelocation.toString());
            } else {
               return new ItemPredicateArgument.TagPredicate(itag, itemparser.getNbt());
            }
         };
      }
   }

   public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSource> p_199847_0_, String p_199847_1_) throws CommandSyntaxException {
      return p_199847_0_.getArgument(p_199847_1_, ItemPredicateArgument.IResult.class).create(p_199847_0_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
      stringreader.setCursor(p_listSuggestions_2_.getStart());
      ItemParser itemparser = new ItemParser(stringreader, true);

      try {
         itemparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return itemparser.fillSuggestions(p_listSuggestions_2_, ItemTags.getAllTags());
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface IResult {
      Predicate<ItemStack> create(CommandContext<CommandSource> p_create_1_) throws CommandSyntaxException;
   }

   static class ItemPredicate implements Predicate<ItemStack> {
      private final Item item;
      @Nullable
      private final CompoundNBT nbt;

      public ItemPredicate(Item p_i48221_1_, @Nullable CompoundNBT p_i48221_2_) {
         this.item = p_i48221_1_;
         this.nbt = p_i48221_2_;
      }

      public boolean test(ItemStack p_test_1_) {
         return p_test_1_.getItem() == this.item && NBTUtil.compareNbt(this.nbt, p_test_1_.getTag(), true);
      }
   }

   static class TagPredicate implements Predicate<ItemStack> {
      private final ITag<Item> tag;
      @Nullable
      private final CompoundNBT nbt;

      public TagPredicate(ITag<Item> p_i48220_1_, @Nullable CompoundNBT p_i48220_2_) {
         this.tag = p_i48220_1_;
         this.nbt = p_i48220_2_;
      }

      public boolean test(ItemStack p_test_1_) {
         return this.tag.contains(p_test_1_.getItem()) && NBTUtil.compareNbt(this.nbt, p_test_1_.getTag(), true);
      }
   }
}
