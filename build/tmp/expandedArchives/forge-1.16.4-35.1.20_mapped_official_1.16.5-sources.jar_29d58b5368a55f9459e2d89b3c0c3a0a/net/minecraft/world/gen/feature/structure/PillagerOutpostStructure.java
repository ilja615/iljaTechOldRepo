package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

public class PillagerOutpostStructure extends JigsawStructure {
   private static final List<MobSpawnInfo.Spawners> OUTPOST_ENEMIES = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.PILLAGER, 1, 1, 1));

   public PillagerOutpostStructure(Codec<VillageConfig> p_i231977_1_) {
      super(p_i231977_1_, 0, true, true);
   }

   @Override
   public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
      return OUTPOST_ENEMIES;
   }

   protected boolean isFeatureChunk(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, VillageConfig p_230363_10_) {
      int i = p_230363_6_ >> 4;
      int j = p_230363_7_ >> 4;
      p_230363_5_.setSeed((long)(i ^ j << 4) ^ p_230363_3_);
      p_230363_5_.nextInt();
      if (p_230363_5_.nextInt(5) != 0) {
         return false;
      } else {
         return !this.isNearVillage(p_230363_1_, p_230363_3_, p_230363_5_, p_230363_6_, p_230363_7_);
      }
   }

   private boolean isNearVillage(ChunkGenerator p_242782_1_, long p_242782_2_, SharedSeedRandom p_242782_4_, int p_242782_5_, int p_242782_6_) {
      StructureSeparationSettings structureseparationsettings = p_242782_1_.getSettings().getConfig(Structure.VILLAGE);
      if (structureseparationsettings == null) {
         return false;
      } else {
         for(int i = p_242782_5_ - 10; i <= p_242782_5_ + 10; ++i) {
            for(int j = p_242782_6_ - 10; j <= p_242782_6_ + 10; ++j) {
               ChunkPos chunkpos = Structure.VILLAGE.getPotentialFeatureChunk(structureseparationsettings, p_242782_2_, p_242782_4_, i, j);
               if (i == chunkpos.x && j == chunkpos.z) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
