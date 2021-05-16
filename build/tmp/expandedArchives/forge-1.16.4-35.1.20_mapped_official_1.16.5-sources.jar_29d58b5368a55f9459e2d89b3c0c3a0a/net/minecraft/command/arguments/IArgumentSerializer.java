package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketBuffer;

public interface IArgumentSerializer<T extends ArgumentType<?>> {
   void serializeToNetwork(T p_197072_1_, PacketBuffer p_197072_2_);

   T deserializeFromNetwork(PacketBuffer p_197071_1_);

   void serializeToJson(T p_212244_1_, JsonObject p_212244_2_);
}
