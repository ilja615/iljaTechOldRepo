package net.minecraft.client.util;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.List;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BidiReorderer {
   public static IReorderingProcessor reorder(ITextProperties p_243508_0_, boolean p_243508_1_) {
      BidiReorder bidireorder = BidiReorder.create(p_243508_0_, UCharacter::getMirror, BidiReorderer::shape);
      Bidi bidi = new Bidi(bidireorder.getPlainText(), p_243508_1_ ? 127 : 126);
      bidi.setReorderingMode(0);
      List<IReorderingProcessor> list = Lists.newArrayList();
      int i = bidi.countRuns();

      for(int j = 0; j < i; ++j) {
         BidiRun bidirun = bidi.getVisualRun(j);
         list.addAll(bidireorder.substring(bidirun.getStart(), bidirun.getLength(), bidirun.isOddRun()));
      }

      return IReorderingProcessor.composite(list);
   }

   private static String shape(String p_243507_0_) {
      try {
         return (new ArabicShaping(8)).shape(p_243507_0_);
      } catch (Exception exception) {
         return p_243507_0_;
      }
   }
}
