package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class FloatArgumentSerializer implements IArgumentSerializer<FloatArgumentType> {
   public void serializeToNetwork(FloatArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      boolean flag = p_197072_1_.getMinimum() != -Float.MAX_VALUE;
      boolean flag1 = p_197072_1_.getMaximum() != Float.MAX_VALUE;
      p_197072_2_.writeByte(BrigadierSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_197072_2_.writeFloat(p_197072_1_.getMinimum());
      }

      if (flag1) {
         p_197072_2_.writeFloat(p_197072_1_.getMaximum());
      }

   }

   public FloatArgumentType deserializeFromNetwork(PacketBuffer p_197071_1_) {
      byte b0 = p_197071_1_.readByte();
      float f = BrigadierSerializers.numberHasMin(b0) ? p_197071_1_.readFloat() : -Float.MAX_VALUE;
      float f1 = BrigadierSerializers.numberHasMax(b0) ? p_197071_1_.readFloat() : Float.MAX_VALUE;
      return FloatArgumentType.floatArg(f, f1);
   }

   public void serializeToJson(FloatArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != -Float.MAX_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Float.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}
