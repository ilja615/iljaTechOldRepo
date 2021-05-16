package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.entity.invalid"));
   public static final Function<String, DataCommand.IDataProvider> PROVIDER = (p_218922_0_) -> {
      return new DataCommand.IDataProvider() {
         public IDataAccessor access(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity(p_198919_1_, p_218922_0_));
         }

         public ArgumentBuilder<CommandSource, ?> wrap(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
            return p_198920_1_.then(Commands.literal("entity").then((ArgumentBuilder)p_198920_2_.apply(Commands.argument(p_218922_0_, EntityArgument.entity()))));
         }
      };
   };
   private final Entity entity;

   public EntityDataAccessor(Entity p_i47917_1_) {
      this.entity = p_i47917_1_;
   }

   public void setData(CompoundNBT p_198925_1_) throws CommandSyntaxException {
      if (this.entity instanceof PlayerEntity) {
         throw ERROR_NO_PLAYERS.create();
      } else {
         UUID uuid = this.entity.getUUID();
         this.entity.load(p_198925_1_);
         this.entity.setUUID(uuid);
      }
   }

   public CompoundNBT getData() {
      return NBTPredicate.getEntityTagToCompare(this.entity);
   }

   public ITextComponent getModifiedSuccess() {
      return new TranslationTextComponent("commands.data.entity.modified", this.entity.getDisplayName());
   }

   public ITextComponent getPrintSuccess(INBT p_198924_1_) {
      return new TranslationTextComponent("commands.data.entity.query", this.entity.getDisplayName(), p_198924_1_.getPrettyDisplay());
   }

   public ITextComponent getPrintSuccess(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TranslationTextComponent("commands.data.entity.get", p_198922_1_, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_);
   }
}
