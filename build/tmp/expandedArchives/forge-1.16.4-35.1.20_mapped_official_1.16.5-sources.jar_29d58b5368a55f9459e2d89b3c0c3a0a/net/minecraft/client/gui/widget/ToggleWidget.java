package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleWidget extends Widget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public ToggleWidget(int p_i51128_1_, int p_i51128_2_, int p_i51128_3_, int p_i51128_4_, boolean p_i51128_5_) {
      super(p_i51128_1_, p_i51128_2_, p_i51128_3_, p_i51128_4_, StringTextComponent.EMPTY);
      this.isStateTriggered = p_i51128_5_;
   }

   public void initTextureValues(int p_191751_1_, int p_191751_2_, int p_191751_3_, int p_191751_4_, ResourceLocation p_191751_5_) {
      this.xTexStart = p_191751_1_;
      this.yTexStart = p_191751_2_;
      this.xDiffTex = p_191751_3_;
      this.yDiffTex = p_191751_4_;
      this.resourceLocation = p_191751_5_;
   }

   public void setStateTriggered(boolean p_191753_1_) {
      this.isStateTriggered = p_191753_1_;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   public void setPosition(int p_191752_1_, int p_191752_2_) {
      this.x = p_191752_1_;
      this.y = p_191752_2_;
   }

   public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bind(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.isStateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHovered()) {
         j += this.yDiffTex;
      }

      this.blit(p_230431_1_, this.x, this.y, i, j, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}
