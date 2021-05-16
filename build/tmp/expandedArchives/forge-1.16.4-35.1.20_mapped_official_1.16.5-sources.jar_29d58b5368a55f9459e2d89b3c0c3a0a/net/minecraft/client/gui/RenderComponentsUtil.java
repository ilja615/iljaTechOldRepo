package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextPropertiesManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderComponentsUtil {
   private static final IReorderingProcessor INDENT = IReorderingProcessor.codepoint(32, Style.EMPTY);

   private static String stripColor(String p_238504_0_) {
      return Minecraft.getInstance().options.chatColors ? p_238504_0_ : TextFormatting.stripFormatting(p_238504_0_);
   }

   public static List<IReorderingProcessor> wrapComponents(ITextProperties p_238505_0_, int p_238505_1_, FontRenderer p_238505_2_) {
      TextPropertiesManager textpropertiesmanager = new TextPropertiesManager();
      p_238505_0_.visit((p_238503_1_, p_238503_2_) -> {
         textpropertiesmanager.append(ITextProperties.of(stripColor(p_238503_2_), p_238503_1_));
         return Optional.empty();
      }, Style.EMPTY);
      List<IReorderingProcessor> list = Lists.newArrayList();
      p_238505_2_.getSplitter().splitLines(textpropertiesmanager.getResultOrEmpty(), p_238505_1_, Style.EMPTY, (p_243256_1_, p_243256_2_) -> {
         IReorderingProcessor ireorderingprocessor = LanguageMap.getInstance().getVisualOrder(p_243256_1_);
         list.add(p_243256_2_ ? IReorderingProcessor.composite(INDENT, ireorderingprocessor) : ireorderingprocessor);
      });
      return (List<IReorderingProcessor>)(list.isEmpty() ? Lists.newArrayList(IReorderingProcessor.EMPTY) : list);
   }
}
