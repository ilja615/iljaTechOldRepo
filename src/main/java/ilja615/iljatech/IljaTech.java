package ilja615.iljatech;

import ilja615.iljatech.init.*;
import ilja615.iljatech.networking.ModMessages;
import ilja615.iljatech.util.ModEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
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
    public static CreativeModeTab CREATIVE_TAB;

    public IljaTech()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModMessages.register();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(IljaTech::afterCommonSetup);

        ModMultiBlocks.registerMultiBlocks();
    }

    static void afterCommonSetup()
    {
        System.out.println("IljaTech afterCommonSetup now run.");

        ModIngredient.registerIngredient();
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
        public static void registerParticles(RegisterParticleProvidersEvent event) {
            ModParticles.registerParticles(event); //It registers particles
        }

        @SubscribeEvent
        public static void entityAttributes(final EntityAttributeCreationEvent event) {
            ModEntities.CreateEntityAttributes(event); //It creates entity attributes
        }

        @SubscribeEvent
        public static void registerTabs(CreativeModeTabEvent.Register event)
        {
            CREATIVE_TAB = event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "worldupgrade_tab"), builder -> builder
                    .icon(() -> new ItemStack(ModItems.BRONZE_GEAR.get(), 1))
                    .title(Component.translatable("tabs.worldupgrade.worldupgrade_tab"))
                    .displayItems((featureFlags, output, hasOp) -> {
                        output.accept(ModItems.BRONZE_GEAR.get());
                    })
            );
        }

        @SubscribeEvent
        public static void fillTabs(CreativeModeTabEvent.BuildContents event)
        {
            if (event.getTab() == CREATIVE_TAB)
            {
                ModItems.ITEMS.getEntries().forEach(itemRegistryObject -> event.accept(itemRegistryObject.get()));
                //ModBlocks.BLOCKS.getEntries().forEach(blockRegistryObject -> event.accept(blockRegistryObject.get()));
            }
        }
    }
}
