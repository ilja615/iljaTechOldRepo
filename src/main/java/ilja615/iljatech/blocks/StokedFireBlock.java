package ilja615.iljatech.blocks;

import ilja615.iljatech.util.interactions.Heat;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.Random;

import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class StokedFireBlock extends BaseFireBlock
{
    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((facingProperty) -> {
        return facingProperty.getKey() != Direction.DOWN;
    }).collect(Util.toMap());
    public static final IntegerProperty AIR = IntegerProperty.create("air", 0, 3);

    public StokedFireBlock(BlockBehaviour.Properties properties) {
        super(properties, 3.0F);
        this.registerDefaultState(this.stateDefinition.any().setValue(AIR, 3));
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (worldIn.random.nextInt(16) == 0)
        {
            Heat.emitHeat(worldIn, pos);
        }
        worldIn.getBlockTicks().scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        super.tick(state, worldIn, pos, rand);
        if (!state.hasProperty(AIR)) return;
        int air = state.getValue(AIR);
        if (air == 0)
        {
            worldIn.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        } else {
            worldIn.setBlockAndUpdate(pos, state.setValue(AIR, Math.max(0, air - 1)));
            worldIn.getBlockTicks().scheduleTick(pos, this, 20);
            if (rand.nextInt(16) == 0)
            {
                Heat.emitHeat(worldIn, pos);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState();
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        int amountAir = stateIn.getValue(AIR);
        return this.canSurvive(stateIn, worldIn, currentPos) ? this.defaultBlockState().setValue(AIR, amountAir) : Blocks.AIR.defaultBlockState();
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return worldIn.getBlockState(blockpos).isFaceSturdy(worldIn, blockpos, Direction.UP);
    }

    protected boolean canBurn(BlockState state) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AIR);
    }
}
