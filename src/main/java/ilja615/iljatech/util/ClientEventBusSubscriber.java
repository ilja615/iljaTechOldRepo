package ilja615.iljatech.util;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.gui.CrafterMachineScreen;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IljaTech.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
    @SubscribeEvent
    public static void ClientSetup(FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(ModContainerTypes.CRAFTER_MACHINE.get(), CrafterMachineScreen::new);

        //render layers
        RenderTypeLookup.setRenderLayer(ModBlocks.IRON_NAILS.get(), RenderType.getCutout());
    }
}
