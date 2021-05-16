package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;

public class ArgumentSerializer<T extends ArgumentType<?>> implements IArgumentSerializer<T> {
   private final Supplier<T> constructor;

   public ArgumentSerializer(Supplier<T> p_i47957_1_) {
      this.constructor = p_i47957_1_;
   }

   public void serializeToNetwork(T p_197072_1_, PacketBuffer p_197072_2_) {
   }

   public T deserializeFromNetwork(PacketBuffer p_197071_1_) {
      return this.constructor.get();
   }

   public void serializeToJson(T p_212244_1_, JsonObject p_212244_2_) {
   }
}
