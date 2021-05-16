package net.minecraft.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.functions.EnchantRandomly;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class PiglinBarteringAddition implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      p_accept_1_.accept(LootTables.PIGLIN_BARTERING, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.BOOK).setWeight(5).apply((new EnchantRandomly.Builder()).withEnchantment(Enchantments.SOUL_SPEED))).add(ItemLootEntry.lootTableItem(Items.IRON_BOOTS).setWeight(8).apply((new EnchantRandomly.Builder()).withEnchantment(Enchantments.SOUL_SPEED))).add(ItemLootEntry.lootTableItem(Items.POTION).setWeight(8).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_239833_0_) -> {
         p_239833_0_.putString("Potion", "minecraft:fire_resistance");
      })))).add(ItemLootEntry.lootTableItem(Items.SPLASH_POTION).setWeight(8).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_239832_0_) -> {
         p_239832_0_.putString("Potion", "minecraft:fire_resistance");
      })))).add(ItemLootEntry.lootTableItem(Items.POTION).setWeight(10).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_243684_0_) -> {
         p_243684_0_.putString("Potion", "minecraft:water");
      })))).add(ItemLootEntry.lootTableItem(Items.IRON_NUGGET).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(10.0F, 36.0F)))).add(ItemLootEntry.lootTableItem(Items.ENDER_PEARL).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Items.STRING).setWeight(20).apply(SetCount.setCount(RandomValueRange.between(3.0F, 9.0F)))).add(ItemLootEntry.lootTableItem(Items.QUARTZ).setWeight(20).apply(SetCount.setCount(RandomValueRange.between(5.0F, 12.0F)))).add(ItemLootEntry.lootTableItem(Items.OBSIDIAN).setWeight(40)).add(ItemLootEntry.lootTableItem(Items.CRYING_OBSIDIAN).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F)))).add(ItemLootEntry.lootTableItem(Items.FIRE_CHARGE).setWeight(40)).add(ItemLootEntry.lootTableItem(Items.LEATHER).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Items.SOUL_SAND).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(2.0F, 8.0F)))).add(ItemLootEntry.lootTableItem(Items.NETHER_BRICK).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(2.0F, 8.0F)))).add(ItemLootEntry.lootTableItem(Items.SPECTRAL_ARROW).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(6.0F, 12.0F)))).add(ItemLootEntry.lootTableItem(Items.GRAVEL).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(8.0F, 16.0F)))).add(ItemLootEntry.lootTableItem(Items.BLACKSTONE).setWeight(40).apply(SetCount.setCount(RandomValueRange.between(8.0F, 16.0F))))));
   }
}
