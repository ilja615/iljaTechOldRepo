package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class CuredZombieVillagerTrigger extends AbstractCriterionTrigger<CuredZombieVillagerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

   public ResourceLocation getId() {
      return ID;
   }

   public CuredZombieVillagerTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "zombie", p_230241_3_);
      EntityPredicate.AndPredicate entitypredicate$andpredicate1 = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "villager", p_230241_3_);
      return new CuredZombieVillagerTrigger.Instance(p_230241_2_, entitypredicate$andpredicate, entitypredicate$andpredicate1);
   }

   public void trigger(ServerPlayerEntity p_192183_1_, ZombieEntity p_192183_2_, VillagerEntity p_192183_3_) {
      LootContext lootcontext = EntityPredicate.createContext(p_192183_1_, p_192183_2_);
      LootContext lootcontext1 = EntityPredicate.createContext(p_192183_1_, p_192183_3_);
      this.trigger(p_192183_1_, (p_233969_2_) -> {
         return p_233969_2_.matches(lootcontext, lootcontext1);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate zombie;
      private final EntityPredicate.AndPredicate villager;

      public Instance(EntityPredicate.AndPredicate p_i231535_1_, EntityPredicate.AndPredicate p_i231535_2_, EntityPredicate.AndPredicate p_i231535_3_) {
         super(CuredZombieVillagerTrigger.ID, p_i231535_1_);
         this.zombie = p_i231535_2_;
         this.villager = p_i231535_3_;
      }

      public static CuredZombieVillagerTrigger.Instance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY);
      }

      public boolean matches(LootContext p_233970_1_, LootContext p_233970_2_) {
         if (!this.zombie.matches(p_233970_1_)) {
            return false;
         } else {
            return this.villager.matches(p_233970_2_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("zombie", this.zombie.toJson(p_230240_1_));
         jsonobject.add("villager", this.villager.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
