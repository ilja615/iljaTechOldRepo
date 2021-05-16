package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.AlternativesLootEntry;
import net.minecraft.loot.BinomialRange;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.DynamicLootEntry;
import net.minecraft.loot.ILootConditionConsumer;
import net.minecraft.loot.ILootFunctionConsumer;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.IntClamper;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LocationCheck;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.conditions.TableBonus;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.CopyBlockState;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.ExplosionDecay;
import net.minecraft.loot.functions.LimitCount;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   private static final ILootCondition.IBuilder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
   private static final ILootCondition.IBuilder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
   private static final ILootCondition.IBuilder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
   private static final ILootCondition.IBuilder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
   private static final ILootCondition.IBuilder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
   private static final Set<Item> EXPLOSION_RESISTANT = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(IItemProvider::asItem).collect(ImmutableSet.toImmutableSet());
   private static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
   private static final float[] JUNGLE_LEAVES_SAPLING_CHANGES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
   private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();

   protected static <T> T applyExplosionDecay(IItemProvider p_218552_0_, ILootFunctionConsumer<T> p_218552_1_) {
      return (T)(!EXPLOSION_RESISTANT.contains(p_218552_0_.asItem()) ? p_218552_1_.apply(ExplosionDecay.explosionDecay()) : p_218552_1_.unwrap());
   }

   protected static <T> T applyExplosionCondition(IItemProvider p_218560_0_, ILootConditionConsumer<T> p_218560_1_) {
      return (T)(!EXPLOSION_RESISTANT.contains(p_218560_0_.asItem()) ? p_218560_1_.when(SurvivesExplosion.survivesExplosion()) : p_218560_1_.unwrap());
   }

   protected static LootTable.Builder createSingleItemTable(IItemProvider p_218546_0_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(p_218546_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218546_0_))));
   }

   protected static LootTable.Builder createSelfDropDispatchTable(Block p_218494_0_, ILootCondition.IBuilder p_218494_1_, LootEntry.Builder<?> p_218494_2_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218494_0_).when(p_218494_1_).otherwise(p_218494_2_)));
   }

   protected static LootTable.Builder createSilkTouchDispatchTable(Block p_218519_0_, LootEntry.Builder<?> p_218519_1_) {
      return createSelfDropDispatchTable(p_218519_0_, HAS_SILK_TOUCH, p_218519_1_);
   }

   protected static LootTable.Builder createShearsDispatchTable(Block p_218511_0_, LootEntry.Builder<?> p_218511_1_) {
      return createSelfDropDispatchTable(p_218511_0_, HAS_SHEARS, p_218511_1_);
   }

   protected static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block p_218535_0_, LootEntry.Builder<?> p_218535_1_) {
      return createSelfDropDispatchTable(p_218535_0_, HAS_SHEARS_OR_SILK_TOUCH, p_218535_1_);
   }

   protected static LootTable.Builder createSingleItemTableWithSilkTouch(Block p_218515_0_, IItemProvider p_218515_1_) {
      return createSilkTouchDispatchTable(p_218515_0_, applyExplosionCondition(p_218515_0_, ItemLootEntry.lootTableItem(p_218515_1_)));
   }

   protected static LootTable.Builder createSingleItemTable(IItemProvider p_218463_0_, IRandomRange p_218463_1_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(p_218463_0_, ItemLootEntry.lootTableItem(p_218463_0_).apply(SetCount.setCount(p_218463_1_)))));
   }

   protected static LootTable.Builder createSingleItemTableWithSilkTouch(Block p_218530_0_, IItemProvider p_218530_1_, IRandomRange p_218530_2_) {
      return createSilkTouchDispatchTable(p_218530_0_, applyExplosionDecay(p_218530_0_, ItemLootEntry.lootTableItem(p_218530_1_).apply(SetCount.setCount(p_218530_2_))));
   }

   protected static LootTable.Builder createSilkTouchOnlyTable(IItemProvider p_218561_0_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218561_0_)));
   }

   protected static LootTable.Builder createPotFlowerItemTable(IItemProvider p_218523_0_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Blocks.FLOWER_POT)))).withPool(applyExplosionCondition(p_218523_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218523_0_))));
   }

   protected static LootTable.Builder createSlabItemTable(Block p_218513_0_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(p_218513_0_, ItemLootEntry.lootTableItem(p_218513_0_).apply(SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(p_218513_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)))))));
   }

   protected static <T extends Comparable<T> & IStringSerializable> LootTable.Builder createSinglePropConditionTable(Block p_218562_0_, Property<T> p_218562_1_, T p_218562_2_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(p_218562_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218562_0_).when(BlockStateProperty.hasBlockStateProperties(p_218562_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(p_218562_1_, p_218562_2_))))));
   }

   protected static LootTable.Builder createNameableBlockEntityTable(Block p_218481_0_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(p_218481_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218481_0_).apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)))));
   }

   protected static LootTable.Builder createShulkerBoxDrop(Block p_218544_0_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(p_218544_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218544_0_).apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("Lock", "BlockEntityTag.Lock").copy("LootTable", "BlockEntityTag.LootTable").copy("LootTableSeed", "BlockEntityTag.LootTableSeed")).apply(SetContents.setContents().withEntry(DynamicLootEntry.dynamicEntry(ShulkerBoxBlock.CONTENTS))))));
   }

   protected static LootTable.Builder createBannerDrop(Block p_218559_0_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(p_218559_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218559_0_).apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("Patterns", "BlockEntityTag.Patterns")))));
   }

   private static LootTable.Builder createBeeNestDrop(Block p_229436_0_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_229436_0_).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees")).apply(CopyBlockState.copyState(p_229436_0_).copy(BeehiveBlock.HONEY_LEVEL))));
   }

   private static LootTable.Builder createBeeHiveDrop(Block p_229437_0_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_229437_0_).when(HAS_SILK_TOUCH).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees")).apply(CopyBlockState.copyState(p_229437_0_).copy(BeehiveBlock.HONEY_LEVEL)).otherwise(ItemLootEntry.lootTableItem(p_229437_0_))));
   }

   protected static LootTable.Builder createOreDrop(Block p_218476_0_, Item p_218476_1_) {
      return createSilkTouchDispatchTable(p_218476_0_, applyExplosionDecay(p_218476_0_, ItemLootEntry.lootTableItem(p_218476_1_).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected static LootTable.Builder createMushroomBlockDrop(Block p_218491_0_, IItemProvider p_218491_1_) {
      return createSilkTouchDispatchTable(p_218491_0_, applyExplosionDecay(p_218491_0_, ItemLootEntry.lootTableItem(p_218491_1_).apply(SetCount.setCount(RandomValueRange.between(-6.0F, 2.0F))).apply(LimitCount.limitCount(IntClamper.lowerBound(0)))));
   }

   protected static LootTable.Builder createGrassDrops(Block p_218570_0_) {
      return createShearsDispatchTable(p_218570_0_, applyExplosionDecay(p_218570_0_, ItemLootEntry.lootTableItem(Items.WHEAT_SEEDS).when(RandomChance.randomChance(0.125F)).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2))));
   }

   protected static LootTable.Builder createStemDrops(Block p_218475_0_, Item p_218475_1_) {
      return LootTable.lootTable().withPool(applyExplosionDecay(p_218475_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218475_1_).apply(SetCount.setCount(BinomialRange.binomial(3, 0.06666667F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 0)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.13333334F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 1)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.2F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 2)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.26666668F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 3)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.33333334F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 4)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.4F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 5)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.46666667F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 6)))).apply(SetCount.setCount(BinomialRange.binomial(3, 0.53333336F)).when(BlockStateProperty.hasBlockStateProperties(p_218475_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 7)))))));
   }

   private static LootTable.Builder createAttachedStemDrops(Block p_229435_0_, Item p_229435_1_) {
      return LootTable.lootTable().withPool(applyExplosionDecay(p_229435_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_229435_1_).apply(SetCount.setCount(BinomialRange.binomial(3, 0.53333336F))))));
   }

   protected static LootTable.Builder createShearsOnlyDrop(IItemProvider p_218486_0_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(HAS_SHEARS).add(ItemLootEntry.lootTableItem(p_218486_0_)));
   }

   protected static LootTable.Builder createLeavesDrops(Block p_218540_0_, Block p_218540_1_, float... p_218540_2_) {
      return createSilkTouchOrShearsDispatchTable(p_218540_0_, applyExplosionCondition(p_218540_0_, ItemLootEntry.lootTableItem(p_218540_1_)).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, p_218540_2_))).withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(applyExplosionDecay(p_218540_0_, ItemLootEntry.lootTableItem(Items.STICK).apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F)))).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
   }

   protected static LootTable.Builder createOakLeavesDrops(Block p_218526_0_, Block p_218526_1_, float... p_218526_2_) {
      return createLeavesDrops(p_218526_0_, p_218526_1_, p_218526_2_).withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(applyExplosionCondition(p_218526_0_, ItemLootEntry.lootTableItem(Items.APPLE)).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
   }

   protected static LootTable.Builder createCropDrops(Block p_218541_0_, Item p_218541_1_, Item p_218541_2_, ILootCondition.IBuilder p_218541_3_) {
      return applyExplosionDecay(p_218541_0_, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(p_218541_1_).when(p_218541_3_).otherwise(ItemLootEntry.lootTableItem(p_218541_2_)))).withPool(LootPool.lootPool().when(p_218541_3_).add(ItemLootEntry.lootTableItem(p_218541_2_).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
   }

   private static LootTable.Builder createDoublePlantShearsDrop(Block p_241750_0_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SHEARS).add(ItemLootEntry.lootTableItem(p_241750_0_).apply(SetCount.setCount(ConstantRange.exactly(2)))));
   }

   private static LootTable.Builder createDoublePlantWithSeedDrops(Block p_241749_0_, Block p_241749_1_) {
      LootEntry.Builder<?> builder = ItemLootEntry.lootTableItem(p_241749_1_).apply(SetCount.setCount(ConstantRange.exactly(2))).when(HAS_SHEARS).otherwise(applyExplosionCondition(p_241749_0_, ItemLootEntry.lootTableItem(Items.WHEAT_SEEDS)).when(RandomChance.randomChance(0.125F)));
      return LootTable.lootTable().withPool(LootPool.lootPool().add(builder).when(BlockStateProperty.hasBlockStateProperties(p_241749_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(p_241749_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).build()).build()), new BlockPos(0, 1, 0)))).withPool(LootPool.lootPool().add(builder).when(BlockStateProperty.hasBlockStateProperties(p_241749_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(p_241749_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).build()).build()), new BlockPos(0, -1, 0))));
   }

   public static LootTable.Builder noDrop() {
      return LootTable.lootTable();
   }

   protected void addTables() {
      this.dropSelf(Blocks.GRANITE);
      this.dropSelf(Blocks.POLISHED_GRANITE);
      this.dropSelf(Blocks.DIORITE);
      this.dropSelf(Blocks.POLISHED_DIORITE);
      this.dropSelf(Blocks.ANDESITE);
      this.dropSelf(Blocks.POLISHED_ANDESITE);
      this.dropSelf(Blocks.DIRT);
      this.dropSelf(Blocks.COARSE_DIRT);
      this.dropSelf(Blocks.COBBLESTONE);
      this.dropSelf(Blocks.OAK_PLANKS);
      this.dropSelf(Blocks.SPRUCE_PLANKS);
      this.dropSelf(Blocks.BIRCH_PLANKS);
      this.dropSelf(Blocks.JUNGLE_PLANKS);
      this.dropSelf(Blocks.ACACIA_PLANKS);
      this.dropSelf(Blocks.DARK_OAK_PLANKS);
      this.dropSelf(Blocks.OAK_SAPLING);
      this.dropSelf(Blocks.SPRUCE_SAPLING);
      this.dropSelf(Blocks.BIRCH_SAPLING);
      this.dropSelf(Blocks.JUNGLE_SAPLING);
      this.dropSelf(Blocks.ACACIA_SAPLING);
      this.dropSelf(Blocks.DARK_OAK_SAPLING);
      this.dropSelf(Blocks.SAND);
      this.dropSelf(Blocks.RED_SAND);
      this.dropSelf(Blocks.GOLD_ORE);
      this.dropSelf(Blocks.IRON_ORE);
      this.dropSelf(Blocks.OAK_LOG);
      this.dropSelf(Blocks.SPRUCE_LOG);
      this.dropSelf(Blocks.BIRCH_LOG);
      this.dropSelf(Blocks.JUNGLE_LOG);
      this.dropSelf(Blocks.ACACIA_LOG);
      this.dropSelf(Blocks.DARK_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_SPRUCE_LOG);
      this.dropSelf(Blocks.STRIPPED_BIRCH_LOG);
      this.dropSelf(Blocks.STRIPPED_JUNGLE_LOG);
      this.dropSelf(Blocks.STRIPPED_ACACIA_LOG);
      this.dropSelf(Blocks.STRIPPED_DARK_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_WARPED_STEM);
      this.dropSelf(Blocks.STRIPPED_CRIMSON_STEM);
      this.dropSelf(Blocks.OAK_WOOD);
      this.dropSelf(Blocks.SPRUCE_WOOD);
      this.dropSelf(Blocks.BIRCH_WOOD);
      this.dropSelf(Blocks.JUNGLE_WOOD);
      this.dropSelf(Blocks.ACACIA_WOOD);
      this.dropSelf(Blocks.DARK_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_SPRUCE_WOOD);
      this.dropSelf(Blocks.STRIPPED_BIRCH_WOOD);
      this.dropSelf(Blocks.STRIPPED_JUNGLE_WOOD);
      this.dropSelf(Blocks.STRIPPED_ACACIA_WOOD);
      this.dropSelf(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.dropSelf(Blocks.STRIPPED_WARPED_HYPHAE);
      this.dropSelf(Blocks.SPONGE);
      this.dropSelf(Blocks.WET_SPONGE);
      this.dropSelf(Blocks.LAPIS_BLOCK);
      this.dropSelf(Blocks.SANDSTONE);
      this.dropSelf(Blocks.CHISELED_SANDSTONE);
      this.dropSelf(Blocks.CUT_SANDSTONE);
      this.dropSelf(Blocks.NOTE_BLOCK);
      this.dropSelf(Blocks.POWERED_RAIL);
      this.dropSelf(Blocks.DETECTOR_RAIL);
      this.dropSelf(Blocks.STICKY_PISTON);
      this.dropSelf(Blocks.PISTON);
      this.dropSelf(Blocks.WHITE_WOOL);
      this.dropSelf(Blocks.ORANGE_WOOL);
      this.dropSelf(Blocks.MAGENTA_WOOL);
      this.dropSelf(Blocks.LIGHT_BLUE_WOOL);
      this.dropSelf(Blocks.YELLOW_WOOL);
      this.dropSelf(Blocks.LIME_WOOL);
      this.dropSelf(Blocks.PINK_WOOL);
      this.dropSelf(Blocks.GRAY_WOOL);
      this.dropSelf(Blocks.LIGHT_GRAY_WOOL);
      this.dropSelf(Blocks.CYAN_WOOL);
      this.dropSelf(Blocks.PURPLE_WOOL);
      this.dropSelf(Blocks.BLUE_WOOL);
      this.dropSelf(Blocks.BROWN_WOOL);
      this.dropSelf(Blocks.GREEN_WOOL);
      this.dropSelf(Blocks.RED_WOOL);
      this.dropSelf(Blocks.BLACK_WOOL);
      this.dropSelf(Blocks.DANDELION);
      this.dropSelf(Blocks.POPPY);
      this.dropSelf(Blocks.BLUE_ORCHID);
      this.dropSelf(Blocks.ALLIUM);
      this.dropSelf(Blocks.AZURE_BLUET);
      this.dropSelf(Blocks.RED_TULIP);
      this.dropSelf(Blocks.ORANGE_TULIP);
      this.dropSelf(Blocks.WHITE_TULIP);
      this.dropSelf(Blocks.PINK_TULIP);
      this.dropSelf(Blocks.OXEYE_DAISY);
      this.dropSelf(Blocks.CORNFLOWER);
      this.dropSelf(Blocks.WITHER_ROSE);
      this.dropSelf(Blocks.LILY_OF_THE_VALLEY);
      this.dropSelf(Blocks.BROWN_MUSHROOM);
      this.dropSelf(Blocks.RED_MUSHROOM);
      this.dropSelf(Blocks.GOLD_BLOCK);
      this.dropSelf(Blocks.IRON_BLOCK);
      this.dropSelf(Blocks.BRICKS);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE);
      this.dropSelf(Blocks.OBSIDIAN);
      this.dropSelf(Blocks.CRYING_OBSIDIAN);
      this.dropSelf(Blocks.TORCH);
      this.dropSelf(Blocks.OAK_STAIRS);
      this.dropSelf(Blocks.REDSTONE_WIRE);
      this.dropSelf(Blocks.DIAMOND_BLOCK);
      this.dropSelf(Blocks.CRAFTING_TABLE);
      this.dropSelf(Blocks.OAK_SIGN);
      this.dropSelf(Blocks.SPRUCE_SIGN);
      this.dropSelf(Blocks.BIRCH_SIGN);
      this.dropSelf(Blocks.ACACIA_SIGN);
      this.dropSelf(Blocks.JUNGLE_SIGN);
      this.dropSelf(Blocks.DARK_OAK_SIGN);
      this.dropSelf(Blocks.LADDER);
      this.dropSelf(Blocks.RAIL);
      this.dropSelf(Blocks.COBBLESTONE_STAIRS);
      this.dropSelf(Blocks.LEVER);
      this.dropSelf(Blocks.STONE_PRESSURE_PLATE);
      this.dropSelf(Blocks.OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.SPRUCE_PRESSURE_PLATE);
      this.dropSelf(Blocks.BIRCH_PRESSURE_PLATE);
      this.dropSelf(Blocks.JUNGLE_PRESSURE_PLATE);
      this.dropSelf(Blocks.ACACIA_PRESSURE_PLATE);
      this.dropSelf(Blocks.DARK_OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.REDSTONE_TORCH);
      this.dropSelf(Blocks.STONE_BUTTON);
      this.dropSelf(Blocks.CACTUS);
      this.dropSelf(Blocks.SUGAR_CANE);
      this.dropSelf(Blocks.JUKEBOX);
      this.dropSelf(Blocks.OAK_FENCE);
      this.dropSelf(Blocks.PUMPKIN);
      this.dropSelf(Blocks.NETHERRACK);
      this.dropSelf(Blocks.SOUL_SAND);
      this.dropSelf(Blocks.SOUL_SOIL);
      this.dropSelf(Blocks.BASALT);
      this.dropSelf(Blocks.POLISHED_BASALT);
      this.dropSelf(Blocks.SOUL_TORCH);
      this.dropSelf(Blocks.CARVED_PUMPKIN);
      this.dropSelf(Blocks.JACK_O_LANTERN);
      this.dropSelf(Blocks.REPEATER);
      this.dropSelf(Blocks.OAK_TRAPDOOR);
      this.dropSelf(Blocks.SPRUCE_TRAPDOOR);
      this.dropSelf(Blocks.BIRCH_TRAPDOOR);
      this.dropSelf(Blocks.JUNGLE_TRAPDOOR);
      this.dropSelf(Blocks.ACACIA_TRAPDOOR);
      this.dropSelf(Blocks.DARK_OAK_TRAPDOOR);
      this.dropSelf(Blocks.STONE_BRICKS);
      this.dropSelf(Blocks.MOSSY_STONE_BRICKS);
      this.dropSelf(Blocks.CRACKED_STONE_BRICKS);
      this.dropSelf(Blocks.CHISELED_STONE_BRICKS);
      this.dropSelf(Blocks.IRON_BARS);
      this.dropSelf(Blocks.OAK_FENCE_GATE);
      this.dropSelf(Blocks.BRICK_STAIRS);
      this.dropSelf(Blocks.STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.LILY_PAD);
      this.dropSelf(Blocks.NETHER_BRICKS);
      this.dropSelf(Blocks.NETHER_BRICK_FENCE);
      this.dropSelf(Blocks.NETHER_BRICK_STAIRS);
      this.dropSelf(Blocks.CAULDRON);
      this.dropSelf(Blocks.END_STONE);
      this.dropSelf(Blocks.REDSTONE_LAMP);
      this.dropSelf(Blocks.SANDSTONE_STAIRS);
      this.dropSelf(Blocks.TRIPWIRE_HOOK);
      this.dropSelf(Blocks.EMERALD_BLOCK);
      this.dropSelf(Blocks.SPRUCE_STAIRS);
      this.dropSelf(Blocks.BIRCH_STAIRS);
      this.dropSelf(Blocks.JUNGLE_STAIRS);
      this.dropSelf(Blocks.COBBLESTONE_WALL);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE_WALL);
      this.dropSelf(Blocks.FLOWER_POT);
      this.dropSelf(Blocks.OAK_BUTTON);
      this.dropSelf(Blocks.SPRUCE_BUTTON);
      this.dropSelf(Blocks.BIRCH_BUTTON);
      this.dropSelf(Blocks.JUNGLE_BUTTON);
      this.dropSelf(Blocks.ACACIA_BUTTON);
      this.dropSelf(Blocks.DARK_OAK_BUTTON);
      this.dropSelf(Blocks.SKELETON_SKULL);
      this.dropSelf(Blocks.WITHER_SKELETON_SKULL);
      this.dropSelf(Blocks.ZOMBIE_HEAD);
      this.dropSelf(Blocks.CREEPER_HEAD);
      this.dropSelf(Blocks.DRAGON_HEAD);
      this.dropSelf(Blocks.ANVIL);
      this.dropSelf(Blocks.CHIPPED_ANVIL);
      this.dropSelf(Blocks.DAMAGED_ANVIL);
      this.dropSelf(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
      this.dropSelf(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
      this.dropSelf(Blocks.COMPARATOR);
      this.dropSelf(Blocks.DAYLIGHT_DETECTOR);
      this.dropSelf(Blocks.REDSTONE_BLOCK);
      this.dropSelf(Blocks.QUARTZ_BLOCK);
      this.dropSelf(Blocks.CHISELED_QUARTZ_BLOCK);
      this.dropSelf(Blocks.QUARTZ_PILLAR);
      this.dropSelf(Blocks.QUARTZ_STAIRS);
      this.dropSelf(Blocks.ACTIVATOR_RAIL);
      this.dropSelf(Blocks.WHITE_TERRACOTTA);
      this.dropSelf(Blocks.ORANGE_TERRACOTTA);
      this.dropSelf(Blocks.MAGENTA_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.dropSelf(Blocks.YELLOW_TERRACOTTA);
      this.dropSelf(Blocks.LIME_TERRACOTTA);
      this.dropSelf(Blocks.PINK_TERRACOTTA);
      this.dropSelf(Blocks.GRAY_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.dropSelf(Blocks.CYAN_TERRACOTTA);
      this.dropSelf(Blocks.PURPLE_TERRACOTTA);
      this.dropSelf(Blocks.BLUE_TERRACOTTA);
      this.dropSelf(Blocks.BROWN_TERRACOTTA);
      this.dropSelf(Blocks.GREEN_TERRACOTTA);
      this.dropSelf(Blocks.RED_TERRACOTTA);
      this.dropSelf(Blocks.BLACK_TERRACOTTA);
      this.dropSelf(Blocks.ACACIA_STAIRS);
      this.dropSelf(Blocks.DARK_OAK_STAIRS);
      this.dropSelf(Blocks.SLIME_BLOCK);
      this.dropSelf(Blocks.IRON_TRAPDOOR);
      this.dropSelf(Blocks.PRISMARINE);
      this.dropSelf(Blocks.PRISMARINE_BRICKS);
      this.dropSelf(Blocks.DARK_PRISMARINE);
      this.dropSelf(Blocks.PRISMARINE_STAIRS);
      this.dropSelf(Blocks.PRISMARINE_BRICK_STAIRS);
      this.dropSelf(Blocks.DARK_PRISMARINE_STAIRS);
      this.dropSelf(Blocks.HAY_BLOCK);
      this.dropSelf(Blocks.WHITE_CARPET);
      this.dropSelf(Blocks.ORANGE_CARPET);
      this.dropSelf(Blocks.MAGENTA_CARPET);
      this.dropSelf(Blocks.LIGHT_BLUE_CARPET);
      this.dropSelf(Blocks.YELLOW_CARPET);
      this.dropSelf(Blocks.LIME_CARPET);
      this.dropSelf(Blocks.PINK_CARPET);
      this.dropSelf(Blocks.GRAY_CARPET);
      this.dropSelf(Blocks.LIGHT_GRAY_CARPET);
      this.dropSelf(Blocks.CYAN_CARPET);
      this.dropSelf(Blocks.PURPLE_CARPET);
      this.dropSelf(Blocks.BLUE_CARPET);
      this.dropSelf(Blocks.BROWN_CARPET);
      this.dropSelf(Blocks.GREEN_CARPET);
      this.dropSelf(Blocks.RED_CARPET);
      this.dropSelf(Blocks.BLACK_CARPET);
      this.dropSelf(Blocks.TERRACOTTA);
      this.dropSelf(Blocks.COAL_BLOCK);
      this.dropSelf(Blocks.RED_SANDSTONE);
      this.dropSelf(Blocks.CHISELED_RED_SANDSTONE);
      this.dropSelf(Blocks.CUT_RED_SANDSTONE);
      this.dropSelf(Blocks.RED_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_STONE);
      this.dropSelf(Blocks.SMOOTH_SANDSTONE);
      this.dropSelf(Blocks.SMOOTH_QUARTZ);
      this.dropSelf(Blocks.SMOOTH_RED_SANDSTONE);
      this.dropSelf(Blocks.SPRUCE_FENCE_GATE);
      this.dropSelf(Blocks.BIRCH_FENCE_GATE);
      this.dropSelf(Blocks.JUNGLE_FENCE_GATE);
      this.dropSelf(Blocks.ACACIA_FENCE_GATE);
      this.dropSelf(Blocks.DARK_OAK_FENCE_GATE);
      this.dropSelf(Blocks.SPRUCE_FENCE);
      this.dropSelf(Blocks.BIRCH_FENCE);
      this.dropSelf(Blocks.JUNGLE_FENCE);
      this.dropSelf(Blocks.ACACIA_FENCE);
      this.dropSelf(Blocks.DARK_OAK_FENCE);
      this.dropSelf(Blocks.END_ROD);
      this.dropSelf(Blocks.PURPUR_BLOCK);
      this.dropSelf(Blocks.PURPUR_PILLAR);
      this.dropSelf(Blocks.PURPUR_STAIRS);
      this.dropSelf(Blocks.END_STONE_BRICKS);
      this.dropSelf(Blocks.MAGMA_BLOCK);
      this.dropSelf(Blocks.NETHER_WART_BLOCK);
      this.dropSelf(Blocks.RED_NETHER_BRICKS);
      this.dropSelf(Blocks.BONE_BLOCK);
      this.dropSelf(Blocks.OBSERVER);
      this.dropSelf(Blocks.TARGET);
      this.dropSelf(Blocks.WHITE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.ORANGE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.MAGENTA_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.YELLOW_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIME_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.PINK_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.GRAY_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.CYAN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.PURPLE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BLUE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BROWN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.GREEN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.RED_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BLACK_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.WHITE_CONCRETE);
      this.dropSelf(Blocks.ORANGE_CONCRETE);
      this.dropSelf(Blocks.MAGENTA_CONCRETE);
      this.dropSelf(Blocks.LIGHT_BLUE_CONCRETE);
      this.dropSelf(Blocks.YELLOW_CONCRETE);
      this.dropSelf(Blocks.LIME_CONCRETE);
      this.dropSelf(Blocks.PINK_CONCRETE);
      this.dropSelf(Blocks.GRAY_CONCRETE);
      this.dropSelf(Blocks.LIGHT_GRAY_CONCRETE);
      this.dropSelf(Blocks.CYAN_CONCRETE);
      this.dropSelf(Blocks.PURPLE_CONCRETE);
      this.dropSelf(Blocks.BLUE_CONCRETE);
      this.dropSelf(Blocks.BROWN_CONCRETE);
      this.dropSelf(Blocks.GREEN_CONCRETE);
      this.dropSelf(Blocks.RED_CONCRETE);
      this.dropSelf(Blocks.BLACK_CONCRETE);
      this.dropSelf(Blocks.WHITE_CONCRETE_POWDER);
      this.dropSelf(Blocks.ORANGE_CONCRETE_POWDER);
      this.dropSelf(Blocks.MAGENTA_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
      this.dropSelf(Blocks.YELLOW_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIME_CONCRETE_POWDER);
      this.dropSelf(Blocks.PINK_CONCRETE_POWDER);
      this.dropSelf(Blocks.GRAY_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
      this.dropSelf(Blocks.CYAN_CONCRETE_POWDER);
      this.dropSelf(Blocks.PURPLE_CONCRETE_POWDER);
      this.dropSelf(Blocks.BLUE_CONCRETE_POWDER);
      this.dropSelf(Blocks.BROWN_CONCRETE_POWDER);
      this.dropSelf(Blocks.GREEN_CONCRETE_POWDER);
      this.dropSelf(Blocks.RED_CONCRETE_POWDER);
      this.dropSelf(Blocks.BLACK_CONCRETE_POWDER);
      this.dropSelf(Blocks.KELP);
      this.dropSelf(Blocks.DRIED_KELP_BLOCK);
      this.dropSelf(Blocks.DEAD_TUBE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_BRAIN_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_FIRE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_HORN_CORAL_BLOCK);
      this.dropSelf(Blocks.CONDUIT);
      this.dropSelf(Blocks.DRAGON_EGG);
      this.dropSelf(Blocks.BAMBOO);
      this.dropSelf(Blocks.POLISHED_GRANITE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.MOSSY_STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.POLISHED_DIORITE_STAIRS);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE_STAIRS);
      this.dropSelf(Blocks.END_STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.STONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_QUARTZ_STAIRS);
      this.dropSelf(Blocks.GRANITE_STAIRS);
      this.dropSelf(Blocks.ANDESITE_STAIRS);
      this.dropSelf(Blocks.RED_NETHER_BRICK_STAIRS);
      this.dropSelf(Blocks.POLISHED_ANDESITE_STAIRS);
      this.dropSelf(Blocks.DIORITE_STAIRS);
      this.dropSelf(Blocks.BRICK_WALL);
      this.dropSelf(Blocks.PRISMARINE_WALL);
      this.dropSelf(Blocks.RED_SANDSTONE_WALL);
      this.dropSelf(Blocks.MOSSY_STONE_BRICK_WALL);
      this.dropSelf(Blocks.GRANITE_WALL);
      this.dropSelf(Blocks.STONE_BRICK_WALL);
      this.dropSelf(Blocks.NETHER_BRICK_WALL);
      this.dropSelf(Blocks.ANDESITE_WALL);
      this.dropSelf(Blocks.RED_NETHER_BRICK_WALL);
      this.dropSelf(Blocks.SANDSTONE_WALL);
      this.dropSelf(Blocks.END_STONE_BRICK_WALL);
      this.dropSelf(Blocks.DIORITE_WALL);
      this.dropSelf(Blocks.LOOM);
      this.dropSelf(Blocks.SCAFFOLDING);
      this.dropSelf(Blocks.HONEY_BLOCK);
      this.dropSelf(Blocks.HONEYCOMB_BLOCK);
      this.dropSelf(Blocks.RESPAWN_ANCHOR);
      this.dropSelf(Blocks.LODESTONE);
      this.dropSelf(Blocks.WARPED_STEM);
      this.dropSelf(Blocks.WARPED_HYPHAE);
      this.dropSelf(Blocks.WARPED_FUNGUS);
      this.dropSelf(Blocks.WARPED_WART_BLOCK);
      this.dropSelf(Blocks.CRIMSON_STEM);
      this.dropSelf(Blocks.CRIMSON_HYPHAE);
      this.dropSelf(Blocks.CRIMSON_FUNGUS);
      this.dropSelf(Blocks.SHROOMLIGHT);
      this.dropSelf(Blocks.CRIMSON_PLANKS);
      this.dropSelf(Blocks.WARPED_PLANKS);
      this.dropSelf(Blocks.WARPED_PRESSURE_PLATE);
      this.dropSelf(Blocks.WARPED_FENCE);
      this.dropSelf(Blocks.WARPED_TRAPDOOR);
      this.dropSelf(Blocks.WARPED_FENCE_GATE);
      this.dropSelf(Blocks.WARPED_STAIRS);
      this.dropSelf(Blocks.WARPED_BUTTON);
      this.dropSelf(Blocks.WARPED_SIGN);
      this.dropSelf(Blocks.CRIMSON_PRESSURE_PLATE);
      this.dropSelf(Blocks.CRIMSON_FENCE);
      this.dropSelf(Blocks.CRIMSON_TRAPDOOR);
      this.dropSelf(Blocks.CRIMSON_FENCE_GATE);
      this.dropSelf(Blocks.CRIMSON_STAIRS);
      this.dropSelf(Blocks.CRIMSON_BUTTON);
      this.dropSelf(Blocks.CRIMSON_SIGN);
      this.dropSelf(Blocks.NETHERITE_BLOCK);
      this.dropSelf(Blocks.ANCIENT_DEBRIS);
      this.dropSelf(Blocks.BLACKSTONE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICKS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      this.dropSelf(Blocks.BLACKSTONE_STAIRS);
      this.dropSelf(Blocks.BLACKSTONE_WALL);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      this.dropSelf(Blocks.CHISELED_POLISHED_BLACKSTONE);
      this.dropSelf(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_STAIRS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BUTTON);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_WALL);
      this.dropSelf(Blocks.CHISELED_NETHER_BRICKS);
      this.dropSelf(Blocks.CRACKED_NETHER_BRICKS);
      this.dropSelf(Blocks.QUARTZ_BRICKS);
      this.dropSelf(Blocks.CHAIN);
      this.dropSelf(Blocks.WARPED_ROOTS);
      this.dropSelf(Blocks.CRIMSON_ROOTS);
      this.dropOther(Blocks.FARMLAND, Blocks.DIRT);
      this.dropOther(Blocks.TRIPWIRE, Items.STRING);
      this.dropOther(Blocks.GRASS_PATH, Blocks.DIRT);
      this.dropOther(Blocks.KELP_PLANT, Blocks.KELP);
      this.dropOther(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
      this.add(Blocks.STONE, (p_218490_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218490_0_, Blocks.COBBLESTONE);
      });
      this.add(Blocks.GRASS_BLOCK, (p_218529_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218529_0_, Blocks.DIRT);
      });
      this.add(Blocks.PODZOL, (p_218514_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218514_0_, Blocks.DIRT);
      });
      this.add(Blocks.MYCELIUM, (p_218501_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218501_0_, Blocks.DIRT);
      });
      this.add(Blocks.TUBE_CORAL_BLOCK, (p_218539_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218539_0_, Blocks.DEAD_TUBE_CORAL_BLOCK);
      });
      this.add(Blocks.BRAIN_CORAL_BLOCK, (p_218462_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218462_0_, Blocks.DEAD_BRAIN_CORAL_BLOCK);
      });
      this.add(Blocks.BUBBLE_CORAL_BLOCK, (p_218505_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218505_0_, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      });
      this.add(Blocks.FIRE_CORAL_BLOCK, (p_218499_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218499_0_, Blocks.DEAD_FIRE_CORAL_BLOCK);
      });
      this.add(Blocks.HORN_CORAL_BLOCK, (p_218502_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218502_0_, Blocks.DEAD_HORN_CORAL_BLOCK);
      });
      this.add(Blocks.CRIMSON_NYLIUM, (p_239296_0_) -> {
         return createSingleItemTableWithSilkTouch(p_239296_0_, Blocks.NETHERRACK);
      });
      this.add(Blocks.WARPED_NYLIUM, (p_239293_0_) -> {
         return createSingleItemTableWithSilkTouch(p_239293_0_, Blocks.NETHERRACK);
      });
      this.add(Blocks.BOOKSHELF, (p_218534_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218534_0_, Items.BOOK, ConstantRange.exactly(3));
      });
      this.add(Blocks.CLAY, (p_218465_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218465_0_, Items.CLAY_BALL, ConstantRange.exactly(4));
      });
      this.add(Blocks.ENDER_CHEST, (p_218558_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218558_0_, Blocks.OBSIDIAN, ConstantRange.exactly(8));
      });
      this.add(Blocks.SNOW_BLOCK, (p_218556_0_) -> {
         return createSingleItemTableWithSilkTouch(p_218556_0_, Items.SNOWBALL, ConstantRange.exactly(4));
      });
      this.add(Blocks.CHORUS_PLANT, createSingleItemTable(Items.CHORUS_FRUIT, RandomValueRange.between(0.0F, 1.0F)));
      this.dropPottedContents(Blocks.POTTED_OAK_SAPLING);
      this.dropPottedContents(Blocks.POTTED_SPRUCE_SAPLING);
      this.dropPottedContents(Blocks.POTTED_BIRCH_SAPLING);
      this.dropPottedContents(Blocks.POTTED_JUNGLE_SAPLING);
      this.dropPottedContents(Blocks.POTTED_ACACIA_SAPLING);
      this.dropPottedContents(Blocks.POTTED_DARK_OAK_SAPLING);
      this.dropPottedContents(Blocks.POTTED_FERN);
      this.dropPottedContents(Blocks.POTTED_DANDELION);
      this.dropPottedContents(Blocks.POTTED_POPPY);
      this.dropPottedContents(Blocks.POTTED_BLUE_ORCHID);
      this.dropPottedContents(Blocks.POTTED_ALLIUM);
      this.dropPottedContents(Blocks.POTTED_AZURE_BLUET);
      this.dropPottedContents(Blocks.POTTED_RED_TULIP);
      this.dropPottedContents(Blocks.POTTED_ORANGE_TULIP);
      this.dropPottedContents(Blocks.POTTED_WHITE_TULIP);
      this.dropPottedContents(Blocks.POTTED_PINK_TULIP);
      this.dropPottedContents(Blocks.POTTED_OXEYE_DAISY);
      this.dropPottedContents(Blocks.POTTED_CORNFLOWER);
      this.dropPottedContents(Blocks.POTTED_LILY_OF_THE_VALLEY);
      this.dropPottedContents(Blocks.POTTED_WITHER_ROSE);
      this.dropPottedContents(Blocks.POTTED_RED_MUSHROOM);
      this.dropPottedContents(Blocks.POTTED_BROWN_MUSHROOM);
      this.dropPottedContents(Blocks.POTTED_DEAD_BUSH);
      this.dropPottedContents(Blocks.POTTED_CACTUS);
      this.dropPottedContents(Blocks.POTTED_BAMBOO);
      this.dropPottedContents(Blocks.POTTED_CRIMSON_FUNGUS);
      this.dropPottedContents(Blocks.POTTED_WARPED_FUNGUS);
      this.dropPottedContents(Blocks.POTTED_CRIMSON_ROOTS);
      this.dropPottedContents(Blocks.POTTED_WARPED_ROOTS);
      this.add(Blocks.ACACIA_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.BIRCH_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.COBBLESTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.DARK_OAK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.DARK_PRISMARINE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.JUNGLE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.NETHER_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.OAK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.PETRIFIED_OAK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.PRISMARINE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.PURPUR_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.QUARTZ_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.RED_SANDSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.SANDSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.CUT_SANDSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.SPRUCE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.STONE_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.STONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.SMOOTH_STONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.POLISHED_GRANITE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.POLISHED_DIORITE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.END_STONE_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.GRANITE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.ANDESITE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.DIORITE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.CRIMSON_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.WARPED_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.BLACKSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLootTables::createSlabItemTable);
      this.add(Blocks.ACACIA_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.BIRCH_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.DARK_OAK_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.IRON_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.JUNGLE_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.OAK_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.SPRUCE_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.WARPED_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.CRIMSON_DOOR, BlockLootTables::createDoorTable);
      this.add(Blocks.BLACK_BED, (p_218567_0_) -> {
         return createSinglePropConditionTable(p_218567_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.BLUE_BED, (p_218555_0_) -> {
         return createSinglePropConditionTable(p_218555_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.BROWN_BED, (p_218543_0_) -> {
         return createSinglePropConditionTable(p_218543_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.CYAN_BED, (p_218479_0_) -> {
         return createSinglePropConditionTable(p_218479_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.GRAY_BED, (p_218521_0_) -> {
         return createSinglePropConditionTable(p_218521_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.GREEN_BED, (p_218470_0_) -> {
         return createSinglePropConditionTable(p_218470_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIGHT_BLUE_BED, (p_218536_0_) -> {
         return createSinglePropConditionTable(p_218536_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIGHT_GRAY_BED, (p_218545_0_) -> {
         return createSinglePropConditionTable(p_218545_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIME_BED, (p_218557_0_) -> {
         return createSinglePropConditionTable(p_218557_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.MAGENTA_BED, (p_218566_0_) -> {
         return createSinglePropConditionTable(p_218566_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.PURPLE_BED, (p_218520_0_) -> {
         return createSinglePropConditionTable(p_218520_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.ORANGE_BED, (p_218472_0_) -> {
         return createSinglePropConditionTable(p_218472_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.PINK_BED, (p_218537_0_) -> {
         return createSinglePropConditionTable(p_218537_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.RED_BED, (p_218549_0_) -> {
         return createSinglePropConditionTable(p_218549_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.WHITE_BED, (p_218569_0_) -> {
         return createSinglePropConditionTable(p_218569_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.YELLOW_BED, (p_218517_0_) -> {
         return createSinglePropConditionTable(p_218517_0_, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LILAC, (p_218488_0_) -> {
         return createSinglePropConditionTable(p_218488_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.SUNFLOWER, (p_218503_0_) -> {
         return createSinglePropConditionTable(p_218503_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.PEONY, (p_218497_0_) -> {
         return createSinglePropConditionTable(p_218497_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.ROSE_BUSH, (p_218504_0_) -> {
         return createSinglePropConditionTable(p_218504_0_, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.TNT, LootTable.lootTable().withPool(applyExplosionCondition(Blocks.TNT, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Blocks.TNT).when(BlockStateProperty.hasBlockStateProperties(Blocks.TNT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TNTBlock.UNSTABLE, false)))))));
      this.add(Blocks.COCOA, (p_218516_0_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(p_218516_0_, ItemLootEntry.lootTableItem(Items.COCOA_BEANS).apply(SetCount.setCount(ConstantRange.exactly(3)).when(BlockStateProperty.hasBlockStateProperties(p_218516_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CocoaBlock.AGE, 2)))))));
      });
      this.add(Blocks.SEA_PICKLE, (p_218478_0_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(Blocks.SEA_PICKLE, ItemLootEntry.lootTableItem(p_218478_0_).apply(SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(p_218478_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, 2)))).apply(SetCount.setCount(ConstantRange.exactly(3)).when(BlockStateProperty.hasBlockStateProperties(p_218478_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, 3)))).apply(SetCount.setCount(ConstantRange.exactly(4)).when(BlockStateProperty.hasBlockStateProperties(p_218478_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, 4)))))));
      });
      this.add(Blocks.COMPOSTER, (p_218551_0_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().add(applyExplosionDecay(p_218551_0_, ItemLootEntry.lootTableItem(Items.COMPOSTER)))).withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.BONE_MEAL)).when(BlockStateProperty.hasBlockStateProperties(p_218551_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ComposterBlock.LEVEL, 8))));
      });
      this.add(Blocks.BEACON, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.BREWING_STAND, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.CHEST, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.DISPENSER, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.DROPPER, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.ENCHANTING_TABLE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.FURNACE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.HOPPER, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.TRAPPED_CHEST, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.SMOKER, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.BLAST_FURNACE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.BARREL, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.CARTOGRAPHY_TABLE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.FLETCHING_TABLE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.GRINDSTONE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.LECTERN, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.SMITHING_TABLE, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.STONECUTTER, BlockLootTables::createNameableBlockEntityTable);
      this.add(Blocks.BELL, BlockLootTables::createSingleItemTable);
      this.add(Blocks.LANTERN, BlockLootTables::createSingleItemTable);
      this.add(Blocks.SOUL_LANTERN, BlockLootTables::createSingleItemTable);
      this.add(Blocks.SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.BLACK_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.BLUE_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.BROWN_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.CYAN_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.GRAY_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.GREEN_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.LIME_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.MAGENTA_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.ORANGE_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.PINK_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.PURPLE_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.RED_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.WHITE_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.YELLOW_SHULKER_BOX, BlockLootTables::createShulkerBoxDrop);
      this.add(Blocks.BLACK_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.BLUE_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.BROWN_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.CYAN_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.GRAY_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.GREEN_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.LIGHT_BLUE_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.LIGHT_GRAY_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.LIME_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.MAGENTA_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.ORANGE_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.PINK_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.PURPLE_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.RED_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.WHITE_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.YELLOW_BANNER, BlockLootTables::createBannerDrop);
      this.add(Blocks.PLAYER_HEAD, (p_218565_0_) -> {
         return LootTable.lootTable().withPool(applyExplosionCondition(p_218565_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218565_0_).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("SkullOwner", "SkullOwner")))));
      });
      this.add(Blocks.BEE_NEST, BlockLootTables::createBeeNestDrop);
      this.add(Blocks.BEEHIVE, BlockLootTables::createBeeHiveDrop);
      this.add(Blocks.BIRCH_LEAVES, (p_218473_0_) -> {
         return createLeavesDrops(p_218473_0_, Blocks.BIRCH_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.ACACIA_LEAVES, (p_218518_0_) -> {
         return createLeavesDrops(p_218518_0_, Blocks.ACACIA_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.JUNGLE_LEAVES, (p_218477_0_) -> {
         return createLeavesDrops(p_218477_0_, Blocks.JUNGLE_SAPLING, JUNGLE_LEAVES_SAPLING_CHANGES);
      });
      this.add(Blocks.SPRUCE_LEAVES, (p_218500_0_) -> {
         return createLeavesDrops(p_218500_0_, Blocks.SPRUCE_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.OAK_LEAVES, (p_218506_0_) -> {
         return createOakLeavesDrops(p_218506_0_, Blocks.OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.DARK_OAK_LEAVES, (p_241172_0_) -> {
         return createOakLeavesDrops(p_241172_0_, Blocks.DARK_OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      ILootCondition.IBuilder ilootcondition$ibuilder = BlockStateProperty.hasBlockStateProperties(Blocks.BEETROOTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BeetrootBlock.AGE, 3));
      this.add(Blocks.BEETROOTS, createCropDrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, ilootcondition$ibuilder));
      ILootCondition.IBuilder ilootcondition$ibuilder1 = BlockStateProperty.hasBlockStateProperties(Blocks.WHEAT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropsBlock.AGE, 7));
      this.add(Blocks.WHEAT, createCropDrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, ilootcondition$ibuilder1));
      ILootCondition.IBuilder ilootcondition$ibuilder2 = BlockStateProperty.hasBlockStateProperties(Blocks.CARROTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CarrotBlock.AGE, 7));
      this.add(Blocks.CARROTS, applyExplosionDecay(Blocks.CARROTS, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.CARROT))).withPool(LootPool.lootPool().when(ilootcondition$ibuilder2).add(ItemLootEntry.lootTableItem(Items.CARROT).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
      ILootCondition.IBuilder ilootcondition$ibuilder3 = BlockStateProperty.hasBlockStateProperties(Blocks.POTATOES).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PotatoBlock.AGE, 7));
      this.add(Blocks.POTATOES, applyExplosionDecay(Blocks.POTATOES, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.POTATO))).withPool(LootPool.lootPool().when(ilootcondition$ibuilder3).add(ItemLootEntry.lootTableItem(Items.POTATO).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))).withPool(LootPool.lootPool().when(ilootcondition$ibuilder3).add(ItemLootEntry.lootTableItem(Items.POISONOUS_POTATO).when(RandomChance.randomChance(0.02F))))));
      this.add(Blocks.SWEET_BERRY_BUSH, (p_241171_0_) -> {
         return applyExplosionDecay(p_241171_0_, LootTable.lootTable().withPool(LootPool.lootPool().when(BlockStateProperty.hasBlockStateProperties(Blocks.SWEET_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))).add(ItemLootEntry.lootTableItem(Items.SWEET_BERRIES)).apply(SetCount.setCount(RandomValueRange.between(2.0F, 3.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))).withPool(LootPool.lootPool().when(BlockStateProperty.hasBlockStateProperties(Blocks.SWEET_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))).add(ItemLootEntry.lootTableItem(Items.SWEET_BERRIES)).apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.BROWN_MUSHROOM_BLOCK, (p_229434_0_) -> {
         return createMushroomBlockDrop(p_229434_0_, Blocks.BROWN_MUSHROOM);
      });
      this.add(Blocks.RED_MUSHROOM_BLOCK, (p_229433_0_) -> {
         return createMushroomBlockDrop(p_229433_0_, Blocks.RED_MUSHROOM);
      });
      this.add(Blocks.COAL_ORE, (p_241170_0_) -> {
         return createOreDrop(p_241170_0_, Items.COAL);
      });
      this.add(Blocks.EMERALD_ORE, (p_229431_0_) -> {
         return createOreDrop(p_229431_0_, Items.EMERALD);
      });
      this.add(Blocks.NETHER_QUARTZ_ORE, (p_218554_0_) -> {
         return createOreDrop(p_218554_0_, Items.QUARTZ);
      });
      this.add(Blocks.DIAMOND_ORE, (p_218568_0_) -> {
         return createOreDrop(p_218568_0_, Items.DIAMOND);
      });
      this.add(Blocks.NETHER_GOLD_ORE, (p_218471_0_) -> {
         return createSilkTouchDispatchTable(p_218471_0_, applyExplosionDecay(p_218471_0_, ItemLootEntry.lootTableItem(Items.GOLD_NUGGET).apply(SetCount.setCount(RandomValueRange.between(2.0F, 6.0F))).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.LAPIS_ORE, (p_218548_0_) -> {
         return createSilkTouchDispatchTable(p_218548_0_, applyExplosionDecay(p_218548_0_, ItemLootEntry.lootTableItem(Items.LAPIS_LAZULI).apply(SetCount.setCount(RandomValueRange.between(4.0F, 9.0F))).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.COBWEB, (p_218487_0_) -> {
         return createSilkTouchOrShearsDispatchTable(p_218487_0_, applyExplosionCondition(p_218487_0_, ItemLootEntry.lootTableItem(Items.STRING)));
      });
      this.add(Blocks.DEAD_BUSH, (p_218525_0_) -> {
         return createShearsDispatchTable(p_218525_0_, applyExplosionDecay(p_218525_0_, ItemLootEntry.lootTableItem(Items.STICK).apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))));
      });
      this.add(Blocks.NETHER_SPROUTS, BlockLootTables::createShearsOnlyDrop);
      this.add(Blocks.SEAGRASS, BlockLootTables::createShearsOnlyDrop);
      this.add(Blocks.VINE, BlockLootTables::createShearsOnlyDrop);
      this.add(Blocks.TALL_SEAGRASS, createDoublePlantShearsDrop(Blocks.SEAGRASS));
      this.add(Blocks.LARGE_FERN, (p_218572_0_) -> {
         return createDoublePlantWithSeedDrops(p_218572_0_, Blocks.FERN);
      });
      this.add(Blocks.TALL_GRASS, (p_241752_0_) -> {
         return createDoublePlantWithSeedDrops(p_241752_0_, Blocks.GRASS);
      });
      this.add(Blocks.MELON_STEM, (p_218550_0_) -> {
         return createStemDrops(p_218550_0_, Items.MELON_SEEDS);
      });
      this.add(Blocks.ATTACHED_MELON_STEM, (p_218531_0_) -> {
         return createAttachedStemDrops(p_218531_0_, Items.MELON_SEEDS);
      });
      this.add(Blocks.PUMPKIN_STEM, (p_218467_0_) -> {
         return createStemDrops(p_218467_0_, Items.PUMPKIN_SEEDS);
      });
      this.add(Blocks.ATTACHED_PUMPKIN_STEM, (p_218509_0_) -> {
         return createAttachedStemDrops(p_218509_0_, Items.PUMPKIN_SEEDS);
      });
      this.add(Blocks.CHORUS_FLOWER, (p_218512_0_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionCondition(p_218512_0_, ItemLootEntry.lootTableItem(p_218512_0_)).when(EntityHasProperty.entityPresent(LootContext.EntityTarget.THIS))));
      });
      this.add(Blocks.FERN, BlockLootTables::createGrassDrops);
      this.add(Blocks.GRASS, BlockLootTables::createGrassDrops);
      this.add(Blocks.GLOWSTONE, (p_218496_0_) -> {
         return createSilkTouchDispatchTable(p_218496_0_, applyExplosionDecay(p_218496_0_, ItemLootEntry.lootTableItem(Items.GLOWSTONE_DUST).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntClamper.clamp(1, 4)))));
      });
      this.add(Blocks.MELON, (p_218532_0_) -> {
         return createSilkTouchDispatchTable(p_218532_0_, applyExplosionDecay(p_218532_0_, ItemLootEntry.lootTableItem(Items.MELON_SLICE).apply(SetCount.setCount(RandomValueRange.between(3.0F, 7.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntClamper.upperBound(9)))));
      });
      this.add(Blocks.REDSTONE_ORE, (p_218464_0_) -> {
         return createSilkTouchDispatchTable(p_218464_0_, applyExplosionDecay(p_218464_0_, ItemLootEntry.lootTableItem(Items.REDSTONE).apply(SetCount.setCount(RandomValueRange.between(4.0F, 5.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.SEA_LANTERN, (p_218571_0_) -> {
         return createSilkTouchDispatchTable(p_218571_0_, applyExplosionDecay(p_218571_0_, ItemLootEntry.lootTableItem(Items.PRISMARINE_CRYSTALS).apply(SetCount.setCount(RandomValueRange.between(2.0F, 3.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntClamper.clamp(1, 5)))));
      });
      this.add(Blocks.NETHER_WART, (p_218553_0_) -> {
         return LootTable.lootTable().withPool(applyExplosionDecay(p_218553_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.NETHER_WART).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F)).when(BlockStateProperty.hasBlockStateProperties(p_218553_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(NetherWartBlock.AGE, 3)))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE).when(BlockStateProperty.hasBlockStateProperties(p_218553_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(NetherWartBlock.AGE, 3)))))));
      });
      this.add(Blocks.SNOW, (p_218485_0_) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().when(EntityHasProperty.entityPresent(LootContext.EntityTarget.THIS)).add(AlternativesLootEntry.alternatives(AlternativesLootEntry.alternatives(ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 1))), ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 2))).apply(SetCount.setCount(ConstantRange.exactly(2))), ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 3))).apply(SetCount.setCount(ConstantRange.exactly(3))), ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 4))).apply(SetCount.setCount(ConstantRange.exactly(4))), ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 5))).apply(SetCount.setCount(ConstantRange.exactly(5))), ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 6))).apply(SetCount.setCount(ConstantRange.exactly(6))), ItemLootEntry.lootTableItem(Items.SNOWBALL).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 7))).apply(SetCount.setCount(ConstantRange.exactly(7))), ItemLootEntry.lootTableItem(Items.SNOWBALL).apply(SetCount.setCount(ConstantRange.exactly(8)))).when(HAS_NO_SILK_TOUCH), AlternativesLootEntry.alternatives(ItemLootEntry.lootTableItem(Blocks.SNOW).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 1))), ItemLootEntry.lootTableItem(Blocks.SNOW).apply(SetCount.setCount(ConstantRange.exactly(2))).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 2))), ItemLootEntry.lootTableItem(Blocks.SNOW).apply(SetCount.setCount(ConstantRange.exactly(3))).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 3))), ItemLootEntry.lootTableItem(Blocks.SNOW).apply(SetCount.setCount(ConstantRange.exactly(4))).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 4))), ItemLootEntry.lootTableItem(Blocks.SNOW).apply(SetCount.setCount(ConstantRange.exactly(5))).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 5))), ItemLootEntry.lootTableItem(Blocks.SNOW).apply(SetCount.setCount(ConstantRange.exactly(6))).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 6))), ItemLootEntry.lootTableItem(Blocks.SNOW).apply(SetCount.setCount(ConstantRange.exactly(7))).when(BlockStateProperty.hasBlockStateProperties(p_218485_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowBlock.LAYERS, 7))), ItemLootEntry.lootTableItem(Blocks.SNOW_BLOCK)))));
      });
      this.add(Blocks.GRAVEL, (p_218533_0_) -> {
         return createSilkTouchDispatchTable(p_218533_0_, applyExplosionCondition(p_218533_0_, ItemLootEntry.lootTableItem(Items.FLINT).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(ItemLootEntry.lootTableItem(p_218533_0_))));
      });
      this.add(Blocks.CAMPFIRE, (p_218469_0_) -> {
         return createSilkTouchDispatchTable(p_218469_0_, applyExplosionCondition(p_218469_0_, ItemLootEntry.lootTableItem(Items.CHARCOAL).apply(SetCount.setCount(ConstantRange.exactly(2)))));
      });
      this.add(Blocks.GILDED_BLACKSTONE, (p_229432_0_) -> {
         return createSilkTouchDispatchTable(p_229432_0_, applyExplosionCondition(p_229432_0_, ItemLootEntry.lootTableItem(Items.GOLD_NUGGET).apply(SetCount.setCount(RandomValueRange.between(2.0F, 5.0F))).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(ItemLootEntry.lootTableItem(p_229432_0_))));
      });
      this.add(Blocks.SOUL_CAMPFIRE, (p_218538_0_) -> {
         return createSilkTouchDispatchTable(p_218538_0_, applyExplosionCondition(p_218538_0_, ItemLootEntry.lootTableItem(Items.SOUL_SOIL).apply(SetCount.setCount(ConstantRange.exactly(1)))));
      });
      this.dropWhenSilkTouch(Blocks.GLASS);
      this.dropWhenSilkTouch(Blocks.WHITE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.ORANGE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.YELLOW_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIME_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.PINK_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GRAY_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.CYAN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.PURPLE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BLUE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BROWN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GREEN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.RED_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BLACK_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.ICE);
      this.dropWhenSilkTouch(Blocks.PACKED_ICE);
      this.dropWhenSilkTouch(Blocks.BLUE_ICE);
      this.dropWhenSilkTouch(Blocks.TURTLE_EGG);
      this.dropWhenSilkTouch(Blocks.MUSHROOM_STEM);
      this.dropWhenSilkTouch(Blocks.DEAD_TUBE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_BRAIN_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_FIRE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_HORN_CORAL);
      this.dropWhenSilkTouch(Blocks.TUBE_CORAL);
      this.dropWhenSilkTouch(Blocks.BRAIN_CORAL);
      this.dropWhenSilkTouch(Blocks.BUBBLE_CORAL);
      this.dropWhenSilkTouch(Blocks.FIRE_CORAL);
      this.dropWhenSilkTouch(Blocks.HORN_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.TUBE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.BRAIN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.BUBBLE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.FIRE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.HORN_CORAL_FAN);
      this.otherWhenSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
      this.otherWhenSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
      this.otherWhenSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
      this.addNetherVinesDropTable(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
      this.addNetherVinesDropTable(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
      this.add(Blocks.CAKE, noDrop());
      this.add(Blocks.FROSTED_ICE, noDrop());
      this.add(Blocks.SPAWNER, noDrop());
      this.add(Blocks.FIRE, noDrop());
      this.add(Blocks.SOUL_FIRE, noDrop());
      this.add(Blocks.NETHER_PORTAL, noDrop());
   }

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      this.addTables();
      Set<ResourceLocation> set = Sets.newHashSet();

      for(Block block : getKnownBlocks()) {
         ResourceLocation resourcelocation = block.getLootTable();
         if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {
            LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
            if (loottable$builder == null) {
               throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
            }

            p_accept_1_.accept(resourcelocation, loottable$builder);
         }
      }

      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
      }
   }

   private void addNetherVinesDropTable(Block p_239830_1_, Block p_239830_2_) {
      LootTable.Builder loottable$builder = createSilkTouchOrShearsDispatchTable(p_239830_1_, ItemLootEntry.lootTableItem(p_239830_1_).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F)));
      this.add(p_239830_1_, loottable$builder);
      this.add(p_239830_2_, loottable$builder);
   }

   public static LootTable.Builder createDoorTable(Block p_239829_0_) {
      return createSinglePropConditionTable(p_239829_0_, DoorBlock.HALF, DoubleBlockHalf.LOWER);
   }

   protected Iterable<Block> getKnownBlocks() {
       return Registry.BLOCK;
   }

   public void dropPottedContents(Block p_218547_1_) {
      this.add(p_218547_1_, (p_241751_0_) -> {
         return createPotFlowerItemTable(((FlowerPotBlock)p_241751_0_).getContent());
      });
   }

   public void otherWhenSilkTouch(Block p_218564_1_, Block p_218564_2_) {
      this.add(p_218564_1_, createSilkTouchOnlyTable(p_218564_2_));
   }

   public void dropOther(Block p_218493_1_, IItemProvider p_218493_2_) {
      this.add(p_218493_1_, createSingleItemTable(p_218493_2_));
   }

   public void dropWhenSilkTouch(Block p_218466_1_) {
      this.otherWhenSilkTouch(p_218466_1_, p_218466_1_);
   }

   public void dropSelf(Block p_218492_1_) {
      this.dropOther(p_218492_1_, p_218492_1_);
   }

   protected void add(Block p_218522_1_, Function<Block, LootTable.Builder> p_218522_2_) {
      this.add(p_218522_1_, p_218522_2_.apply(p_218522_1_));
   }

   protected void add(Block p_218507_1_, LootTable.Builder p_218507_2_) {
      this.map.put(p_218507_1_.getLootTable(), p_218507_2_);
   }
}
