package net.minecraft.command.impl.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;

public interface IDataAccessor {
   void setData(CompoundNBT p_198925_1_) throws CommandSyntaxException;

   CompoundNBT getData() throws CommandSyntaxException;

   ITextComponent getModifiedSuccess();

   ITextComponent getPrintSuccess(INBT p_198924_1_);

   ITextComponent getPrintSuccess(NBTPathArgument.NBTPath p_198922_1_, double p_198922_2_, int p_198922_4_);
}
