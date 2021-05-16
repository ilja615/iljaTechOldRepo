package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public abstract class CriterionInstance implements ICriterionInstance {
   private final ResourceLocation criterion;
   private final EntityPredicate.AndPredicate player;

   public CriterionInstance(ResourceLocation p_i231464_1_, EntityPredicate.AndPredicate p_i231464_2_) {
      this.criterion = p_i231464_1_;
      this.player = p_i231464_2_;
   }

   public ResourceLocation getCriterion() {
      return this.criterion;
   }

   protected EntityPredicate.AndPredicate getPlayerPredicate() {
      return this.player;
   }

   public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("player", this.player.toJson(p_230240_1_));
      return jsonobject;
   }

   public String toString() {
      return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
   }
}
