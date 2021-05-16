package net.minecraft.world.gen.settings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.Structure;

public class DimensionStructuresSettings {
   public static final Codec<DimensionStructuresSettings> CODEC = RecordCodecBuilder.create((p_236198_0_) -> {
      return p_236198_0_.group(StructureSpreadSettings.CODEC.optionalFieldOf("stronghold").forGetter((p_236200_0_) -> {
         return Optional.ofNullable(p_236200_0_.stronghold);
      }), Codec.simpleMap(Registry.STRUCTURE_FEATURE, StructureSeparationSettings.CODEC, Registry.STRUCTURE_FEATURE).fieldOf("structures").forGetter((p_236196_0_) -> {
         return p_236196_0_.structureConfig;
      })).apply(p_236198_0_, DimensionStructuresSettings::new);
   });
   public static final ImmutableMap<Structure<?>, StructureSeparationSettings> DEFAULTS = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().put(Structure.VILLAGE, new StructureSeparationSettings(32, 8, 10387312)).put(Structure.DESERT_PYRAMID, new StructureSeparationSettings(32, 8, 14357617)).put(Structure.IGLOO, new StructureSeparationSettings(32, 8, 14357618)).put(Structure.JUNGLE_TEMPLE, new StructureSeparationSettings(32, 8, 14357619)).put(Structure.SWAMP_HUT, new StructureSeparationSettings(32, 8, 14357620)).put(Structure.PILLAGER_OUTPOST, new StructureSeparationSettings(32, 8, 165745296)).put(Structure.STRONGHOLD, new StructureSeparationSettings(1, 0, 0)).put(Structure.OCEAN_MONUMENT, new StructureSeparationSettings(32, 5, 10387313)).put(Structure.END_CITY, new StructureSeparationSettings(20, 11, 10387313)).put(Structure.WOODLAND_MANSION, new StructureSeparationSettings(80, 20, 10387319)).put(Structure.BURIED_TREASURE, new StructureSeparationSettings(1, 0, 0)).put(Structure.MINESHAFT, new StructureSeparationSettings(1, 0, 0)).put(Structure.RUINED_PORTAL, new StructureSeparationSettings(40, 15, 34222645)).put(Structure.SHIPWRECK, new StructureSeparationSettings(24, 4, 165745295)).put(Structure.OCEAN_RUIN, new StructureSeparationSettings(20, 8, 14357621)).put(Structure.BASTION_REMNANT, new StructureSeparationSettings(27, 4, 30084232)).put(Structure.NETHER_BRIDGE, new StructureSeparationSettings(27, 4, 30084232)).put(Structure.NETHER_FOSSIL, new StructureSeparationSettings(2, 1, 14357921)).build();
   public static final StructureSpreadSettings DEFAULT_STRONGHOLD;
   private final Map<Structure<?>, StructureSeparationSettings> structureConfig;
   @Nullable
   private final StructureSpreadSettings stronghold;

   public DimensionStructuresSettings(Optional<StructureSpreadSettings> p_i231912_1_, Map<Structure<?>, StructureSeparationSettings> p_i231912_2_) {
      this.stronghold = p_i231912_1_.orElse((StructureSpreadSettings)null);
      this.structureConfig = p_i231912_2_;
   }

   public DimensionStructuresSettings(boolean p_i231913_1_) {
      this.structureConfig = Maps.newHashMap(DEFAULTS);
      this.stronghold = p_i231913_1_ ? DEFAULT_STRONGHOLD : null;
   }

   public Map<Structure<?>, StructureSeparationSettings> structureConfig() {
      return this.structureConfig;
   }

   @Nullable
   public StructureSeparationSettings getConfig(Structure<?> p_236197_1_) {
      return this.structureConfig.get(p_236197_1_);
   }

   @Nullable
   public StructureSpreadSettings stronghold() {
      return this.stronghold;
   }

   static {
      for(Structure<?> structure : Registry.STRUCTURE_FEATURE) {
         if (!DEFAULTS.containsKey(structure)) {
            throw new IllegalStateException("Structure feature without default settings: " + Registry.STRUCTURE_FEATURE.getKey(structure));
         }
      }

      DEFAULT_STRONGHOLD = new StructureSpreadSettings(32, 3, 128);
   }
}
