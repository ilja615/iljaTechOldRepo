package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class BastionRemnantsPieces {
   public static final JigsawPattern START = JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/starts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/units/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/hoglin_stable/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/treasure/big_air_full", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));

   public static void bootstrap() {
      BastionRemnantsMainPools.bootstrap();
      BastionRemnantsStablesPools.bootstrap();
      BastionRemnantsTreasurePools.bootstrap();
      BastionRemnantsBridgePools.bootstrap();
      BastionRemnantsMobsPools.bootstrap();
   }
}
