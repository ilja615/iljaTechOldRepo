package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public class ProjectileOwner extends DataFix {
   public ProjectileOwner(Schema p_i231450_1_) {
      super(p_i231450_1_, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(TypeReferences.ENTITY), this::updateProjectiles);
   }

   private Typed<?> updateProjectiles(Typed<?> p_233183_1_) {
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:egg", this::updateOwnerThrowable);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:ender_pearl", this::updateOwnerThrowable);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:experience_bottle", this::updateOwnerThrowable);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:snowball", this::updateOwnerThrowable);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:potion", this::updateOwnerThrowable);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:potion", this::updateItemPotion);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:llama_spit", this::updateOwnerLlamaSpit);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:arrow", this::updateOwnerArrow);
      p_233183_1_ = this.updateEntity(p_233183_1_, "minecraft:spectral_arrow", this::updateOwnerArrow);
      return this.updateEntity(p_233183_1_, "minecraft:trident", this::updateOwnerArrow);
   }

   private Dynamic<?> updateOwnerArrow(Dynamic<?> p_233185_1_) {
      long i = p_233185_1_.get("OwnerUUIDMost").asLong(0L);
      long j = p_233185_1_.get("OwnerUUIDLeast").asLong(0L);
      return this.setUUID(p_233185_1_, i, j).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
   }

   private Dynamic<?> updateOwnerLlamaSpit(Dynamic<?> p_233188_1_) {
      OptionalDynamic<?> optionaldynamic = p_233188_1_.get("Owner");
      long i = optionaldynamic.get("OwnerUUIDMost").asLong(0L);
      long j = optionaldynamic.get("OwnerUUIDLeast").asLong(0L);
      return this.setUUID(p_233188_1_, i, j).remove("Owner");
   }

   private Dynamic<?> updateItemPotion(Dynamic<?> p_233189_1_) {
      OptionalDynamic<?> optionaldynamic = p_233189_1_.get("Potion");
      return p_233189_1_.set("Item", optionaldynamic.orElseEmptyMap()).remove("Potion");
   }

   private Dynamic<?> updateOwnerThrowable(Dynamic<?> p_233190_1_) {
      String s = "owner";
      OptionalDynamic<?> optionaldynamic = p_233190_1_.get("owner");
      long i = optionaldynamic.get("M").asLong(0L);
      long j = optionaldynamic.get("L").asLong(0L);
      return this.setUUID(p_233190_1_, i, j).remove("owner");
   }

   private Dynamic<?> setUUID(Dynamic<?> p_233186_1_, long p_233186_2_, long p_233186_4_) {
      String s = "OwnerUUID";
      return p_233186_2_ != 0L && p_233186_4_ != 0L ? p_233186_1_.set("OwnerUUID", p_233186_1_.createIntList(Arrays.stream(createUUIDArray(p_233186_2_, p_233186_4_)))) : p_233186_1_;
   }

   private static int[] createUUIDArray(long p_233182_0_, long p_233182_2_) {
      return new int[]{(int)(p_233182_0_ >> 32), (int)p_233182_0_, (int)(p_233182_2_ >> 32), (int)p_233182_2_};
   }

   private Typed<?> updateEntity(Typed<?> p_233184_1_, String p_233184_2_, Function<Dynamic<?>, Dynamic<?>> p_233184_3_) {
      Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, p_233184_2_);
      Type<?> type1 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, p_233184_2_);
      return p_233184_1_.updateTyped(DSL.namedChoice(p_233184_2_, type), type1, (p_233187_1_) -> {
         return p_233187_1_.update(DSL.remainderFinder(), p_233184_3_);
      });
   }
}
