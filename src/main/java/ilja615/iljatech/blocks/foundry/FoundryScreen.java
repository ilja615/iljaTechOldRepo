package ilja615.iljatech.blocks.foundry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoundryScreen extends AbstractContainerScreen<FoundryContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(IljaTech.MOD_ID, "textures/gui/foundry.png");
    FoundryContainer container;

    public FoundryScreen(FoundryContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.container = container;
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 175;
        this.imageHeight = 165;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int i = this.leftPos;
        int j = this.topPos;
        if (this.container.te.isBurning()) {
            int k = this.container.te.getLitProgress();
            this.blit(matrixStack, i + 10, j + 48 - k, 176, 12 - k, 64, k + 1);
        }

        int l = this.container.te.getProgress();
        this.blit(matrixStack, i + 106, j + 34, 176, 84, l + 1, 16);
    }
}
