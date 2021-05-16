package net.minecraft.resources.data;

import com.google.gson.JsonObject;

public interface IMetadataSectionSerializer<T> {
   String getMetadataSectionName();

   T fromJson(JsonObject p_195812_1_);
}
