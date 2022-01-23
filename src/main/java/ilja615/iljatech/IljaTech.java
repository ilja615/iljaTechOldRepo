package ilja615.iljatech;

import ilja615.iljatech.blocks.stretcher.StretcherSpecialRenderer;
import ilja615.iljatech.entity.AluminiumGolemEntity;
import ilja615.iljatech.entity.ElectricFishEntity;
import ilja615.iljatech.entity.SaltGolemEntity;
import ilja615.iljatech.init.*;
import ilja615.iljatech.particles.StarParticle;
import ilja615.iljatech.particles.SteamParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import static ilja615.iljatech.IljaTech.MOD_ID;

@Mod(MOD_ID)
public class IljaTech
{
    public static final String MOD_ID = "iljatech";

    public IljaTech()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.addListener(this::biomeModification);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(IljaTech::afterCommonSetup);
    }

    static void afterCommonSetup()
    {
        System.out.println("IljaTech afterCommonSetup now run.");
        ModFeatures.registerFeatures(); //It registers features
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(IljaTech::afterClientSetup);
    }

    static void afterClientSetup()
    {
        System.out.println("IljaTech afterClientSetup now run.");
        ModBlockEntityTypes.registerBlockEntityRenderers(); //It registers block entity renderers
    }

    public void biomeModification(final BiomeLoadingEvent event) {
        ModFeatures.addFeaturesToBiomes(event); //Add worldgen features
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            final IForgeRegistry<Item> registry = event.getRegistry();
            ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block ->
            {
                final BlockItem blockItem = new BlockItem(block, ModProperties.ITEM_PROPERTY);
                blockItem.setRegistryName(block.getRegistryName());
                registry.register(blockItem); //Register item for all the blocks
            });
            ModEntities.registerEntitySpawnEggs(event); //It registers the spawn egg items
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void registerParticles(ParticleFactoryRegisterEvent event) {
            ModParticles.registerParticles(event); //It registers particles
        }

        @SubscribeEvent
        public static void entityAttributes(final EntityAttributeCreationEvent event) {
            ModEntities.CreateEntityAttributes(event); //It creates entity attributes
        }
    }
}
