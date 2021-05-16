package net.minecraft.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;

public class BlockModelWriter implements Supplier<JsonElement> {
   private final ResourceLocation parent;

   public BlockModelWriter(ResourceLocation p_i232545_1_) {
      this.parent = p_i232545_1_;
   }

   public JsonElement get() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("parent", this.parent.toString());
      return jsonobject;
   }
}
