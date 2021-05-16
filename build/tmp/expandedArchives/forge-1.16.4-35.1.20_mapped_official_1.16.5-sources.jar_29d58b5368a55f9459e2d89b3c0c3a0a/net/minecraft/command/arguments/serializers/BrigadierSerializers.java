package net.minecraft.command.arguments.serializers;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class BrigadierSerializers {
   public static void bootstrap() {
      ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new ArgumentSerializer<>(BoolArgumentType::bool));
      ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
      ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
      ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntArgumentSerializer());
      ArgumentTypes.register("brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
      ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
   }

   public static byte createNumberFlags(boolean p_197508_0_, boolean p_197508_1_) {
      byte b0 = 0;
      if (p_197508_0_) {
         b0 = (byte)(b0 | 1);
      }

      if (p_197508_1_) {
         b0 = (byte)(b0 | 2);
      }

      return b0;
   }

   public static boolean numberHasMin(byte p_197510_0_) {
      return (p_197510_0_ & 1) != 0;
   }

   public static boolean numberHasMax(byte p_197509_0_) {
      return (p_197509_0_ & 2) != 0;
   }
}
