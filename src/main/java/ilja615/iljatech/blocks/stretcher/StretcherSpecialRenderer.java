package ilja615.iljatech.blocks.stretcher;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ilja615.iljatech.util.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class StretcherSpecialRenderer implements BlockEntityRenderer<StretcherBlockEntity>
{
    private static final float SIZE = 0.75F;

    public StretcherSpecialRenderer(BlockEntityRendererProvider.Context p_173602_) {
    }

    public void render(StretcherBlockEntity stretcherBlockEntity, float p_112345_, PoseStack poseStack, MultiBufferSource p_112347_, int p_112348_, int p_112349_) {
        int i = (int)stretcherBlockEntity.getBlockPos().asLong();
        stretcherBlockEntity.stretcherItemStackHandler.ifPresent(h ->
        {
            NonNullList<ItemStack> nonnulllist = ModUtils.ListFromItemHandler(h);

            ItemStack first = nonnulllist.get(0);
            if (first != ItemStack.EMPTY) {
                poseStack.pushPose();
                poseStack.translate(0.5d, 1.1d - (0.0005 * Math.round(stretcherBlockEntity.getProcessingTime() * 10)), 0.5d);
                poseStack.scale(SIZE, SIZE, SIZE);
                Minecraft.getInstance().getItemRenderer().renderStatic(first, ItemTransforms.TransformType.FIXED, p_112348_, p_112349_, poseStack, p_112347_, i);
                poseStack.popPose();
            }

            ItemStack itemstack = nonnulllist.get(1);
            if (itemstack != ItemStack.EMPTY) {
                poseStack.pushPose();
                poseStack.translate(0.5d, 0.5d, 0.5d);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(SIZE, SIZE, SIZE);
                Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, p_112348_, p_112349_, poseStack, p_112347_, i);
                poseStack.popPose();
            }
        });
    }
}
