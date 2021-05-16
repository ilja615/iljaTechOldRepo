package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.Property;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.item.tag.disallowed"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((p_208696_0_) -> {
      return new TranslationTextComponent("argument.item.id.invalid", p_208696_0_);
   });
   private static final BiFunction<SuggestionsBuilder, ITagCollection<Item>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_239571_0_, p_239571_1_) -> {
      return p_239571_0_.buildFuture();
   };
   private final StringReader reader;
   private final boolean forTesting;
   private final Map<Property<?>, Comparable<?>> properties = Maps.newHashMap();
   private Item item;
   @Nullable
   private CompoundNBT nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int tagCursor;
   private BiFunction<SuggestionsBuilder, ITagCollection<Item>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   public ItemParser(StringReader p_i48213_1_, boolean p_i48213_2_) {
      this.reader = p_i48213_1_;
      this.forTesting = p_i48213_2_;
   }

   public Item getItem() {
      return this.item;
   }

   @Nullable
   public CompoundNBT getNbt() {
      return this.nbt;
   }

   public ResourceLocation getTag() {
      return this.tag;
   }

   public void readItem() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
      this.item = Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
         this.reader.setCursor(i);
         return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, resourcelocation.toString());
      });
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new JsonToNBT(this.reader)).readStruct();
   }

   public ItemParser parse() throws CommandSyntaxException {
      this.suggestions = this::suggestItemIdOrTag;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
      } else {
         this.readItem();
         this.suggestions = this::suggestOpenNbt;
      }

      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder p_197328_1_, ITagCollection<Item> p_197328_2_) {
      if (p_197328_1_.getRemaining().isEmpty()) {
         p_197328_1_.suggest(String.valueOf('{'));
      }

      return p_197328_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_201955_1_, ITagCollection<Item> p_201955_2_) {
      return ISuggestionProvider.suggestResource(p_201955_2_.getAvailableTags(), p_201955_1_.createOffset(this.tagCursor));
   }

   private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder p_197331_1_, ITagCollection<Item> p_197331_2_) {
      if (this.forTesting) {
         ISuggestionProvider.suggestResource(p_197331_2_.getAvailableTags(), p_197331_1_, String.valueOf('#'));
      }

      return ISuggestionProvider.suggestResource(Registry.ITEM.keySet(), p_197331_1_);
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_197329_1_, ITagCollection<Item> p_197329_2_) {
      return this.suggestions.apply(p_197329_1_.createOffset(this.reader.getCursor()), p_197329_2_);
   }
}
