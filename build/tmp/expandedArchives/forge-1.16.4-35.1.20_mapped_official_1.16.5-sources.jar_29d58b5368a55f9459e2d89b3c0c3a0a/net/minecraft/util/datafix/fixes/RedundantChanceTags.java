package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import net.minecraft.util.datafix.TypeReferences;

public class RedundantChanceTags extends DataFix {
   private static final Codec<List<Float>> FLOAT_LIST_CODEC = Codec.FLOAT.listOf();

   public RedundantChanceTags(Schema p_i49657_1_, boolean p_i49657_2_) {
      super(p_i49657_1_, p_i49657_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_210996_0_) -> {
         return p_210996_0_.update(DSL.remainderFinder(), (p_206334_0_) -> {
            if (isZeroList(p_206334_0_.get("HandDropChances"), 2)) {
               p_206334_0_ = p_206334_0_.remove("HandDropChances");
            }

            if (isZeroList(p_206334_0_.get("ArmorDropChances"), 4)) {
               p_206334_0_ = p_206334_0_.remove("ArmorDropChances");
            }

            return p_206334_0_;
         });
      });
   }

   private static boolean isZeroList(OptionalDynamic<?> p_241306_0_, int p_241306_1_) {
      return p_241306_0_.flatMap(FLOAT_LIST_CODEC::parse).map((p_241304_1_) -> {
         return p_241304_1_.size() == p_241306_1_ && p_241304_1_.stream().allMatch((p_241307_0_) -> {
            return p_241307_0_ == 0.0F;
         });
      }).result().orElse(false);
   }
}
