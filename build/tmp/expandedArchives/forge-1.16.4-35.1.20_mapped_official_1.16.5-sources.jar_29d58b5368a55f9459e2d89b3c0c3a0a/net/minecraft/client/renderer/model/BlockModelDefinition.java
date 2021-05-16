package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelDefinition {
   private final Map<String, VariantList> variants = Maps.newLinkedHashMap();
   private Multipart multiPart;

   public static BlockModelDefinition fromStream(BlockModelDefinition.ContainerHolder p_209577_0_, Reader p_209577_1_) {
      return JSONUtils.fromJson(p_209577_0_.gson, p_209577_1_, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map<String, VariantList> p_i46572_1_, Multipart p_i46572_2_) {
      this.multiPart = p_i46572_2_;
      this.variants.putAll(p_i46572_1_);
   }

   public BlockModelDefinition(List<BlockModelDefinition> p_i46222_1_) {
      BlockModelDefinition blockmodeldefinition = null;

      for(BlockModelDefinition blockmodeldefinition1 : p_i46222_1_) {
         if (blockmodeldefinition1.isMultiPart()) {
            this.variants.clear();
            blockmodeldefinition = blockmodeldefinition1;
         }

         this.variants.putAll(blockmodeldefinition1.variants);
      }

      if (blockmodeldefinition != null) {
         this.multiPart = blockmodeldefinition.multiPart;
      }

   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         if (p_equals_1_ instanceof BlockModelDefinition) {
            BlockModelDefinition blockmodeldefinition = (BlockModelDefinition)p_equals_1_;
            if (this.variants.equals(blockmodeldefinition.variants)) {
               return this.isMultiPart() ? this.multiPart.equals(blockmodeldefinition.multiPart) : !blockmodeldefinition.isMultiPart();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
   }

   public Map<String, VariantList> getVariants() {
      return this.variants;
   }

   public boolean isMultiPart() {
      return this.multiPart != null;
   }

   public Multipart getMultiPart() {
      return this.multiPart;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class ContainerHolder {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(VariantList.class, new VariantList.Deserializer()).registerTypeAdapter(Multipart.class, new Multipart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateContainer<Block, BlockState> definition;

      public StateContainer<Block, BlockState> getDefinition() {
         return this.definition;
      }

      public void setDefinition(StateContainer<Block, BlockState> p_209573_1_) {
         this.definition = p_209573_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
      public BlockModelDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Map<String, VariantList> map = this.getVariants(p_deserialize_3_, jsonobject);
         Multipart multipart = this.getMultiPart(p_deserialize_3_, jsonobject);
         if (!map.isEmpty() || multipart != null && !multipart.getMultiVariants().isEmpty()) {
            return new BlockModelDefinition(map, multipart);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, VariantList> getVariants(JsonDeserializationContext p_187999_1_, JsonObject p_187999_2_) {
         Map<String, VariantList> map = Maps.newHashMap();
         if (p_187999_2_.has("variants")) {
            JsonObject jsonobject = JSONUtils.getAsJsonObject(p_187999_2_, "variants");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), p_187999_1_.deserialize(entry.getValue(), VariantList.class));
            }
         }

         return map;
      }

      @Nullable
      protected Multipart getMultiPart(JsonDeserializationContext p_187998_1_, JsonObject p_187998_2_) {
         if (!p_187998_2_.has("multipart")) {
            return null;
         } else {
            JsonArray jsonarray = JSONUtils.getAsJsonArray(p_187998_2_, "multipart");
            return p_187998_1_.deserialize(jsonarray, Multipart.class);
         }
      }
   }
}
