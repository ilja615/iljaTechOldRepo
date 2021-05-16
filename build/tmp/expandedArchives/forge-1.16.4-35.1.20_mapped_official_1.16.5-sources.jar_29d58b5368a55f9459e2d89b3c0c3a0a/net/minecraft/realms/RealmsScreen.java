package net.minecraft.realms;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsScreen extends Screen {
   public RealmsScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   protected static int row(int p_239562_0_) {
      return 40 + p_239562_0_ * 13;
   }

   public void tick() {
      for(Widget widget : this.buttons) {
         if (widget instanceof IScreen) {
            ((IScreen)widget).tick();
         }
      }

   }

   public void narrateLabels() {
      List<String> list = this.children.stream().filter(RealmsLabel.class::isInstance).map(RealmsLabel.class::cast).map(RealmsLabel::getText).collect(Collectors.toList());
      RealmsNarratorHelper.now(list);
   }
}
