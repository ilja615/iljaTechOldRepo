package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RightClickBlockWithItemTrigger extends AbstractCriterionTrigger<RightClickBlockWithItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("item_used_on_block");

   public ResourceLocation getId() {
      return ID;
   }

   public RightClickBlockWithItemTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_230241_1_.get("location"));
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      return new RightClickBlockWithItemTrigger.Instance(p_230241_2_, locationpredicate, itempredicate);
   }

   public void trigger(ServerPlayerEntity p_226695_1_, BlockPos p_226695_2_, ItemStack p_226695_3_) {
      BlockState blockstate = p_226695_1_.getLevel().getBlockState(p_226695_2_);
      this.trigger(p_226695_1_, (p_226694_4_) -> {
         return p_226694_4_.matches(blockstate, p_226695_1_.getLevel(), p_226695_2_, p_226695_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate location;
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate p_i231602_1_, LocationPredicate p_i231602_2_, ItemPredicate p_i231602_3_) {
         super(RightClickBlockWithItemTrigger.ID, p_i231602_1_);
         this.location = p_i231602_2_;
         this.item = p_i231602_3_;
      }

      public static RightClickBlockWithItemTrigger.Instance itemUsedOnBlock(LocationPredicate.Builder p_234852_0_, ItemPredicate.Builder p_234852_1_) {
         return new RightClickBlockWithItemTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_234852_0_.build(), p_234852_1_.build());
      }

      public boolean matches(BlockState p_226700_1_, ServerWorld p_226700_2_, BlockPos p_226700_3_, ItemStack p_226700_4_) {
         return !this.location.matches(p_226700_2_, (double)p_226700_3_.getX() + 0.5D, (double)p_226700_3_.getY() + 0.5D, (double)p_226700_3_.getZ() + 0.5D) ? false : this.item.matches(p_226700_4_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}
