package net.minecraft.client.renderer.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Selector {
   private final ICondition condition;
   private final VariantList variant;

   public Selector(ICondition p_i46562_1_, VariantList p_i46562_2_) {
      if (p_i46562_1_ == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if (p_i46562_2_ == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.condition = p_i46562_1_;
         this.variant = p_i46562_2_;
      }
   }

   public VariantList getVariant() {
      return this.variant;
   }

   public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_188166_1_) {
      return this.condition.getPredicate(p_188166_1_);
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Selector> {
      public Selector deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         return new Selector(this.getSelector(jsonobject), p_deserialize_3_.deserialize(jsonobject.get("apply"), VariantList.class));
      }

      private ICondition getSelector(JsonObject p_188159_1_) {
         return p_188159_1_.has("when") ? getCondition(JSONUtils.getAsJsonObject(p_188159_1_, "when")) : ICondition.TRUE;
      }

      @VisibleForTesting
      static ICondition getCondition(JsonObject p_188158_0_) {
         Set<Entry<String, JsonElement>> set = p_188158_0_.entrySet();
         if (set.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (set.size() == 1) {
            if (p_188158_0_.has("OR")) {
               List<ICondition> list1 = Streams.stream(JSONUtils.getAsJsonArray(p_188158_0_, "OR")).map((p_200692_0_) -> {
                  return getCondition(p_200692_0_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(list1);
            } else if (p_188158_0_.has("AND")) {
               List<ICondition> list = Streams.stream(JSONUtils.getAsJsonArray(p_188158_0_, "AND")).map((p_200691_0_) -> {
                  return getCondition(p_200691_0_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(list);
            } else {
               return getKeyValueCondition(set.iterator().next());
            }
         } else {
            return new AndCondition(set.stream().map(Selector.Deserializer::getKeyValueCondition).collect(Collectors.toList()));
         }
      }

      private static ICondition getKeyValueCondition(Entry<String, JsonElement> p_188161_0_) {
         return new PropertyValueCondition(p_188161_0_.getKey(), p_188161_0_.getValue().getAsString());
      }
   }
}
