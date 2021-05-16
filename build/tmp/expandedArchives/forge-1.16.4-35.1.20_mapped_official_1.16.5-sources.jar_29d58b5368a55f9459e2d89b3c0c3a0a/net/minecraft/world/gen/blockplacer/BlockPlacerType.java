package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockPlacerType<P extends BlockPlacer> extends net.minecraftforge.registries.ForgeRegistryEntry<BlockPlacerType<?>> {
   public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK_PLACER = register("simple_block_placer", SimpleBlockPlacer.CODEC);
   public static final BlockPlacerType<DoublePlantBlockPlacer> DOUBLE_PLANT_PLACER = register("double_plant_placer", DoublePlantBlockPlacer.CODEC);
   public static final BlockPlacerType<ColumnBlockPlacer> COLUMN_PLACER = register("column_placer", ColumnBlockPlacer.CODEC);
   private final Codec<P> codec;

   private static <P extends BlockPlacer> BlockPlacerType<P> register(String p_236438_0_, Codec<P> p_236438_1_) {
      return Registry.register(Registry.BLOCK_PLACER_TYPES, p_236438_0_, new BlockPlacerType<>(p_236438_1_));
   }

   public BlockPlacerType(Codec<P> p_i232006_1_) {
      this.codec = p_i232006_1_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
