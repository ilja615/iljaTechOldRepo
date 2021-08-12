package ilja615.iljatech.client;

import ilja615.iljatech.client.render.GassEntityRender;
import ilja615.iljatech.init.ModEntities;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ModEntityRenderRegistry
{
    public static void registerEntityRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.STEAM_CLOUD.get(), GassEntityRender::new);
    }
}
