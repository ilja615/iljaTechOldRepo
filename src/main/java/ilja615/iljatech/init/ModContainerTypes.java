package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.containers.CrafterMachineContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, IljaTech.MOD_ID);

    public static final RegistryObject<MenuType<CrafterMachineContainer>> CRAFTER_MACHINE = CONTAINER_TYPES
            .register("crafter_machine", () -> IForgeMenuType.create(CrafterMachineContainer::new));
}
