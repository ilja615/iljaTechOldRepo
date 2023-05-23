package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.battery_box.BatteryBoxContainer;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineContainer;
import ilja615.iljatech.blocks.dynamo.DynamoContainer;
import ilja615.iljatech.blocks.foundry.ChuteContainer;
import ilja615.iljatech.blocks.foundry.FoundryContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<MenuType<CrafterMachineContainer>> CRAFTER_MACHINE = CONTAINER_TYPES
            .register("crafter_machine", () -> IForgeMenuType.create(CrafterMachineContainer::new));
    public static final RegistryObject<MenuType<FoundryContainer>> FOUNDRY = CONTAINER_TYPES
            .register("foundry", () -> IForgeMenuType.create(FoundryContainer::new));
    public static final RegistryObject<MenuType<ChuteContainer>> CHUTE = CONTAINER_TYPES
            .register("chute", () -> IForgeMenuType.create(ChuteContainer::new));
    public static final RegistryObject<MenuType<DynamoContainer>> DYNAMO = CONTAINER_TYPES
            .register("dynamo", () -> IForgeMenuType.create(DynamoContainer::new));
    public static final RegistryObject<MenuType<BatteryBoxContainer>> BATTERY_BOX = CONTAINER_TYPES
            .register("battery_box", () -> IForgeMenuType.create(BatteryBoxContainer::new));
}
