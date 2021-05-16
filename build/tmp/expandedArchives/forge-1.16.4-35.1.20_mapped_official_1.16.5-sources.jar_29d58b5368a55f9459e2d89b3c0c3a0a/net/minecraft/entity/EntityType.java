package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
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
import net.minecraft.entity.passive.MooshroomEntity;
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
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> extends net.minecraftforge.registries.ForgeRegistryEntry<EntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.<AreaEffectCloudEntity>of(AreaEffectCloudEntity::new, EntityClassification.MISC).fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<ArmorStandEntity> ARMOR_STAND = register("armor_stand", EntityType.Builder.<ArmorStandEntity>of(ArmorStandEntity::new, EntityClassification.MISC).sized(0.5F, 1.975F).clientTrackingRange(10));
   public static final EntityType<ArrowEntity> ARROW = register("arrow", EntityType.Builder.<ArrowEntity>of(ArrowEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.<BatEntity>of(BatEntity::new, EntityClassification.AMBIENT).sized(0.5F, 0.9F).clientTrackingRange(5));
   public static final EntityType<BeeEntity> BEE = register("bee", EntityType.Builder.<BeeEntity>of(BeeEntity::new, EntityClassification.CREATURE).sized(0.7F, 0.6F).clientTrackingRange(8));
   public static final EntityType<BlazeEntity> BLAZE = register("blaze", EntityType.Builder.<BlazeEntity>of(BlazeEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8));
   public static final EntityType<BoatEntity> BOAT = register("boat", EntityType.Builder.<BoatEntity>of(BoatEntity::new, EntityClassification.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));
   public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.<CatEntity>of(CatEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8));
   public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register("cave_spider", EntityType.Builder.<CaveSpiderEntity>of(CaveSpiderEntity::new, EntityClassification.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(8));
   public static final EntityType<ChickenEntity> CHICKEN = register("chicken", EntityType.Builder.<ChickenEntity>of(ChickenEntity::new, EntityClassification.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
   public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.<CodEntity>of(CodEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
   public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.<CowEntity>of(CowEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
   public static final EntityType<CreeperEntity> CREEPER = register("creeper", EntityType.Builder.<CreeperEntity>of(CreeperEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8));
   public static final EntityType<DolphinEntity> DOLPHIN = register("dolphin", EntityType.Builder.<DolphinEntity>of(DolphinEntity::new, EntityClassification.WATER_CREATURE).sized(0.9F, 0.6F));
   public static final EntityType<DonkeyEntity> DONKEY = register("donkey", EntityType.Builder.<DonkeyEntity>of(DonkeyEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.5F).clientTrackingRange(10));
   public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.<DragonFireballEntity>of(DragonFireballEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<DrownedEntity> DROWNED = register("drowned", EntityType.Builder.<DrownedEntity>of(DrownedEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.<ElderGuardianEntity>of(ElderGuardianEntity::new, EntityClassification.MONSTER).sized(1.9975F, 1.9975F).clientTrackingRange(10));
   public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register("end_crystal", EntityType.Builder.<EnderCrystalEntity>of(EnderCrystalEntity::new, EntityClassification.MISC).sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register("ender_dragon", EntityType.Builder.<EnderDragonEntity>of(EnderDragonEntity::new, EntityClassification.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(10));
   public static final EntityType<EndermanEntity> ENDERMAN = register("enderman", EntityType.Builder.<EndermanEntity>of(EndermanEntity::new, EntityClassification.MONSTER).sized(0.6F, 2.9F).clientTrackingRange(8));
   public static final EntityType<EndermiteEntity> ENDERMITE = register("endermite", EntityType.Builder.<EndermiteEntity>of(EndermiteEntity::new, EntityClassification.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
   public static final EntityType<EvokerEntity> EVOKER = register("evoker", EntityType.Builder.<EvokerEntity>of(EvokerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.<EvokerFangsEntity>of(EvokerFangsEntity::new, EntityClassification.MISC).sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2));
   public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.<ExperienceOrbEntity>of(ExperienceOrbEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));
   public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.<EyeOfEnderEntity>of(EyeOfEnderEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4));
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register("falling_block", EntityType.Builder.<FallingBlockEntity>of(FallingBlockEntity::new, EntityClassification.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20));
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<FoxEntity> FOX = register("fox", EntityType.Builder.<FoxEntity>of(FoxEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
   public static final EntityType<GhastEntity> GHAST = register("ghast", EntityType.Builder.<GhastEntity>of(GhastEntity::new, EntityClassification.MONSTER).fireImmune().sized(4.0F, 4.0F).clientTrackingRange(10));
   public static final EntityType<GiantEntity> GIANT = register("giant", EntityType.Builder.<GiantEntity>of(GiantEntity::new, EntityClassification.MONSTER).sized(3.6F, 12.0F).clientTrackingRange(10));
   public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.Builder.<GuardianEntity>of(GuardianEntity::new, EntityClassification.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(8));
   public static final EntityType<HoglinEntity> HOGLIN = register("hoglin", EntityType.Builder.<HoglinEntity>of(HoglinEntity::new, EntityClassification.MONSTER).sized(1.3964844F, 1.4F).clientTrackingRange(8));
   public static final EntityType<HorseEntity> HORSE = register("horse", EntityType.Builder.<HorseEntity>of(HorseEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<HuskEntity> HUSK = register("husk", EntityType.Builder.<HuskEntity>of(HuskEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<IllusionerEntity> ILLUSIONER = register("illusioner", EntityType.Builder.<IllusionerEntity>of(IllusionerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<IronGolemEntity> IRON_GOLEM = register("iron_golem", EntityType.Builder.<IronGolemEntity>of(IronGolemEntity::new, EntityClassification.MISC).sized(1.4F, 2.7F).clientTrackingRange(10));
   public static final EntityType<ItemEntity> ITEM = register("item", EntityType.Builder.<ItemEntity>of(ItemEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
   public static final EntityType<ItemFrameEntity> ITEM_FRAME = register("item_frame", EntityType.Builder.<ItemFrameEntity>of(ItemFrameEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<FireballEntity> FIREBALL = register("fireball", EntityType.Builder.<FireballEntity>of(FireballEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<LeashKnotEntity> LEASH_KNOT = register("leash_knot", EntityType.Builder.<LeashKnotEntity>of(LeashKnotEntity::new, EntityClassification.MISC).noSave().sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LightningBoltEntity> LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.<LightningBoltEntity>of(LightningBoltEntity::new, EntityClassification.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LlamaEntity> LLAMA = register("llama", EntityType.Builder.<LlamaEntity>of(LlamaEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
   public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register("llama_spit", EntityType.Builder.<LlamaSpitEntity>of(LlamaSpitEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register("magma_cube", EntityType.Builder.<MagmaCubeEntity>of(MagmaCubeEntity::new, EntityClassification.MONSTER).fireImmune().sized(2.04F, 2.04F).clientTrackingRange(8));
   public static final EntityType<MinecartEntity> MINECART = register("minecart", EntityType.Builder.<MinecartEntity>of(MinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register("chest_minecart", EntityType.Builder.<ChestMinecartEntity>of(ChestMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.<CommandBlockMinecartEntity>of(CommandBlockMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>of(FurnaceMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.<HopperMinecartEntity>of(HopperMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.<SpawnerMinecartEntity>of(SpawnerMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<TNTMinecartEntity> TNT_MINECART = register("tnt_minecart", EntityType.Builder.<TNTMinecartEntity>of(TNTMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MuleEntity> MULE = register("mule", EntityType.Builder.<MuleEntity>of(MuleEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(8));
   public static final EntityType<MooshroomEntity> MOOSHROOM = register("mooshroom", EntityType.Builder.<MooshroomEntity>of(MooshroomEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
   public static final EntityType<OcelotEntity> OCELOT = register("ocelot", EntityType.Builder.<OcelotEntity>of(OcelotEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10));
   public static final EntityType<PaintingEntity> PAINTING = register("painting", EntityType.Builder.<PaintingEntity>of(PaintingEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<PandaEntity> PANDA = register("panda", EntityType.Builder.<PandaEntity>of(PandaEntity::new, EntityClassification.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
   public static final EntityType<ParrotEntity> PARROT = register("parrot", EntityType.Builder.<ParrotEntity>of(ParrotEntity::new, EntityClassification.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8));
   public static final EntityType<PhantomEntity> PHANTOM = register("phantom", EntityType.Builder.<PhantomEntity>of(PhantomEntity::new, EntityClassification.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8));
   public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.<PigEntity>of(PigEntity::new, EntityClassification.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
   public static final EntityType<PiglinEntity> PIGLIN = register("piglin", EntityType.Builder.<PiglinEntity>of(PiglinEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PiglinBruteEntity> PIGLIN_BRUTE = register("piglin_brute", EntityType.Builder.<PiglinBruteEntity>of(PiglinBruteEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PillagerEntity> PILLAGER = register("pillager", EntityType.Builder.<PillagerEntity>of(PillagerEntity::new, EntityClassification.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PolarBearEntity> POLAR_BEAR = register("polar_bear", EntityType.Builder.<PolarBearEntity>of(PolarBearEntity::new, EntityClassification.CREATURE).sized(1.4F, 1.4F).clientTrackingRange(10));
   public static final EntityType<TNTEntity> TNT = register("tnt", EntityType.Builder.<TNTEntity>of(TNTEntity::new, EntityClassification.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10));
   public static final EntityType<PufferfishEntity> PUFFERFISH = register("pufferfish", EntityType.Builder.<PufferfishEntity>of(PufferfishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4));
   public static final EntityType<RabbitEntity> RABBIT = register("rabbit", EntityType.Builder.<RabbitEntity>of(RabbitEntity::new, EntityClassification.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8));
   public static final EntityType<RavagerEntity> RAVAGER = register("ravager", EntityType.Builder.<RavagerEntity>of(RavagerEntity::new, EntityClassification.MONSTER).sized(1.95F, 2.2F).clientTrackingRange(10));
   public static final EntityType<SalmonEntity> SALMON = register("salmon", EntityType.Builder.<SalmonEntity>of(SalmonEntity::new, EntityClassification.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4));
   public static final EntityType<SheepEntity> SHEEP = register("sheep", EntityType.Builder.<SheepEntity>of(SheepEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
   public static final EntityType<ShulkerEntity> SHULKER = register("shulker", EntityType.Builder.<ShulkerEntity>of(ShulkerEntity::new, EntityClassification.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).clientTrackingRange(10));
   public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>of(ShulkerBulletEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(8));
   public static final EntityType<SilverfishEntity> SILVERFISH = register("silverfish", EntityType.Builder.<SilverfishEntity>of(SilverfishEntity::new, EntityClassification.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
   public static final EntityType<SkeletonEntity> SKELETON = register("skeleton", EntityType.Builder.<SkeletonEntity>of(SkeletonEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
   public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.<SkeletonHorseEntity>of(SkeletonHorseEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<SlimeEntity> SLIME = register("slime", EntityType.Builder.<SlimeEntity>of(SlimeEntity::new, EntityClassification.MONSTER).sized(2.04F, 2.04F).clientTrackingRange(10));
   public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register("small_fireball", EntityType.Builder.<SmallFireballEntity>of(SmallFireballEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register("snow_golem", EntityType.Builder.<SnowGolemEntity>of(SnowGolemEntity::new, EntityClassification.MISC).sized(0.7F, 1.9F).clientTrackingRange(8));
   public static final EntityType<SnowballEntity> SNOWBALL = register("snowball", EntityType.Builder.<SnowballEntity>of(SnowballEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.<SpectralArrowEntity>of(SpectralArrowEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<SpiderEntity> SPIDER = register("spider", EntityType.Builder.<SpiderEntity>of(SpiderEntity::new, EntityClassification.MONSTER).sized(1.4F, 0.9F).clientTrackingRange(8));
   public static final EntityType<SquidEntity> SQUID = register("squid", EntityType.Builder.<SquidEntity>of(SquidEntity::new, EntityClassification.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));
   public static final EntityType<StrayEntity> STRAY = register("stray", EntityType.Builder.<StrayEntity>of(StrayEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
   public static final EntityType<StriderEntity> STRIDER = register("strider", EntityType.Builder.<StriderEntity>of(StriderEntity::new, EntityClassification.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10));
   public static final EntityType<EggEntity> EGG = register("egg", EntityType.Builder.<EggEntity>of(EggEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<EnderPearlEntity> ENDER_PEARL = register("ender_pearl", EntityType.Builder.<EnderPearlEntity>of(EnderPearlEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.<ExperienceBottleEntity>of(ExperienceBottleEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<PotionEntity> POTION = register("potion", EntityType.Builder.<PotionEntity>of(PotionEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<TridentEntity> TRIDENT = register("trident", EntityType.Builder.<TridentEntity>of(TridentEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = register("trader_llama", EntityType.Builder.<TraderLlamaEntity>of(TraderLlamaEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
   public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.<TropicalFishEntity>of(TropicalFishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4));
   public static final EntityType<TurtleEntity> TURTLE = register("turtle", EntityType.Builder.<TurtleEntity>of(TurtleEntity::new, EntityClassification.CREATURE).sized(1.2F, 0.4F).clientTrackingRange(10));
   public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.<VexEntity>of(VexEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.4F, 0.8F).clientTrackingRange(8));
   public static final EntityType<VillagerEntity> VILLAGER = register("villager", EntityType.Builder.<VillagerEntity>of(VillagerEntity::new, EntityClassification.MISC).sized(0.6F, 1.95F).clientTrackingRange(10));
   public static final EntityType<VindicatorEntity> VINDICATOR = register("vindicator", EntityType.Builder.<VindicatorEntity>of(VindicatorEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = register("wandering_trader", EntityType.Builder.<WanderingTraderEntity>of(WanderingTraderEntity::new, EntityClassification.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(10));
   public static final EntityType<WitchEntity> WITCH = register("witch", EntityType.Builder.<WitchEntity>of(WitchEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<WitherEntity> WITHER = register("wither", EntityType.Builder.<WitherEntity>of(WitherEntity::new, EntityClassification.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10));
   public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.<WitherSkeletonEntity>of(WitherSkeletonEntity::new, EntityClassification.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8));
   public static final EntityType<WitherSkullEntity> WITHER_SKULL = register("wither_skull", EntityType.Builder.<WitherSkullEntity>of(WitherSkullEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<WolfEntity> WOLF = register("wolf", EntityType.Builder.<WolfEntity>of(WolfEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
   public static final EntityType<ZoglinEntity> ZOGLIN = register("zoglin", EntityType.Builder.<ZoglinEntity>of(ZoglinEntity::new, EntityClassification.MONSTER).fireImmune().sized(1.3964844F, 1.4F).clientTrackingRange(8));
   public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", EntityType.Builder.<ZombieEntity>of(ZombieEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.<ZombieHorseEntity>of(ZombieHorseEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.<ZombieVillagerEntity>of(ZombieVillagerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN = register("zombified_piglin", EntityType.Builder.<ZombifiedPiglinEntity>of(ZombifiedPiglinEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PlayerEntity> PLAYER = register("player", EntityType.Builder.<PlayerEntity>createNothing(EntityClassification.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2));
   public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<FishingBobberEntity>createNothing(EntityClassification.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
   private final EntityType.IFactory<T> factory;
   private final EntityClassification category;
   private final ImmutableSet<Block> immuneTo;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   @Nullable
   private String descriptionId;
   @Nullable
   private ITextComponent description;
   @Nullable
   private ResourceLocation lootTable;
   private final EntitySize dimensions;

   private final java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier;
   private final java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier;
   private final java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier;
   private final java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory;
   private final net.minecraftforge.common.util.ReverseTagWrapper<EntityType<?>> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, net.minecraft.tags.EntityTypeTags::getAllTags);

   private static <T extends Entity> EntityType<T> register(String p_200712_0_, EntityType.Builder<T> p_200712_1_) {
      return Registry.register(Registry.ENTITY_TYPE, p_200712_0_, p_200712_1_.build(p_200712_0_));
   }

   public static ResourceLocation getKey(EntityType<?> p_200718_0_) {
      return Registry.ENTITY_TYPE.getKey(p_200718_0_);
   }

   public static Optional<EntityType<?>> byString(String p_220327_0_) {
      return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(p_220327_0_));
   }

   public EntityType(EntityType.IFactory<T> p_i231489_1_, EntityClassification p_i231489_2_, boolean p_i231489_3_, boolean p_i231489_4_, boolean p_i231489_5_, boolean p_i231489_6_, ImmutableSet<Block> p_i231489_7_, EntitySize p_i231489_8_, int p_i231489_9_, int p_i231489_10_) {
       this(p_i231489_1_, p_i231489_2_, p_i231489_3_, p_i231489_4_, p_i231489_5_, p_i231489_6_, p_i231489_7_, p_i231489_8_, p_i231489_9_, p_i231489_10_, EntityType::defaultVelocitySupplier, EntityType::defaultTrackingRangeSupplier, EntityType::defaultUpdateIntervalSupplier, null);
   }
   public EntityType(EntityType.IFactory<T> p_i231489_1_, EntityClassification p_i231489_2_, boolean p_i231489_3_, boolean p_i231489_4_, boolean p_i231489_5_, boolean p_i231489_6_, ImmutableSet<Block> p_i231489_7_, EntitySize p_i231489_8_, int p_i231489_9_, int p_i231489_10_, final java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier, final java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier, final java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier, final java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory) {
      this.factory = p_i231489_1_;
      this.category = p_i231489_2_;
      this.canSpawnFarFromPlayer = p_i231489_6_;
      this.serialize = p_i231489_3_;
      this.summon = p_i231489_4_;
      this.fireImmune = p_i231489_5_;
      this.immuneTo = p_i231489_7_;
      this.dimensions = p_i231489_8_;
      this.clientTrackingRange = p_i231489_9_;
      this.updateInterval = p_i231489_10_;
      this.velocityUpdateSupplier = velocityUpdateSupplier;
      this.trackingRangeSupplier = trackingRangeSupplier;
      this.updateIntervalSupplier = updateIntervalSupplier;
      this.customClientFactory = customClientFactory;
   }

   @Nullable
   public Entity spawn(ServerWorld p_220331_1_, @Nullable ItemStack p_220331_2_, @Nullable PlayerEntity p_220331_3_, BlockPos p_220331_4_, SpawnReason p_220331_5_, boolean p_220331_6_, boolean p_220331_7_) {
      return this.spawn(p_220331_1_, p_220331_2_ == null ? null : p_220331_2_.getTag(), p_220331_2_ != null && p_220331_2_.hasCustomHoverName() ? p_220331_2_.getHoverName() : null, p_220331_3_, p_220331_4_, p_220331_5_, p_220331_6_, p_220331_7_);
   }

   @Nullable
   public T spawn(ServerWorld p_220342_1_, @Nullable CompoundNBT p_220342_2_, @Nullable ITextComponent p_220342_3_, @Nullable PlayerEntity p_220342_4_, BlockPos p_220342_5_, SpawnReason p_220342_6_, boolean p_220342_7_, boolean p_220342_8_) {
      T t = this.create(p_220342_1_, p_220342_2_, p_220342_3_, p_220342_4_, p_220342_5_, p_220342_6_, p_220342_7_, p_220342_8_);
      if (t != null) {
         if (t instanceof net.minecraft.entity.MobEntity && net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn((net.minecraft.entity.MobEntity) t, p_220342_1_, p_220342_5_.getX(), p_220342_5_.getY(), p_220342_5_.getZ(), null, p_220342_6_)) return null;
         p_220342_1_.addFreshEntityWithPassengers(t);
      }

      return t;
   }

   @Nullable
   public T create(ServerWorld p_220349_1_, @Nullable CompoundNBT p_220349_2_, @Nullable ITextComponent p_220349_3_, @Nullable PlayerEntity p_220349_4_, BlockPos p_220349_5_, SpawnReason p_220349_6_, boolean p_220349_7_, boolean p_220349_8_) {
      T t = this.create(p_220349_1_);
      if (t == null) {
         return (T)null;
      } else {
         double d0;
         if (p_220349_7_) {
            t.setPos((double)p_220349_5_.getX() + 0.5D, (double)(p_220349_5_.getY() + 1), (double)p_220349_5_.getZ() + 0.5D);
            d0 = getYOffset(p_220349_1_, p_220349_5_, p_220349_8_, t.getBoundingBox());
         } else {
            d0 = 0.0D;
         }

         t.moveTo((double)p_220349_5_.getX() + 0.5D, (double)p_220349_5_.getY() + d0, (double)p_220349_5_.getZ() + 0.5D, MathHelper.wrapDegrees(p_220349_1_.random.nextFloat() * 360.0F), 0.0F);
         if (t instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)t;
            mobentity.yHeadRot = mobentity.yRot;
            mobentity.yBodyRot = mobentity.yRot;
            mobentity.finalizeSpawn(p_220349_1_, p_220349_1_.getCurrentDifficultyAt(mobentity.blockPosition()), p_220349_6_, (ILivingEntityData)null, p_220349_2_);
            mobentity.playAmbientSound();
         }

         if (p_220349_3_ != null && t instanceof LivingEntity) {
            t.setCustomName(p_220349_3_);
         }

         updateCustomEntityTag(p_220349_1_, p_220349_4_, t, p_220349_2_);
         return t;
      }
   }

   protected static double getYOffset(IWorldReader p_208051_0_, BlockPos p_208051_1_, boolean p_208051_2_, AxisAlignedBB p_208051_3_) {
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_208051_1_);
      if (p_208051_2_) {
         axisalignedbb = axisalignedbb.expandTowards(0.0D, -1.0D, 0.0D);
      }

      Stream<VoxelShape> stream = p_208051_0_.getCollisions((Entity)null, axisalignedbb, (p_233596_0_) -> {
         return true;
      });
      return 1.0D + VoxelShapes.collide(Direction.Axis.Y, p_208051_3_, stream, p_208051_2_ ? -2.0D : -1.0D);
   }

   public static void updateCustomEntityTag(World p_208048_0_, @Nullable PlayerEntity p_208048_1_, @Nullable Entity p_208048_2_, @Nullable CompoundNBT p_208048_3_) {
      if (p_208048_3_ != null && p_208048_3_.contains("EntityTag", 10)) {
         MinecraftServer minecraftserver = p_208048_0_.getServer();
         if (minecraftserver != null && p_208048_2_ != null) {
            if (p_208048_0_.isClientSide || !p_208048_2_.onlyOpCanSetNbt() || p_208048_1_ != null && minecraftserver.getPlayerList().isOp(p_208048_1_.getGameProfile())) {
               CompoundNBT compoundnbt = p_208048_2_.saveWithoutId(new CompoundNBT());
               UUID uuid = p_208048_2_.getUUID();
               compoundnbt.merge(p_208048_3_.getCompound("EntityTag"));
               p_208048_2_.setUUID(uuid);
               p_208048_2_.load(compoundnbt);
            }
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public EntityClassification getCategory() {
      return this.category;
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("entity", Registry.ENTITY_TYPE.getKey(this));
      }

      return this.descriptionId;
   }

   public ITextComponent getDescription() {
      if (this.description == null) {
         this.description = new TranslationTextComponent(this.getDescriptionId());
      }

      return this.description;
   }

   public String toString() {
      return this.getDescriptionId();
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.lootTable == null) {
         ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(this);
         this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width;
   }

   public float getHeight() {
      return this.dimensions.height;
   }

   @Nullable
   public T create(World p_200721_1_) {
      return this.factory.create(this, p_200721_1_);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Entity create(int p_200717_0_, World p_200717_1_) {
      return create(p_200717_1_, Registry.ENTITY_TYPE.byId(p_200717_0_));
   }

   public static Optional<Entity> create(CompoundNBT p_220330_0_, World p_220330_1_) {
      return Util.ifElse(by(p_220330_0_).map((p_220337_1_) -> {
         return p_220337_1_.create(p_220330_1_);
      }), (p_220329_1_) -> {
         p_220329_1_.load(p_220330_0_);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", (Object)p_220330_0_.getString("id"));
      });
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static Entity create(World p_200719_0_, @Nullable EntityType<?> p_200719_1_) {
      return p_200719_1_ == null ? null : p_200719_1_.create(p_200719_0_);
   }

   public AxisAlignedBB getAABB(double p_220328_1_, double p_220328_3_, double p_220328_5_) {
      float f = this.getWidth() / 2.0F;
      return new AxisAlignedBB(p_220328_1_ - (double)f, p_220328_3_, p_220328_5_ - (double)f, p_220328_1_ + (double)f, p_220328_3_ + (double)this.getHeight(), p_220328_5_ + (double)f);
   }

   public boolean isBlockDangerous(BlockState p_233597_1_) {
      if (this.immuneTo.contains(p_233597_1_.getBlock())) {
         return false;
      } else if (this.fireImmune || !p_233597_1_.is(BlockTags.FIRE) && !p_233597_1_.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(p_233597_1_) && !p_233597_1_.is(Blocks.LAVA)) {
         return p_233597_1_.is(Blocks.WITHER_ROSE) || p_233597_1_.is(Blocks.SWEET_BERRY_BUSH) || p_233597_1_.is(Blocks.CACTUS);
      } else {
         return true;
      }
   }

   public EntitySize getDimensions() {
      return this.dimensions;
   }

   public static Optional<EntityType<?>> by(CompoundNBT p_220347_0_) {
      return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(p_220347_0_.getString("id")));
   }

   @Nullable
   public static Entity loadEntityRecursive(CompoundNBT p_220335_0_, World p_220335_1_, Function<Entity, Entity> p_220335_2_) {
      return loadStaticEntity(p_220335_0_, p_220335_1_).map(p_220335_2_).map((p_220346_3_) -> {
         if (p_220335_0_.contains("Passengers", 9)) {
            ListNBT listnbt = p_220335_0_.getList("Passengers", 10);

            for(int i = 0; i < listnbt.size(); ++i) {
               Entity entity = loadEntityRecursive(listnbt.getCompound(i), p_220335_1_, p_220335_2_);
               if (entity != null) {
                  entity.startRiding(p_220346_3_, true);
               }
            }
         }

         return p_220346_3_;
      }).orElse((Entity)null);
   }

   private static Optional<Entity> loadStaticEntity(CompoundNBT p_220343_0_, World p_220343_1_) {
      try {
         return create(p_220343_0_, p_220343_1_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.warn("Exception loading entity: ", (Throwable)runtimeexception);
         return Optional.empty();
      }
   }

   public int clientTrackingRange() {
      return trackingRangeSupplier.applyAsInt(this);
   }
   private int defaultTrackingRangeSupplier() {
      return this.clientTrackingRange;
   }

   public int updateInterval() {
      return updateIntervalSupplier.applyAsInt(this);
   }
   private int defaultUpdateIntervalSupplier() {
      return this.updateInterval;
   }

   public boolean trackDeltas() {
      return velocityUpdateSupplier.test(this);
   }
   private boolean defaultVelocitySupplier() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   public boolean is(ITag<EntityType<?>> p_220341_1_) {
      return p_220341_1_.contains(this);
   }

   public T customClientSpawn(net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity packet, World world) {
       if (customClientFactory == null) return this.create(world);
       return customClientFactory.apply(packet, world);
   }

   /**
    * Retrieves a list of tags names this is known to be associated with.
    * This should be used in favor of TagCollection.getOwningTags, as this caches the result and automatically updates when the TagCollection changes.
    */
   public java.util.Set<ResourceLocation> getTags() {
       return reverseTags.getTagNames();
   }

   public static class Builder<T extends Entity> {
      private final EntityType.IFactory<T> factory;
      private final EntityClassification category;
      private ImmutableSet<Block> immuneTo = ImmutableSet.of();
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
      private EntitySize dimensions = EntitySize.scalable(0.6F, 1.8F);

      private java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier = EntityType::defaultVelocitySupplier;
      private java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier = EntityType::defaultTrackingRangeSupplier;
      private java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier = EntityType::defaultUpdateIntervalSupplier;
      private java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory;

      private Builder(EntityType.IFactory<T> p_i50479_1_, EntityClassification p_i50479_2_) {
         this.factory = p_i50479_1_;
         this.category = p_i50479_2_;
         this.canSpawnFarFromPlayer = p_i50479_2_ == EntityClassification.CREATURE || p_i50479_2_ == EntityClassification.MISC;
      }

      public static <T extends Entity> EntityType.Builder<T> of(EntityType.IFactory<T> p_220322_0_, EntityClassification p_220322_1_) {
         return new EntityType.Builder<>(p_220322_0_, p_220322_1_);
      }

      public static <T extends Entity> EntityType.Builder<T> createNothing(EntityClassification p_220319_0_) {
         return new EntityType.Builder<>((p_220323_0_, p_220323_1_) -> {
            return (T)null;
         }, p_220319_0_);
      }

      public EntityType.Builder<T> sized(float p_220321_1_, float p_220321_2_) {
         this.dimensions = EntitySize.scalable(p_220321_1_, p_220321_2_);
         return this;
      }

      public EntityType.Builder<T> noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder<T> noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder<T> fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder<T> immuneTo(Block... p_233607_1_) {
         this.immuneTo = ImmutableSet.copyOf(p_233607_1_);
         return this;
      }

      public EntityType.Builder<T> canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType.Builder<T> clientTrackingRange(int p_233606_1_) {
         this.clientTrackingRange = p_233606_1_;
         return this;
      }

      public EntityType.Builder<T> updateInterval(int p_233608_1_) {
         this.updateInterval = p_233608_1_;
         return this;
      }

      public EntityType.Builder<T> setUpdateInterval(int interval) {
          this.updateIntervalSupplier = t->interval;
          return this;
      }

      public EntityType.Builder<T> setTrackingRange(int range) {
          this.trackingRangeSupplier = t->range;
          return this;
      }

      public EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean value) {
          this.velocityUpdateSupplier = t->value;
          return this;
      }

      /**
       * By default, entities are spawned clientside via {@link EntityType#create(World)}.
       * If you need finer control over the spawning process, use this to get read access to the spawn packet.
       */
      public EntityType.Builder<T> setCustomClientFactory(java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, T> customClientFactory) {
          this.customClientFactory = customClientFactory;
          return this;
      }

      public EntityType<T> build(String p_206830_1_) {
         if (this.serialize) {
            Util.fetchChoiceType(TypeReferences.ENTITY_TREE, p_206830_1_);
         }

         return new EntityType<>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.clientTrackingRange, this.updateInterval, velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
      }
   }

   public interface IFactory<T extends Entity> {
      T create(EntityType<T> p_create_1_, World p_create_2_);
   }
}
