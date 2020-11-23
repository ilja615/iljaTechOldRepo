package ilja615.iljatech;

import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.proxy.ClientProxy;
import ilja615.iljatech.proxy.IProxy;
import ilja615.iljatech.proxy.ServerProxy;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import static ilja615.iljatech.IljaTech.MOD_ID;

@Mod(MOD_ID)
public class IljaTech
{
    public static final String MOD_ID = "iljatech";
    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public IljaTech()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

//        ModItemsNew.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        proxy.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
            final IForgeRegistry<Item> registry = event.getRegistry();
            ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
                final BlockItem blockItem = new BlockItem(block, ModProperties.ITEM_PROPERTY);
                blockItem.setRegistryName(block.getRegistryName());
                registry.register(blockItem);
            });
        }
    }
}
