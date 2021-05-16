package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class ElderGuardianSplit extends EntityRenameHelper {
   public ElderGuardianSplit(Schema p_i49668_1_, boolean p_i49668_2_) {
      super("EntityElderGuardianSplitFix", p_i49668_1_, p_i49668_2_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_209758_1_, Dynamic<?> p_209758_2_) {
      return Pair.of(Objects.equals(p_209758_1_, "Guardian") && p_209758_2_.get("Elder").asBoolean(false) ? "ElderGuardian" : p_209758_1_, p_209758_2_);
   }
}
