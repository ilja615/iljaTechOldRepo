package ilja615.iljatech.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ilja615.iljatech.IljaTech;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyInfoArea extends GuiComponent
{
    protected final Rect2i area;
    private final IEnergyStorage energy;
    public static final ResourceLocation ENERGY_BAR = new ResourceLocation(IljaTech.MOD_ID, "textures/gui/energy_bar.png");

    public EnergyInfoArea(int xMin, int yMin)  {
        this(xMin, yMin, null,160,12);
    }

    public EnergyInfoArea(int xMin, int yMin, IEnergyStorage energy)  {
        this(xMin, yMin, energy,160,12);
    }

    public EnergyInfoArea(int xMin, int yMin, IEnergyStorage energy, int width, int height)  {
        this.area = new Rect2i(xMin, yMin, width, height);
        this.energy = energy;
    }

    public List<Component> getTooltips() {
        return List.of(Component.literal(energy.getEnergyStored()+"/"+energy.getMaxEnergyStored()+" FE"));
    }

    public void draw(PoseStack stack, int x, int y, int yOffset) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ENERGY_BAR);

        int e = (int)(area.getWidth()*(energy.getEnergyStored()/(float)energy.getMaxEnergyStored()));
        this.blit(stack, x + 8, y + yOffset, e, area.getHeight(), 0, 0, e, area.getHeight(), 160, 12);
    }
}