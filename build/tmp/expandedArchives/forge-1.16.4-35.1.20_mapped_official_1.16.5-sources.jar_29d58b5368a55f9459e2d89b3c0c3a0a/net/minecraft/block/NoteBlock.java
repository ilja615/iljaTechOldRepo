package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class NoteBlock extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

   public NoteBlock(AbstractBlock.Properties p_i48359_1_) {
      super(p_i48359_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(INSTRUMENT, NoteBlockInstrument.HARP).setValue(NOTE, Integer.valueOf(0)).setValue(POWERED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(INSTRUMENT, NoteBlockInstrument.byState(p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos().below())));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN ? p_196271_1_.setValue(INSTRUMENT, NoteBlockInstrument.byState(p_196271_3_)) : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_);
      if (flag != p_220069_1_.getValue(POWERED)) {
         if (flag) {
            this.playNote(p_220069_2_, p_220069_3_);
         }

         p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(POWERED, Boolean.valueOf(flag)), 3);
      }

   }

   private void playNote(World p_196482_1_, BlockPos p_196482_2_) {
      if (p_196482_1_.isEmptyBlock(p_196482_2_.above())) {
         p_196482_1_.blockEvent(p_196482_2_, this, 0, 0);
      }

   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         int _new = net.minecraftforge.common.ForgeHooks.onNoteChange(p_225533_2_, p_225533_3_, p_225533_1_, p_225533_1_.getValue(NOTE), p_225533_1_.cycle(NOTE).getValue(NOTE));
         if (_new == -1) return ActionResultType.FAIL;
         p_225533_1_ = p_225533_1_.setValue(NOTE, _new);
         p_225533_2_.setBlock(p_225533_3_, p_225533_1_, 3);
         this.playNote(p_225533_2_, p_225533_3_);
         p_225533_4_.awardStat(Stats.TUNE_NOTEBLOCK);
         return ActionResultType.CONSUME;
      }
   }

   public void attack(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      if (!p_196270_2_.isClientSide) {
         this.playNote(p_196270_2_, p_196270_3_);
         p_196270_4_.awardStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   public boolean triggerEvent(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      net.minecraftforge.event.world.NoteBlockEvent.Play e = new net.minecraftforge.event.world.NoteBlockEvent.Play(p_189539_2_, p_189539_3_, p_189539_1_, p_189539_1_.getValue(NOTE), p_189539_1_.getValue(INSTRUMENT));
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) return false;
      p_189539_1_ = p_189539_1_.setValue(NOTE, e.getVanillaNoteId()).setValue(INSTRUMENT, e.getInstrument());
      int i = p_189539_1_.getValue(NOTE);
      float f = (float)Math.pow(2.0D, (double)(i - 12) / 12.0D);
      p_189539_2_.playSound((PlayerEntity)null, p_189539_3_, p_189539_1_.getValue(INSTRUMENT).getSoundEvent(), SoundCategory.RECORDS, 3.0F, f);
      p_189539_2_.addParticle(ParticleTypes.NOTE, (double)p_189539_3_.getX() + 0.5D, (double)p_189539_3_.getY() + 1.2D, (double)p_189539_3_.getZ() + 0.5D, (double)i / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(INSTRUMENT, POWERED, NOTE);
   }
}
