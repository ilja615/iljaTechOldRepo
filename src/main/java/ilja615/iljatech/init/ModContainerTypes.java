package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.containers.CrafterMachineContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes
{
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, IljaTech.MOD_ID);

    public static final RegistryObject<ContainerType<CrafterMachineContainer>> CRAFTER_MACHINE = CONTAINER_TYPES
            .register("crafter_machine", () -> IForgeContainerType.create(CrafterMachineContainer::new));
}
