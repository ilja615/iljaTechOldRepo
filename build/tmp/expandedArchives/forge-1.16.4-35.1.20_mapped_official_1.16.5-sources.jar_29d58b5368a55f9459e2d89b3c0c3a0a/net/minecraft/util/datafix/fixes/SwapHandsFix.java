package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class SwapHandsFix extends DataFix {
   private final String fixName;
   private final String fieldFrom;
   private final String fieldTo;

   public SwapHandsFix(Schema p_i241231_1_, boolean p_i241231_2_, String p_i241231_3_, String p_i241231_4_, String p_i241231_5_) {
      super(p_i241231_1_, p_i241231_2_);
      this.fixName = p_i241231_3_;
      this.fieldFrom = p_i241231_4_;
      this.fieldTo = p_i241231_5_;
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.fixName, this.getInputSchema().getType(TypeReferences.OPTIONS), (p_241318_1_) -> {
         return p_241318_1_.update(DSL.remainderFinder(), (p_241319_1_) -> {
            return DataFixUtils.orElse(p_241319_1_.get(this.fieldFrom).result().map((p_241320_2_) -> {
               return p_241319_1_.set(this.fieldTo, p_241320_2_).remove(this.fieldFrom);
            }), p_241319_1_);
         });
      });
   }
}
