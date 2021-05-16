package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.FishingPredicate;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LocationCheck;
import net.minecraft.loot.functions.EnchantWithLevels;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetDamage;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;

public class FishingLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public static final ILootCondition.IBuilder IN_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE));
   public static final ILootCondition.IBuilder IN_JUNGLE_HILLS = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE_HILLS));
   public static final ILootCondition.IBuilder IN_JUNGLE_EDGE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE_EDGE));
   public static final ILootCondition.IBuilder IN_BAMBOO_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE));
   public static final ILootCondition.IBuilder IN_MODIFIED_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.MODIFIED_JUNGLE));
   public static final ILootCondition.IBuilder IN_MODIFIED_JUNGLE_EDGE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.MODIFIED_JUNGLE_EDGE));
   public static final ILootCondition.IBuilder IN_BAMBOO_JUNGLE_HILLS = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE_HILLS));

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      p_accept_1_.accept(LootTables.FISHING, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(TableLootEntry.lootTableReference(LootTables.FISHING_JUNK).setWeight(10).setQuality(-2)).add(TableLootEntry.lootTableReference(LootTables.FISHING_TREASURE).setWeight(5).setQuality(2).when(EntityHasProperty.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().fishingHook(FishingPredicate.inOpenWater(true))))).add(TableLootEntry.lootTableReference(LootTables.FISHING_FISH).setWeight(85).setQuality(-1))));
      p_accept_1_.accept(LootTables.FISHING_FISH, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.COD).setWeight(60)).add(ItemLootEntry.lootTableItem(Items.SALMON).setWeight(25)).add(ItemLootEntry.lootTableItem(Items.TROPICAL_FISH).setWeight(2)).add(ItemLootEntry.lootTableItem(Items.PUFFERFISH).setWeight(13))));
      p_accept_1_.accept(LootTables.FISHING_JUNK, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Blocks.LILY_PAD).setWeight(17)).add(ItemLootEntry.lootTableItem(Items.LEATHER_BOOTS).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.9F)))).add(ItemLootEntry.lootTableItem(Items.LEATHER).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.BONE).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.POTION).setWeight(10).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218588_0_) -> {
         p_218588_0_.putString("Potion", "minecraft:water");
      })))).add(ItemLootEntry.lootTableItem(Items.STRING).setWeight(5)).add(ItemLootEntry.lootTableItem(Items.FISHING_ROD).setWeight(2).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.9F)))).add(ItemLootEntry.lootTableItem(Items.BOWL).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.STICK).setWeight(5)).add(ItemLootEntry.lootTableItem(Items.INK_SAC).setWeight(1).apply(SetCount.setCount(ConstantRange.exactly(10)))).add(ItemLootEntry.lootTableItem(Blocks.TRIPWIRE_HOOK).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)).add(ItemLootEntry.lootTableItem(Blocks.BAMBOO).when(IN_JUNGLE.or(IN_JUNGLE_HILLS).or(IN_JUNGLE_EDGE).or(IN_BAMBOO_JUNGLE).or(IN_MODIFIED_JUNGLE).or(IN_MODIFIED_JUNGLE_EDGE).or(IN_BAMBOO_JUNGLE_HILLS)).setWeight(10))));
      p_accept_1_.accept(LootTables.FISHING_TREASURE, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.NAME_TAG)).add(ItemLootEntry.lootTableItem(Items.SADDLE)).add(ItemLootEntry.lootTableItem(Items.BOW).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.25F))).apply(EnchantWithLevels.enchantWithLevels(ConstantRange.exactly(30)).allowTreasure())).add(ItemLootEntry.lootTableItem(Items.FISHING_ROD).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.25F))).apply(EnchantWithLevels.enchantWithLevels(ConstantRange.exactly(30)).allowTreasure())).add(ItemLootEntry.lootTableItem(Items.BOOK).apply(EnchantWithLevels.enchantWithLevels(ConstantRange.exactly(30)).allowTreasure())).add(ItemLootEntry.lootTableItem(Items.NAUTILUS_SHELL))));
   }
}
