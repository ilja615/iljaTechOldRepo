package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public abstract class ParentedLootEntry extends LootEntry {
   protected final LootEntry[] children;
   private final ILootEntry composedChildren;

   protected ParentedLootEntry(LootEntry[] p_i51262_1_, ILootCondition[] p_i51262_2_) {
      super(p_i51262_2_);
      this.children = p_i51262_1_;
      this.composedChildren = this.compose(p_i51262_1_);
   }

   public void validate(ValidationTracker p_225579_1_) {
      super.validate(p_225579_1_);
      if (this.children.length == 0) {
         p_225579_1_.reportProblem("Empty children list");
      }

      for(int i = 0; i < this.children.length; ++i) {
         this.children[i].validate(p_225579_1_.forChild(".entry[" + i + "]"));
      }

   }

   protected abstract ILootEntry compose(ILootEntry[] p_216146_1_);

   public final boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      return !this.canRun(p_expand_1_) ? false : this.composedChildren.expand(p_expand_1_, p_expand_2_);
   }

   public static <T extends ParentedLootEntry> LootEntry.Serializer<T> createSerializer(final ParentedLootEntry.IFactory<T> p_237409_0_) {
      return new LootEntry.Serializer<T>() {
         public void serializeCustom(JsonObject p_230422_1_, T p_230422_2_, JsonSerializationContext p_230422_3_) {
            p_230422_1_.add("children", p_230422_3_.serialize(p_230422_2_.children));
         }

         public final T deserializeCustom(JsonObject p_230421_1_, JsonDeserializationContext p_230421_2_, ILootCondition[] p_230421_3_) {
            LootEntry[] alootentry = JSONUtils.getAsObject(p_230421_1_, "children", p_230421_2_, LootEntry[].class);
            return p_237409_0_.create(alootentry, p_230421_3_);
         }
      };
   }

   @FunctionalInterface
   public interface IFactory<T extends ParentedLootEntry> {
      T create(LootEntry[] p_create_1_, ILootCondition[] p_create_2_);
   }
}
