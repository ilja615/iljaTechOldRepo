package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootContext;

public abstract class AbstractCriterionTrigger<T extends CriterionInstance> implements ICriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   public final void addPlayerListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<T> p_192165_2_) {
      this.players.computeIfAbsent(p_192165_1_, (p_227072_0_) -> {
         return Sets.newHashSet();
      }).add(p_192165_2_);
   }

   public final void removePlayerListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<T> p_192164_2_) {
      Set<ICriterionTrigger.Listener<T>> set = this.players.get(p_192164_1_);
      if (set != null) {
         set.remove(p_192164_2_);
         if (set.isEmpty()) {
            this.players.remove(p_192164_1_);
         }
      }

   }

   public final void removePlayerListeners(PlayerAdvancements p_192167_1_) {
      this.players.remove(p_192167_1_);
   }

   protected abstract T createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_);

   public final T createInstance(JsonObject p_230307_1_, ConditionArrayParser p_230307_2_) {
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230307_1_, "player", p_230307_2_);
      return this.createInstance(p_230307_1_, entitypredicate$andpredicate, p_230307_2_);
   }

   protected void trigger(ServerPlayerEntity p_235959_1_, Predicate<T> p_235959_2_) {
      PlayerAdvancements playeradvancements = p_235959_1_.getAdvancements();
      Set<ICriterionTrigger.Listener<T>> set = this.players.get(playeradvancements);
      if (set != null && !set.isEmpty()) {
         LootContext lootcontext = EntityPredicate.createContext(p_235959_1_, p_235959_1_);
         List<ICriterionTrigger.Listener<T>> list = null;

         for(ICriterionTrigger.Listener<T> listener : set) {
            T t = listener.getTriggerInstance();
            if (t.getPlayerPredicate().matches(lootcontext) && p_235959_2_.test(t)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<T> listener1 : list) {
               listener1.run(playeradvancements);
            }
         }

      }
   }
}
