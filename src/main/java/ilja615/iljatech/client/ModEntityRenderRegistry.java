package ilja615.iljatech.client;

import ilja615.iljatech.client.render.GassEntityRender;
import ilja615.iljatech.init.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ModEntityRenderRegistry
{
    public static ModelLayerLocation PETROLYMER_HELMET_LAYER = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "petrolymer_helmet");

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntities.STEAM_CLOUD.get(), GassEntityRender::new);
    }
}
