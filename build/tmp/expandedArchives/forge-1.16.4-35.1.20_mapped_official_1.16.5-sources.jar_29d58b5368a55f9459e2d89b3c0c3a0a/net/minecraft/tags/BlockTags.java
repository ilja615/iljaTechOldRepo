package net.minecraft.tags;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public final class BlockTags {
   protected static final TagRegistry<Block> HELPER = TagRegistryManager.create(new ResourceLocation("block"), ITagCollectionSupplier::getBlocks);
   public static final ITag.INamedTag<Block> WOOL = bind("wool");
   public static final ITag.INamedTag<Block> PLANKS = bind("planks");
   public static final ITag.INamedTag<Block> STONE_BRICKS = bind("stone_bricks");
   public static final ITag.INamedTag<Block> WOODEN_BUTTONS = bind("wooden_buttons");
   public static final ITag.INamedTag<Block> BUTTONS = bind("buttons");
   public static final ITag.INamedTag<Block> CARPETS = bind("carpets");
   public static final ITag.INamedTag<Block> WOODEN_DOORS = bind("wooden_doors");
   public static final ITag.INamedTag<Block> WOODEN_STAIRS = bind("wooden_stairs");
   public static final ITag.INamedTag<Block> WOODEN_SLABS = bind("wooden_slabs");
   public static final ITag.INamedTag<Block> WOODEN_FENCES = bind("wooden_fences");
   public static final ITag.INamedTag<Block> PRESSURE_PLATES = bind("pressure_plates");
   public static final ITag.INamedTag<Block> WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
   public static final ITag.INamedTag<Block> STONE_PRESSURE_PLATES = bind("stone_pressure_plates");
   public static final ITag.INamedTag<Block> WOODEN_TRAPDOORS = bind("wooden_trapdoors");
   public static final ITag.INamedTag<Block> DOORS = bind("doors");
   public static final ITag.INamedTag<Block> SAPLINGS = bind("saplings");
   public static final ITag.INamedTag<Block> LOGS_THAT_BURN = bind("logs_that_burn");
   public static final ITag.INamedTag<Block> LOGS = bind("logs");
   public static final ITag.INamedTag<Block> DARK_OAK_LOGS = bind("dark_oak_logs");
   public static final ITag.INamedTag<Block> OAK_LOGS = bind("oak_logs");
   public static final ITag.INamedTag<Block> BIRCH_LOGS = bind("birch_logs");
   public static final ITag.INamedTag<Block> ACACIA_LOGS = bind("acacia_logs");
   public static final ITag.INamedTag<Block> JUNGLE_LOGS = bind("jungle_logs");
   public static final ITag.INamedTag<Block> SPRUCE_LOGS = bind("spruce_logs");
   public static final ITag.INamedTag<Block> CRIMSON_STEMS = bind("crimson_stems");
   public static final ITag.INamedTag<Block> WARPED_STEMS = bind("warped_stems");
   public static final ITag.INamedTag<Block> BANNERS = bind("banners");
   public static final ITag.INamedTag<Block> SAND = bind("sand");
   public static final ITag.INamedTag<Block> STAIRS = bind("stairs");
   public static final ITag.INamedTag<Block> SLABS = bind("slabs");
   public static final ITag.INamedTag<Block> WALLS = bind("walls");
   public static final ITag.INamedTag<Block> ANVIL = bind("anvil");
   public static final ITag.INamedTag<Block> RAILS = bind("rails");
   public static final ITag.INamedTag<Block> LEAVES = bind("leaves");
   public static final ITag.INamedTag<Block> TRAPDOORS = bind("trapdoors");
   public static final ITag.INamedTag<Block> SMALL_FLOWERS = bind("small_flowers");
   public static final ITag.INamedTag<Block> BEDS = bind("beds");
   public static final ITag.INamedTag<Block> FENCES = bind("fences");
   public static final ITag.INamedTag<Block> TALL_FLOWERS = bind("tall_flowers");
   public static final ITag.INamedTag<Block> FLOWERS = bind("flowers");
   public static final ITag.INamedTag<Block> PIGLIN_REPELLENTS = bind("piglin_repellents");
   public static final ITag.INamedTag<Block> GOLD_ORES = bind("gold_ores");
   public static final ITag.INamedTag<Block> NON_FLAMMABLE_WOOD = bind("non_flammable_wood");
   public static final ITag.INamedTag<Block> FLOWER_POTS = bind("flower_pots");
   public static final ITag.INamedTag<Block> ENDERMAN_HOLDABLE = bind("enderman_holdable");
   public static final ITag.INamedTag<Block> ICE = bind("ice");
   public static final ITag.INamedTag<Block> VALID_SPAWN = bind("valid_spawn");
   public static final ITag.INamedTag<Block> IMPERMEABLE = bind("impermeable");
   public static final ITag.INamedTag<Block> UNDERWATER_BONEMEALS = bind("underwater_bonemeals");
   public static final ITag.INamedTag<Block> CORAL_BLOCKS = bind("coral_blocks");
   public static final ITag.INamedTag<Block> WALL_CORALS = bind("wall_corals");
   public static final ITag.INamedTag<Block> CORAL_PLANTS = bind("coral_plants");
   public static final ITag.INamedTag<Block> CORALS = bind("corals");
   public static final ITag.INamedTag<Block> BAMBOO_PLANTABLE_ON = bind("bamboo_plantable_on");
   public static final ITag.INamedTag<Block> STANDING_SIGNS = bind("standing_signs");
   public static final ITag.INamedTag<Block> WALL_SIGNS = bind("wall_signs");
   public static final ITag.INamedTag<Block> SIGNS = bind("signs");
   public static final ITag.INamedTag<Block> DRAGON_IMMUNE = bind("dragon_immune");
   public static final ITag.INamedTag<Block> WITHER_IMMUNE = bind("wither_immune");
   public static final ITag.INamedTag<Block> WITHER_SUMMON_BASE_BLOCKS = bind("wither_summon_base_blocks");
   public static final ITag.INamedTag<Block> BEEHIVES = bind("beehives");
   public static final ITag.INamedTag<Block> CROPS = bind("crops");
   public static final ITag.INamedTag<Block> BEE_GROWABLES = bind("bee_growables");
   public static final ITag.INamedTag<Block> PORTALS = bind("portals");
   public static final ITag.INamedTag<Block> FIRE = bind("fire");
   public static final ITag.INamedTag<Block> NYLIUM = bind("nylium");
   public static final ITag.INamedTag<Block> WART_BLOCKS = bind("wart_blocks");
   public static final ITag.INamedTag<Block> BEACON_BASE_BLOCKS = bind("beacon_base_blocks");
   public static final ITag.INamedTag<Block> SOUL_SPEED_BLOCKS = bind("soul_speed_blocks");
   public static final ITag.INamedTag<Block> WALL_POST_OVERRIDE = bind("wall_post_override");
   public static final ITag.INamedTag<Block> CLIMBABLE = bind("climbable");
   public static final ITag.INamedTag<Block> SHULKER_BOXES = bind("shulker_boxes");
   public static final ITag.INamedTag<Block> HOGLIN_REPELLENTS = bind("hoglin_repellents");
   public static final ITag.INamedTag<Block> SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
   public static final ITag.INamedTag<Block> STRIDER_WARM_BLOCKS = bind("strider_warm_blocks");
   public static final ITag.INamedTag<Block> CAMPFIRES = bind("campfires");
   public static final ITag.INamedTag<Block> GUARDED_BY_PIGLINS = bind("guarded_by_piglins");
   public static final ITag.INamedTag<Block> PREVENT_MOB_SPAWNING_INSIDE = bind("prevent_mob_spawning_inside");
   public static final ITag.INamedTag<Block> FENCE_GATES = bind("fence_gates");
   public static final ITag.INamedTag<Block> UNSTABLE_BOTTOM_CENTER = bind("unstable_bottom_center");
   public static final ITag.INamedTag<Block> MUSHROOM_GROW_BLOCK = bind("mushroom_grow_block");
   public static final ITag.INamedTag<Block> INFINIBURN_OVERWORLD = bind("infiniburn_overworld");
   public static final ITag.INamedTag<Block> INFINIBURN_NETHER = bind("infiniburn_nether");
   public static final ITag.INamedTag<Block> INFINIBURN_END = bind("infiniburn_end");
   public static final ITag.INamedTag<Block> BASE_STONE_OVERWORLD = bind("base_stone_overworld");
   public static final ITag.INamedTag<Block> BASE_STONE_NETHER = bind("base_stone_nether");

   public static ITag.INamedTag<Block> bind(String p_199894_0_) {
      return HELPER.bind(p_199894_0_);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Block> createOptional(ResourceLocation name) {
       return createOptional(name, null);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Block> createOptional(ResourceLocation name, @javax.annotation.Nullable java.util.Set<java.util.function.Supplier<Block>> defaults) {
      return HELPER.createOptional(name, defaults);
   }

   public static ITagCollection<Block> getAllTags() {
      return HELPER.getAllTags();
   }

   public static List<? extends ITag.INamedTag<Block>> getWrappers() {
      return HELPER.getWrappers();
   }
}
