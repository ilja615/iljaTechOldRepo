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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_208682_0_) -> {
      return new TranslationTextComponent("arguments.block.tag.unknown", p_208682_0_);
   });

   public static BlockPredicateArgument blockPredicate() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      BlockStateParser blockstateparser = (new BlockStateParser(p_parse_1_, true)).parse(true);
      if (blockstateparser.getState() != null) {
         BlockPredicateArgument.BlockPredicate blockpredicateargument$blockpredicate = new BlockPredicateArgument.BlockPredicate(blockstateparser.getState(), blockstateparser.getProperties().keySet(), blockstateparser.getNbt());
         return (p_199823_1_) -> {
            return blockpredicateargument$blockpredicate;
         };
      } else {
         ResourceLocation resourcelocation = blockstateparser.getTag();
         return (p_199822_2_) -> {
            ITag<Block> itag = p_199822_2_.getBlocks().getTag(resourcelocation);
            if (itag == null) {
               throw ERROR_UNKNOWN_TAG.create(resourcelocation.toString());
            } else {
               return new BlockPredicateArgument.TagPredicate(itag, blockstateparser.getVagueProperties(), blockstateparser.getNbt());
            }
         };
      }
   }

   public static Predicate<CachedBlockInfo> getBlockPredicate(CommandContext<CommandSource> p_199825_0_, String p_199825_1_) throws CommandSyntaxException {
      return p_199825_0_.getArgument(p_199825_1_, BlockPredicateArgument.IResult.class).create(p_199825_0_.getSource().getServer().getTags());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
      stringreader.setCursor(p_listSuggestions_2_.getStart());
      BlockStateParser blockstateparser = new BlockStateParser(stringreader, true);

      try {
         blockstateparser.parse(true);
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return blockstateparser.fillSuggestions(p_listSuggestions_2_, BlockTags.getAllTags());
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static class BlockPredicate implements Predicate<CachedBlockInfo> {
      private final BlockState state;
      private final Set<Property<?>> properties;
      @Nullable
      private final CompoundNBT nbt;

      public BlockPredicate(BlockState p_i48210_1_, Set<Property<?>> p_i48210_2_, @Nullable CompoundNBT p_i48210_3_) {
         this.state = p_i48210_1_;
         this.properties = p_i48210_2_;
         this.nbt = p_i48210_3_;
      }

      public boolean test(CachedBlockInfo p_test_1_) {
         BlockState blockstate = p_test_1_.getState();
         if (!blockstate.is(this.state.getBlock())) {
            return false;
         } else {
            for(Property<?> property : this.properties) {
               if (blockstate.getValue(property) != this.state.getValue(property)) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               TileEntity tileentity = p_test_1_.getEntity();
               return tileentity != null && NBTUtil.compareNbt(this.nbt, tileentity.save(new CompoundNBT()), true);
            }
         }
      }
   }

   public interface IResult {
      Predicate<CachedBlockInfo> create(ITagCollectionSupplier p_create_1_) throws CommandSyntaxException;
   }

   static class TagPredicate implements Predicate<CachedBlockInfo> {
      private final ITag<Block> tag;
      @Nullable
      private final CompoundNBT nbt;
      private final Map<String, String> vagueProperties;

      private TagPredicate(ITag<Block> p_i48238_1_, Map<String, String> p_i48238_2_, @Nullable CompoundNBT p_i48238_3_) {
         this.tag = p_i48238_1_;
         this.vagueProperties = p_i48238_2_;
         this.nbt = p_i48238_3_;
      }

      public boolean test(CachedBlockInfo p_test_1_) {
         BlockState blockstate = p_test_1_.getState();
         if (!blockstate.is(this.tag)) {
            return false;
         } else {
            for(Entry<String, String> entry : this.vagueProperties.entrySet()) {
               Property<?> property = blockstate.getBlock().getStateDefinition().getProperty(entry.getKey());
               if (property == null) {
                  return false;
               }

               Comparable<?> comparable = (Comparable)property.getValue(entry.getValue()).orElse(null);
               if (comparable == null) {
                  return false;
               }

               if (blockstate.getValue(property) != comparable) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               TileEntity tileentity = p_test_1_.getEntity();
               return tileentity != null && NBTUtil.compareNbt(this.nbt, tileentity.save(new CompoundNBT()), true);
            }
         }
      }
   }
}
