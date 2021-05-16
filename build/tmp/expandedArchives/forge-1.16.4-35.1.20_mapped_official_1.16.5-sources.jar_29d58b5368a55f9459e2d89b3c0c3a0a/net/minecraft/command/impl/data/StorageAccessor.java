package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.CommandStorage;

public class StorageAccessor implements IDataAccessor {
   private static final SuggestionProvider<CommandSource> SUGGEST_STORAGE = (p_229838_0_, p_229838_1_) -> {
      return ISuggestionProvider.suggestResource(getGlobalTags(p_229838_0_).keys(), p_229838_1_);
   };
   public static final Function<String, DataCommand.IDataProvider> PROVIDER = (p_229839_0_) -> {
      return new DataCommand.IDataProvider() {
         public IDataAccessor access(CommandContext<CommandSource> p_198919_1_) {
            return new StorageAccessor(StorageAccessor.getGlobalTags(p_198919_1_), ResourceLocationArgument.getId(p_198919_1_, p_229839_0_));
         }

         public ArgumentBuilder<CommandSource, ?> wrap(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
            return p_198920_1_.then(Commands.literal("storage").then((ArgumentBuilder)p_198920_2_.apply(Commands.argument(p_229839_0_, ResourceLocationArgument.id()).suggests(StorageAccessor.SUGGEST_STORAGE))));
         }
      };
   };
   private final CommandStorage storage;
   private final ResourceLocation id;

   private static CommandStorage getGlobalTags(CommandContext<CommandSource> p_229840_0_) {
      return p_229840_0_.getSource().getServer().getCommandStorage();
   }

   private StorageAccessor(CommandStorage p_i226092_1_, ResourceLocation p_i226092_2_) {
      this.storage = p_i226092_1_;
      this.id = p_i226092_2_;
   }

   public void setData(CompoundNBT p_198925_1_) {
      this.storage.set(this.id, p_198925_1_);
   }

   public CompoundNBT getData() {
      return this.storage.get(this.id);
   }

   public ITextComponent getModifiedSuccess() {
      return new TranslationTextComponent("commands.data.storage.modified", this.id);
   }

   public ITextComponent getPrintSuccess(INBT p_198924_1_) {
      return new TranslationTextComponent("commands.data.storage.query", this.id, p_198924_1_.getPrettyDisplay());
   }

   public ITextComponent getPrintSuccess(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TranslationTextComponent("commands.data.storage.get", p_198922_1_, this.id, String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_);
   }
}
