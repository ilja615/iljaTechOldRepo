package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandSuggestionHelper {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private static final Style UNPARSED_STYLE = Style.EMPTY.withColor(TextFormatting.RED);
   private static final Style LITERAL_STYLE = Style.EMPTY.withColor(TextFormatting.GRAY);
   private static final List<Style> ARGUMENT_STYLES = Stream.of(TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD).map(Style.EMPTY::withColor).collect(ImmutableList.toImmutableList());
   private final Minecraft minecraft;
   private final Screen screen;
   private final TextFieldWidget input;
   private final FontRenderer font;
   private final boolean commandsOnly;
   private final boolean onlyShowIfCursorPastError;
   private final int lineStartOffset;
   private final int suggestionLineLimit;
   private final boolean anchorToBottom;
   private final int fillColor;
   private final List<IReorderingProcessor> commandUsage = Lists.newArrayList();
   private int commandUsagePosition;
   private int commandUsageWidth;
   private ParseResults<ISuggestionProvider> currentParse;
   private CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> pendingSuggestions;
   private CommandSuggestionHelper.Suggestions suggestions;
   private boolean allowSuggestions;
   private boolean keepSuggestions;

   public CommandSuggestionHelper(Minecraft p_i225919_1_, Screen p_i225919_2_, TextFieldWidget p_i225919_3_, FontRenderer p_i225919_4_, boolean p_i225919_5_, boolean p_i225919_6_, int p_i225919_7_, int p_i225919_8_, boolean p_i225919_9_, int p_i225919_10_) {
      this.minecraft = p_i225919_1_;
      this.screen = p_i225919_2_;
      this.input = p_i225919_3_;
      this.font = p_i225919_4_;
      this.commandsOnly = p_i225919_5_;
      this.onlyShowIfCursorPastError = p_i225919_6_;
      this.lineStartOffset = p_i225919_7_;
      this.suggestionLineLimit = p_i225919_8_;
      this.anchorToBottom = p_i225919_9_;
      this.fillColor = p_i225919_10_;
      p_i225919_3_.setFormatter(this::formatChat);
   }

   public void setAllowSuggestions(boolean p_228124_1_) {
      this.allowSuggestions = p_228124_1_;
      if (!p_228124_1_) {
         this.suggestions = null;
      }

   }

   public boolean keyPressed(int p_228115_1_, int p_228115_2_, int p_228115_3_) {
      if (this.suggestions != null && this.suggestions.keyPressed(p_228115_1_, p_228115_2_, p_228115_3_)) {
         return true;
      } else if (this.screen.getFocused() == this.input && p_228115_1_ == 258) {
         this.showSuggestions(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_228112_1_) {
      return this.suggestions != null && this.suggestions.mouseScrolled(MathHelper.clamp(p_228112_1_, -1.0D, 1.0D));
   }

   public boolean mouseClicked(double p_228113_1_, double p_228113_3_, int p_228113_5_) {
      return this.suggestions != null && this.suggestions.mouseClicked((int)p_228113_1_, (int)p_228113_3_, p_228113_5_);
   }

   public void showSuggestions(boolean p_228128_1_) {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         com.mojang.brigadier.suggestion.Suggestions suggestions = this.pendingSuggestions.join();
         if (!suggestions.isEmpty()) {
            int i = 0;

            for(Suggestion suggestion : suggestions.getList()) {
               i = Math.max(i, this.font.width(suggestion.getText()));
            }

            int j = MathHelper.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
            int k = this.anchorToBottom ? this.screen.height - 12 : 72;
            this.suggestions = new CommandSuggestionHelper.Suggestions(j, k, i, this.sortSuggestions(suggestions), p_228128_1_);
         }
      }

   }

   private List<Suggestion> sortSuggestions(com.mojang.brigadier.suggestion.Suggestions p_241575_1_) {
      String s = this.input.getValue().substring(0, this.input.getCursorPosition());
      int i = getLastWordIndex(s);
      String s1 = s.substring(i).toLowerCase(Locale.ROOT);
      List<Suggestion> list = Lists.newArrayList();
      List<Suggestion> list1 = Lists.newArrayList();

      for(Suggestion suggestion : p_241575_1_.getList()) {
         if (!suggestion.getText().startsWith(s1) && !suggestion.getText().startsWith("minecraft:" + s1)) {
            list1.add(suggestion);
         } else {
            list.add(suggestion);
         }
      }

      list.addAll(list1);
      return list;
   }

   public void updateCommandInfo() {
      String s = this.input.getValue();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(s)) {
         this.currentParse = null;
      }

      if (!this.keepSuggestions) {
         this.input.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      StringReader stringreader = new StringReader(s);
      boolean flag = stringreader.canRead() && stringreader.peek() == '/';
      if (flag) {
         stringreader.skip();
      }

      boolean flag1 = this.commandsOnly || flag;
      int i = this.input.getCursorPosition();
      if (flag1) {
         CommandDispatcher<ISuggestionProvider> commanddispatcher = this.minecraft.player.connection.getCommands();
         if (this.currentParse == null) {
            this.currentParse = commanddispatcher.parse(stringreader, this.minecraft.player.connection.getSuggestionsProvider());
         }

         int j = this.onlyShowIfCursorPastError ? stringreader.getCursor() : 1;
         if (i >= j && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = commanddispatcher.getCompletionSuggestions(this.currentParse, i);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo();
               }
            });
         }
      } else {
         String s1 = s.substring(0, i);
         int k = getLastWordIndex(s1);
         Collection<String> collection = this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames();
         this.pendingSuggestions = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));
      }

   }

   private static int getLastWordIndex(String p_228121_0_) {
      if (Strings.isNullOrEmpty(p_228121_0_)) {
         return 0;
      } else {
         int i = 0;

         for(Matcher matcher = WHITESPACE_PATTERN.matcher(p_228121_0_); matcher.find(); i = matcher.end()) {
         }

         return i;
      }
   }

   private static IReorderingProcessor getExceptionMessage(CommandSyntaxException p_243255_0_) {
      ITextComponent itextcomponent = TextComponentUtils.fromMessage(p_243255_0_.getRawMessage());
      String s = p_243255_0_.getContext();
      return s == null ? itextcomponent.getVisualOrderText() : (new TranslationTextComponent("command.context.parse_error", itextcomponent, p_243255_0_.getCursor(), s)).getVisualOrderText();
   }

   private void updateUsageInfo() {
      if (this.input.getCursorPosition() == this.input.getValue().length()) {
         if (this.pendingSuggestions.join().isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
            int i = 0;

            for(Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
               CommandSyntaxException commandsyntaxexception = entry.getValue();
               if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                  ++i;
               } else {
                  this.commandUsage.add(getExceptionMessage(commandsyntaxexception));
               }
            }

            if (i > 0) {
               this.commandUsage.add(getExceptionMessage(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
            }
         } else if (this.currentParse.getReader().canRead()) {
            this.commandUsage.add(getExceptionMessage(Commands.getParseException(this.currentParse)));
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.screen.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(TextFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.allowSuggestions && this.minecraft.options.autoSuggestions) {
         this.showSuggestions(false);
      }

   }

   private void fillNodeUsage(TextFormatting p_228120_1_) {
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.currentParse.getContext();
      SuggestionContext<ISuggestionProvider> suggestioncontext = commandcontextbuilder.findSuggestionContext(this.input.getCursorPosition());
      Map<CommandNode<ISuggestionProvider>, String> map = this.minecraft.player.connection.getCommands().getSmartUsage(suggestioncontext.parent, this.minecraft.player.connection.getSuggestionsProvider());
      List<IReorderingProcessor> list = Lists.newArrayList();
      int i = 0;
      Style style = Style.EMPTY.withColor(p_228120_1_);

      for(Entry<CommandNode<ISuggestionProvider>, String> entry : map.entrySet()) {
         if (!(entry.getKey() instanceof LiteralCommandNode)) {
            list.add(IReorderingProcessor.forward(entry.getValue(), style));
            i = Math.max(i, this.font.width(entry.getValue()));
         }
      }

      if (!list.isEmpty()) {
         this.commandUsage.addAll(list);
         this.commandUsagePosition = MathHelper.clamp(this.input.getScreenX(suggestioncontext.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
         this.commandUsageWidth = i;
      }

   }

   private IReorderingProcessor formatChat(String p_228122_1_, int p_228122_2_) {
      return this.currentParse != null ? formatText(this.currentParse, p_228122_1_, p_228122_2_) : IReorderingProcessor.forward(p_228122_1_, Style.EMPTY);
   }

   @Nullable
   private static String calculateSuggestionSuffix(String p_228127_0_, String p_228127_1_) {
      return p_228127_1_.startsWith(p_228127_0_) ? p_228127_1_.substring(p_228127_0_.length()) : null;
   }

   private static IReorderingProcessor formatText(ParseResults<ISuggestionProvider> p_228116_0_, String p_228116_1_, int p_228116_2_) {
      List<IReorderingProcessor> list = Lists.newArrayList();
      int i = 0;
      int j = -1;
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = p_228116_0_.getContext().getLastChild();

      for(ParsedArgument<ISuggestionProvider, ?> parsedargument : commandcontextbuilder.getArguments().values()) {
         ++j;
         if (j >= ARGUMENT_STYLES.size()) {
            j = 0;
         }

         int k = Math.max(parsedargument.getRange().getStart() - p_228116_2_, 0);
         if (k >= p_228116_1_.length()) {
            break;
         }

         int l = Math.min(parsedargument.getRange().getEnd() - p_228116_2_, p_228116_1_.length());
         if (l > 0) {
            list.add(IReorderingProcessor.forward(p_228116_1_.substring(i, k), LITERAL_STYLE));
            list.add(IReorderingProcessor.forward(p_228116_1_.substring(k, l), ARGUMENT_STYLES.get(j)));
            i = l;
         }
      }

      if (p_228116_0_.getReader().canRead()) {
         int i1 = Math.max(p_228116_0_.getReader().getCursor() - p_228116_2_, 0);
         if (i1 < p_228116_1_.length()) {
            int j1 = Math.min(i1 + p_228116_0_.getReader().getRemainingLength(), p_228116_1_.length());
            list.add(IReorderingProcessor.forward(p_228116_1_.substring(i, i1), LITERAL_STYLE));
            list.add(IReorderingProcessor.forward(p_228116_1_.substring(i1, j1), UNPARSED_STYLE));
            i = j1;
         }
      }

      list.add(IReorderingProcessor.forward(p_228116_1_.substring(i), LITERAL_STYLE));
      return IReorderingProcessor.composite(list);
   }

   public void render(MatrixStack p_238500_1_, int p_238500_2_, int p_238500_3_) {
      if (this.suggestions != null) {
         this.suggestions.render(p_238500_1_, p_238500_2_, p_238500_3_);
      } else {
         int i = 0;

         for(IReorderingProcessor ireorderingprocessor : this.commandUsage) {
            int j = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * i : 72 + 12 * i;
            AbstractGui.fill(p_238500_1_, this.commandUsagePosition - 1, j, this.commandUsagePosition + this.commandUsageWidth + 1, j + 12, this.fillColor);
            this.font.drawShadow(p_238500_1_, ireorderingprocessor, (float)this.commandUsagePosition, (float)(j + 2), -1);
            ++i;
         }
      }

   }

   public String getNarrationMessage() {
      return this.suggestions != null ? "\n" + this.suggestions.getNarrationMessage() : "";
   }

   @OnlyIn(Dist.CLIENT)
   public class Suggestions {
      private final Rectangle2d rect;
      private final String originalContents;
      private final List<Suggestion> suggestionList;
      private int offset;
      private int current;
      private Vector2f lastMouse = Vector2f.ZERO;
      private boolean tabCycles;
      private int lastNarratedEntry;

      private Suggestions(int p_i241247_2_, int p_i241247_3_, int p_i241247_4_, List<Suggestion> p_i241247_5_, boolean p_i241247_6_) {
         int i = p_i241247_2_ - 1;
         int j = CommandSuggestionHelper.this.anchorToBottom ? p_i241247_3_ - 3 - Math.min(p_i241247_5_.size(), CommandSuggestionHelper.this.suggestionLineLimit) * 12 : p_i241247_3_;
         this.rect = new Rectangle2d(i, j, p_i241247_4_ + 1, Math.min(p_i241247_5_.size(), CommandSuggestionHelper.this.suggestionLineLimit) * 12);
         this.originalContents = CommandSuggestionHelper.this.input.getValue();
         this.lastNarratedEntry = p_i241247_6_ ? -1 : 0;
         this.suggestionList = p_i241247_5_;
         this.select(0);
      }

      public void render(MatrixStack p_238501_1_, int p_238501_2_, int p_238501_3_) {
         int i = Math.min(this.suggestionList.size(), CommandSuggestionHelper.this.suggestionLineLimit);
         int j = -5592406;
         boolean flag = this.offset > 0;
         boolean flag1 = this.suggestionList.size() > this.offset + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.lastMouse.x != (float)p_238501_2_ || this.lastMouse.y != (float)p_238501_3_;
         if (flag3) {
            this.lastMouse = new Vector2f((float)p_238501_2_, (float)p_238501_3_);
         }

         if (flag2) {
            AbstractGui.fill(p_238501_1_, this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestionHelper.this.fillColor);
            AbstractGui.fill(p_238501_1_, this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestionHelper.this.fillColor);
            if (flag) {
               for(int k = 0; k < this.rect.getWidth(); ++k) {
                  if (k % 2 == 0) {
                     AbstractGui.fill(p_238501_1_, this.rect.getX() + k, this.rect.getY() - 1, this.rect.getX() + k + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for(int i1 = 0; i1 < this.rect.getWidth(); ++i1) {
                  if (i1 % 2 == 0) {
                     AbstractGui.fill(p_238501_1_, this.rect.getX() + i1, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + i1 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean flag4 = false;

         for(int l = 0; l < i; ++l) {
            Suggestion suggestion = this.suggestionList.get(l + this.offset);
            AbstractGui.fill(p_238501_1_, this.rect.getX(), this.rect.getY() + 12 * l, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * l + 12, CommandSuggestionHelper.this.fillColor);
            if (p_238501_2_ > this.rect.getX() && p_238501_2_ < this.rect.getX() + this.rect.getWidth() && p_238501_3_ > this.rect.getY() + 12 * l && p_238501_3_ < this.rect.getY() + 12 * l + 12) {
               if (flag3) {
                  this.select(l + this.offset);
               }

               flag4 = true;
            }

            CommandSuggestionHelper.this.font.drawShadow(p_238501_1_, suggestion.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * l), l + this.offset == this.current ? -256 : -5592406);
         }

         if (flag4) {
            Message message = this.suggestionList.get(this.current).getTooltip();
            if (message != null) {
               CommandSuggestionHelper.this.screen.renderTooltip(p_238501_1_, TextComponentUtils.fromMessage(message), p_238501_2_, p_238501_3_);
            }
         }

      }

      public boolean mouseClicked(int p_228150_1_, int p_228150_2_, int p_228150_3_) {
         if (!this.rect.contains(p_228150_1_, p_228150_2_)) {
            return false;
         } else {
            int i = (p_228150_2_ - this.rect.getY()) / 12 + this.offset;
            if (i >= 0 && i < this.suggestionList.size()) {
               this.select(i);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double p_228147_1_) {
         int i = (int)(CommandSuggestionHelper.this.minecraft.mouseHandler.xpos() * (double)CommandSuggestionHelper.this.minecraft.getWindow().getGuiScaledWidth() / (double)CommandSuggestionHelper.this.minecraft.getWindow().getScreenWidth());
         int j = (int)(CommandSuggestionHelper.this.minecraft.mouseHandler.ypos() * (double)CommandSuggestionHelper.this.minecraft.getWindow().getGuiScaledHeight() / (double)CommandSuggestionHelper.this.minecraft.getWindow().getScreenHeight());
         if (this.rect.contains(i, j)) {
            this.offset = MathHelper.clamp((int)((double)this.offset - p_228147_1_), 0, Math.max(this.suggestionList.size() - CommandSuggestionHelper.this.suggestionLineLimit, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int p_228154_1_, int p_228154_2_, int p_228154_3_) {
         if (p_228154_1_ == 265) {
            this.cycle(-1);
            this.tabCycles = false;
            return true;
         } else if (p_228154_1_ == 264) {
            this.cycle(1);
            this.tabCycles = false;
            return true;
         } else if (p_228154_1_ == 258) {
            if (this.tabCycles) {
               this.cycle(Screen.hasShiftDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (p_228154_1_ == 256) {
            this.hide();
            return true;
         } else {
            return false;
         }
      }

      public void cycle(int p_228148_1_) {
         this.select(this.current + p_228148_1_);
         int i = this.offset;
         int j = this.offset + CommandSuggestionHelper.this.suggestionLineLimit - 1;
         if (this.current < i) {
            this.offset = MathHelper.clamp(this.current, 0, Math.max(this.suggestionList.size() - CommandSuggestionHelper.this.suggestionLineLimit, 0));
         } else if (this.current > j) {
            this.offset = MathHelper.clamp(this.current + CommandSuggestionHelper.this.lineStartOffset - CommandSuggestionHelper.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - CommandSuggestionHelper.this.suggestionLineLimit, 0));
         }

      }

      public void select(int p_228153_1_) {
         this.current = p_228153_1_;
         if (this.current < 0) {
            this.current += this.suggestionList.size();
         }

         if (this.current >= this.suggestionList.size()) {
            this.current -= this.suggestionList.size();
         }

         Suggestion suggestion = this.suggestionList.get(this.current);
         CommandSuggestionHelper.this.input.setSuggestion(CommandSuggestionHelper.calculateSuggestionSuffix(CommandSuggestionHelper.this.input.getValue(), suggestion.apply(this.originalContents)));
         if (NarratorChatListener.INSTANCE.isActive() && this.lastNarratedEntry != this.current) {
            NarratorChatListener.INSTANCE.sayNow(this.getNarrationMessage());
         }

      }

      public void useSuggestion() {
         Suggestion suggestion = this.suggestionList.get(this.current);
         CommandSuggestionHelper.this.keepSuggestions = true;
         CommandSuggestionHelper.this.input.setValue(suggestion.apply(this.originalContents));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         CommandSuggestionHelper.this.input.setCursorPosition(i);
         CommandSuggestionHelper.this.input.setHighlightPos(i);
         this.select(this.current);
         CommandSuggestionHelper.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      private String getNarrationMessage() {
         this.lastNarratedEntry = this.current;
         Suggestion suggestion = this.suggestionList.get(this.current);
         Message message = suggestion.getTooltip();
         return message != null ? I18n.get("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), suggestion.getText(), message.getString()) : I18n.get("narration.suggestion", this.current + 1, this.suggestionList.size(), suggestion.getText());
      }

      public void hide() {
         CommandSuggestionHelper.this.suggestions = null;
      }
   }
}
