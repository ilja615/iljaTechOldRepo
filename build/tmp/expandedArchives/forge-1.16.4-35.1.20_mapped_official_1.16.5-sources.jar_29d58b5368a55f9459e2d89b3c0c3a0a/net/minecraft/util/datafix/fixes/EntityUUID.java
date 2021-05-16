package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.TypeReferences;

public class EntityUUID extends AbstractUUIDFix {
   private static final Set<String> ABSTRACT_HORSES = Sets.newHashSet();
   private static final Set<String> TAMEABLE_ANIMALS = Sets.newHashSet();
   private static final Set<String> ANIMALS = Sets.newHashSet();
   private static final Set<String> MOBS = Sets.newHashSet();
   private static final Set<String> LIVING_ENTITIES = Sets.newHashSet();
   private static final Set<String> PROJECTILES = Sets.newHashSet();

   public EntityUUID(Schema p_i231452_1_) {
      super(p_i231452_1_, TypeReferences.ENTITY);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.typeReference), (p_233210_1_) -> {
         p_233210_1_ = p_233210_1_.update(DSL.remainderFinder(), EntityUUID::updateEntityUUID);

         for(String s : ABSTRACT_HORSES) {
            p_233210_1_ = this.updateNamedChoice(p_233210_1_, s, EntityUUID::updateAnimalOwner);
         }

         for(String s1 : TAMEABLE_ANIMALS) {
            p_233210_1_ = this.updateNamedChoice(p_233210_1_, s1, EntityUUID::updateAnimalOwner);
         }

         for(String s2 : ANIMALS) {
            p_233210_1_ = this.updateNamedChoice(p_233210_1_, s2, EntityUUID::updateAnimal);
         }

         for(String s3 : MOBS) {
            p_233210_1_ = this.updateNamedChoice(p_233210_1_, s3, EntityUUID::updateMob);
         }

         for(String s4 : LIVING_ENTITIES) {
            p_233210_1_ = this.updateNamedChoice(p_233210_1_, s4, EntityUUID::updateLivingEntity);
         }

         for(String s5 : PROJECTILES) {
            p_233210_1_ = this.updateNamedChoice(p_233210_1_, s5, EntityUUID::updateProjectile);
         }

         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:bee", EntityUUID::updateHurtBy);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:zombified_piglin", EntityUUID::updateHurtBy);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:fox", EntityUUID::updateFox);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:item", EntityUUID::updateItem);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:shulker_bullet", EntityUUID::updateShulkerBullet);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:area_effect_cloud", EntityUUID::updateAreaEffectCloud);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:zombie_villager", EntityUUID::updateZombieVillager);
         p_233210_1_ = this.updateNamedChoice(p_233210_1_, "minecraft:evoker_fangs", EntityUUID::updateEvokerFangs);
         return this.updateNamedChoice(p_233210_1_, "minecraft:piglin", EntityUUID::updatePiglin);
      });
   }

   private static Dynamic<?> updatePiglin(Dynamic<?> p_233216_0_) {
      return p_233216_0_.update("Brain", (p_233235_0_) -> {
         return p_233235_0_.update("memories", (p_233236_0_) -> {
            return p_233236_0_.update("minecraft:angry_at", (p_233237_0_) -> {
               return replaceUUIDString(p_233237_0_, "value", "value").orElseGet(() -> {
                  LOGGER.warn("angry_at has no value.");
                  return p_233237_0_;
               });
            });
         });
      });
   }

   private static Dynamic<?> updateEvokerFangs(Dynamic<?> p_233218_0_) {
      return replaceUUIDLeastMost(p_233218_0_, "OwnerUUID", "Owner").orElse(p_233218_0_);
   }

   private static Dynamic<?> updateZombieVillager(Dynamic<?> p_233220_0_) {
      return replaceUUIDLeastMost(p_233220_0_, "ConversionPlayer", "ConversionPlayer").orElse(p_233220_0_);
   }

   private static Dynamic<?> updateAreaEffectCloud(Dynamic<?> p_233221_0_) {
      return replaceUUIDLeastMost(p_233221_0_, "OwnerUUID", "Owner").orElse(p_233221_0_);
   }

   private static Dynamic<?> updateShulkerBullet(Dynamic<?> p_233222_0_) {
      p_233222_0_ = replaceUUIDMLTag(p_233222_0_, "Owner", "Owner").orElse(p_233222_0_);
      return replaceUUIDMLTag(p_233222_0_, "Target", "Target").orElse(p_233222_0_);
   }

   private static Dynamic<?> updateItem(Dynamic<?> p_233223_0_) {
      p_233223_0_ = replaceUUIDMLTag(p_233223_0_, "Owner", "Owner").orElse(p_233223_0_);
      return replaceUUIDMLTag(p_233223_0_, "Thrower", "Thrower").orElse(p_233223_0_);
   }

   private static Dynamic<?> updateFox(Dynamic<?> p_233224_0_) {
      Optional<Dynamic<?>> optional = p_233224_0_.get("TrustedUUIDs").result().map((p_233219_1_) -> {
         return p_233224_0_.createList(p_233219_1_.asStream().map((p_233233_0_) -> {
            return createUUIDFromML(p_233233_0_).orElseGet(() -> {
               LOGGER.warn("Trusted contained invalid data.");
               return p_233233_0_;
            });
         }));
      });
      return DataFixUtils.orElse(optional.map((p_233217_1_) -> {
         return p_233224_0_.remove("TrustedUUIDs").set("Trusted", p_233217_1_);
      }), p_233224_0_);
   }

   private static Dynamic<?> updateHurtBy(Dynamic<?> p_233225_0_) {
      return replaceUUIDString(p_233225_0_, "HurtBy", "HurtBy").orElse(p_233225_0_);
   }

   private static Dynamic<?> updateAnimalOwner(Dynamic<?> p_233226_0_) {
      Dynamic<?> dynamic = updateAnimal(p_233226_0_);
      return replaceUUIDString(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
   }

   private static Dynamic<?> updateAnimal(Dynamic<?> p_233227_0_) {
      Dynamic<?> dynamic = updateMob(p_233227_0_);
      return replaceUUIDLeastMost(dynamic, "LoveCause", "LoveCause").orElse(dynamic);
   }

   private static Dynamic<?> updateMob(Dynamic<?> p_233228_0_) {
      return updateLivingEntity(p_233228_0_).update("Leash", (p_233232_0_) -> {
         return replaceUUIDLeastMost(p_233232_0_, "UUID", "UUID").orElse(p_233232_0_);
      });
   }

   public static Dynamic<?> updateLivingEntity(Dynamic<?> p_233212_0_) {
      return p_233212_0_.update("Attributes", (p_233213_1_) -> {
         return p_233212_0_.createList(p_233213_1_.asStream().map((p_233230_0_) -> {
            return p_233230_0_.update("Modifiers", (p_233215_1_) -> {
               return p_233230_0_.createList(p_233215_1_.asStream().map((p_233231_0_) -> {
                  return replaceUUIDLeastMost(p_233231_0_, "UUID", "UUID").orElse(p_233231_0_);
               }));
            });
         }));
      });
   }

   private static Dynamic<?> updateProjectile(Dynamic<?> p_233229_0_) {
      return DataFixUtils.orElse(p_233229_0_.get("OwnerUUID").result().map((p_233211_1_) -> {
         return p_233229_0_.remove("OwnerUUID").set("Owner", p_233211_1_);
      }), p_233229_0_);
   }

   public static Dynamic<?> updateEntityUUID(Dynamic<?> p_233214_0_) {
      return replaceUUIDLeastMost(p_233214_0_, "UUID", "UUID").orElse(p_233214_0_);
   }

   static {
      ABSTRACT_HORSES.add("minecraft:donkey");
      ABSTRACT_HORSES.add("minecraft:horse");
      ABSTRACT_HORSES.add("minecraft:llama");
      ABSTRACT_HORSES.add("minecraft:mule");
      ABSTRACT_HORSES.add("minecraft:skeleton_horse");
      ABSTRACT_HORSES.add("minecraft:trader_llama");
      ABSTRACT_HORSES.add("minecraft:zombie_horse");
      TAMEABLE_ANIMALS.add("minecraft:cat");
      TAMEABLE_ANIMALS.add("minecraft:parrot");
      TAMEABLE_ANIMALS.add("minecraft:wolf");
      ANIMALS.add("minecraft:bee");
      ANIMALS.add("minecraft:chicken");
      ANIMALS.add("minecraft:cow");
      ANIMALS.add("minecraft:fox");
      ANIMALS.add("minecraft:mooshroom");
      ANIMALS.add("minecraft:ocelot");
      ANIMALS.add("minecraft:panda");
      ANIMALS.add("minecraft:pig");
      ANIMALS.add("minecraft:polar_bear");
      ANIMALS.add("minecraft:rabbit");
      ANIMALS.add("minecraft:sheep");
      ANIMALS.add("minecraft:turtle");
      ANIMALS.add("minecraft:hoglin");
      MOBS.add("minecraft:bat");
      MOBS.add("minecraft:blaze");
      MOBS.add("minecraft:cave_spider");
      MOBS.add("minecraft:cod");
      MOBS.add("minecraft:creeper");
      MOBS.add("minecraft:dolphin");
      MOBS.add("minecraft:drowned");
      MOBS.add("minecraft:elder_guardian");
      MOBS.add("minecraft:ender_dragon");
      MOBS.add("minecraft:enderman");
      MOBS.add("minecraft:endermite");
      MOBS.add("minecraft:evoker");
      MOBS.add("minecraft:ghast");
      MOBS.add("minecraft:giant");
      MOBS.add("minecraft:guardian");
      MOBS.add("minecraft:husk");
      MOBS.add("minecraft:illusioner");
      MOBS.add("minecraft:magma_cube");
      MOBS.add("minecraft:pufferfish");
      MOBS.add("minecraft:zombified_piglin");
      MOBS.add("minecraft:salmon");
      MOBS.add("minecraft:shulker");
      MOBS.add("minecraft:silverfish");
      MOBS.add("minecraft:skeleton");
      MOBS.add("minecraft:slime");
      MOBS.add("minecraft:snow_golem");
      MOBS.add("minecraft:spider");
      MOBS.add("minecraft:squid");
      MOBS.add("minecraft:stray");
      MOBS.add("minecraft:tropical_fish");
      MOBS.add("minecraft:vex");
      MOBS.add("minecraft:villager");
      MOBS.add("minecraft:iron_golem");
      MOBS.add("minecraft:vindicator");
      MOBS.add("minecraft:pillager");
      MOBS.add("minecraft:wandering_trader");
      MOBS.add("minecraft:witch");
      MOBS.add("minecraft:wither");
      MOBS.add("minecraft:wither_skeleton");
      MOBS.add("minecraft:zombie");
      MOBS.add("minecraft:zombie_villager");
      MOBS.add("minecraft:phantom");
      MOBS.add("minecraft:ravager");
      MOBS.add("minecraft:piglin");
      LIVING_ENTITIES.add("minecraft:armor_stand");
      PROJECTILES.add("minecraft:arrow");
      PROJECTILES.add("minecraft:dragon_fireball");
      PROJECTILES.add("minecraft:firework_rocket");
      PROJECTILES.add("minecraft:fireball");
      PROJECTILES.add("minecraft:llama_spit");
      PROJECTILES.add("minecraft:small_fireball");
      PROJECTILES.add("minecraft:snowball");
      PROJECTILES.add("minecraft:spectral_arrow");
      PROJECTILES.add("minecraft:egg");
      PROJECTILES.add("minecraft:ender_pearl");
      PROJECTILES.add("minecraft:experience_bottle");
      PROJECTILES.add("minecraft:potion");
      PROJECTILES.add("minecraft:trident");
      PROJECTILES.add("minecraft:wither_skull");
   }
}
