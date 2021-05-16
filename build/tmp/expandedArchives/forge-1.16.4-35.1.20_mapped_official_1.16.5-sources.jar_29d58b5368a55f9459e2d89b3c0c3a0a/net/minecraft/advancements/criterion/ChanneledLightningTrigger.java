package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger extends AbstractCriterionTrigger<ChanneledLightningTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJsonArray(p_230241_1_, "victims", p_230241_3_);
      return new ChanneledLightningTrigger.Instance(p_230241_2_, aentitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_204814_1_, Collection<? extends Entity> p_204814_2_) {
      List<LootContext> list = p_204814_2_.stream().map((p_233674_1_) -> {
         return EntityPredicate.createContext(p_204814_1_, p_233674_1_);
      }).collect(Collectors.toList());
      this.trigger(p_204814_1_, (p_233673_1_) -> {
         return p_233673_1_.matches(list);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate[] victims;

      public Instance(EntityPredicate.AndPredicate p_i231493_1_, EntityPredicate.AndPredicate[] p_i231493_2_) {
         super(ChanneledLightningTrigger.ID, p_i231493_1_);
         this.victims = p_i231493_2_;
      }

      public static ChanneledLightningTrigger.Instance channeledLightning(EntityPredicate... p_204824_0_) {
         return new ChanneledLightningTrigger.Instance(EntityPredicate.AndPredicate.ANY, Stream.of(p_204824_0_).map(EntityPredicate.AndPredicate::wrap).toArray((p_233675_0_) -> {
            return new EntityPredicate.AndPredicate[p_233675_0_];
         }));
      }

      public boolean matches(Collection<? extends LootContext> p_233676_1_) {
         for(EntityPredicate.AndPredicate entitypredicate$andpredicate : this.victims) {
            boolean flag = false;

            for(LootContext lootcontext : p_233676_1_) {
               if (entitypredicate$andpredicate.matches(lootcontext)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("victims", EntityPredicate.AndPredicate.toJson(this.victims, p_230240_1_));
         return jsonobject;
      }
   }
}
