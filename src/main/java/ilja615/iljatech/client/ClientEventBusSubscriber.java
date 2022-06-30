package ilja615.iljatech.client;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.gui.CrafterMachineScreen;
import ilja615.iljatech.client.models.AluminiumGolemModel;
import ilja615.iljatech.client.models.ElectricFishModel;
import ilja615.iljatech.client.models.PetrolymerHelmetModel;
import ilja615.iljatech.client.models.SaltGolemModel;
import ilja615.iljatech.client.render.*;
import ilja615.iljatech.entity.AluminiumGolemEntity;
import ilja615.iljatech.entity.SaltGolemEntity;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import ilja615.iljatech.init.ModEntities;
import ilja615.iljatech.init.ModItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IljaTech.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber
{
    public static ModelLayerLocation PETROLYMER_HELMET_LAYER = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "petrolymer_helmet");

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

        //item properties registration
        event.enqueueWork(() -> ItemProperties.register(ModItems.IRON_HAMMER.get(), new ResourceLocation(IljaTech.MOD_ID, "cooldown"),
                (stack, world, entity, seed) -> stack.getTag() == null ? 0 : stack.getTag().getInt("coolDown")));
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntities.STEAM_CLOUD.get(), GassEntityRender::new);
        event.registerEntityRenderer(ModEntities.IRON_NAILS_PROJECTILE.get(), (renderManager) -> new ThrownItemRenderer<>(renderManager, 2.0f, true));
        event.registerEntityRenderer(ModEntities.ELECTRIC_FISH.get(), ElectricFishRender::new);
        event.registerEntityRenderer(ModEntities.ALUMINIUM_GOLEM.get(), AluminiumGolemRender::new);
        event.registerEntityRenderer(ModEntities.SALT_GOLEM.get(), SaltGolemRender::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(PETROLYMER_HELMET_LAYER, PetrolymerHelmetModel::createBodyLayer);
        event.registerLayerDefinition(ElectricFishRender.ELECTRIC_FISH_LAYER, ElectricFishModel::createBodyLayer);
        event.registerLayerDefinition(AluminiumGolemRender.ALUMINIUM_GOLEM_LAYER, AluminiumGolemModel::createBodyLayer);
        event.registerLayerDefinition(SaltGolemRender.SALT_GOLEM_LAYER, SaltGolemModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void construct(EntityRenderersEvent.AddLayers event)
    {
        addLayerToEntity(event, EntityType.ARMOR_STAND);
        addLayerToEntity(event, EntityType.ZOMBIE);
        addLayerToEntity(event, EntityType.SKELETON);
        addLayerToEntity(event, EntityType.HUSK);
        addLayerToEntity(event, EntityType.DROWNED);
        addLayerToEntity(event, EntityType.STRAY);

        addLayerToPlayerSkin(event, "default");
        addLayerToPlayerSkin(event, "slim");
    }

    private static <T extends LivingEntity, M extends HumanoidModel<T>, R extends LivingEntityRenderer<T, M>> void addLayerToEntity(
            EntityRenderersEvent.AddLayers event, EntityType<? extends T> entityType)
    {
        R renderer = event.getRenderer(entityType);
        if (renderer != null) renderer.addLayer(new PetrolymerHelmetLayer<>(renderer));

    }

    private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, String skinName)
    {
        EntityRenderer<? extends Player> render = event.getSkin(skinName);
        if (render instanceof LivingEntityRenderer livingRenderer)
        {
            livingRenderer.addLayer(new PetrolymerHelmetLayer(livingRenderer));
        }
    }
}