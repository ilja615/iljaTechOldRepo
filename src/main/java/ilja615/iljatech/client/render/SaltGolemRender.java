package ilja615.iljatech.client.render;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.models.SaltGolemModel;
import ilja615.iljatech.entity.SaltGolemEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaltGolemRender<T extends SaltGolemEntity> extends MobRenderer<T, SaltGolemModel<T>>
{

    public static final ModelLayerLocation SALT_GOLEM_LAYER = new ModelLayerLocation(
            new ResourceLocation(IljaTech.MOD_ID, "salt_golem"), "main");

    private static final ResourceLocation ENTITY_TEXTURE = new ResourceLocation(IljaTech.MOD_ID,
            "textures/entity/salt_golem.png");

    public SaltGolemRender(final Context context) {
        super(context, new SaltGolemModel<T>(context.bakeLayer(SALT_GOLEM_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return ENTITY_TEXTURE;
    }
}
