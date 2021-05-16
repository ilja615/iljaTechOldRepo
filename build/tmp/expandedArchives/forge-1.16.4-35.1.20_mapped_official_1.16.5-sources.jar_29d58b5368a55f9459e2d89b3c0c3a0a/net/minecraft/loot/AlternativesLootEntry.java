package net.minecraft.loot;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.conditions.ILootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesLootEntry extends ParentedLootEntry {
   AlternativesLootEntry(LootEntry[] p_i51263_1_, ILootCondition[] p_i51263_2_) {
      super(p_i51263_1_, p_i51263_2_);
   }

   public LootPoolEntryType getType() {
      return LootEntryManager.ALTERNATIVES;
   }

   protected ILootEntry compose(ILootEntry[] p_216146_1_) {
      switch(p_216146_1_.length) {
      case 0:
         return ALWAYS_FALSE;
      case 1:
         return p_216146_1_[0];
      case 2:
         return p_216146_1_[0].or(p_216146_1_[1]);
      default:
         return (p_216150_1_, p_216150_2_) -> {
            for(ILootEntry ilootentry : p_216146_1_) {
               if (ilootentry.expand(p_216150_1_, p_216150_2_)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   public void validate(ValidationTracker p_225579_1_) {
      super.validate(p_225579_1_);

      for(int i = 0; i < this.children.length - 1; ++i) {
         if (ArrayUtils.isEmpty((Object[])this.children[i].conditions)) {
            p_225579_1_.reportProblem("Unreachable entry!");
         }
      }

   }

   public static AlternativesLootEntry.Builder alternatives(LootEntry.Builder<?>... p_216149_0_) {
      return new AlternativesLootEntry.Builder(p_216149_0_);
   }

   public static class Builder extends LootEntry.Builder<AlternativesLootEntry.Builder> {
      private final List<LootEntry> entries = Lists.newArrayList();

      public Builder(LootEntry.Builder<?>... p_i50579_1_) {
         for(LootEntry.Builder<?> builder : p_i50579_1_) {
            this.entries.add(builder.build());
         }

      }

      protected AlternativesLootEntry.Builder getThis() {
         return this;
      }

      public AlternativesLootEntry.Builder otherwise(LootEntry.Builder<?> p_216080_1_) {
         this.entries.add(p_216080_1_.build());
         return this;
      }

      public LootEntry build() {
         return new AlternativesLootEntry(this.entries.toArray(new LootEntry[0]), this.getConditions());
      }
   }
}
