package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class LongArgumentSerializer implements IArgumentSerializer<LongArgumentType> {
   public void serializeToNetwork(LongArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      boolean flag = p_197072_1_.getMinimum() != Long.MIN_VALUE;
      boolean flag1 = p_197072_1_.getMaximum() != Long.MAX_VALUE;
      p_197072_2_.writeByte(BrigadierSerializers.createNumberFlags(flag, flag1));
      if (flag) {
         p_197072_2_.writeLong(p_197072_1_.getMinimum());
      }

      if (flag1) {
         p_197072_2_.writeLong(p_197072_1_.getMaximum());
      }

   }

   public LongArgumentType deserializeFromNetwork(PacketBuffer p_197071_1_) {
      byte b0 = p_197071_1_.readByte();
      long i = BrigadierSerializers.numberHasMin(b0) ? p_197071_1_.readLong() : Long.MIN_VALUE;
      long j = BrigadierSerializers.numberHasMax(b0) ? p_197071_1_.readLong() : Long.MAX_VALUE;
      return LongArgumentType.longArg(i, j);
   }

   public void serializeToJson(LongArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != Long.MIN_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Long.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}
