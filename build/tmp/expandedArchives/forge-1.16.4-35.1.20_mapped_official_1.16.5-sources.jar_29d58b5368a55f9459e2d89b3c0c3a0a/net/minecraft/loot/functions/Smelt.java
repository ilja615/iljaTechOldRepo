package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Smelt extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();

   private Smelt(ILootCondition[] p_i46619_1_) {
      super(p_i46619_1_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.FURNACE_SMELT;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isEmpty()) {
         return p_215859_1_;
      } else {
         Optional<FurnaceRecipe> optional = p_215859_2_.getLevel().getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(p_215859_1_), p_215859_2_.getLevel());
         if (optional.isPresent()) {
            ItemStack itemstack = optional.get().getResultItem();
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(p_215859_1_.getCount() * itemstack.getCount()); //Forge: Support smelting returning multiple
               return itemstack1;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)p_215859_1_);
         return p_215859_1_;
      }
   }

   public static LootFunction.Builder<?> smelted() {
      return simpleBuilder(Smelt::new);
   }

   public static class Serializer extends LootFunction.Serializer<Smelt> {
      public Smelt deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return new Smelt(p_186530_3_);
      }
   }
}
