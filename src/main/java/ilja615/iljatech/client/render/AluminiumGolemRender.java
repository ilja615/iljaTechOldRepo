package ilja615.iljatech.client.render;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.models.AluminiumGolemModel;
import ilja615.iljatech.client.models.ElectricFishModel;
import ilja615.iljatech.entity.AluminiumGolemEntity;
import ilja615.iljatech.entity.ElectricFishEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AluminiumGolemRender<T extends AluminiumGolemEntity> extends MobRenderer<T, AluminiumGolemModel<T>>
{

    public static final ModelLayerLocation ALUMINIUM_GOLEM_LAYER = new ModelLayerLocation(
            new ResourceLocation(IljaTech.MOD_ID, "aluminium_golem"), "main");

    private static final ResourceLocation ENTITY_TEXTURE = new ResourceLocation(IljaTech.MOD_ID,
            "textures/entity/aluminium_golem.png");

    public AluminiumGolemRender(final Context context) {
        super(context, new AluminiumGolemModel<T>(context.bakeLayer(ALUMINIUM_GOLEM_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return ENTITY_TEXTURE;
    }
}
