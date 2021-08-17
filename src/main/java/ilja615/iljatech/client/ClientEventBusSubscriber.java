package ilja615.iljatech.client;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.gui.CrafterMachineScreen;
import ilja615.iljatech.client.models.PetrolymerHelmetModel;
import ilja615.iljatech.client.render.ArmorRenderer;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import ilja615.iljatech.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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

        //render layers
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_NAILS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRANK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BELLOWS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.STOKED_FIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CONVEYOR_BELT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_SCAFFOLDING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RESEARCH_TABLE.get(), RenderType.cutout());

        //item properties registration
        event.enqueueWork(() -> ItemProperties.register(ModItems.IRON_HAMMER.get(), new ResourceLocation(IljaTech.MOD_ID, "cooldown"),
                (stack, world, entity, seed) -> stack.getTag() == null ? 0 : stack.getTag().getInt("coolDown")));
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        ModEntityRenderRegistry.registerEntityRenderers(event);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(ModEntityRenderRegistry.PETROLYMER_HELMT_LAYER, PetrolymerHelmetModel::createBodyLayer);
        ((ArmorRenderer)ModItems.PETROLYMER_HELMET.get().asItem()).armorModel = new PetrolymerHelmetModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntityRenderRegistry.PETROLYMER_HELMT_LAYER));
    }
}
