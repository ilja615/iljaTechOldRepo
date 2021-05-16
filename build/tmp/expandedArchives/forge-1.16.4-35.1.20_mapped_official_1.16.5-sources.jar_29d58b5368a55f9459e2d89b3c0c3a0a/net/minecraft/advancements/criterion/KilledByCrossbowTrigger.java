package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class KilledByCrossbowTrigger extends AbstractCriterionTrigger<KilledByCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public KilledByCrossbowTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJsonArray(p_230241_1_, "victims", p_230241_3_);
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_230241_1_.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.Instance(p_230241_2_, aentitypredicate$andpredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_234941_1_, Collection<Entity> p_234941_2_) {
      List<LootContext> list = Lists.newArrayList();
      Set<EntityType<?>> set = Sets.newHashSet();

      for(Entity entity : p_234941_2_) {
         set.add(entity.getType());
         list.add(EntityPredicate.createContext(p_234941_1_, entity));
      }

      this.trigger(p_234941_1_, (p_234940_2_) -> {
         return p_234940_2_.matches(list, set.size());
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate[] victims;
      private final MinMaxBounds.IntBound uniqueEntityTypes;

      public Instance(EntityPredicate.AndPredicate p_i231619_1_, EntityPredicate.AndPredicate[] p_i231619_2_, MinMaxBounds.IntBound p_i231619_3_) {
         super(KilledByCrossbowTrigger.ID, p_i231619_1_);
         this.victims = p_i231619_2_;
         this.uniqueEntityTypes = p_i231619_3_;
      }

      public static KilledByCrossbowTrigger.Instance crossbowKilled(EntityPredicate.Builder... p_215116_0_) {
         EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[p_215116_0_.length];

         for(int i = 0; i < p_215116_0_.length; ++i) {
            EntityPredicate.Builder entitypredicate$builder = p_215116_0_[i];
            aentitypredicate$andpredicate[i] = EntityPredicate.AndPredicate.wrap(entitypredicate$builder.build());
         }

         return new KilledByCrossbowTrigger.Instance(EntityPredicate.AndPredicate.ANY, aentitypredicate$andpredicate, MinMaxBounds.IntBound.ANY);
      }

      public static KilledByCrossbowTrigger.Instance crossbowKilled(MinMaxBounds.IntBound p_215117_0_) {
         EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = new EntityPredicate.AndPredicate[0];
         return new KilledByCrossbowTrigger.Instance(EntityPredicate.AndPredicate.ANY, aentitypredicate$andpredicate, p_215117_0_);
      }

      public boolean matches(Collection<LootContext> p_234942_1_, int p_234942_2_) {
         if (this.victims.length > 0) {
            List<LootContext> list = Lists.newArrayList(p_234942_1_);

            for(EntityPredicate.AndPredicate entitypredicate$andpredicate : this.victims) {
               boolean flag = false;
               Iterator<LootContext> iterator = list.iterator();

               while(iterator.hasNext()) {
                  LootContext lootcontext = iterator.next();
                  if (entitypredicate$andpredicate.matches(lootcontext)) {
                     iterator.remove();
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }

         return this.uniqueEntityTypes.matches(p_234942_2_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("victims", EntityPredicate.AndPredicate.toJson(this.victims, p_230240_1_));
         jsonobject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return jsonobject;
      }
   }
}
