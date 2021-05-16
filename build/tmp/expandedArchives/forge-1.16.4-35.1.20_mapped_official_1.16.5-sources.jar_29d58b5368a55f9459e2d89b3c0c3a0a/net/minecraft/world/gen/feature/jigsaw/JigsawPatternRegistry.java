package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.structure.BastionRemnantsPieces;
import net.minecraft.world.gen.feature.structure.PillagerOutpostPools;
import net.minecraft.world.gen.feature.structure.VillagesPools;

public class JigsawPatternRegistry {
   public static final RegistryKey<JigsawPattern> EMPTY = RegistryKey.create(Registry.TEMPLATE_POOL_REGISTRY, new ResourceLocation("empty"));
   private static final JigsawPattern BUILTIN_EMPTY = register(new JigsawPattern(EMPTY.location(), EMPTY.location(), ImmutableList.of(), JigsawPattern.PlacementBehaviour.RIGID));

   public static JigsawPattern register(JigsawPattern p_244094_0_) {
      return WorldGenRegistries.register(WorldGenRegistries.TEMPLATE_POOL, p_244094_0_.getName(), p_244094_0_);
   }

   public static JigsawPattern bootstrap() {
      BastionRemnantsPieces.bootstrap();
      PillagerOutpostPools.bootstrap();
      VillagesPools.bootstrap();
      return BUILTIN_EMPTY;
   }

   static {
      bootstrap();
   }
}
