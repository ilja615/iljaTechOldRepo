package ilja615.iljatech;

import ilja615.iljatech.init.*;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModRecipe.RECIPE_SERIALIZERS.register(modEventBus);
        ModRecipe.RECIPE_TYPES.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(IljaTech::afterCommonSetup);
    }

    static void afterCommonSetup()
    {
        System.out.println("IljaTech afterCommonSetup now run.");
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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        // TODO add spawn egg registry back somewhere

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
