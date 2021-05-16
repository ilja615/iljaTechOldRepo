package net.minecraft.tileentity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityType<T extends TileEntity> extends net.minecraftforge.registries.ForgeRegistryEntry<TileEntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TileEntityType<FurnaceTileEntity> FURNACE = register("furnace", TileEntityType.Builder.of(FurnaceTileEntity::new, Blocks.FURNACE));
   public static final TileEntityType<ChestTileEntity> CHEST = register("chest", TileEntityType.Builder.of(ChestTileEntity::new, Blocks.CHEST));
   public static final TileEntityType<TrappedChestTileEntity> TRAPPED_CHEST = register("trapped_chest", TileEntityType.Builder.of(TrappedChestTileEntity::new, Blocks.TRAPPED_CHEST));
   public static final TileEntityType<EnderChestTileEntity> ENDER_CHEST = register("ender_chest", TileEntityType.Builder.of(EnderChestTileEntity::new, Blocks.ENDER_CHEST));
   public static final TileEntityType<JukeboxTileEntity> JUKEBOX = register("jukebox", TileEntityType.Builder.of(JukeboxTileEntity::new, Blocks.JUKEBOX));
   public static final TileEntityType<DispenserTileEntity> DISPENSER = register("dispenser", TileEntityType.Builder.of(DispenserTileEntity::new, Blocks.DISPENSER));
   public static final TileEntityType<DropperTileEntity> DROPPER = register("dropper", TileEntityType.Builder.of(DropperTileEntity::new, Blocks.DROPPER));
   public static final TileEntityType<SignTileEntity> SIGN = register("sign", TileEntityType.Builder.of(SignTileEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN));
   public static final TileEntityType<MobSpawnerTileEntity> MOB_SPAWNER = register("mob_spawner", TileEntityType.Builder.of(MobSpawnerTileEntity::new, Blocks.SPAWNER));
   public static final TileEntityType<PistonTileEntity> PISTON = register("piston", TileEntityType.Builder.of(PistonTileEntity::new, Blocks.MOVING_PISTON));
   public static final TileEntityType<BrewingStandTileEntity> BREWING_STAND = register("brewing_stand", TileEntityType.Builder.of(BrewingStandTileEntity::new, Blocks.BREWING_STAND));
   public static final TileEntityType<EnchantingTableTileEntity> ENCHANTING_TABLE = register("enchanting_table", TileEntityType.Builder.of(EnchantingTableTileEntity::new, Blocks.ENCHANTING_TABLE));
   public static final TileEntityType<EndPortalTileEntity> END_PORTAL = register("end_portal", TileEntityType.Builder.of(EndPortalTileEntity::new, Blocks.END_PORTAL));
   public static final TileEntityType<BeaconTileEntity> BEACON = register("beacon", TileEntityType.Builder.of(BeaconTileEntity::new, Blocks.BEACON));
   public static final TileEntityType<SkullTileEntity> SKULL = register("skull", TileEntityType.Builder.of(SkullTileEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
   public static final TileEntityType<DaylightDetectorTileEntity> DAYLIGHT_DETECTOR = register("daylight_detector", TileEntityType.Builder.of(DaylightDetectorTileEntity::new, Blocks.DAYLIGHT_DETECTOR));
   public static final TileEntityType<HopperTileEntity> HOPPER = register("hopper", TileEntityType.Builder.of(HopperTileEntity::new, Blocks.HOPPER));
   public static final TileEntityType<ComparatorTileEntity> COMPARATOR = register("comparator", TileEntityType.Builder.of(ComparatorTileEntity::new, Blocks.COMPARATOR));
   public static final TileEntityType<BannerTileEntity> BANNER = register("banner", TileEntityType.Builder.of(BannerTileEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
   public static final TileEntityType<StructureBlockTileEntity> STRUCTURE_BLOCK = register("structure_block", TileEntityType.Builder.of(StructureBlockTileEntity::new, Blocks.STRUCTURE_BLOCK));
   public static final TileEntityType<EndGatewayTileEntity> END_GATEWAY = register("end_gateway", TileEntityType.Builder.of(EndGatewayTileEntity::new, Blocks.END_GATEWAY));
   public static final TileEntityType<CommandBlockTileEntity> COMMAND_BLOCK = register("command_block", TileEntityType.Builder.of(CommandBlockTileEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
   public static final TileEntityType<ShulkerBoxTileEntity> SHULKER_BOX = register("shulker_box", TileEntityType.Builder.of(ShulkerBoxTileEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
   public static final TileEntityType<BedTileEntity> BED = register("bed", TileEntityType.Builder.of(BedTileEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
   public static final TileEntityType<ConduitTileEntity> CONDUIT = register("conduit", TileEntityType.Builder.of(ConduitTileEntity::new, Blocks.CONDUIT));
   public static final TileEntityType<BarrelTileEntity> BARREL = register("barrel", TileEntityType.Builder.of(BarrelTileEntity::new, Blocks.BARREL));
   public static final TileEntityType<SmokerTileEntity> SMOKER = register("smoker", TileEntityType.Builder.of(SmokerTileEntity::new, Blocks.SMOKER));
   public static final TileEntityType<BlastFurnaceTileEntity> BLAST_FURNACE = register("blast_furnace", TileEntityType.Builder.of(BlastFurnaceTileEntity::new, Blocks.BLAST_FURNACE));
   public static final TileEntityType<LecternTileEntity> LECTERN = register("lectern", TileEntityType.Builder.of(LecternTileEntity::new, Blocks.LECTERN));
   public static final TileEntityType<BellTileEntity> BELL = register("bell", TileEntityType.Builder.of(BellTileEntity::new, Blocks.BELL));
   public static final TileEntityType<JigsawTileEntity> JIGSAW = register("jigsaw", TileEntityType.Builder.of(JigsawTileEntity::new, Blocks.JIGSAW));
   public static final TileEntityType<CampfireTileEntity> CAMPFIRE = register("campfire", TileEntityType.Builder.of(CampfireTileEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));
   public static final TileEntityType<BeehiveTileEntity> BEEHIVE = register("beehive", TileEntityType.Builder.of(BeehiveTileEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE));
   private final Supplier<? extends T> factory;
   private final Set<Block> validBlocks;
   private final Type<?> dataType;
   private final net.minecraftforge.common.util.ReverseTagWrapper<TileEntityType<?>> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, () -> net.minecraft.tags.TagCollectionManager.getInstance().getCustomTypeCollection(net.minecraftforge.registries.ForgeRegistries.TILE_ENTITIES));

   @Nullable
   public static ResourceLocation getKey(TileEntityType<?> p_200969_0_) {
      return Registry.BLOCK_ENTITY_TYPE.getKey(p_200969_0_);
   }

   private static <T extends TileEntity> TileEntityType<T> register(String p_200966_0_, TileEntityType.Builder<T> p_200966_1_) {
      if (p_200966_1_.validBlocks.isEmpty()) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", (Object)p_200966_0_);
      }

      Type<?> type = Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, p_200966_0_);
      return Registry.register(Registry.BLOCK_ENTITY_TYPE, p_200966_0_, p_200966_1_.build(type));
   }

   public TileEntityType(Supplier<? extends T> p_i51497_1_, Set<Block> p_i51497_2_, Type<?> p_i51497_3_) {
      this.factory = p_i51497_1_;
      this.validBlocks = p_i51497_2_;
      this.dataType = p_i51497_3_;
   }

   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   public boolean isIn(net.minecraft.tags.ITag<TileEntityType<?>> tag) {
      return tag.contains(this);
   }

   @Nullable
   public T create() {
      return this.factory.get();
   }

   public boolean isValid(Block p_223045_1_) {
      return this.validBlocks.contains(p_223045_1_);
   }

   @Nullable
   public T getBlockEntity(IBlockReader p_226986_1_, BlockPos p_226986_2_) {
      TileEntity tileentity = p_226986_1_.getBlockEntity(p_226986_2_);
      return (T)(tileentity != null && tileentity.getType() == this ? tileentity : null);
   }

   public static final class Builder<T extends TileEntity> {
      private final Supplier<? extends T> factory;
      private final Set<Block> validBlocks;

      private Builder(Supplier<? extends T> p_i51498_1_, Set<Block> p_i51498_2_) {
         this.factory = p_i51498_1_;
         this.validBlocks = p_i51498_2_;
      }

      public static <T extends TileEntity> TileEntityType.Builder<T> of(Supplier<? extends T> p_223042_0_, Block... p_223042_1_) {
         return new TileEntityType.Builder<>(p_223042_0_, ImmutableSet.copyOf(p_223042_1_));
      }

      public TileEntityType<T> build(Type<?> p_206865_1_) {
         return new TileEntityType<>(this.factory, this.validBlocks, p_206865_1_);
      }
   }
}
