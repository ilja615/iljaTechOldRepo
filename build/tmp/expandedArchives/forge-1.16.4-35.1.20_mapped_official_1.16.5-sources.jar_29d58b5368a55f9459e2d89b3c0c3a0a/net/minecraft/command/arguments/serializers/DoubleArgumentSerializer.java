package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class DoubleArgumentSerializer implements IArgumentSerializer<DoubleArgumentType> {
   public void serializeToNetwork(DoubleArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      boolean flag = p_197072_1_.getMinimum() != -Double.MAX_VALUE;
      boolean flag1 = p_197072_1_.getMaximum() != Double.MAX_VALUE;
      p_197072_2_.writeByte(BrigadierSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_197072_2_.writeDouble(p_197072_1_.getMinimum());
      }

      if (flag1) {
         p_197072_2_.writeDouble(p_197072_1_.getMaximum());
      }

   }

   public DoubleArgumentType deserializeFromNetwork(PacketBuffer p_197071_1_) {
      byte b0 = p_197071_1_.readByte();
      double d0 = BrigadierSerializers.numberHasMin(b0) ? p_197071_1_.readDouble() : -Double.MAX_VALUE;
      double d1 = BrigadierSerializers.numberHasMax(b0) ? p_197071_1_.readDouble() : Double.MAX_VALUE;
      return DoubleArgumentType.doubleArg(d0, d1);
   }

   public void serializeToJson(DoubleArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != -Double.MAX_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Double.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}
