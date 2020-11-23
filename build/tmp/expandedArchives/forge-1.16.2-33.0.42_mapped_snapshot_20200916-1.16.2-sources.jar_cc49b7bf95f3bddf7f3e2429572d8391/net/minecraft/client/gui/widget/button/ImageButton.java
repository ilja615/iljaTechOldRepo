package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImageButton extends Button {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffText;
   private final int textureWidth;
   private final int textureHeight;

   public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, Button.IPressable onPressIn) {
      this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, 256, 256, onPressIn);
   }

   public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int p_i51135_9_, int p_i51135_10_, Button.IPressable onPressIn) {
      this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, p_i51135_9_, p_i51135_10_, onPressIn, StringTextComponent.EMPTY);
   }

   public ImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation resourceLocation, int textureWidth, int textureHeight, Button.IPressable onPress, ITextComponent title) {
      super(x, y, width, height, title, onPress);
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.xTexStart = xTexStart;
      this.yTexStart = yTexStart;
      this.yDiffText = yDiffText;
      this.resourceLocation = resourceLocation;
   }

   public void setPosition(int xIn, int yIn) {
      this.x = xIn;
      this.y = yIn;
   }

   public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bindTexture(this.resourceLocation);
      int i = this.yTexStart;
      if (this.isHovered()) {
         i += this.yDiffText;
      }

      RenderSystem.enableDepthTest();
      blit(matrixStack, this.x, this.y, (float)this.xTexStart, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
   }
}