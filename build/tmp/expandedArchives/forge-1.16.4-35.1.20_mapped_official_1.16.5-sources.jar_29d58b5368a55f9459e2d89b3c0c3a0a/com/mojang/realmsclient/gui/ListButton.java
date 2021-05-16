package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ListButton {
   public final int width;
   public final int height;
   public final int xOffset;
   public final int yOffset;

   public ListButton(int p_i51779_1_, int p_i51779_2_, int p_i51779_3_, int p_i51779_4_) {
      this.width = p_i51779_1_;
      this.height = p_i51779_2_;
      this.xOffset = p_i51779_3_;
      this.yOffset = p_i51779_4_;
   }

   public void drawForRowAt(MatrixStack p_237726_1_, int p_237726_2_, int p_237726_3_, int p_237726_4_, int p_237726_5_) {
      int i = p_237726_2_ + this.xOffset;
      int j = p_237726_3_ + this.yOffset;
      boolean flag = false;
      if (p_237726_4_ >= i && p_237726_4_ <= i + this.width && p_237726_5_ >= j && p_237726_5_ <= j + this.height) {
         flag = true;
      }

      this.draw(p_237726_1_, i, j, flag);
   }

   protected abstract void draw(MatrixStack p_230435_1_, int p_230435_2_, int p_230435_3_, boolean p_230435_4_);

   public int getRight() {
      return this.xOffset + this.width;
   }

   public int getBottom() {
      return this.yOffset + this.height;
   }

   public abstract void onClick(int p_225121_1_);

   public static void drawButtonsInRow(MatrixStack p_237727_0_, List<ListButton> p_237727_1_, RealmsObjectSelectionList<?> p_237727_2_, int p_237727_3_, int p_237727_4_, int p_237727_5_, int p_237727_6_) {
      for(ListButton listbutton : p_237727_1_) {
         if (p_237727_2_.getRowWidth() > listbutton.getRight()) {
            listbutton.drawForRowAt(p_237727_0_, p_237727_3_, p_237727_4_, p_237727_5_, p_237727_6_);
         }
      }

   }

   public static void rowButtonMouseClicked(RealmsObjectSelectionList<?> p_237728_0_, ExtendedList.AbstractListEntry<?> p_237728_1_, List<ListButton> p_237728_2_, int p_237728_3_, double p_237728_4_, double p_237728_6_) {
      if (p_237728_3_ == 0) {
         int i = p_237728_0_.children().indexOf(p_237728_1_);
         if (i > -1) {
            p_237728_0_.selectItem(i);
            int j = p_237728_0_.getRowLeft();
            int k = p_237728_0_.getRowTop(i);
            int l = (int)(p_237728_4_ - (double)j);
            int i1 = (int)(p_237728_6_ - (double)k);

            for(ListButton listbutton : p_237728_2_) {
               if (l >= listbutton.xOffset && l <= listbutton.getRight() && i1 >= listbutton.yOffset && i1 <= listbutton.getBottom()) {
                  listbutton.onClick(i);
               }
            }
         }
      }

   }
}
