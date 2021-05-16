package net.minecraft.tags;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class ItemTags {
   protected static final TagRegistry<Item> HELPER = TagRegistryManager.create(new ResourceLocation("item"), ITagCollectionSupplier::getItems);
   public static final ITag.INamedTag<Item> WOOL = bind("wool");
   public static final ITag.INamedTag<Item> PLANKS = bind("planks");
   public static final ITag.INamedTag<Item> STONE_BRICKS = bind("stone_bricks");
   public static final ITag.INamedTag<Item> WOODEN_BUTTONS = bind("wooden_buttons");
   public static final ITag.INamedTag<Item> BUTTONS = bind("buttons");
   public static final ITag.INamedTag<Item> CARPETS = bind("carpets");
   public static final ITag.INamedTag<Item> WOODEN_DOORS = bind("wooden_doors");
   public static final ITag.INamedTag<Item> WOODEN_STAIRS = bind("wooden_stairs");
   public static final ITag.INamedTag<Item> WOODEN_SLABS = bind("wooden_slabs");
   public static final ITag.INamedTag<Item> WOODEN_FENCES = bind("wooden_fences");
   public static final ITag.INamedTag<Item> WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
   public static final ITag.INamedTag<Item> WOODEN_TRAPDOORS = bind("wooden_trapdoors");
   public static final ITag.INamedTag<Item> DOORS = bind("doors");
   public static final ITag.INamedTag<Item> SAPLINGS = bind("saplings");
   public static final ITag.INamedTag<Item> LOGS_THAT_BURN = bind("logs_that_burn");
   public static final ITag.INamedTag<Item> LOGS = bind("logs");
   public static final ITag.INamedTag<Item> DARK_OAK_LOGS = bind("dark_oak_logs");
   public static final ITag.INamedTag<Item> OAK_LOGS = bind("oak_logs");
   public static final ITag.INamedTag<Item> BIRCH_LOGS = bind("birch_logs");
   public static final ITag.INamedTag<Item> ACACIA_LOGS = bind("acacia_logs");
   public static final ITag.INamedTag<Item> JUNGLE_LOGS = bind("jungle_logs");
   public static final ITag.INamedTag<Item> SPRUCE_LOGS = bind("spruce_logs");
   public static final ITag.INamedTag<Item> CRIMSON_STEMS = bind("crimson_stems");
   public static final ITag.INamedTag<Item> WARPED_STEMS = bind("warped_stems");
   public static final ITag.INamedTag<Item> BANNERS = bind("banners");
   public static final ITag.INamedTag<Item> SAND = bind("sand");
   public static final ITag.INamedTag<Item> STAIRS = bind("stairs");
   public static final ITag.INamedTag<Item> SLABS = bind("slabs");
   public static final ITag.INamedTag<Item> WALLS = bind("walls");
   public static final ITag.INamedTag<Item> ANVIL = bind("anvil");
   public static final ITag.INamedTag<Item> RAILS = bind("rails");
   public static final ITag.INamedTag<Item> LEAVES = bind("leaves");
   public static final ITag.INamedTag<Item> TRAPDOORS = bind("trapdoors");
   public static final ITag.INamedTag<Item> SMALL_FLOWERS = bind("small_flowers");
   public static final ITag.INamedTag<Item> BEDS = bind("beds");
   public static final ITag.INamedTag<Item> FENCES = bind("fences");
   public static final ITag.INamedTag<Item> TALL_FLOWERS = bind("tall_flowers");
   public static final ITag.INamedTag<Item> FLOWERS = bind("flowers");
   public static final ITag.INamedTag<Item> PIGLIN_REPELLENTS = bind("piglin_repellents");
   public static final ITag.INamedTag<Item> PIGLIN_LOVED = bind("piglin_loved");
   public static final ITag.INamedTag<Item> GOLD_ORES = bind("gold_ores");
   public static final ITag.INamedTag<Item> NON_FLAMMABLE_WOOD = bind("non_flammable_wood");
   public static final ITag.INamedTag<Item> SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
   public static final ITag.INamedTag<Item> BOATS = bind("boats");
   public static final ITag.INamedTag<Item> FISHES = bind("fishes");
   public static final ITag.INamedTag<Item> SIGNS = bind("signs");
   public static final ITag.INamedTag<Item> MUSIC_DISCS = bind("music_discs");
   public static final ITag.INamedTag<Item> CREEPER_DROP_MUSIC_DISCS = bind("creeper_drop_music_discs");
   public static final ITag.INamedTag<Item> COALS = bind("coals");
   public static final ITag.INamedTag<Item> ARROWS = bind("arrows");
   public static final ITag.INamedTag<Item> LECTERN_BOOKS = bind("lectern_books");
   public static final ITag.INamedTag<Item> BEACON_PAYMENT_ITEMS = bind("beacon_payment_items");
   public static final ITag.INamedTag<Item> STONE_TOOL_MATERIALS = bind("stone_tool_materials");
   public static final ITag.INamedTag<Item> STONE_CRAFTING_MATERIALS = bind("stone_crafting_materials");

   public static ITag.INamedTag<Item> bind(String p_199901_0_) {
      return HELPER.bind(p_199901_0_);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Item> createOptional(ResourceLocation name) {
       return createOptional(name, null);
   }

   public static net.minecraftforge.common.Tags.IOptionalNamedTag<Item> createOptional(ResourceLocation name, @javax.annotation.Nullable java.util.Set<java.util.function.Supplier<Item>> defaults) {
      return HELPER.createOptional(name, defaults);
   }

   public static ITagCollection<Item> getAllTags() {
      return HELPER.getAllTags();
   }

   public static List<? extends ITag.INamedTag<Item>> getWrappers() {
      return HELPER.getWrappers();
   }
}
