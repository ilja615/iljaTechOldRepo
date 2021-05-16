package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class BastionRemnantsBridgePools {
   public static void bootstrap() {
   }

   static {
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/starting_pieces"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/starting_pieces/entrance", ProcessorLists.ENTRANCE_REPLACEMENT), 1), Pair.of(JigsawPiece.single("bastion/bridge/starting_pieces/entrance_face", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/bridge_pieces"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/bridge_pieces/bridge", ProcessorLists.BRIDGE), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/legs"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/legs/leg_0", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/bridge/legs/leg_1", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/walls"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/walls/wall_base_0", ProcessorLists.RAMPART_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/bridge/walls/wall_base_1", ProcessorLists.RAMPART_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/ramparts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/ramparts/rampart_0", ProcessorLists.RAMPART_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/bridge/ramparts/rampart_1", ProcessorLists.RAMPART_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/rampart_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/rampart_plates/plate_0", ProcessorLists.RAMPART_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("bastion/bridge/connectors"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("bastion/bridge/connectors/back_bridge_top", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1), Pair.of(JigsawPiece.single("bastion/bridge/connectors/back_bridge_bottom", ProcessorLists.BASTION_GENERIC_DEGRADATION), 1)), JigsawPattern.PlacementBehaviour.RIGID));
   }
}
