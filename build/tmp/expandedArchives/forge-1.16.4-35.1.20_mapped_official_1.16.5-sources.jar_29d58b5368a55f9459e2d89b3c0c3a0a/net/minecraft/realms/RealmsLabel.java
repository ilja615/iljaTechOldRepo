package net.minecraft.realms;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLabel implements IGuiEventListener {
   private final ITextComponent text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(ITextComponent p_i232502_1_, int p_i232502_2_, int p_i232502_3_, int p_i232502_4_) {
      this.text = p_i232502_1_;
      this.x = p_i232502_2_;
      this.y = p_i232502_3_;
      this.color = p_i232502_4_;
   }

   public void render(Screen p_239560_1_, MatrixStack p_239560_2_) {
      Screen.drawCenteredString(p_239560_2_, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
   }

   public String getText() {
      return this.text.getString();
   }
}
