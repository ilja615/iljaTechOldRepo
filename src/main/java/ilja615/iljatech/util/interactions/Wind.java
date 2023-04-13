package ilja615.iljatech.util.interactions;

import ilja615.iljatech.blocks.StokedFireBlock;
import ilja615.iljatech.blocks.foundry.FoundryBlockEntity;
import ilja615.iljatech.init.ModBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Wind
{
    public static void addWind(Level level, BlockPos startPosition, Direction direction, int length, float speed)
    {
        for (int i = 1; i < length; i++)
        {
            // The wind goes to the next block
            BlockPos newPos = startPosition.relative(direction, i);

            // The wind can stoke a fires.
            if (level.getBlockState(newPos).getBlock() == Blocks.FIRE || level.getBlockState(newPos).getBlock() == ModBlocks.STOKED_FIRE.get())
                level.setBlock(newPos, ModBlocks.STOKED_FIRE.get().defaultBlockState().setValue(StokedFireBlock.AIR, 3), 3);

            // The wind can stoke a brick foundry via tuyeres pipe
            if (level.getBlockState(newPos).getBlock() == ModBlocks.BRICK_FOUNDRY_PIPING.get())
            {
                for (int c = -2; c <= 2; c++)
                {
                    BlockPos checkPos = newPos.relative(direction).relative(direction.getClockWise(), c);
                    if (level.getBlockEntity(checkPos) instanceof FoundryBlockEntity foundryBlockEntity)
                    {
                        foundryBlockEntity.setStokedFireTicks(Math.min(100, foundryBlockEntity.getStokedFireTicks() + 100));
                        break;
                    }
                }
            }

            // The wind stops when there was a block that fully occupies the block face
            if (Block.isShapeFullBlock(level.getBlockState(newPos).getCollisionShape(level, newPos).getFaceShape(direction.getOpposite())))
                break;
            else
            {
                if (level instanceof ServerLevel serverLevel)
                    serverLevel.sendParticles(ParticleTypes.POOF, newPos.getX() + level.random.nextFloat()*0.5f + 0.25f, newPos.getY() + level.random.nextFloat()*0.5f + 0.25f, newPos.getZ() + level.random.nextFloat()*0.5f + 0.25f, 1, direction.getStepX() * speed, direction.getStepY() * speed, direction.getStepZ() * speed, 0.0f);
                else
                    level.addParticle(ParticleTypes.POOF, newPos.getX() + level.random.nextFloat()*0.5f + 0.25f, newPos.getY() + level.random.nextFloat()*0.5f + 0.25f, newPos.getZ() + level.random.nextFloat()*0.5f + 0.25f, direction.getStepX() * speed, direction.getStepY() * speed, direction.getStepZ() * speed);
            }
        }
    }
}
