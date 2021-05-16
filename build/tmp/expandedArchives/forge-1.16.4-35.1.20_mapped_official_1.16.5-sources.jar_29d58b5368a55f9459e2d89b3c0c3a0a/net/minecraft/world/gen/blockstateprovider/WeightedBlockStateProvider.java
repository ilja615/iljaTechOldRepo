package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedList;
import net.minecraft.util.math.BlockPos;

public class WeightedBlockStateProvider extends BlockStateProvider {
   public static final Codec<WeightedBlockStateProvider> CODEC = WeightedList.codec(BlockState.CODEC).comapFlatMap(WeightedBlockStateProvider::create, (p_236813_0_) -> {
      return p_236813_0_.weightedList;
   }).fieldOf("entries").codec();
   private final WeightedList<BlockState> weightedList;

   private static DataResult<WeightedBlockStateProvider> create(WeightedList<BlockState> p_236812_0_) {
      return p_236812_0_.isEmpty() ? DataResult.error("WeightedStateProvider with no states") : DataResult.success(new WeightedBlockStateProvider(p_236812_0_));
   }

   private WeightedBlockStateProvider(WeightedList<BlockState> p_i225862_1_) {
      this.weightedList = p_i225862_1_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
   }

   public WeightedBlockStateProvider() {
      this(new WeightedList<>());
   }

   public WeightedBlockStateProvider add(BlockState p_227407_1_, int p_227407_2_) {
      this.weightedList.add(p_227407_1_, p_227407_2_);
      return this;
   }

   public BlockState getState(Random p_225574_1_, BlockPos p_225574_2_) {
      return this.weightedList.getOne(p_225574_1_);
   }
}
