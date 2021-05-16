package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger extends AbstractCriterionTrigger<TameAnimalTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "entity", p_230241_3_);
      return new TameAnimalTrigger.Instance(p_230241_2_, entitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_193178_1_, AnimalEntity p_193178_2_) {
      LootContext lootcontext = EntityPredicate.createContext(p_193178_1_, p_193178_2_);
      this.trigger(p_193178_1_, (p_227251_1_) -> {
         return p_227251_1_.matches(lootcontext);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate entity;

      public Instance(EntityPredicate.AndPredicate p_i231963_1_, EntityPredicate.AndPredicate p_i231963_2_) {
         super(TameAnimalTrigger.ID, p_i231963_1_);
         this.entity = p_i231963_2_;
      }

      public static TameAnimalTrigger.Instance tamedAnimal() {
         return new TameAnimalTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY);
      }

      public static TameAnimalTrigger.Instance tamedAnimal(EntityPredicate p_215124_0_) {
         return new TameAnimalTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(p_215124_0_));
      }

      public boolean matches(LootContext p_236323_1_) {
         return this.entity.matches(p_236323_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("entity", this.entity.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
