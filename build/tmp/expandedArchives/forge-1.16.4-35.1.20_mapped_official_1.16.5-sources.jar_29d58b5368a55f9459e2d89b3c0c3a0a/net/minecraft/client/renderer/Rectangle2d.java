package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Rectangle2d {
   private int xPos;
   private int yPos;
   private int width;
   private int height;

   public Rectangle2d(int p_i47637_1_, int p_i47637_2_, int p_i47637_3_, int p_i47637_4_) {
      this.xPos = p_i47637_1_;
      this.yPos = p_i47637_2_;
      this.width = p_i47637_3_;
      this.height = p_i47637_4_;
   }

   public int getX() {
      return this.xPos;
   }

   public int getY() {
      return this.yPos;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean contains(int p_199315_1_, int p_199315_2_) {
      return p_199315_1_ >= this.xPos && p_199315_1_ <= this.xPos + this.width && p_199315_2_ >= this.yPos && p_199315_2_ <= this.yPos + this.height;
   }
}
