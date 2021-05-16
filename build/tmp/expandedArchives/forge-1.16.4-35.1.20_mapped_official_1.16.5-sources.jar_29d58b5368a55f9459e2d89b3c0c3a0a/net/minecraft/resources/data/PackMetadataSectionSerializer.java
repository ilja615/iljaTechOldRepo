package net.minecraft.resources.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;

public class PackMetadataSectionSerializer implements IMetadataSectionSerializer<PackMetadataSection> {
   public PackMetadataSection fromJson(JsonObject p_195812_1_) {
      ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(p_195812_1_.get("description"));
      if (itextcomponent == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int i = JSONUtils.getAsInt(p_195812_1_, "pack_format");
         return new PackMetadataSection(itextcomponent, i);
      }
   }

   public String getMetadataSectionName() {
      return "pack";
   }
}
