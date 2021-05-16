package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProgressUpdate {
   void progressStartNoAbort(ITextComponent p_200210_1_);

   @OnlyIn(Dist.CLIENT)
   void progressStart(ITextComponent p_200211_1_);

   void progressStage(ITextComponent p_200209_1_);

   void progressStagePercentage(int p_73718_1_);

   @OnlyIn(Dist.CLIENT)
   void stop();
}
