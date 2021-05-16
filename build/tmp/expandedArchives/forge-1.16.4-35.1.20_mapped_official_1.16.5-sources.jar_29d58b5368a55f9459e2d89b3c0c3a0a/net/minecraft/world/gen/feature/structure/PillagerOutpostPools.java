package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class PillagerOutpostPools {
   public static final JigsawPattern START = JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pillager_outpost/base_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.legacy("pillager_outpost/base_plate"), 1)), JigsawPattern.PlacementBehaviour.RIGID));

   public static void bootstrap() {
   }

   static {
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pillager_outpost/towers"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.list(ImmutableList.of(JigsawPiece.legacy("pillager_outpost/watchtower"), JigsawPiece.legacy("pillager_outpost/watchtower_overgrown", ProcessorLists.OUTPOST_ROT))), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pillager_outpost/feature_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.legacy("pillager_outpost/feature_plate"), 1)), JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pillager_outpost/features"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.legacy("pillager_outpost/feature_cage1"), 1), Pair.of(JigsawPiece.legacy("pillager_outpost/feature_cage2"), 1), Pair.of(JigsawPiece.legacy("pillager_outpost/feature_logs"), 1), Pair.of(JigsawPiece.legacy("pillager_outpost/feature_tent1"), 1), Pair.of(JigsawPiece.legacy("pillager_outpost/feature_tent2"), 1), Pair.of(JigsawPiece.legacy("pillager_outpost/feature_targets"), 1), Pair.of(JigsawPiece.empty(), 6)), JigsawPattern.PlacementBehaviour.RIGID));
   }
}
