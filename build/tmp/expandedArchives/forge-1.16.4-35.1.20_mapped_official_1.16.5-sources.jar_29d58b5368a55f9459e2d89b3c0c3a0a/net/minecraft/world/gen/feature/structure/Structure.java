package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<Structure<?>> implements net.minecraftforge.common.extensions.IForgeStructure {
   public static final BiMap<String, Structure<?>> STRUCTURES_REGISTRY = HashBiMap.create();
   private static final Map<Structure<?>, GenerationStage.Decoration> STEP = Maps.newHashMap();
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Structure<VillageConfig> PILLAGER_OUTPOST = register("Pillager_Outpost", new PillagerOutpostStructure(VillageConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<MineshaftConfig> MINESHAFT = register("Mineshaft", new MineshaftStructure(MineshaftConfig.CODEC), GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
   public static final Structure<NoFeatureConfig> WOODLAND_MANSION = register("Mansion", new WoodlandMansionStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<NoFeatureConfig> JUNGLE_TEMPLE = register("Jungle_Pyramid", new JunglePyramidStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<NoFeatureConfig> DESERT_PYRAMID = register("Desert_Pyramid", new DesertPyramidStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<NoFeatureConfig> IGLOO = register("Igloo", new IglooStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<RuinedPortalFeature> RUINED_PORTAL = register("Ruined_Portal", new RuinedPortalStructure(RuinedPortalFeature.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<ShipwreckConfig> SHIPWRECK = register("Shipwreck", new ShipwreckStructure(ShipwreckConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final SwampHutStructure SWAMP_HUT = register("Swamp_Hut", new SwampHutStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<NoFeatureConfig> STRONGHOLD = register("Stronghold", new StrongholdStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.STRONGHOLDS);
   public static final Structure<NoFeatureConfig> OCEAN_MONUMENT = register("Monument", new OceanMonumentStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<OceanRuinConfig> OCEAN_RUIN = register("Ocean_Ruin", new OceanRuinStructure(OceanRuinConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<NoFeatureConfig> NETHER_BRIDGE = register("Fortress", new FortressStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.UNDERGROUND_DECORATION);
   public static final Structure<NoFeatureConfig> END_CITY = register("EndCity", new EndCityStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<ProbabilityConfig> BURIED_TREASURE = register("Buried_Treasure", new BuriedTreasureStructure(ProbabilityConfig.CODEC), GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
   public static final Structure<VillageConfig> VILLAGE = register("Village", new VillageStructure(VillageConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final Structure<NoFeatureConfig> NETHER_FOSSIL = register("Nether_Fossil", new NetherFossilStructure(NoFeatureConfig.CODEC), GenerationStage.Decoration.UNDERGROUND_DECORATION);
   public static final Structure<VillageConfig> BASTION_REMNANT = register("Bastion_Remnant", new BastionRemantsStructure(VillageConfig.CODEC), GenerationStage.Decoration.SURFACE_STRUCTURES);
   public static final List<Structure<?>> NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
   private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
   private static final Map<ResourceLocation, ResourceLocation> RENAMES = ImmutableMap.<ResourceLocation, ResourceLocation>builder().put(new ResourceLocation("nvi"), JIGSAW_RENAME).put(new ResourceLocation("pcp"), JIGSAW_RENAME).put(new ResourceLocation("bastionremnant"), JIGSAW_RENAME).put(new ResourceLocation("runtime"), JIGSAW_RENAME).build();
   private final Codec<StructureFeature<C, Structure<C>>> configuredStructureCodec;

   private static <F extends Structure<?>> F register(String p_236394_0_, F p_236394_1_, GenerationStage.Decoration p_236394_2_) {
      STRUCTURES_REGISTRY.put(p_236394_0_.toLowerCase(Locale.ROOT), p_236394_1_);
      STEP.put(p_236394_1_, p_236394_2_);
      return Registry.register(Registry.STRUCTURE_FEATURE, p_236394_0_.toLowerCase(Locale.ROOT), p_236394_1_);
   }

   public Structure(Codec<C> p_i231997_1_) {
      this.configuredStructureCodec = p_i231997_1_.fieldOf("config").xmap((p_236395_1_) -> {
         return new StructureFeature<>(this, p_236395_1_);
      }, (p_236390_0_) -> {
         return p_236390_0_.config;
      }).codec();
   }

   public GenerationStage.Decoration step() {
      return STEP.get(this);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart<?> loadStaticStart(TemplateManager p_236393_0_, CompoundNBT p_236393_1_, long p_236393_2_) {
      String s = p_236393_1_.getString("id");
      if ("INVALID".equals(s)) {
         return StructureStart.INVALID_START;
      } else {
         Structure<?> structure = Registry.STRUCTURE_FEATURE.get(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
         if (structure == null) {
            LOGGER.error("Unknown feature id: {}", (Object)s);
            return null;
         } else {
            int i = p_236393_1_.getInt("ChunkX");
            int j = p_236393_1_.getInt("ChunkZ");
            int k = p_236393_1_.getInt("references");
            MutableBoundingBox mutableboundingbox = p_236393_1_.contains("BB") ? new MutableBoundingBox(p_236393_1_.getIntArray("BB")) : MutableBoundingBox.getUnknownBox();
            ListNBT listnbt = p_236393_1_.getList("Children", 10);

            try {
               StructureStart<?> structurestart = structure.createStart(i, j, mutableboundingbox, k, p_236393_2_);

               for(int l = 0; l < listnbt.size(); ++l) {
                  CompoundNBT compoundnbt = listnbt.getCompound(l);
                  String s1 = compoundnbt.getString("id").toLowerCase(Locale.ROOT);
                  ResourceLocation resourcelocation = new ResourceLocation(s1);
                  ResourceLocation resourcelocation1 = RENAMES.getOrDefault(resourcelocation, resourcelocation);
                  IStructurePieceType istructurepiecetype = Registry.STRUCTURE_PIECE.get(resourcelocation1);
                  if (istructurepiecetype == null) {
                     LOGGER.error("Unknown structure piece id: {}", (Object)resourcelocation1);
                  } else {
                     try {
                        StructurePiece structurepiece = istructurepiecetype.load(p_236393_0_, compoundnbt);
                        structurestart.getPieces().add(structurepiece);
                     } catch (Exception exception) {
                        LOGGER.error("Exception loading structure piece with id {}", resourcelocation1, exception);
                     }
                  }
               }

               return structurestart;
            } catch (Exception exception1) {
               LOGGER.error("Failed Start with id {}", s, exception1);
               return null;
            }
         }
      }
   }

   public Codec<StructureFeature<C, Structure<C>>> configuredStructureCodec() {
      return this.configuredStructureCodec;
   }

   public StructureFeature<C, ? extends Structure<C>> configured(C p_236391_1_) {
      return new StructureFeature<>(this, p_236391_1_);
   }

   @Nullable
   public BlockPos getNearestGeneratedFeature(IWorldReader p_236388_1_, StructureManager p_236388_2_, BlockPos p_236388_3_, int p_236388_4_, boolean p_236388_5_, long p_236388_6_, StructureSeparationSettings p_236388_8_) {
      int i = p_236388_8_.spacing();
      int j = p_236388_3_.getX() >> 4;
      int k = p_236388_3_.getZ() >> 4;
      int l = 0;

      for(SharedSeedRandom sharedseedrandom = new SharedSeedRandom(); l <= p_236388_4_; ++l) {
         for(int i1 = -l; i1 <= l; ++i1) {
            boolean flag = i1 == -l || i1 == l;

            for(int j1 = -l; j1 <= l; ++j1) {
               boolean flag1 = j1 == -l || j1 == l;
               if (flag || flag1) {
                  int k1 = j + i * i1;
                  int l1 = k + i * j1;
                  ChunkPos chunkpos = this.getPotentialFeatureChunk(p_236388_8_, p_236388_6_, sharedseedrandom, k1, l1);
                  IChunk ichunk = p_236388_1_.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS);
                  StructureStart<?> structurestart = p_236388_2_.getStartForFeature(SectionPos.of(ichunk.getPos(), 0), this, ichunk);
                  if (structurestart != null && structurestart.isValid()) {
                     if (p_236388_5_ && structurestart.canBeReferenced()) {
                        structurestart.addReference();
                        return structurestart.getLocatePos();
                     }

                     if (!p_236388_5_) {
                        return structurestart.getLocatePos();
                     }
                  }

                  if (l == 0) {
                     break;
                  }
               }
            }

            if (l == 0) {
               break;
            }
         }
      }

      return null;
   }

   protected boolean linearSeparation() {
      return true;
   }

   public final ChunkPos getPotentialFeatureChunk(StructureSeparationSettings p_236392_1_, long p_236392_2_, SharedSeedRandom p_236392_4_, int p_236392_5_, int p_236392_6_) {
      int i = p_236392_1_.spacing();
      int j = p_236392_1_.separation();
      int k = Math.floorDiv(p_236392_5_, i);
      int l = Math.floorDiv(p_236392_6_, i);
      p_236392_4_.setLargeFeatureWithSalt(p_236392_2_, k, l, p_236392_1_.salt());
      int i1;
      int j1;
      if (this.linearSeparation()) {
         i1 = p_236392_4_.nextInt(i - j);
         j1 = p_236392_4_.nextInt(i - j);
      } else {
         i1 = (p_236392_4_.nextInt(i - j) + p_236392_4_.nextInt(i - j)) / 2;
         j1 = (p_236392_4_.nextInt(i - j) + p_236392_4_.nextInt(i - j)) / 2;
      }

      return new ChunkPos(k * i + i1, l * i + j1);
   }

   protected boolean isFeatureChunk(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, C p_230363_10_) {
      return true;
   }

   private StructureStart<C> createStart(int p_236387_1_, int p_236387_2_, MutableBoundingBox p_236387_3_, int p_236387_4_, long p_236387_5_) {
      return this.getStartFactory().create(this, p_236387_1_, p_236387_2_, p_236387_3_, p_236387_4_, p_236387_5_);
   }

   public StructureStart<?> generate(DynamicRegistries p_242785_1_, ChunkGenerator p_242785_2_, BiomeProvider p_242785_3_, TemplateManager p_242785_4_, long p_242785_5_, ChunkPos p_242785_7_, Biome p_242785_8_, int p_242785_9_, SharedSeedRandom p_242785_10_, StructureSeparationSettings p_242785_11_, C p_242785_12_) {
      ChunkPos chunkpos = this.getPotentialFeatureChunk(p_242785_11_, p_242785_5_, p_242785_10_, p_242785_7_.x, p_242785_7_.z);
      if (p_242785_7_.x == chunkpos.x && p_242785_7_.z == chunkpos.z && this.isFeatureChunk(p_242785_2_, p_242785_3_, p_242785_5_, p_242785_10_, p_242785_7_.x, p_242785_7_.z, p_242785_8_, chunkpos, p_242785_12_)) {
         StructureStart<C> structurestart = this.createStart(p_242785_7_.x, p_242785_7_.z, MutableBoundingBox.getUnknownBox(), p_242785_9_, p_242785_5_);
         structurestart.generatePieces(p_242785_1_, p_242785_2_, p_242785_4_, p_242785_7_.x, p_242785_7_.z, p_242785_8_, p_242785_12_);
         if (structurestart.isValid()) {
            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public abstract Structure.IStartFactory<C> getStartFactory();

   public String getFeatureName() {
      return STRUCTURES_REGISTRY.inverse().get(this);
   }

   public List<MobSpawnInfo.Spawners> getSpecialEnemies() {
      return getSpawnList(net.minecraft.entity.EntityClassification.MONSTER);
   }

   public List<MobSpawnInfo.Spawners> getSpecialAnimals() {
      return getSpawnList(net.minecraft.entity.EntityClassification.CREATURE);
   }

   @Override
   public final List<MobSpawnInfo.Spawners> getSpawnList(net.minecraft.entity.EntityClassification classification) {
      return net.minecraftforge.common.world.StructureSpawnManager.getSpawnList(getStructure(), classification);
   }

   public interface IStartFactory<C extends IFeatureConfig> {
      StructureStart<C> create(Structure<C> p_create_1_, int p_create_2_, int p_create_3_, MutableBoundingBox p_create_4_, int p_create_5_, long p_create_6_);
   }
}
