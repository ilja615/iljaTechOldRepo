package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.block.invalid"));
   public static final Function<String, DataCommand.IDataProvider> PROVIDER = (p_218923_0_) -> {
      return new DataCommand.IDataProvider() {
         public IDataAccessor access(CommandContext<CommandSource> p_198919_1_) throws CommandSyntaxException {
            BlockPos blockpos = BlockPosArgument.getLoadedBlockPos(p_198919_1_, p_218923_0_ + "Pos");
            TileEntity tileentity = ((CommandSource)p_198919_1_.getSource()).getLevel().getBlockEntity(blockpos);
            if (tileentity == null) {
               throw BlockDataAccessor.ERROR_NOT_A_BLOCK_ENTITY.create();
            } else {
               return new BlockDataAccessor(tileentity, blockpos);
            }
         }

         public ArgumentBuilder<CommandSource, ?> wrap(ArgumentBuilder<CommandSource, ?> p_198920_1_, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> p_198920_2_) {
            return p_198920_1_.then(Commands.literal("block").then((ArgumentBuilder)p_198920_2_.apply(Commands.argument(p_218923_0_ + "Pos", BlockPosArgument.blockPos()))));
         }
      };
   };
   private final TileEntity entity;
   private final BlockPos pos;

   public BlockDataAccessor(TileEntity p_i47918_1_, BlockPos p_i47918_2_) {
      this.entity = p_i47918_1_;
      this.pos = p_i47918_2_;
   }

   public void setData(CompoundNBT p_198925_1_) {
      p_198925_1_.putInt("x", this.pos.getX());
      p_198925_1_.putInt("y", this.pos.getY());
      p_198925_1_.putInt("z", this.pos.getZ());
      BlockState blockstate = this.entity.getLevel().getBlockState(this.pos);
      this.entity.load(blockstate, p_198925_1_);
      this.entity.setChanged();
      this.entity.getLevel().sendBlockUpdated(this.pos, blockstate, blockstate, 3);
   }

   public CompoundNBT getData() {
      return this.entity.save(new CompoundNBT());
   }

   public ITextComponent getModifiedSuccess() {
      return new TranslationTextComponent("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   public ITextComponent getPrintSuccess(INBT p_198924_1_) {
      return new TranslationTextComponent("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), p_198924_1_.getPrettyDisplay());
   }

   public ITextComponent getPrintSuccess(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_) {
      return new TranslationTextComponent("commands.data.block.get", p_198922_1_, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", p_198922_2_), p_198922_4_);
   }
}
