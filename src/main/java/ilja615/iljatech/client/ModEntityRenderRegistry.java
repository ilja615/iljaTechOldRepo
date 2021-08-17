package ilja615.iljatech.client;

import ilja615.iljatech.client.render.GassEntityRender;
import ilja615.iljatech.init.ModEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ModEntityRenderRegistry
{
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntities.STEAM_CLOUD.get(), GassEntityRender::new);
    }
}
