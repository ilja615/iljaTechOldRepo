package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerType<T extends Container> extends net.minecraftforge.registries.ForgeRegistryEntry<ContainerType<?>> implements net.minecraftforge.common.extensions.IForgeContainerType<T> {
   public static final ContainerType<ChestContainer> GENERIC_9x1 = register("generic_9x1", ChestContainer::oneRow);
   public static final ContainerType<ChestContainer> GENERIC_9x2 = register("generic_9x2", ChestContainer::twoRows);
   public static final ContainerType<ChestContainer> GENERIC_9x3 = register("generic_9x3", ChestContainer::threeRows);
   public static final ContainerType<ChestContainer> GENERIC_9x4 = register("generic_9x4", ChestContainer::fourRows);
   public static final ContainerType<ChestContainer> GENERIC_9x5 = register("generic_9x5", ChestContainer::fiveRows);
   public static final ContainerType<ChestContainer> GENERIC_9x6 = register("generic_9x6", ChestContainer::sixRows);
   public static final ContainerType<DispenserContainer> GENERIC_3x3 = register("generic_3x3", DispenserContainer::new);
   public static final ContainerType<RepairContainer> ANVIL = register("anvil", RepairContainer::new);
   public static final ContainerType<BeaconContainer> BEACON = register("beacon", BeaconContainer::new);
   public static final ContainerType<BlastFurnaceContainer> BLAST_FURNACE = register("blast_furnace", BlastFurnaceContainer::new);
   public static final ContainerType<BrewingStandContainer> BREWING_STAND = register("brewing_stand", BrewingStandContainer::new);
   public static final ContainerType<WorkbenchContainer> CRAFTING = register("crafting", WorkbenchContainer::new);
   public static final ContainerType<EnchantmentContainer> ENCHANTMENT = register("enchantment", EnchantmentContainer::new);
   public static final ContainerType<FurnaceContainer> FURNACE = register("furnace", FurnaceContainer::new);
   public static final ContainerType<GrindstoneContainer> GRINDSTONE = register("grindstone", GrindstoneContainer::new);
   public static final ContainerType<HopperContainer> HOPPER = register("hopper", HopperContainer::new);
   public static final ContainerType<LecternContainer> LECTERN = register("lectern", (p_221504_0_, p_221504_1_) -> {
      return new LecternContainer(p_221504_0_);
   });
   public static final ContainerType<LoomContainer> LOOM = register("loom", LoomContainer::new);
   public static final ContainerType<MerchantContainer> MERCHANT = register("merchant", MerchantContainer::new);
   public static final ContainerType<ShulkerBoxContainer> SHULKER_BOX = register("shulker_box", ShulkerBoxContainer::new);
   public static final ContainerType<SmithingTableContainer> SMITHING = register("smithing", SmithingTableContainer::new);
   public static final ContainerType<SmokerContainer> SMOKER = register("smoker", SmokerContainer::new);
   public static final ContainerType<CartographyContainer> CARTOGRAPHY_TABLE = register("cartography_table", CartographyContainer::new);
   public static final ContainerType<StonecutterContainer> STONECUTTER = register("stonecutter", StonecutterContainer::new);
   private final ContainerType.IFactory<T> constructor;

   private static <T extends Container> ContainerType<T> register(String p_221505_0_, ContainerType.IFactory<T> p_221505_1_) {
      return Registry.register(Registry.MENU, p_221505_0_, new ContainerType<>(p_221505_1_));
   }

   public ContainerType(ContainerType.IFactory<T> p_i50072_1_) {
      this.constructor = p_i50072_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public T create(int p_221506_1_, PlayerInventory p_221506_2_) {
      return this.constructor.create(p_221506_1_, p_221506_2_);
   }
   
   @Override
   public T create(int windowId, PlayerInventory playerInv, net.minecraft.network.PacketBuffer extraData) {
      if (this.constructor instanceof net.minecraftforge.fml.network.IContainerFactory) {
         return ((net.minecraftforge.fml.network.IContainerFactory<T>) this.constructor).create(windowId, playerInv, extraData);
      }
      return create(windowId, playerInv);
   }

   public interface IFactory<T extends Container> {
      @OnlyIn(Dist.CLIENT)
      T create(int p_create_1_, PlayerInventory p_create_2_);
   }
}
