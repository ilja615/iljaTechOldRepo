package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class TippedArrow extends TypedEntityRenameHelper {
   public TippedArrow(Schema p_i49650_1_, boolean p_i49650_2_) {
      super("EntityTippedArrowFix", p_i49650_1_, p_i49650_2_);
   }

   protected String rename(String p_211311_1_) {
      return Objects.equals(p_211311_1_, "TippedArrow") ? "Arrow" : p_211311_1_;
   }
}
