package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const.PrimitiveType;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.ResourceLocation;

public class NamespacedSchema extends Schema {
   public static final PrimitiveCodec<String> NAMESPACED_STRING_CODEC = new PrimitiveCodec<String>() {
      public <T> DataResult<String> read(DynamicOps<T> p_read_1_, T p_read_2_) {
         return p_read_1_.getStringValue(p_read_2_).map(NamespacedSchema::ensureNamespaced);
      }

      public <T> T write(DynamicOps<T> p_write_1_, String p_write_2_) {
         return p_write_1_.createString(p_write_2_);
      }

      public String toString() {
         return "NamespacedString";
      }
   };
   private static final Type<String> NAMESPACED_STRING = new PrimitiveType<>(NAMESPACED_STRING_CODEC);

   public NamespacedSchema(int p_i49612_1_, Schema p_i49612_2_) {
      super(p_i49612_1_, p_i49612_2_);
   }

   public static String ensureNamespaced(String p_206477_0_) {
      ResourceLocation resourcelocation = ResourceLocation.tryParse(p_206477_0_);
      return resourcelocation != null ? resourcelocation.toString() : p_206477_0_;
   }

   public static Type<String> namespacedString() {
      return NAMESPACED_STRING;
   }

   public Type<?> getChoiceType(TypeReference p_getChoiceType_1_, String p_getChoiceType_2_) {
      return super.getChoiceType(p_getChoiceType_1_, ensureNamespaced(p_getChoiceType_2_));
   }
}
