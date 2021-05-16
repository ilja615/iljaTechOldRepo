package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger extends AbstractCriterionTrigger<EntityHurtPlayerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

   public ResourceLocation getId() {
      return ID;
   }

   public EntityHurtPlayerTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      DamagePredicate damagepredicate = DamagePredicate.fromJson(p_230241_1_.get("damage"));
      return new EntityHurtPlayerTrigger.Instance(p_230241_2_, damagepredicate);
   }

   public void trigger(ServerPlayerEntity p_192200_1_, DamageSource p_192200_2_, float p_192200_3_, float p_192200_4_, boolean p_192200_5_) {
      this.trigger(p_192200_1_, (p_226603_5_) -> {
         return p_226603_5_.matches(p_192200_1_, p_192200_2_, p_192200_3_, p_192200_4_, p_192200_5_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final DamagePredicate damage;

      public Instance(EntityPredicate.AndPredicate p_i231572_1_, DamagePredicate p_i231572_2_) {
         super(EntityHurtPlayerTrigger.ID, p_i231572_1_);
         this.damage = p_i231572_2_;
      }

      public static EntityHurtPlayerTrigger.Instance entityHurtPlayer(DamagePredicate.Builder p_203921_0_) {
         return new EntityHurtPlayerTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203921_0_.build());
      }

      public boolean matches(ServerPlayerEntity p_192263_1_, DamageSource p_192263_2_, float p_192263_3_, float p_192263_4_, boolean p_192263_5_) {
         return this.damage.matches(p_192263_1_, p_192263_2_, p_192263_3_, p_192263_4_, p_192263_5_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("damage", this.damage.serializeToJson());
         return jsonobject;
      }
   }
}
