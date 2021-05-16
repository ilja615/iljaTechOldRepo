package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class PlayerGeneratesContainerLootTrigger extends AbstractCriterionTrigger<PlayerGeneratesContainerLootTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("player_generates_container_loot");

   public ResourceLocation getId() {
      return ID;
   }

   protected PlayerGeneratesContainerLootTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_230241_1_, "loot_table"));
      return new PlayerGeneratesContainerLootTrigger.Instance(p_230241_2_, resourcelocation);
   }

   public void trigger(ServerPlayerEntity p_235478_1_, ResourceLocation p_235478_2_) {
      this.trigger(p_235478_1_, (p_235477_1_) -> {
         return p_235477_1_.matches(p_235478_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ResourceLocation lootTable;

      public Instance(EntityPredicate.AndPredicate p_i231684_1_, ResourceLocation p_i231684_2_) {
         super(PlayerGeneratesContainerLootTrigger.ID, p_i231684_1_);
         this.lootTable = p_i231684_2_;
      }

      public static PlayerGeneratesContainerLootTrigger.Instance lootTableUsed(ResourceLocation p_235481_0_) {
         return new PlayerGeneratesContainerLootTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_235481_0_);
      }

      public boolean matches(ResourceLocation p_235482_1_) {
         return this.lootTable.equals(p_235482_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.addProperty("loot_table", this.lootTable.toString());
         return jsonobject;
      }
   }
}
