package net.minecraft.village;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class PointOfInterestType extends net.minecraftforge.registries.ForgeRegistryEntry<PointOfInterestType> {
   private static final Supplier<Set<PointOfInterestType>> ALL_JOB_POI_TYPES = Suppliers.memoize(() -> {
      return Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getJobPoiType).collect(Collectors.toSet());
   });
   public static final Predicate<PointOfInterestType> ALL_JOBS = (p_221049_0_) -> {
      return ALL_JOB_POI_TYPES.get().contains(p_221049_0_);
   };
   public static final Predicate<PointOfInterestType> ALL = (p_234172_0_) -> {
      return true;
   };
   private static final Set<BlockState> BEDS = ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED).stream().flatMap((p_234171_0_) -> {
      return p_234171_0_.getStateDefinition().getPossibleStates().stream();
   }).filter((p_234173_0_) -> {
      return p_234173_0_.getValue(BedBlock.PART) == BedPart.HEAD;
   }).collect(ImmutableSet.toImmutableSet());
   private static final Map<BlockState, PointOfInterestType> TYPE_BY_STATE = net.minecraftforge.registries.GameData.getBlockStatePointOfInterestTypeMap();
   public static final PointOfInterestType UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, ALL_JOBS, 1);
   public static final PointOfInterestType ARMORER = register("armorer", getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
   public static final PointOfInterestType BUTCHER = register("butcher", getBlockStates(Blocks.SMOKER), 1, 1);
   public static final PointOfInterestType CARTOGRAPHER = register("cartographer", getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
   public static final PointOfInterestType CLERIC = register("cleric", getBlockStates(Blocks.BREWING_STAND), 1, 1);
   public static final PointOfInterestType FARMER = register("farmer", getBlockStates(Blocks.COMPOSTER), 1, 1);
   public static final PointOfInterestType FISHERMAN = register("fisherman", getBlockStates(Blocks.BARREL), 1, 1);
   public static final PointOfInterestType FLETCHER = register("fletcher", getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
   public static final PointOfInterestType LEATHERWORKER = register("leatherworker", getBlockStates(Blocks.CAULDRON), 1, 1);
   public static final PointOfInterestType LIBRARIAN = register("librarian", getBlockStates(Blocks.LECTERN), 1, 1);
   public static final PointOfInterestType MASON = register("mason", getBlockStates(Blocks.STONECUTTER), 1, 1);
   public static final PointOfInterestType NITWIT = register("nitwit", ImmutableSet.of(), 1, 1);
   public static final PointOfInterestType SHEPHERD = register("shepherd", getBlockStates(Blocks.LOOM), 1, 1);
   public static final PointOfInterestType TOOLSMITH = register("toolsmith", getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
   public static final PointOfInterestType WEAPONSMITH = register("weaponsmith", getBlockStates(Blocks.GRINDSTONE), 1, 1);
   public static final PointOfInterestType HOME = register("home", BEDS, 1, 1);
   public static final PointOfInterestType MEETING = register("meeting", getBlockStates(Blocks.BELL), 32, 6);
   public static final PointOfInterestType BEEHIVE = register("beehive", getBlockStates(Blocks.BEEHIVE), 0, 1);
   public static final PointOfInterestType BEE_NEST = register("bee_nest", getBlockStates(Blocks.BEE_NEST), 0, 1);
   public static final PointOfInterestType NETHER_PORTAL = register("nether_portal", getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
   public static final PointOfInterestType LODESTONE = register("lodestone", getBlockStates(Blocks.LODESTONE), 0, 1);
   protected static final Set<BlockState> ALL_STATES = new ObjectOpenHashSet<>(TYPE_BY_STATE.keySet());
   private final String name;
   private final Set<BlockState> matchingStates;
   private final int maxTickets;
   private final Predicate<PointOfInterestType> predicate;
   private final int validRange;

   public static Set<BlockState> getBlockStates(Block p_221042_0_) {
      return ImmutableSet.copyOf(p_221042_0_.getStateDefinition().getPossibleStates());
   }

   public PointOfInterestType(String p_i225713_1_, Set<BlockState> p_i225713_2_, int p_i225713_3_, Predicate<PointOfInterestType> p_i225713_4_, int p_i225713_5_) {
      this.name = p_i225713_1_;
      this.matchingStates = ImmutableSet.copyOf(p_i225713_2_);
      this.maxTickets = p_i225713_3_;
      this.predicate = p_i225713_4_;
      this.validRange = p_i225713_5_;
   }

   public PointOfInterestType(String p_i225712_1_, Set<BlockState> p_i225712_2_, int p_i225712_3_, int p_i225712_4_) {
      this.name = p_i225712_1_;
      this.matchingStates = ImmutableSet.copyOf(p_i225712_2_);
      this.maxTickets = p_i225712_3_;
      this.predicate = (p_234170_1_) -> {
         return p_234170_1_ == this;
      };
      this.validRange = p_i225712_4_;
   }

   public int getMaxTickets() {
      return this.maxTickets;
   }

   public Predicate<PointOfInterestType> getPredicate() {
      return this.predicate;
   }

   public int getValidRange() {
      return this.validRange;
   }

   public String toString() {
      return this.name;
   }

   private static PointOfInterestType register(String p_226359_0_, Set<BlockState> p_226359_1_, int p_226359_2_, int p_226359_3_) {
      return registerBlockStates(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new ResourceLocation(p_226359_0_), new PointOfInterestType(p_226359_0_, p_226359_1_, p_226359_2_, p_226359_3_)));
   }

   private static PointOfInterestType register(String p_226360_0_, Set<BlockState> p_226360_1_, int p_226360_2_, Predicate<PointOfInterestType> p_226360_3_, int p_226360_4_) {
      return registerBlockStates(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new ResourceLocation(p_226360_0_), new PointOfInterestType(p_226360_0_, p_226360_1_, p_226360_2_, p_226360_3_, p_226360_4_)));
   }

   private static PointOfInterestType registerBlockStates(PointOfInterestType p_221052_0_) {
      return p_221052_0_;
   }

   public static Optional<PointOfInterestType> forState(BlockState p_221047_0_) {
      return Optional.ofNullable(TYPE_BY_STATE.get(p_221047_0_));
   }
   
   public ImmutableSet<BlockState> getBlockStates() {
      return ImmutableSet.copyOf(this.matchingStates);
   }
}
