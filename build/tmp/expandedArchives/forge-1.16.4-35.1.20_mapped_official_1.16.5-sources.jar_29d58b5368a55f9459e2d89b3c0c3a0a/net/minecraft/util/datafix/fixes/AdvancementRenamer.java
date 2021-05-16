package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public class AdvancementRenamer extends DataFix {
   private final String name;
   private final Function<String, String> renamer;

   public AdvancementRenamer(Schema p_i230046_1_, boolean p_i230046_2_, String p_i230046_3_, Function<String, String> p_i230046_4_) {
      super(p_i230046_1_, p_i230046_2_);
      this.name = p_i230046_3_;
      this.renamer = p_i230046_4_;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(TypeReferences.ADVANCEMENTS), (p_230071_1_) -> {
         return p_230071_1_.update(DSL.remainderFinder(), (p_230068_1_) -> {
            return p_230068_1_.updateMapValues((p_233068_2_) -> {
               String s = p_233068_2_.getFirst().asString("");
               return p_233068_2_.mapFirst((p_233069_3_) -> {
                  return p_230068_1_.createString(this.renamer.apply(s));
               });
            });
         });
      });
   }
}
