package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class PlacedBlockTrigger extends AbstractCriterionTrigger<PlacedBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");

   public ResourceLocation getId() {
      return ID;
   }

   public PlacedBlockTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      Block block = deserializeBlock(p_230241_1_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_230241_1_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_226948_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_226948_1_ + ":");
         });
      }

      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_230241_1_.get("location"));
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      return new PlacedBlockTrigger.Instance(p_230241_2_, block, statepropertiespredicate, locationpredicate, itempredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_226950_0_) {
      if (p_226950_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_226950_0_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_193173_1_, BlockPos p_193173_2_, ItemStack p_193173_3_) {
      BlockState blockstate = p_193173_1_.getLevel().getBlockState(p_193173_2_);
      this.trigger(p_193173_1_, (p_226949_4_) -> {
         return p_226949_4_.matches(blockstate, p_193173_2_, p_193173_1_.getLevel(), p_193173_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate p_i231810_1_, @Nullable Block p_i231810_2_, StatePropertiesPredicate p_i231810_3_, LocationPredicate p_i231810_4_, ItemPredicate p_i231810_5_) {
         super(PlacedBlockTrigger.ID, p_i231810_1_);
         this.block = p_i231810_2_;
         this.state = p_i231810_3_;
         this.location = p_i231810_4_;
         this.item = p_i231810_5_;
      }

      public static PlacedBlockTrigger.Instance placedBlock(Block p_203934_0_) {
         return new PlacedBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203934_0_, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(BlockState p_193210_1_, BlockPos p_193210_2_, ServerWorld p_193210_3_, ItemStack p_193210_4_) {
         if (this.block != null && !p_193210_1_.is(this.block)) {
            return false;
         } else if (!this.state.matches(p_193210_1_)) {
            return false;
         } else if (!this.location.matches(p_193210_3_, (float)p_193210_2_.getX(), (float)p_193210_2_.getY(), (float)p_193210_2_.getZ())) {
            return false;
         } else {
            return this.item.matches(p_193210_4_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}
