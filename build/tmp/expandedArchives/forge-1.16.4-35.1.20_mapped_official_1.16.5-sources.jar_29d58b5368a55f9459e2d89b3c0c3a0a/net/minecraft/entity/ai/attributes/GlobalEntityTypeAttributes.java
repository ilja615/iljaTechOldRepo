package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalEntityTypeAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<EntityType<? extends LivingEntity>, AttributeModifierMap> SUPPLIERS = ImmutableMap.<EntityType<? extends LivingEntity>, AttributeModifierMap>builder().put(EntityType.ARMOR_STAND, LivingEntity.createLivingAttributes().build()).put(EntityType.BAT, BatEntity.createAttributes().build()).put(EntityType.BEE, BeeEntity.createAttributes().build()).put(EntityType.BLAZE, BlazeEntity.createAttributes().build()).put(EntityType.CAT, CatEntity.createAttributes().build()).put(EntityType.CAVE_SPIDER, CaveSpiderEntity.createCaveSpider().build()).put(EntityType.CHICKEN, ChickenEntity.createAttributes().build()).put(EntityType.COD, AbstractFishEntity.createAttributes().build()).put(EntityType.COW, CowEntity.createAttributes().build()).put(EntityType.CREEPER, CreeperEntity.createAttributes().build()).put(EntityType.DOLPHIN, DolphinEntity.createAttributes().build()).put(EntityType.DONKEY, AbstractChestedHorseEntity.createBaseChestedHorseAttributes().build()).put(EntityType.DROWNED, ZombieEntity.createAttributes().build()).put(EntityType.ELDER_GUARDIAN, ElderGuardianEntity.createAttributes().build()).put(EntityType.ENDERMAN, EndermanEntity.createAttributes().build()).put(EntityType.ENDERMITE, EndermiteEntity.createAttributes().build()).put(EntityType.ENDER_DRAGON, EnderDragonEntity.createAttributes().build()).put(EntityType.EVOKER, EvokerEntity.createAttributes().build()).put(EntityType.FOX, FoxEntity.createAttributes().build()).put(EntityType.GHAST, GhastEntity.createAttributes().build()).put(EntityType.GIANT, GiantEntity.createAttributes().build()).put(EntityType.GUARDIAN, GuardianEntity.createAttributes().build()).put(EntityType.HOGLIN, HoglinEntity.createAttributes().build()).put(EntityType.HORSE, AbstractHorseEntity.createBaseHorseAttributes().build()).put(EntityType.HUSK, ZombieEntity.createAttributes().build()).put(EntityType.ILLUSIONER, IllusionerEntity.createAttributes().build()).put(EntityType.IRON_GOLEM, IronGolemEntity.createAttributes().build()).put(EntityType.LLAMA, LlamaEntity.createAttributes().build()).put(EntityType.MAGMA_CUBE, MagmaCubeEntity.createAttributes().build()).put(EntityType.MOOSHROOM, CowEntity.createAttributes().build()).put(EntityType.MULE, AbstractChestedHorseEntity.createBaseChestedHorseAttributes().build()).put(EntityType.OCELOT, OcelotEntity.createAttributes().build()).put(EntityType.PANDA, PandaEntity.createAttributes().build()).put(EntityType.PARROT, ParrotEntity.createAttributes().build()).put(EntityType.PHANTOM, MonsterEntity.createMonsterAttributes().build()).put(EntityType.PIG, PigEntity.createAttributes().build()).put(EntityType.PIGLIN, PiglinEntity.createAttributes().build()).put(EntityType.PIGLIN_BRUTE, PiglinBruteEntity.createAttributes().build()).put(EntityType.PILLAGER, PillagerEntity.createAttributes().build()).put(EntityType.PLAYER, PlayerEntity.createAttributes().build()).put(EntityType.POLAR_BEAR, PolarBearEntity.createAttributes().build()).put(EntityType.PUFFERFISH, AbstractFishEntity.createAttributes().build()).put(EntityType.RABBIT, RabbitEntity.createAttributes().build()).put(EntityType.RAVAGER, RavagerEntity.createAttributes().build()).put(EntityType.SALMON, AbstractFishEntity.createAttributes().build()).put(EntityType.SHEEP, SheepEntity.createAttributes().build()).put(EntityType.SHULKER, ShulkerEntity.createAttributes().build()).put(EntityType.SILVERFISH, SilverfishEntity.createAttributes().build()).put(EntityType.SKELETON, AbstractSkeletonEntity.createAttributes().build()).put(EntityType.SKELETON_HORSE, SkeletonHorseEntity.createAttributes().build()).put(EntityType.SLIME, MonsterEntity.createMonsterAttributes().build()).put(EntityType.SNOW_GOLEM, SnowGolemEntity.createAttributes().build()).put(EntityType.SPIDER, SpiderEntity.createAttributes().build()).put(EntityType.SQUID, SquidEntity.createAttributes().build()).put(EntityType.STRAY, AbstractSkeletonEntity.createAttributes().build()).put(EntityType.STRIDER, StriderEntity.createAttributes().build()).put(EntityType.TRADER_LLAMA, LlamaEntity.createAttributes().build()).put(EntityType.TROPICAL_FISH, AbstractFishEntity.createAttributes().build()).put(EntityType.TURTLE, TurtleEntity.createAttributes().build()).put(EntityType.VEX, VexEntity.createAttributes().build()).put(EntityType.VILLAGER, VillagerEntity.createAttributes().build()).put(EntityType.VINDICATOR, VindicatorEntity.createAttributes().build()).put(EntityType.WANDERING_TRADER, MobEntity.createMobAttributes().build()).put(EntityType.WITCH, WitchEntity.createAttributes().build()).put(EntityType.WITHER, WitherEntity.createAttributes().build()).put(EntityType.WITHER_SKELETON, AbstractSkeletonEntity.createAttributes().build()).put(EntityType.WOLF, WolfEntity.createAttributes().build()).put(EntityType.ZOGLIN, ZoglinEntity.createAttributes().build()).put(EntityType.ZOMBIE, ZombieEntity.createAttributes().build()).put(EntityType.ZOMBIE_HORSE, ZombieHorseEntity.createAttributes().build()).put(EntityType.ZOMBIE_VILLAGER, ZombieEntity.createAttributes().build()).put(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglinEntity.createAttributes().build()).build();
   private static final Map<EntityType<? extends LivingEntity>, AttributeModifierMap> FORGE_ATTRIBUTES = new java.util.HashMap<EntityType<? extends LivingEntity>, AttributeModifierMap>();
   
   public static AttributeModifierMap put(EntityType<? extends LivingEntity> type, AttributeModifierMap map) {
       return FORGE_ATTRIBUTES.put(type, map);
   }

   public static AttributeModifierMap getSupplier(EntityType<? extends LivingEntity> p_233835_0_) {
       AttributeModifierMap map = FORGE_ATTRIBUTES.get(p_233835_0_);
       return map != null ? map : SUPPLIERS.get(p_233835_0_);
   }

   public static boolean hasSupplier(EntityType<?> p_233837_0_) {
      return SUPPLIERS.containsKey(p_233837_0_) || FORGE_ATTRIBUTES.containsKey(p_233837_0_);
   }

   public static void validate() {
      Registry.ENTITY_TYPE.stream().filter((p_233839_0_) -> {
         return p_233839_0_.getCategory() != EntityClassification.MISC;
      }).filter((p_233838_0_) -> {
         return !hasSupplier(p_233838_0_);
      }).map(Registry.ENTITY_TYPE::getKey).forEach((p_233836_0_) -> {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw new IllegalStateException("Entity " + p_233836_0_ + " has no attributes");
         } else {
            LOGGER.error("Entity {} has no attributes", (Object)p_233836_0_);
         }
      });
   }
}
