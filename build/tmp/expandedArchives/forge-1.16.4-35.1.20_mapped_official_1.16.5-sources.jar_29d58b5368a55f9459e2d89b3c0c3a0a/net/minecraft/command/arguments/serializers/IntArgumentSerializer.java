package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class IntArgumentSerializer implements IArgumentSerializer<IntegerArgumentType> {
   public void serializeToNetwork(IntegerArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      boolean flag = p_197072_1_.getMinimum() != Integer.MIN_VALUE;
      boolean flag1 = p_197072_1_.getMaximum() != Integer.MAX_VALUE;
      p_197072_2_.writeByte(BrigadierSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_197072_2_.writeInt(p_197072_1_.getMinimum());
      }

      if (flag1) {
         p_197072_2_.writeInt(p_197072_1_.getMaximum());
      }

   }

   public IntegerArgumentType deserializeFromNetwork(PacketBuffer p_197071_1_) {
      byte b0 = p_197071_1_.readByte();
      int i = BrigadierSerializers.numberHasMin(b0) ? p_197071_1_.readInt() : Integer.MIN_VALUE;
      int j = BrigadierSerializers.numberHasMax(b0) ? p_197071_1_.readInt() : Integer.MAX_VALUE;
      return IntegerArgumentType.integer(i, j);
   }

   public void serializeToJson(IntegerArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != Integer.MIN_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Integer.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}
