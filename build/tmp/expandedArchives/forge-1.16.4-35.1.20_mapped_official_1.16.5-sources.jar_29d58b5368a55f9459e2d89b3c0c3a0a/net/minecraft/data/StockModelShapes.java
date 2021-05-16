package net.minecraft.data;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.util.ResourceLocation;

public class StockModelShapes {
   public static final ModelsUtil CUBE = create("cube", StockTextureAliases.PARTICLE, StockTextureAliases.NORTH, StockTextureAliases.SOUTH, StockTextureAliases.EAST, StockTextureAliases.WEST, StockTextureAliases.UP, StockTextureAliases.DOWN);
   public static final ModelsUtil CUBE_DIRECTIONAL = create("cube_directional", StockTextureAliases.PARTICLE, StockTextureAliases.NORTH, StockTextureAliases.SOUTH, StockTextureAliases.EAST, StockTextureAliases.WEST, StockTextureAliases.UP, StockTextureAliases.DOWN);
   public static final ModelsUtil CUBE_ALL = create("cube_all", StockTextureAliases.ALL);
   public static final ModelsUtil CUBE_MIRRORED_ALL = create("cube_mirrored_all", "_mirrored", StockTextureAliases.ALL);
   public static final ModelsUtil CUBE_COLUMN = create("cube_column", StockTextureAliases.END, StockTextureAliases.SIDE);
   public static final ModelsUtil CUBE_COLUMN_HORIZONTAL = create("cube_column_horizontal", "_horizontal", StockTextureAliases.END, StockTextureAliases.SIDE);
   public static final ModelsUtil CUBE_TOP = create("cube_top", StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil CUBE_BOTTOM_TOP = create("cube_bottom_top", StockTextureAliases.TOP, StockTextureAliases.BOTTOM, StockTextureAliases.SIDE);
   public static final ModelsUtil CUBE_ORIENTABLE = create("orientable", StockTextureAliases.TOP, StockTextureAliases.FRONT, StockTextureAliases.SIDE);
   public static final ModelsUtil CUBE_ORIENTABLE_TOP_BOTTOM = create("orientable_with_bottom", StockTextureAliases.TOP, StockTextureAliases.BOTTOM, StockTextureAliases.SIDE, StockTextureAliases.FRONT);
   public static final ModelsUtil CUBE_ORIENTABLE_VERTICAL = create("orientable_vertical", "_vertical", StockTextureAliases.FRONT, StockTextureAliases.SIDE);
   public static final ModelsUtil BUTTON = create("button", StockTextureAliases.TEXTURE);
   public static final ModelsUtil BUTTON_PRESSED = create("button_pressed", "_pressed", StockTextureAliases.TEXTURE);
   public static final ModelsUtil BUTTON_INVENTORY = create("button_inventory", "_inventory", StockTextureAliases.TEXTURE);
   public static final ModelsUtil DOOR_BOTTOM = create("door_bottom", "_bottom", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
   public static final ModelsUtil DOOR_BOTTOM_HINGE = create("door_bottom_rh", "_bottom_hinge", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
   public static final ModelsUtil DOOR_TOP = create("door_top", "_top", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
   public static final ModelsUtil DOOR_TOP_HINGE = create("door_top_rh", "_top_hinge", StockTextureAliases.TOP, StockTextureAliases.BOTTOM);
   public static final ModelsUtil FENCE_POST = create("fence_post", "_post", StockTextureAliases.TEXTURE);
   public static final ModelsUtil FENCE_SIDE = create("fence_side", "_side", StockTextureAliases.TEXTURE);
   public static final ModelsUtil FENCE_INVENTORY = create("fence_inventory", "_inventory", StockTextureAliases.TEXTURE);
   public static final ModelsUtil WALL_POST = create("template_wall_post", "_post", StockTextureAliases.WALL);
   public static final ModelsUtil WALL_LOW_SIDE = create("template_wall_side", "_side", StockTextureAliases.WALL);
   public static final ModelsUtil WALL_TALL_SIDE = create("template_wall_side_tall", "_side_tall", StockTextureAliases.WALL);
   public static final ModelsUtil WALL_INVENTORY = create("wall_inventory", "_inventory", StockTextureAliases.WALL);
   public static final ModelsUtil FENCE_GATE_CLOSED = create("template_fence_gate", StockTextureAliases.TEXTURE);
   public static final ModelsUtil FENCE_GATE_OPEN = create("template_fence_gate_open", "_open", StockTextureAliases.TEXTURE);
   public static final ModelsUtil FENCE_GATE_WALL_CLOSED = create("template_fence_gate_wall", "_wall", StockTextureAliases.TEXTURE);
   public static final ModelsUtil FENCE_GATE_WALL_OPEN = create("template_fence_gate_wall_open", "_wall_open", StockTextureAliases.TEXTURE);
   public static final ModelsUtil PRESSURE_PLATE_UP = create("pressure_plate_up", StockTextureAliases.TEXTURE);
   public static final ModelsUtil PRESSURE_PLATE_DOWN = create("pressure_plate_down", "_down", StockTextureAliases.TEXTURE);
   public static final ModelsUtil PARTICLE_ONLY = create(StockTextureAliases.PARTICLE);
   public static final ModelsUtil SLAB_BOTTOM = create("slab", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil SLAB_TOP = create("slab_top", "_top", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil LEAVES = create("leaves", StockTextureAliases.ALL);
   public static final ModelsUtil STAIRS_STRAIGHT = create("stairs", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil STAIRS_INNER = create("inner_stairs", "_inner", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil STAIRS_OUTER = create("outer_stairs", "_outer", StockTextureAliases.BOTTOM, StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil TRAPDOOR_TOP = create("template_trapdoor_top", "_top", StockTextureAliases.TEXTURE);
   public static final ModelsUtil TRAPDOOR_BOTTOM = create("template_trapdoor_bottom", "_bottom", StockTextureAliases.TEXTURE);
   public static final ModelsUtil TRAPDOOR_OPEN = create("template_trapdoor_open", "_open", StockTextureAliases.TEXTURE);
   public static final ModelsUtil ORIENTABLE_TRAPDOOR_TOP = create("template_orientable_trapdoor_top", "_top", StockTextureAliases.TEXTURE);
   public static final ModelsUtil ORIENTABLE_TRAPDOOR_BOTTOM = create("template_orientable_trapdoor_bottom", "_bottom", StockTextureAliases.TEXTURE);
   public static final ModelsUtil ORIENTABLE_TRAPDOOR_OPEN = create("template_orientable_trapdoor_open", "_open", StockTextureAliases.TEXTURE);
   public static final ModelsUtil CROSS = create("cross", StockTextureAliases.CROSS);
   public static final ModelsUtil TINTED_CROSS = create("tinted_cross", StockTextureAliases.CROSS);
   public static final ModelsUtil FLOWER_POT_CROSS = create("flower_pot_cross", StockTextureAliases.PLANT);
   public static final ModelsUtil TINTED_FLOWER_POT_CROSS = create("tinted_flower_pot_cross", StockTextureAliases.PLANT);
   public static final ModelsUtil RAIL_FLAT = create("rail_flat", StockTextureAliases.RAIL);
   public static final ModelsUtil RAIL_CURVED = create("rail_curved", "_corner", StockTextureAliases.RAIL);
   public static final ModelsUtil RAIL_RAISED_NE = create("template_rail_raised_ne", "_raised_ne", StockTextureAliases.RAIL);
   public static final ModelsUtil RAIL_RAISED_SW = create("template_rail_raised_sw", "_raised_sw", StockTextureAliases.RAIL);
   public static final ModelsUtil CARPET = create("carpet", StockTextureAliases.WOOL);
   public static final ModelsUtil CORAL_FAN = create("coral_fan", StockTextureAliases.FAN);
   public static final ModelsUtil CORAL_WALL_FAN = create("coral_wall_fan", StockTextureAliases.FAN);
   public static final ModelsUtil GLAZED_TERRACOTTA = create("template_glazed_terracotta", StockTextureAliases.PATTERN);
   public static final ModelsUtil CHORUS_FLOWER = create("template_chorus_flower", StockTextureAliases.TEXTURE);
   public static final ModelsUtil DAYLIGHT_DETECTOR = create("template_daylight_detector", StockTextureAliases.TOP, StockTextureAliases.SIDE);
   public static final ModelsUtil STAINED_GLASS_PANE_NOSIDE = create("template_glass_pane_noside", "_noside", StockTextureAliases.PANE);
   public static final ModelsUtil STAINED_GLASS_PANE_NOSIDE_ALT = create("template_glass_pane_noside_alt", "_noside_alt", StockTextureAliases.PANE);
   public static final ModelsUtil STAINED_GLASS_PANE_POST = create("template_glass_pane_post", "_post", StockTextureAliases.PANE, StockTextureAliases.EDGE);
   public static final ModelsUtil STAINED_GLASS_PANE_SIDE = create("template_glass_pane_side", "_side", StockTextureAliases.PANE, StockTextureAliases.EDGE);
   public static final ModelsUtil STAINED_GLASS_PANE_SIDE_ALT = create("template_glass_pane_side_alt", "_side_alt", StockTextureAliases.PANE, StockTextureAliases.EDGE);
   public static final ModelsUtil COMMAND_BLOCK = create("template_command_block", StockTextureAliases.FRONT, StockTextureAliases.BACK, StockTextureAliases.SIDE);
   public static final ModelsUtil ANVIL = create("template_anvil", StockTextureAliases.TOP);
   public static final ModelsUtil[] STEMS = IntStream.range(0, 8).mapToObj((p_240335_0_) -> {
      return create("stem_growth" + p_240335_0_, "_stage" + p_240335_0_, StockTextureAliases.STEM);
   }).toArray((p_240331_0_) -> {
      return new ModelsUtil[p_240331_0_];
   });
   public static final ModelsUtil ATTACHED_STEM = create("stem_fruit", StockTextureAliases.STEM, StockTextureAliases.UPPER_STEM);
   public static final ModelsUtil CROP = create("crop", StockTextureAliases.CROP);
   public static final ModelsUtil FARMLAND = create("template_farmland", StockTextureAliases.DIRT, StockTextureAliases.TOP);
   public static final ModelsUtil FIRE_FLOOR = create("template_fire_floor", StockTextureAliases.FIRE);
   public static final ModelsUtil FIRE_SIDE = create("template_fire_side", StockTextureAliases.FIRE);
   public static final ModelsUtil FIRE_SIDE_ALT = create("template_fire_side_alt", StockTextureAliases.FIRE);
   public static final ModelsUtil FIRE_UP = create("template_fire_up", StockTextureAliases.FIRE);
   public static final ModelsUtil FIRE_UP_ALT = create("template_fire_up_alt", StockTextureAliases.FIRE);
   public static final ModelsUtil CAMPFIRE = create("template_campfire", StockTextureAliases.FIRE, StockTextureAliases.LIT_LOG);
   public static final ModelsUtil LANTERN = create("template_lantern", StockTextureAliases.LANTERN);
   public static final ModelsUtil HANGING_LANTERN = create("template_hanging_lantern", "_hanging", StockTextureAliases.LANTERN);
   public static final ModelsUtil TORCH = create("template_torch", StockTextureAliases.TORCH);
   public static final ModelsUtil WALL_TORCH = create("template_torch_wall", StockTextureAliases.TORCH);
   public static final ModelsUtil PISTON = create("template_piston", StockTextureAliases.PLATFORM, StockTextureAliases.BOTTOM, StockTextureAliases.SIDE);
   public static final ModelsUtil PISTON_HEAD = create("template_piston_head", StockTextureAliases.PLATFORM, StockTextureAliases.SIDE, StockTextureAliases.UNSTICKY);
   public static final ModelsUtil PISTON_HEAD_SHORT = create("template_piston_head_short", StockTextureAliases.PLATFORM, StockTextureAliases.SIDE, StockTextureAliases.UNSTICKY);
   public static final ModelsUtil SEAGRASS = create("template_seagrass", StockTextureAliases.TEXTURE);
   public static final ModelsUtil TURTLE_EGG = create("template_turtle_egg", StockTextureAliases.ALL);
   public static final ModelsUtil TWO_TURTLE_EGGS = create("template_two_turtle_eggs", StockTextureAliases.ALL);
   public static final ModelsUtil THREE_TURTLE_EGGS = create("template_three_turtle_eggs", StockTextureAliases.ALL);
   public static final ModelsUtil FOUR_TURTLE_EGGS = create("template_four_turtle_eggs", StockTextureAliases.ALL);
   public static final ModelsUtil SINGLE_FACE = create("template_single_face", StockTextureAliases.TEXTURE);
   public static final ModelsUtil FLAT_ITEM = createItem("generated", StockTextureAliases.LAYER0);
   public static final ModelsUtil FLAT_HANDHELD_ITEM = createItem("handheld", StockTextureAliases.LAYER0);
   public static final ModelsUtil FLAT_HANDHELD_ROD_ITEM = createItem("handheld_rod", StockTextureAliases.LAYER0);
   public static final ModelsUtil SHULKER_BOX_INVENTORY = createItem("template_shulker_box", StockTextureAliases.PARTICLE);
   public static final ModelsUtil BED_INVENTORY = createItem("template_bed", StockTextureAliases.PARTICLE);
   public static final ModelsUtil BANNER_INVENTORY = createItem("template_banner");
   public static final ModelsUtil SKULL_INVENTORY = createItem("template_skull");

   private static ModelsUtil create(StockTextureAliases... p_240334_0_) {
      return new ModelsUtil(Optional.empty(), Optional.empty(), p_240334_0_);
   }

   private static ModelsUtil create(String p_240333_0_, StockTextureAliases... p_240333_1_) {
      return new ModelsUtil(Optional.of(new ResourceLocation("minecraft", "block/" + p_240333_0_)), Optional.empty(), p_240333_1_);
   }

   private static ModelsUtil createItem(String p_240336_0_, StockTextureAliases... p_240336_1_) {
      return new ModelsUtil(Optional.of(new ResourceLocation("minecraft", "item/" + p_240336_0_)), Optional.empty(), p_240336_1_);
   }

   private static ModelsUtil create(String p_240332_0_, String p_240332_1_, StockTextureAliases... p_240332_2_) {
      return new ModelsUtil(Optional.of(new ResourceLocation("minecraft", "block/" + p_240332_0_)), Optional.of(p_240332_1_), p_240332_2_);
   }
}
