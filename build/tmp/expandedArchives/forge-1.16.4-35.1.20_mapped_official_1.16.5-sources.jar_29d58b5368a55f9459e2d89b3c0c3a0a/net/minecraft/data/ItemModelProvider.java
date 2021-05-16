package net.minecraft.data;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class ItemModelProvider {
   private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output;

   public ItemModelProvider(BiConsumer<ResourceLocation, Supplier<JsonElement>> p_i232519_1_) {
      this.output = p_i232519_1_;
   }

   private void generateFlatItem(Item p_240076_1_, ModelsUtil p_240076_2_) {
      p_240076_2_.create(ModelsResourceUtil.getModelLocation(p_240076_1_), ModelTextures.layer0(p_240076_1_), this.output);
   }

   private void generateFlatItem(Item p_240077_1_, String p_240077_2_, ModelsUtil p_240077_3_) {
      p_240077_3_.create(ModelsResourceUtil.getModelLocation(p_240077_1_, p_240077_2_), ModelTextures.layer0(ModelTextures.getItemTexture(p_240077_1_, p_240077_2_)), this.output);
   }

   private void generateFlatItem(Item p_240075_1_, Item p_240075_2_, ModelsUtil p_240075_3_) {
      p_240075_3_.create(ModelsResourceUtil.getModelLocation(p_240075_1_), ModelTextures.layer0(p_240075_2_), this.output);
   }

   public void run() {
      this.generateFlatItem(Items.ACACIA_BOAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.APPLE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ARMOR_STAND, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ARROW, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BAKED_POTATO, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BAMBOO, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BEEF, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT_SOUP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BIRCH_BOAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BLACK_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_POWDER, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_ROD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BLUE_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BONE_MEAL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BOOK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BOWL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BREAD, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BRICK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BROWN_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CARROT_ON_A_STICK, StockModelShapes.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, StockModelShapes.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_BOOTS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_CHESTPLATE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_HELMET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHAINMAIL_LEGGINGS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHARCOAL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHEST_MINECART, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHICKEN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CHORUS_FRUIT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CLAY_BALL, StockModelShapes.FLAT_ITEM);

      for(int i = 1; i < 64; ++i) {
         this.generateFlatItem(Items.CLOCK, String.format("_%02d", i), StockModelShapes.FLAT_ITEM);
      }

      this.generateFlatItem(Items.COAL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COD_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, StockModelShapes.FLAT_ITEM);

      for(int j = 0; j < 32; ++j) {
         if (j != 16) {
            this.generateFlatItem(Items.COMPASS, String.format("_%02d", j), StockModelShapes.FLAT_ITEM);
         }
      }

      this.generateFlatItem(Items.COOKED_BEEF, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_CHICKEN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_COD, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_MUTTON, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_PORKCHOP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_RABBIT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_SALMON, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.COOKIE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.CYAN_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DARK_OAK_BOAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_AXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_BOOTS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_CHESTPLATE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_HELMET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_HOE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_LEGGINGS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_PICKAXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SHOVEL, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SWORD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DRAGON_BREATH, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DRIED_KELP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.EGG, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.EMERALD, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ENCHANTED_BOOK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_EYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_PEARL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.END_CRYSTAL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.EXPERIENCE_BOTTLE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FIREWORK_ROCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FIRE_CHARGE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FLINT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FLINT_AND_STEEL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.FURNACE_MINECART, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GHAST_TEAR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GLASS_BOTTLE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GLISTERING_MELON_SLICE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GLOBE_BANNER_PATTER, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GLOWSTONE_DUST, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_APPLE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_AXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_BOOTS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_CARROT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_CHESTPLATE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HELMET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HOE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_LEGGINGS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_PICKAXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SHOVEL, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SWORD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLD_INGOT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GOLD_NUGGET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GRAY_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GREEN_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.GUNPOWDER, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.HEART_OF_THE_SEA, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.HONEYCOMB, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.HONEY_BOTTLE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.HOPPER_MINECART, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.INK_SAC, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_AXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_BOOTS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_CHESTPLATE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_HELMET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_HOE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_HORSE_ARMOR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_INGOT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_LEGGINGS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_NUGGET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_PICKAXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SHOVEL, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SWORD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ITEM_FRAME, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.JUNGLE_BOAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.KNOWLEDGE_BOOK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LAPIS_LAZULI, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LAVA_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER_HORSE_ARMOR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_BLUE_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_GRAY_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.LIME_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MAGENTA_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MAGMA_CREAM, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MAP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MELON_SLICE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MILK_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MINECART, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSHROOM_STEW, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_11, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_13, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_CAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_CHIRP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_FAR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_MALL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_STAL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_STRAD, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_WAIT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_WARD, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.MUTTON, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NAME_TAG, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NAUTILUS_SHELL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_AXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_BOOTS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_CHESTPLATE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_HELMET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_HOE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_INGOT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_LEGGINGS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_PICKAXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SCRAP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_SHOVEL, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SWORD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHER_BRICK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.NETHER_STAR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.OAK_BOAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ORANGE_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PAINTING, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PAPER, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PHANTOM_MEMBRANE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PINK_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.POISONOUS_POTATO, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PORKCHOP, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_CRYSTALS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_SHARD, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PUMPKIN_PIE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.PURPLE_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.QUARTZ, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_FOOT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_HIDE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_STEW, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.RED_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.ROTTEN_FLESH, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SADDLE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SALMON, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SALMON_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SCUTE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SHEARS, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SHULKER_SHELL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SKULL_BANNER_PATTERN, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SLIME_BALL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SNOWBALL, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SPECTRAL_ARROW, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SPIDER_EYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SPRUCE_BOAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.STICK, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_AXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_HOE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_PICKAXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SHOVEL, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SWORD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.SUGAR, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.SUSPICIOUS_STEW, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.TNT_MINECART, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.TOTEM_OF_UNDYING, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.TRIDENT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.TURTLE_HELMET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.WATER_BUCKET, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.WHEAT, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.WHITE_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.WOODEN_AXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_HOE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_PICKAXE, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SHOVEL, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SWORD, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WRITABLE_BOOK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.WRITTEN_BOOK, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.YELLOW_DYE, StockModelShapes.FLAT_ITEM);
      this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, StockModelShapes.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, StockModelShapes.FLAT_ITEM);
   }
}
