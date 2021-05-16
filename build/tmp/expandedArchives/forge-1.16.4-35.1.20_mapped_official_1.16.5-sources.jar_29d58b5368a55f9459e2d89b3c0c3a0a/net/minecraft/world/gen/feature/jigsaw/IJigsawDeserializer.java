package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IJigsawDeserializer<P extends JigsawPiece> {
   IJigsawDeserializer<SingleJigsawPiece> SINGLE = register("single_pool_element", SingleJigsawPiece.CODEC);
   IJigsawDeserializer<ListJigsawPiece> LIST = register("list_pool_element", ListJigsawPiece.CODEC);
   IJigsawDeserializer<FeatureJigsawPiece> FEATURE = register("feature_pool_element", FeatureJigsawPiece.CODEC);
   IJigsawDeserializer<EmptyJigsawPiece> EMPTY = register("empty_pool_element", EmptyJigsawPiece.CODEC);
   IJigsawDeserializer<LegacySingleJigsawPiece> LEGACY = register("legacy_single_pool_element", LegacySingleJigsawPiece.CODEC);

   Codec<P> codec();

   static <P extends JigsawPiece> IJigsawDeserializer<P> register(String p_236851_0_, Codec<P> p_236851_1_) {
      return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, p_236851_0_, () -> {
         return p_236851_1_;
      });
   }
}
