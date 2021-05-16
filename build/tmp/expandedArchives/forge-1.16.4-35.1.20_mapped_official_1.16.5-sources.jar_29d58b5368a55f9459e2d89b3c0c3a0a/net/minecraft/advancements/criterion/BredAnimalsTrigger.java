package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class BredAnimalsTrigger extends AbstractCriterionTrigger<BredAnimalsTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");

   public ResourceLocation getId() {
      return ID;
   }

   public BredAnimalsTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "parent", p_230241_3_);
      EntityPredicate.AndPredicate entitypredicate$andpredicate1 = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "partner", p_230241_3_);
      EntityPredicate.AndPredicate entitypredicate$andpredicate2 = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "child", p_230241_3_);
      return new BredAnimalsTrigger.Instance(p_230241_2_, entitypredicate$andpredicate, entitypredicate$andpredicate1, entitypredicate$andpredicate2);
   }

   public void trigger(ServerPlayerEntity p_192168_1_, AnimalEntity p_192168_2_, AnimalEntity p_192168_3_, @Nullable AgeableEntity p_192168_4_) {
      LootContext lootcontext = EntityPredicate.createContext(p_192168_1_, p_192168_2_);
      LootContext lootcontext1 = EntityPredicate.createContext(p_192168_1_, p_192168_3_);
      LootContext lootcontext2 = p_192168_4_ != null ? EntityPredicate.createContext(p_192168_1_, p_192168_4_) : null;
      this.trigger(p_192168_1_, (p_233510_3_) -> {
         return p_233510_3_.matches(lootcontext, lootcontext1, lootcontext2);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate parent;
      private final EntityPredicate.AndPredicate partner;
      private final EntityPredicate.AndPredicate child;

      public Instance(EntityPredicate.AndPredicate p_i231484_1_, EntityPredicate.AndPredicate p_i231484_2_, EntityPredicate.AndPredicate p_i231484_3_, EntityPredicate.AndPredicate p_i231484_4_) {
         super(BredAnimalsTrigger.ID, p_i231484_1_);
         this.parent = p_i231484_2_;
         this.partner = p_i231484_3_;
         this.child = p_i231484_4_;
      }

      public static BredAnimalsTrigger.Instance bredAnimals() {
         return new BredAnimalsTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY);
      }

      public static BredAnimalsTrigger.Instance bredAnimals(EntityPredicate.Builder p_203909_0_) {
         return new BredAnimalsTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(p_203909_0_.build()));
      }

      public static BredAnimalsTrigger.Instance bredAnimals(EntityPredicate p_241332_0_, EntityPredicate p_241332_1_, EntityPredicate p_241332_2_) {
         return new BredAnimalsTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.wrap(p_241332_0_), EntityPredicate.AndPredicate.wrap(p_241332_1_), EntityPredicate.AndPredicate.wrap(p_241332_2_));
      }

      public boolean matches(LootContext p_233511_1_, LootContext p_233511_2_, @Nullable LootContext p_233511_3_) {
         if (this.child == EntityPredicate.AndPredicate.ANY || p_233511_3_ != null && this.child.matches(p_233511_3_)) {
            return this.parent.matches(p_233511_1_) && this.partner.matches(p_233511_2_) || this.parent.matches(p_233511_2_) && this.partner.matches(p_233511_1_);
         } else {
            return false;
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("parent", this.parent.toJson(p_230240_1_));
         jsonobject.add("partner", this.partner.toJson(p_230240_1_));
         jsonobject.add("child", this.child.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
