package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class WriteAndReadDataFix extends DataFix {
   private final String name;
   private final TypeReference type;

   public WriteAndReadDataFix(Schema p_i49613_1_, String p_i49613_2_, TypeReference p_i49613_3_) {
      super(p_i49613_1_, true);
      this.name = p_i49613_2_;
      this.type = p_i49613_3_;
   }

   protected TypeRewriteRule makeRule() {
      return this.writeAndRead(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type));
   }
}
