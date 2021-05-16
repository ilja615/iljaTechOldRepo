package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class GiftLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      p_accept_1_.accept(LootTables.CAT_MORNING_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.RABBIT_HIDE).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.RABBIT_FOOT).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.CHICKEN).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.FEATHER).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.STRING).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.PHANTOM_MEMBRANE).setWeight(2))));
      p_accept_1_.accept(LootTables.ARMORER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.CHAINMAIL_HELMET)).add(ItemLootEntry.lootTableItem(Items.CHAINMAIL_CHESTPLATE)).add(ItemLootEntry.lootTableItem(Items.CHAINMAIL_LEGGINGS)).add(ItemLootEntry.lootTableItem(Items.CHAINMAIL_BOOTS))));
      p_accept_1_.accept(LootTables.BUTCHER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.COOKED_RABBIT)).add(ItemLootEntry.lootTableItem(Items.COOKED_CHICKEN)).add(ItemLootEntry.lootTableItem(Items.COOKED_PORKCHOP)).add(ItemLootEntry.lootTableItem(Items.COOKED_BEEF)).add(ItemLootEntry.lootTableItem(Items.COOKED_MUTTON))));
      p_accept_1_.accept(LootTables.CARTOGRAPHER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.MAP)).add(ItemLootEntry.lootTableItem(Items.PAPER))));
      p_accept_1_.accept(LootTables.CLERIC_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.REDSTONE)).add(ItemLootEntry.lootTableItem(Items.LAPIS_LAZULI))));
      p_accept_1_.accept(LootTables.FARMER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.BREAD)).add(ItemLootEntry.lootTableItem(Items.PUMPKIN_PIE)).add(ItemLootEntry.lootTableItem(Items.COOKIE))));
      p_accept_1_.accept(LootTables.FISHERMAN_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.COD)).add(ItemLootEntry.lootTableItem(Items.SALMON))));
      p_accept_1_.accept(LootTables.FLETCHER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.ARROW).setWeight(26)).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218596_0_) -> {
         p_218596_0_.putString("Potion", "minecraft:swiftness");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218597_0_) -> {
         p_218597_0_.putString("Potion", "minecraft:slowness");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218608_0_) -> {
         p_218608_0_.putString("Potion", "minecraft:strength");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218606_0_) -> {
         p_218606_0_.putString("Potion", "minecraft:healing");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218603_0_) -> {
         p_218603_0_.putString("Potion", "minecraft:harming");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218607_0_) -> {
         p_218607_0_.putString("Potion", "minecraft:leaping");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218601_0_) -> {
         p_218601_0_.putString("Potion", "minecraft:regeneration");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218600_0_) -> {
         p_218600_0_.putString("Potion", "minecraft:fire_resistance");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218599_0_) -> {
         p_218599_0_.putString("Potion", "minecraft:water_breathing");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218598_0_) -> {
         p_218598_0_.putString("Potion", "minecraft:invisibility");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218605_0_) -> {
         p_218605_0_.putString("Potion", "minecraft:night_vision");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218602_0_) -> {
         p_218602_0_.putString("Potion", "minecraft:weakness");
      })))).add(ItemLootEntry.lootTableItem(Items.TIPPED_ARROW).apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F))).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218604_0_) -> {
         p_218604_0_.putString("Potion", "minecraft:poison");
      }))))));
      p_accept_1_.accept(LootTables.LEATHERWORKER_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.LEATHER))));
      p_accept_1_.accept(LootTables.LIBRARIAN_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.BOOK))));
      p_accept_1_.accept(LootTables.MASON_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.CLAY))));
      p_accept_1_.accept(LootTables.SHEPHERD_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.WHITE_WOOL)).add(ItemLootEntry.lootTableItem(Items.ORANGE_WOOL)).add(ItemLootEntry.lootTableItem(Items.MAGENTA_WOOL)).add(ItemLootEntry.lootTableItem(Items.LIGHT_BLUE_WOOL)).add(ItemLootEntry.lootTableItem(Items.YELLOW_WOOL)).add(ItemLootEntry.lootTableItem(Items.LIME_WOOL)).add(ItemLootEntry.lootTableItem(Items.PINK_WOOL)).add(ItemLootEntry.lootTableItem(Items.GRAY_WOOL)).add(ItemLootEntry.lootTableItem(Items.LIGHT_GRAY_WOOL)).add(ItemLootEntry.lootTableItem(Items.CYAN_WOOL)).add(ItemLootEntry.lootTableItem(Items.PURPLE_WOOL)).add(ItemLootEntry.lootTableItem(Items.BLUE_WOOL)).add(ItemLootEntry.lootTableItem(Items.BROWN_WOOL)).add(ItemLootEntry.lootTableItem(Items.GREEN_WOOL)).add(ItemLootEntry.lootTableItem(Items.RED_WOOL)).add(ItemLootEntry.lootTableItem(Items.BLACK_WOOL))));
      p_accept_1_.accept(LootTables.TOOLSMITH_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.STONE_PICKAXE)).add(ItemLootEntry.lootTableItem(Items.STONE_AXE)).add(ItemLootEntry.lootTableItem(Items.STONE_HOE)).add(ItemLootEntry.lootTableItem(Items.STONE_SHOVEL))));
      p_accept_1_.accept(LootTables.WEAPONSMITH_GIFT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.STONE_AXE)).add(ItemLootEntry.lootTableItem(Items.GOLDEN_AXE)).add(ItemLootEntry.lootTableItem(Items.IRON_AXE))));
   }
}
