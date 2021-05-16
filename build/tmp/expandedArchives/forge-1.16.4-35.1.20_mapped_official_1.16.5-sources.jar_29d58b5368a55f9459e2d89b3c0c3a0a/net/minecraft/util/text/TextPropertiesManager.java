package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextPropertiesManager {
   private final List<ITextProperties> parts = Lists.newArrayList();

   public void append(ITextProperties p_238155_1_) {
      this.parts.add(p_238155_1_);
   }

   @Nullable
   public ITextProperties getResult() {
      if (this.parts.isEmpty()) {
         return null;
      } else {
         return this.parts.size() == 1 ? this.parts.get(0) : ITextProperties.composite(this.parts);
      }
   }

   public ITextProperties getResultOrEmpty() {
      ITextProperties itextproperties = this.getResult();
      return itextproperties != null ? itextproperties : ITextProperties.EMPTY;
   }
}
