package ilja615.iljatech.client.render;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.models.ElectricFishModel;
import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.entity.ElectricFishEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

@OnlyIn(Dist.CLIENT)
public class ElectricFishRender<T extends ElectricFishEntity> extends MobRenderer<T, ElectricFishModel<T>>
{

    public static final ModelLayerLocation ELECTRIC_FISH_LAYER = new ModelLayerLocation(
            new ResourceLocation(IljaTech.MOD_ID, "electric_fish"), "main");

    private static final ResourceLocation ENTITY_TEXTURE = new ResourceLocation(IljaTech.MOD_ID,
            "textures/entity/electric_fish.png");

    public ElectricFishRender(final Context context) {
        super(context, new ElectricFishModel<>(context.bakeLayer(ELECTRIC_FISH_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return ENTITY_TEXTURE;
    }
}
