package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.LoomScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screen.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.DispenserScreen;
import net.minecraft.client.gui.screen.inventory.FurnaceScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.inventory.SmithingTableScreen;
import net.minecraft.client.gui.screen.inventory.SmokerScreen;
import net.minecraft.client.gui.screen.inventory.StonecutterScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ContainerType<?>, ScreenManager.IScreenFactory<?, ?>> SCREENS = Maps.newHashMap();

   public static <T extends Container> void create(@Nullable ContainerType<T> p_216909_0_, Minecraft p_216909_1_, int p_216909_2_, ITextComponent p_216909_3_) {
      getScreenFactory(p_216909_0_, p_216909_1_, p_216909_2_, p_216909_3_).ifPresent(f -> f.fromPacket(p_216909_3_, p_216909_0_, p_216909_1_, p_216909_2_));
   }

   public static <T extends Container> java.util.Optional<IScreenFactory<T, ?>> getScreenFactory(@Nullable ContainerType<T> p_216909_0_, Minecraft p_216909_1_, int p_216909_2_, ITextComponent p_216909_3_) {
      if (p_216909_0_ == null) {
         LOGGER.warn("Trying to open invalid screen with name: {}", (Object)p_216909_3_.getString());
      } else {
         ScreenManager.IScreenFactory<T, ?> iscreenfactory = getConstructor(p_216909_0_);
         if (iscreenfactory == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registry.MENU.getKey(p_216909_0_));
         } else {
            return java.util.Optional.of(iscreenfactory);
         }
      }
      return java.util.Optional.empty();
   }

   @Nullable
   private static <T extends Container> ScreenManager.IScreenFactory<T, ?> getConstructor(ContainerType<T> p_216912_0_) {
      return (ScreenManager.IScreenFactory<T, ?>)SCREENS.get(p_216912_0_);
   }

   public static <M extends Container, U extends Screen & IHasContainer<M>> void register(ContainerType<? extends M> p_216911_0_, ScreenManager.IScreenFactory<M, U> p_216911_1_) {
      ScreenManager.IScreenFactory<?, ?> iscreenfactory = SCREENS.put(p_216911_0_, p_216911_1_);
      if (iscreenfactory != null) {
         throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(p_216911_0_));
      }
   }

   public static boolean selfTest() {
      boolean flag = false;

      for(ContainerType<?> containertype : Registry.MENU) {
         if (!SCREENS.containsKey(containertype)) {
            LOGGER.debug("Menu {} has no matching screen", (Object)Registry.MENU.getKey(containertype));
            flag = true;
         }
      }

      return flag;
   }

   static {
      register(ContainerType.GENERIC_9x1, ChestScreen::new);
      register(ContainerType.GENERIC_9x2, ChestScreen::new);
      register(ContainerType.GENERIC_9x3, ChestScreen::new);
      register(ContainerType.GENERIC_9x4, ChestScreen::new);
      register(ContainerType.GENERIC_9x5, ChestScreen::new);
      register(ContainerType.GENERIC_9x6, ChestScreen::new);
      register(ContainerType.GENERIC_3x3, DispenserScreen::new);
      register(ContainerType.ANVIL, AnvilScreen::new);
      register(ContainerType.BEACON, BeaconScreen::new);
      register(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
      register(ContainerType.BREWING_STAND, BrewingStandScreen::new);
      register(ContainerType.CRAFTING, CraftingScreen::new);
      register(ContainerType.ENCHANTMENT, EnchantmentScreen::new);
      register(ContainerType.FURNACE, FurnaceScreen::new);
      register(ContainerType.GRINDSTONE, GrindstoneScreen::new);
      register(ContainerType.HOPPER, HopperScreen::new);
      register(ContainerType.LECTERN, LecternScreen::new);
      register(ContainerType.LOOM, LoomScreen::new);
      register(ContainerType.MERCHANT, MerchantScreen::new);
      register(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
      register(ContainerType.SMITHING, SmithingTableScreen::new);
      register(ContainerType.SMOKER, SmokerScreen::new);
      register(ContainerType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
      register(ContainerType.STONECUTTER, StonecutterScreen::new);
   }

   @OnlyIn(Dist.CLIENT)
   public interface IScreenFactory<T extends Container, U extends Screen & IHasContainer<T>> {
      default void fromPacket(ITextComponent p_216908_1_, ContainerType<T> p_216908_2_, Minecraft p_216908_3_, int p_216908_4_) {
         U u = this.create(p_216908_2_.create(p_216908_4_, p_216908_3_.player.inventory), p_216908_3_.player.inventory, p_216908_1_);
         p_216908_3_.player.containerMenu = u.getMenu();
         p_216908_3_.setScreen(u);
      }

      U create(T p_create_1_, PlayerInventory p_create_2_, ITextComponent p_create_3_);
   }
}
