package ilja615.iljatech.client;

import ilja615.iljatech.client.models.PetrolymerHelmetModel;
import ilja615.iljatech.client.render.ArmorRenderer;
import ilja615.iljatech.client.render.GassEntityRender;
import ilja615.iljatech.init.ModEntities;
import ilja615.iljatech.init.ModItems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ModEntityRenderRegistry
{
    public static ModelLayerLocation PETROLYMER_HELMT_LAYER = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "petrolymer_helmet");

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntities.STEAM_CLOUD.get(), GassEntityRender::new);
    }
}
