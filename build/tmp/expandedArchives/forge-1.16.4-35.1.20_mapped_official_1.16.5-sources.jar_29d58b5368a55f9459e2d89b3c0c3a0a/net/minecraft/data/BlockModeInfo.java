package net.minecraft.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class BlockModeInfo<T> {
   private final String key;
   private final Function<T, JsonElement> serializer;

   public BlockModeInfo(String p_i232543_1_, Function<T, JsonElement> p_i232543_2_) {
      this.key = p_i232543_1_;
      this.serializer = p_i232543_2_;
   }

   public BlockModeInfo<T>.Field withValue(T p_240213_1_) {
      return new BlockModeInfo.Field(p_240213_1_);
   }

   public String toString() {
      return this.key;
   }

   public class Field {
      private final T value;

      public Field(T p_i232544_2_) {
         this.value = p_i232544_2_;
      }

      public void addToVariant(JsonObject p_240217_1_) {
         p_240217_1_.add(BlockModeInfo.this.key, BlockModeInfo.this.serializer.apply(this.value));
      }

      public String toString() {
         return BlockModeInfo.this.key + "=" + this.value;
      }
   }
}
