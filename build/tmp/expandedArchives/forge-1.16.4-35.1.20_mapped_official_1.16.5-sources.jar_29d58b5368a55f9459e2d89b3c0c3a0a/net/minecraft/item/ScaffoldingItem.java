package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ScaffoldingItem extends BlockItem {
   public ScaffoldingItem(Block p_i50039_1_, Item.Properties p_i50039_2_) {
      super(p_i50039_1_, p_i50039_2_);
   }

   @Nullable
   public BlockItemUseContext updatePlacementContext(BlockItemUseContext p_219984_1_) {
      BlockPos blockpos = p_219984_1_.getClickedPos();
      World world = p_219984_1_.getLevel();
      BlockState blockstate = world.getBlockState(blockpos);
      Block block = this.getBlock();
      if (!blockstate.is(block)) {
         return ScaffoldingBlock.getDistance(world, blockpos) == 7 ? null : p_219984_1_;
      } else {
         Direction direction;
         if (p_219984_1_.isSecondaryUseActive()) {
            direction = p_219984_1_.isInside() ? p_219984_1_.getClickedFace().getOpposite() : p_219984_1_.getClickedFace();
         } else {
            direction = p_219984_1_.getClickedFace() == Direction.UP ? p_219984_1_.getHorizontalDirection() : Direction.UP;
         }

         int i = 0;
         BlockPos.Mutable blockpos$mutable = blockpos.mutable().move(direction);

         while(i < 7) {
            if (!world.isClientSide && !World.isInWorldBounds(blockpos$mutable)) {
               PlayerEntity playerentity = p_219984_1_.getPlayer();
               int j = world.getMaxBuildHeight();
               if (playerentity instanceof ServerPlayerEntity && blockpos$mutable.getY() >= j) {
                  SChatPacket schatpacket = new SChatPacket((new TranslationTextComponent("build.tooHigh", j)).withStyle(TextFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
                  ((ServerPlayerEntity)playerentity).connection.send(schatpacket);
               }
               break;
            }

            blockstate = world.getBlockState(blockpos$mutable);
            if (!blockstate.is(this.getBlock())) {
               if (blockstate.canBeReplaced(p_219984_1_)) {
                  return BlockItemUseContext.at(p_219984_1_, blockpos$mutable, direction);
               }
               break;
            }

            blockpos$mutable.move(direction);
            if (direction.getAxis().isHorizontal()) {
               ++i;
            }
         }

         return null;
      }
   }

   protected boolean mustSurvive() {
      return false;
   }
}
