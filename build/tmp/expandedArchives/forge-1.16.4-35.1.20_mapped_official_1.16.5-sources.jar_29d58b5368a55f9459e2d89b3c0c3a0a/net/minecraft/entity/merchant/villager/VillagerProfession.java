package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;

public class VillagerProfession extends net.minecraftforge.registries.ForgeRegistryEntry<VillagerProfession> {
   public static final VillagerProfession NONE = register("none", PointOfInterestType.UNEMPLOYED, (SoundEvent)null);
   public static final VillagerProfession ARMORER = register("armorer", PointOfInterestType.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
   public static final VillagerProfession BUTCHER = register("butcher", PointOfInterestType.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
   public static final VillagerProfession CARTOGRAPHER = register("cartographer", PointOfInterestType.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
   public static final VillagerProfession CLERIC = register("cleric", PointOfInterestType.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
   public static final VillagerProfession FARMER = register("farmer", PointOfInterestType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
   public static final VillagerProfession FISHERMAN = register("fisherman", PointOfInterestType.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
   public static final VillagerProfession FLETCHER = register("fletcher", PointOfInterestType.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
   public static final VillagerProfession LEATHERWORKER = register("leatherworker", PointOfInterestType.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
   public static final VillagerProfession LIBRARIAN = register("librarian", PointOfInterestType.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
   public static final VillagerProfession MASON = register("mason", PointOfInterestType.MASON, SoundEvents.VILLAGER_WORK_MASON);
   public static final VillagerProfession NITWIT = register("nitwit", PointOfInterestType.NITWIT, (SoundEvent)null);
   public static final VillagerProfession SHEPHERD = register("shepherd", PointOfInterestType.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
   public static final VillagerProfession TOOLSMITH = register("toolsmith", PointOfInterestType.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
   public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PointOfInterestType.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);
   private final String name;
   private final PointOfInterestType jobPoiType;
   private final ImmutableSet<Item> requestedItems;
   private final ImmutableSet<Block> secondaryPoi;
   @Nullable
   private final SoundEvent workSound;

   public VillagerProfession(String p_i225734_1_, PointOfInterestType p_i225734_2_, ImmutableSet<Item> p_i225734_3_, ImmutableSet<Block> p_i225734_4_, @Nullable SoundEvent p_i225734_5_) {
      this.name = p_i225734_1_;
      this.jobPoiType = p_i225734_2_;
      this.requestedItems = p_i225734_3_;
      this.secondaryPoi = p_i225734_4_;
      this.workSound = p_i225734_5_;
   }

   public PointOfInterestType getJobPoiType() {
      return this.jobPoiType;
   }

   public ImmutableSet<Item> getRequestedItems() {
      return this.requestedItems;
   }

   public ImmutableSet<Block> getSecondaryPoi() {
      return this.secondaryPoi;
   }

   @Nullable
   public SoundEvent getWorkSound() {
      return this.workSound;
   }

   public String toString() {
      return this.name;
   }

   static VillagerProfession register(String p_226556_0_, PointOfInterestType p_226556_1_, @Nullable SoundEvent p_226556_2_) {
      return register(p_226556_0_, p_226556_1_, ImmutableSet.of(), ImmutableSet.of(), p_226556_2_);
   }

   static VillagerProfession register(String p_226557_0_, PointOfInterestType p_226557_1_, ImmutableSet<Item> p_226557_2_, ImmutableSet<Block> p_226557_3_, @Nullable SoundEvent p_226557_4_) {
      return Registry.register(Registry.VILLAGER_PROFESSION, new ResourceLocation(p_226557_0_), new VillagerProfession(p_226557_0_, p_226557_1_, p_226557_2_, p_226557_3_, p_226557_4_));
   }
}
