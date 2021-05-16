package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneTorchBlock extends TorchBlock {
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final Map<IBlockReader, List<RedstoneTorchBlock.Toggle>> RECENT_TOGGLES = new WeakHashMap<>();

   public RedstoneTorchBlock(AbstractBlock.Properties p_i48342_1_) {
      super(p_i48342_1_, RedstoneParticleData.REDSTONE);
      this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(true)));
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      for(Direction direction : Direction.values()) {
         p_220082_2_.updateNeighborsAt(p_220082_3_.relative(direction), this);
      }

   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         for(Direction direction : Direction.values()) {
            p_196243_2_.updateNeighborsAt(p_196243_3_.relative(direction), this);
         }

      }
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(LIT) && Direction.UP != p_180656_4_ ? 15 : 0;
   }

   protected boolean hasNeighborSignal(World p_176597_1_, BlockPos p_176597_2_, BlockState p_176597_3_) {
      return p_176597_1_.hasSignal(p_176597_2_.below(), Direction.DOWN);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      boolean flag = this.hasNeighborSignal(p_225534_2_, p_225534_3_, p_225534_1_);
      List<RedstoneTorchBlock.Toggle> list = RECENT_TOGGLES.get(p_225534_2_);

      while(list != null && !list.isEmpty() && p_225534_2_.getGameTime() - (list.get(0)).when > 60L) {
         list.remove(0);
      }

      if (p_225534_1_.getValue(LIT)) {
         if (flag) {
            p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(LIT, Boolean.valueOf(false)), 3);
            if (isToggledTooFrequently(p_225534_2_, p_225534_3_, true)) {
               p_225534_2_.levelEvent(1502, p_225534_3_, 0);
               p_225534_2_.getBlockTicks().scheduleTick(p_225534_3_, p_225534_2_.getBlockState(p_225534_3_).getBlock(), 160);
            }
         }
      } else if (!flag && !isToggledTooFrequently(p_225534_2_, p_225534_3_, false)) {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.setValue(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (p_220069_1_.getValue(LIT) == this.hasNeighborSignal(p_220069_2_, p_220069_3_, p_220069_1_) && !p_220069_2_.getBlockTicks().willTickThisTick(p_220069_3_, this)) {
         p_220069_2_.getBlockTicks().scheduleTick(p_220069_3_, this, 2);
      }

   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_4_ == Direction.DOWN ? p_176211_1_.getSignal(p_176211_2_, p_176211_3_, p_176211_4_) : 0;
   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.getValue(LIT)) {
         double d0 = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double d1 = (double)p_180655_3_.getY() + 0.7D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double d2 = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         p_180655_2_.addParticle(this.flameParticle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LIT);
   }

   private static boolean isToggledTooFrequently(World p_176598_0_, BlockPos p_176598_1_, boolean p_176598_2_) {
      List<RedstoneTorchBlock.Toggle> list = RECENT_TOGGLES.computeIfAbsent(p_176598_0_, (p_220288_0_) -> {
         return Lists.newArrayList();
      });
      if (p_176598_2_) {
         list.add(new RedstoneTorchBlock.Toggle(p_176598_1_.immutable(), p_176598_0_.getGameTime()));
      }

      int i = 0;

      for(int j = 0; j < list.size(); ++j) {
         RedstoneTorchBlock.Toggle redstonetorchblock$toggle = list.get(j);
         if (redstonetorchblock$toggle.pos.equals(p_176598_1_)) {
            ++i;
            if (i >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   public static class Toggle {
      private final BlockPos pos;
      private final long when;

      public Toggle(BlockPos p_i45688_1_, long p_i45688_2_) {
         this.pos = p_i45688_1_;
         this.when = p_i45688_2_;
      }
   }
}
