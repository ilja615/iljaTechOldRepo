package ilja615.iljatech.client;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineScreen;
import ilja615.iljatech.blocks.foundry.ChuteScreen;
import ilja615.iljatech.blocks.foundry.FoundryScreen;
import ilja615.iljatech.client.models.AluminiumGolemModel;
import ilja615.iljatech.client.models.SaltGolemModel;
import ilja615.iljatech.client.render.*;
import ilja615.iljatech.init.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IljaTech.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
    @SubscribeEvent
    public static void ClientSetup(FMLClientSetupEvent event)
    {
        MenuScreens.register(ModContainerTypes.CRAFTER_MACHINE.get(), CrafterMachineScreen::new);
        MenuScreens.register(ModContainerTypes.FOUNDRY.get(), FoundryScreen::new);
        MenuScreens.register(ModContainerTypes.CHUTE.get(), ChuteScreen::new);

        //render layers
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_NAILS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRANK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BELLOWS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STOKED_FIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CONVEYOR_BELT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_SCAFFOLDING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RESEARCH_TABLE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STRETCHER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRUSHER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AZURITE_CLUSTER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RUBY_CLUSTER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CASSITERITE_CLUSTER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AZURITE_SMALL_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RUBY_SMALL_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CASSITERITE_SMALL_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AZURITE_MEDIUM_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RUBY_MEDIUM_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CASSITERITE_MEDIUM_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AZURITE_LARGE_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RUBY_LARGE_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CASSITERITE_LARGE_BUD.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COPPER_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALUMINIUM_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STEEL_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CONSTANTAN_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.NICHROME_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BARBED_WIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_OIL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_OIL.get(), RenderType.translucent());

        //item properties registration
        event.enqueueWork(() -> ItemProperties.register(ModItems.IRON_HAMMER.get(), new ResourceLocation(IljaTech.MOD_ID, "cooldown"),
                (stack, world, entity, seed) -> stack.getTag() == null ? 0 : stack.getTag().getInt("coolDown")));
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntities.STEAM_CLOUD.get(), GassEntityRender::new);
        event.registerEntityRenderer(ModEntities.IRON_NAILS_PROJECTILE.get(), (renderManager) -> new ThrownItemRenderer<>(renderManager, 2.0f, true));
        event.registerEntityRenderer(ModEntities.ALUMINIUM_GOLEM.get(), AluminiumGolemRender::new);
        event.registerEntityRenderer(ModEntities.SALT_GOLEM.get(), SaltGolemRender::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(AluminiumGolemRender.ALUMINIUM_GOLEM_LAYER, AluminiumGolemModel::createBodyLayer);
        event.registerLayerDefinition(SaltGolemRender.SALT_GOLEM_LAYER, SaltGolemModel::createBodyLayer);
    }
}