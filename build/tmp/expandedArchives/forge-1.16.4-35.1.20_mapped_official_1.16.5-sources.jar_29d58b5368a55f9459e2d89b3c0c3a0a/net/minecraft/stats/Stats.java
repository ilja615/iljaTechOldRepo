package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class Stats {
   public static final StatType<Block> BLOCK_MINED = makeRegistryStatType("mined", Registry.BLOCK);
   public static final StatType<Item> ITEM_CRAFTED = makeRegistryStatType("crafted", Registry.ITEM);
   public static final StatType<Item> ITEM_USED = makeRegistryStatType("used", Registry.ITEM);
   public static final StatType<Item> ITEM_BROKEN = makeRegistryStatType("broken", Registry.ITEM);
   public static final StatType<Item> ITEM_PICKED_UP = makeRegistryStatType("picked_up", Registry.ITEM);
   public static final StatType<Item> ITEM_DROPPED = makeRegistryStatType("dropped", Registry.ITEM);
   public static final StatType<EntityType<?>> ENTITY_KILLED = makeRegistryStatType("killed", Registry.ENTITY_TYPE);
   public static final StatType<EntityType<?>> ENTITY_KILLED_BY = makeRegistryStatType("killed_by", Registry.ENTITY_TYPE);
   public static final StatType<ResourceLocation> CUSTOM = makeRegistryStatType("custom", Registry.CUSTOM_STAT);
   public static final ResourceLocation LEAVE_GAME = makeCustomStat("leave_game", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_ONE_MINUTE = makeCustomStat("play_one_minute", IStatFormatter.TIME);
   public static final ResourceLocation TIME_SINCE_DEATH = makeCustomStat("time_since_death", IStatFormatter.TIME);
   public static final ResourceLocation TIME_SINCE_REST = makeCustomStat("time_since_rest", IStatFormatter.TIME);
   public static final ResourceLocation CROUCH_TIME = makeCustomStat("sneak_time", IStatFormatter.TIME);
   public static final ResourceLocation WALK_ONE_CM = makeCustomStat("walk_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation CROUCH_ONE_CM = makeCustomStat("crouch_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation SPRINT_ONE_CM = makeCustomStat("sprint_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation WALK_ON_WATER_ONE_CM = makeCustomStat("walk_on_water_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation FALL_ONE_CM = makeCustomStat("fall_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation CLIMB_ONE_CM = makeCustomStat("climb_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation FLY_ONE_CM = makeCustomStat("fly_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation WALK_UNDER_WATER_ONE_CM = makeCustomStat("walk_under_water_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation MINECART_ONE_CM = makeCustomStat("minecart_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation BOAT_ONE_CM = makeCustomStat("boat_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation PIG_ONE_CM = makeCustomStat("pig_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation HORSE_ONE_CM = makeCustomStat("horse_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation AVIATE_ONE_CM = makeCustomStat("aviate_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation SWIM_ONE_CM = makeCustomStat("swim_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation STRIDER_ONE_CM = makeCustomStat("strider_one_cm", IStatFormatter.DISTANCE);
   public static final ResourceLocation JUMP = makeCustomStat("jump", IStatFormatter.DEFAULT);
   public static final ResourceLocation DROP = makeCustomStat("drop", IStatFormatter.DEFAULT);
   public static final ResourceLocation DAMAGE_DEALT = makeCustomStat("damage_dealt", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_DEALT_ABSORBED = makeCustomStat("damage_dealt_absorbed", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_DEALT_RESISTED = makeCustomStat("damage_dealt_resisted", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_TAKEN = makeCustomStat("damage_taken", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_BLOCKED_BY_SHIELD = makeCustomStat("damage_blocked_by_shield", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_ABSORBED = makeCustomStat("damage_absorbed", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DAMAGE_RESISTED = makeCustomStat("damage_resisted", IStatFormatter.DIVIDE_BY_TEN);
   public static final ResourceLocation DEATHS = makeCustomStat("deaths", IStatFormatter.DEFAULT);
   public static final ResourceLocation MOB_KILLS = makeCustomStat("mob_kills", IStatFormatter.DEFAULT);
   public static final ResourceLocation ANIMALS_BRED = makeCustomStat("animals_bred", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAYER_KILLS = makeCustomStat("player_kills", IStatFormatter.DEFAULT);
   public static final ResourceLocation FISH_CAUGHT = makeCustomStat("fish_caught", IStatFormatter.DEFAULT);
   public static final ResourceLocation TALKED_TO_VILLAGER = makeCustomStat("talked_to_villager", IStatFormatter.DEFAULT);
   public static final ResourceLocation TRADED_WITH_VILLAGER = makeCustomStat("traded_with_villager", IStatFormatter.DEFAULT);
   public static final ResourceLocation EAT_CAKE_SLICE = makeCustomStat("eat_cake_slice", IStatFormatter.DEFAULT);
   public static final ResourceLocation FILL_CAULDRON = makeCustomStat("fill_cauldron", IStatFormatter.DEFAULT);
   public static final ResourceLocation USE_CAULDRON = makeCustomStat("use_cauldron", IStatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_ARMOR = makeCustomStat("clean_armor", IStatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_BANNER = makeCustomStat("clean_banner", IStatFormatter.DEFAULT);
   public static final ResourceLocation CLEAN_SHULKER_BOX = makeCustomStat("clean_shulker_box", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BREWINGSTAND = makeCustomStat("interact_with_brewingstand", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BEACON = makeCustomStat("interact_with_beacon", IStatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_DROPPER = makeCustomStat("inspect_dropper", IStatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_HOPPER = makeCustomStat("inspect_hopper", IStatFormatter.DEFAULT);
   public static final ResourceLocation INSPECT_DISPENSER = makeCustomStat("inspect_dispenser", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_NOTEBLOCK = makeCustomStat("play_noteblock", IStatFormatter.DEFAULT);
   public static final ResourceLocation TUNE_NOTEBLOCK = makeCustomStat("tune_noteblock", IStatFormatter.DEFAULT);
   public static final ResourceLocation POT_FLOWER = makeCustomStat("pot_flower", IStatFormatter.DEFAULT);
   public static final ResourceLocation TRIGGER_TRAPPED_CHEST = makeCustomStat("trigger_trapped_chest", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_ENDERCHEST = makeCustomStat("open_enderchest", IStatFormatter.DEFAULT);
   public static final ResourceLocation ENCHANT_ITEM = makeCustomStat("enchant_item", IStatFormatter.DEFAULT);
   public static final ResourceLocation PLAY_RECORD = makeCustomStat("play_record", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_FURNACE = makeCustomStat("interact_with_furnace", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CRAFTING_TABLE = makeCustomStat("interact_with_crafting_table", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_CHEST = makeCustomStat("open_chest", IStatFormatter.DEFAULT);
   public static final ResourceLocation SLEEP_IN_BED = makeCustomStat("sleep_in_bed", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_SHULKER_BOX = makeCustomStat("open_shulker_box", IStatFormatter.DEFAULT);
   public static final ResourceLocation OPEN_BARREL = makeCustomStat("open_barrel", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_BLAST_FURNACE = makeCustomStat("interact_with_blast_furnace", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_SMOKER = makeCustomStat("interact_with_smoker", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_LECTERN = makeCustomStat("interact_with_lectern", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CAMPFIRE = makeCustomStat("interact_with_campfire", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_CARTOGRAPHY_TABLE = makeCustomStat("interact_with_cartography_table", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_LOOM = makeCustomStat("interact_with_loom", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_STONECUTTER = makeCustomStat("interact_with_stonecutter", IStatFormatter.DEFAULT);
   public static final ResourceLocation BELL_RING = makeCustomStat("bell_ring", IStatFormatter.DEFAULT);
   public static final ResourceLocation RAID_TRIGGER = makeCustomStat("raid_trigger", IStatFormatter.DEFAULT);
   public static final ResourceLocation RAID_WIN = makeCustomStat("raid_win", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_ANVIL = makeCustomStat("interact_with_anvil", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_GRINDSTONE = makeCustomStat("interact_with_grindstone", IStatFormatter.DEFAULT);
   public static final ResourceLocation TARGET_HIT = makeCustomStat("target_hit", IStatFormatter.DEFAULT);
   public static final ResourceLocation INTERACT_WITH_SMITHING_TABLE = makeCustomStat("interact_with_smithing_table", IStatFormatter.DEFAULT);

   private static ResourceLocation makeCustomStat(String p_199084_0_, IStatFormatter p_199084_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_199084_0_);
      Registry.register(Registry.CUSTOM_STAT, p_199084_0_, resourcelocation);
      CUSTOM.get(resourcelocation, p_199084_1_);
      return resourcelocation;
   }

   private static <T> StatType<T> makeRegistryStatType(String p_199085_0_, Registry<T> p_199085_1_) {
      return Registry.register(Registry.STAT_TYPE, p_199085_0_, new StatType<>(p_199085_1_));
   }
}
