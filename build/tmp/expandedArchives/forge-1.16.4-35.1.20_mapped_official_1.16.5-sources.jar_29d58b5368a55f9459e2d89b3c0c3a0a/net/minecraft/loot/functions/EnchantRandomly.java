package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomly extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<Enchantment> enchantments;

   private EnchantRandomly(ILootCondition[] p_i51238_1_, Collection<Enchantment> p_i51238_2_) {
      super(p_i51238_1_);
      this.enchantments = ImmutableList.copyOf(p_i51238_2_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.ENCHANT_RANDOMLY;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Random random = p_215859_2_.getRandom();
      Enchantment enchantment;
      if (this.enchantments.isEmpty()) {
         boolean flag = p_215859_1_.getItem() == Items.BOOK;
         List<Enchantment> list = Registry.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter((p_237421_2_) -> {
            return flag || p_237421_2_.canEnchant(p_215859_1_);
         }).collect(Collectors.toList());
         if (list.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)p_215859_1_);
            return p_215859_1_;
         }

         enchantment = list.get(random.nextInt(list.size()));
      } else {
         enchantment = this.enchantments.get(random.nextInt(this.enchantments.size()));
      }

      return enchantItem(p_215859_1_, enchantment, random);
   }

   private static ItemStack enchantItem(ItemStack p_237420_0_, Enchantment p_237420_1_, Random p_237420_2_) {
      int i = MathHelper.nextInt(p_237420_2_, p_237420_1_.getMinLevel(), p_237420_1_.getMaxLevel());
      if (p_237420_0_.getItem() == Items.BOOK) {
         p_237420_0_ = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantedBookItem.addEnchantment(p_237420_0_, new EnchantmentData(p_237420_1_, i));
      } else {
         p_237420_0_.enchant(p_237420_1_, i);
      }

      return p_237420_0_;
   }

   public static LootFunction.Builder<?> randomApplicableEnchantment() {
      return simpleBuilder((p_237422_0_) -> {
         return new EnchantRandomly(p_237422_0_, ImmutableList.of());
      });
   }

   public static class Builder extends LootFunction.Builder<EnchantRandomly.Builder> {
      private final Set<Enchantment> enchantments = Sets.newHashSet();

      protected EnchantRandomly.Builder getThis() {
         return this;
      }

      public EnchantRandomly.Builder withEnchantment(Enchantment p_237424_1_) {
         this.enchantments.add(p_237424_1_);
         return this;
      }

      public ILootFunction build() {
         return new EnchantRandomly(this.getConditions(), this.enchantments);
      }
   }

   public static class Serializer extends LootFunction.Serializer<EnchantRandomly> {
      public void serialize(JsonObject p_230424_1_, EnchantRandomly p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         if (!p_230424_2_.enchantments.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Enchantment enchantment : p_230424_2_.enchantments) {
               ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchantment);
               if (resourcelocation == null) {
                  throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
               }

               jsonarray.add(new JsonPrimitive(resourcelocation.toString()));
            }

            p_230424_1_.add("enchantments", jsonarray);
         }

      }

      public EnchantRandomly deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         List<Enchantment> list = Lists.newArrayList();
         if (p_186530_1_.has("enchantments")) {
            for(JsonElement jsonelement : JSONUtils.getAsJsonArray(p_186530_1_, "enchantments")) {
               String s = JSONUtils.convertToString(jsonelement, "enchantment");
               Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown enchantment '" + s + "'");
               });
               list.add(enchantment);
            }
         }

         return new EnchantRandomly(p_186530_3_, list);
      }
   }
}
