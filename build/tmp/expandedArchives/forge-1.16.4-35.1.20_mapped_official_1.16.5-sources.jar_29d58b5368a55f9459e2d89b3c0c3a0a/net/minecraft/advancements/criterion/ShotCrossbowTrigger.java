package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ShotCrossbowTrigger extends AbstractCriterionTrigger<ShotCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public ShotCrossbowTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      return new ShotCrossbowTrigger.Instance(p_230241_2_, itempredicate);
   }

   public void trigger(ServerPlayerEntity p_215111_1_, ItemStack p_215111_2_) {
      this.trigger(p_215111_1_, (p_227037_1_) -> {
         return p_227037_1_.matches(p_215111_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate p_i231880_1_, ItemPredicate p_i231880_2_) {
         super(ShotCrossbowTrigger.ID, p_i231880_1_);
         this.item = p_i231880_2_;
      }

      public static ShotCrossbowTrigger.Instance shotCrossbow(IItemProvider p_215122_0_) {
         return new ShotCrossbowTrigger.Instance(EntityPredicate.AndPredicate.ANY, ItemPredicate.Builder.item().of(p_215122_0_).build());
      }

      public boolean matches(ItemStack p_215121_1_) {
         return this.item.matches(p_215121_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}
