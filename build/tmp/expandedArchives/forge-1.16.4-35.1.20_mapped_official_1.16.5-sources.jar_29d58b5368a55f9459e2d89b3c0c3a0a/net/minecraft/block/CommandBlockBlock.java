package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlockBlock extends ContainerBlock {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

   public CommandBlockBlock(AbstractBlock.Properties p_i48425_1_) {
      super(p_i48425_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CONDITIONAL, Boolean.valueOf(false)));
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      CommandBlockTileEntity commandblocktileentity = new CommandBlockTileEntity();
      commandblocktileentity.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
      return commandblocktileentity;
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         TileEntity tileentity = p_220069_2_.getBlockEntity(p_220069_3_);
         if (tileentity instanceof CommandBlockTileEntity) {
            CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
            boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_);
            boolean flag1 = commandblocktileentity.isPowered();
            commandblocktileentity.setPowered(flag);
            if (!flag1 && !commandblocktileentity.isAutomatic() && commandblocktileentity.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
               if (flag) {
                  commandblocktileentity.markConditionMet();
                  p_220069_2_.getBlockTicks().scheduleTick(p_220069_3_, this, 1);
               }

            }
         }
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      TileEntity tileentity = p_225534_2_.getBlockEntity(p_225534_3_);
      if (tileentity instanceof CommandBlockTileEntity) {
         CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
         CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlock();
         boolean flag = !StringUtils.isNullOrEmpty(commandblocklogic.getCommand());
         CommandBlockTileEntity.Mode commandblocktileentity$mode = commandblocktileentity.getMode();
         boolean flag1 = commandblocktileentity.wasConditionMet();
         if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.AUTO) {
            commandblocktileentity.markConditionMet();
            if (flag1) {
               this.execute(p_225534_1_, p_225534_2_, p_225534_3_, commandblocklogic, flag);
            } else if (commandblocktileentity.isConditional()) {
               commandblocklogic.setSuccessCount(0);
            }

            if (commandblocktileentity.isPowered() || commandblocktileentity.isAutomatic()) {
               p_225534_2_.getBlockTicks().scheduleTick(p_225534_3_, this, 1);
            }
         } else if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.REDSTONE) {
            if (flag1) {
               this.execute(p_225534_1_, p_225534_2_, p_225534_3_, commandblocklogic, flag);
            } else if (commandblocktileentity.isConditional()) {
               commandblocklogic.setSuccessCount(0);
            }
         }

         p_225534_2_.updateNeighbourForOutputSignal(p_225534_3_, this);
      }

   }

   private void execute(BlockState p_193387_1_, World p_193387_2_, BlockPos p_193387_3_, CommandBlockLogic p_193387_4_, boolean p_193387_5_) {
      if (p_193387_5_) {
         p_193387_4_.performCommand(p_193387_2_);
      } else {
         p_193387_4_.setSuccessCount(0);
      }

      executeChain(p_193387_2_, p_193387_3_, p_193387_1_.getValue(FACING));
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
      if (tileentity instanceof CommandBlockTileEntity && p_225533_4_.canUseGameMasterBlocks()) {
         p_225533_4_.openCommandBlock((CommandBlockTileEntity)tileentity);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      TileEntity tileentity = p_180641_2_.getBlockEntity(p_180641_3_);
      return tileentity instanceof CommandBlockTileEntity ? ((CommandBlockTileEntity)tileentity).getCommandBlock().getSuccessCount() : 0;
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
      if (tileentity instanceof CommandBlockTileEntity) {
         CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
         CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlock();
         if (p_180633_5_.hasCustomHoverName()) {
            commandblocklogic.setName(p_180633_5_.getHoverName());
         }

         if (!p_180633_1_.isClientSide) {
            if (p_180633_5_.getTagElement("BlockEntityTag") == null) {
               commandblocklogic.setTrackOutput(p_180633_1_.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
               commandblocktileentity.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
            }

            if (commandblocktileentity.getMode() == CommandBlockTileEntity.Mode.SEQUENCE) {
               boolean flag = p_180633_1_.hasNeighborSignal(p_180633_2_);
               commandblocktileentity.setPowered(flag);
            }
         }

      }
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, CONDITIONAL);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(World p_193386_0_, BlockPos p_193386_1_, Direction p_193386_2_) {
      BlockPos.Mutable blockpos$mutable = p_193386_1_.mutable();
      GameRules gamerules = p_193386_0_.getGameRules();

      int i;
      BlockState blockstate;
      for(i = gamerules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH); i-- > 0; p_193386_2_ = blockstate.getValue(FACING)) {
         blockpos$mutable.move(p_193386_2_);
         blockstate = p_193386_0_.getBlockState(blockpos$mutable);
         Block block = blockstate.getBlock();
         if (!blockstate.is(Blocks.CHAIN_COMMAND_BLOCK)) {
            break;
         }

         TileEntity tileentity = p_193386_0_.getBlockEntity(blockpos$mutable);
         if (!(tileentity instanceof CommandBlockTileEntity)) {
            break;
         }

         CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
         if (commandblocktileentity.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
            break;
         }

         if (commandblocktileentity.isPowered() || commandblocktileentity.isAutomatic()) {
            CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlock();
            if (commandblocktileentity.markConditionMet()) {
               if (!commandblocklogic.performCommand(p_193386_0_)) {
                  break;
               }

               p_193386_0_.updateNeighbourForOutputSignal(blockpos$mutable, block);
            } else if (commandblocktileentity.isConditional()) {
               commandblocklogic.setSuccessCount(0);
            }
         }
      }

      if (i <= 0) {
         int j = Math.max(gamerules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", (int)j);
      }

   }
}
