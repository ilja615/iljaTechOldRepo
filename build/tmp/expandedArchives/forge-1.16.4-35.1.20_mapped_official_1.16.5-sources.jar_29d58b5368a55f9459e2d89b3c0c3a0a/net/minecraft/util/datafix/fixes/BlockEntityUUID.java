package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class BlockEntityUUID extends AbstractUUIDFix {
   public BlockEntityUUID(Schema p_i231447_1_) {
      super(p_i231447_1_, TypeReferences.BLOCK_ENTITY);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), (p_233113_1_) -> {
         p_233113_1_ = this.updateNamedChoice(p_233113_1_, "minecraft:conduit", this::updateConduit);
         return this.updateNamedChoice(p_233113_1_, "minecraft:skull", this::updateSkull);
      });
   }

   private Dynamic<?> updateSkull(Dynamic<?> p_233115_1_) {
      return p_233115_1_.get("Owner").get().map((p_233117_0_) -> {
         return replaceUUIDString(p_233117_0_, "Id", "Id").orElse(p_233117_0_);
      }).<Dynamic<?>>map((p_233114_1_) -> {
         return p_233115_1_.remove("Owner").set("SkullOwner", p_233114_1_);
      }).result().orElse(p_233115_1_);
   }

   private Dynamic<?> updateConduit(Dynamic<?> p_233116_1_) {
      return replaceUUIDMLTag(p_233116_1_, "target_uuid", "Target").orElse(p_233116_1_);
   }
}
