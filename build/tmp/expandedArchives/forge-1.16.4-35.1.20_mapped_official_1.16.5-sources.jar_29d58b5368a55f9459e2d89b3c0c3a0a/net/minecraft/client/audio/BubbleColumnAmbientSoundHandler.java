package net.minecraft.client.audio;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubbleColumnAmbientSoundHandler implements IAmbientSoundHandler {
   private final ClientPlayerEntity player;
   private boolean wasInBubbleColumn;
   private boolean firstTick = true;

   public BubbleColumnAmbientSoundHandler(ClientPlayerEntity p_i50900_1_) {
      this.player = p_i50900_1_;
   }

   public void tick() {
      World world = this.player.level;
      BlockState blockstate = world.getBlockStatesIfLoaded(this.player.getBoundingBox().inflate(0.0D, (double)-0.4F, 0.0D).deflate(0.001D)).filter((p_239528_0_) -> {
         return p_239528_0_.is(Blocks.BUBBLE_COLUMN);
      }).findFirst().orElse((BlockState)null);
      if (blockstate != null) {
         if (!this.wasInBubbleColumn && !this.firstTick && blockstate.is(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
            boolean flag = blockstate.getValue(BubbleColumnBlock.DRAG_DOWN);
            if (flag) {
               this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
            } else {
               this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
            }
         }

         this.wasInBubbleColumn = true;
      } else {
         this.wasInBubbleColumn = false;
      }

      this.firstTick = false;
   }
}
