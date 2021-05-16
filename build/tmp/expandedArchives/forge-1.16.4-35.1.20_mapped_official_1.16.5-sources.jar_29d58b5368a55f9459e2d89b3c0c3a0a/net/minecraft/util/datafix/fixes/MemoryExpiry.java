package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class MemoryExpiry extends NamedEntityFix {
   public MemoryExpiry(Schema p_i231460_1_, String p_i231460_2_) {
      super(p_i231460_1_, false, "Memory expiry data fix (" + p_i231460_2_ + ")", TypeReferences.ENTITY, p_i231460_2_);
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }

   public Dynamic<?> fixTag(Dynamic<?> p_233326_1_) {
      return p_233326_1_.update("Brain", this::updateBrain);
   }

   private Dynamic<?> updateBrain(Dynamic<?> p_233327_1_) {
      return p_233327_1_.update("memories", this::updateMemories);
   }

   private Dynamic<?> updateMemories(Dynamic<?> p_233328_1_) {
      return p_233328_1_.updateMapValues(this::updateMemoryEntry);
   }

   private Pair<Dynamic<?>, Dynamic<?>> updateMemoryEntry(Pair<Dynamic<?>, Dynamic<?>> p_233325_1_) {
      return p_233325_1_.mapSecond(this::wrapMemoryValue);
   }

   private Dynamic<?> wrapMemoryValue(Dynamic<?> p_233329_1_) {
      return p_233329_1_.createMap(ImmutableMap.of(p_233329_1_.createString("value"), p_233329_1_));
   }
}
