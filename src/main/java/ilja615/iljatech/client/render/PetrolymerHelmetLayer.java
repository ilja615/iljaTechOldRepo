package ilja615.iljatech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import ilja615.iljatech.IljaTech;
import ilja615.iljatech.client.ClientEventBusSubscriber;
import ilja615.iljatech.client.models.PetrolymerHelmetModel;
import ilja615.iljatech.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class PetrolymerHelmetLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M>
{
    // special thx to : Absolem Jackdaw (Subaraki) for helping me out
    private static final ResourceLocation ourTexture = new ResourceLocation(IljaTech.MOD_ID, "textures/models/armor/petrolymer_helmet.png");

    private final PetrolymerHelmetModel petrolymerHelmetModel;

    public PetrolymerHelmetLayer(LivingEntityRenderer<T, M> owner)
    {
        super(owner);

        petrolymerHelmetModel = new PetrolymerHelmetModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientEventBusSubscriber.PETROLYMER_HELMET_LAYER));
    }

    private void translateToHead(PoseStack matrixStack)
    {
        this.getParentModel().head.translateAndRotate(matrixStack);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int lightness, T player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.petrolymerHelmetModel.young = player.isBaby();
        this.petrolymerHelmetModel.riding = player.isPassenger();

        matrixStack.pushPose();
        this.translateToHead(matrixStack);

        matrixStack.translate(0.0F, 0.5F, 0.0F);

        matrixStack.pushPose();
        if (getParentModel().young)
        {
            matrixStack.translate(0.0F, 0.75F, 0.0F);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }
        matrixStack.popPose();

        player.getArmorSlots().forEach(itemStack ->
        {
            if (itemStack.getItem() == ModItems.PETROLYMER_HELMET.get())
                renderColoredCutoutModel(this.petrolymerHelmetModel, ourTexture, matrixStack, buffer, lightness, player, 1.0f, 1.0f, 1.0f);
        });

        matrixStack.popPose();
    }
}
