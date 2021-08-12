package ilja615.iljatech.util.interactions;

import ilja615.iljatech.blocks.StokedFireBlock;
import ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Wind
{
    public static void addWind(World level, BlockPos startPosition, Direction direction, int length, float speed)
    {
        for (int i = 1; i < length; i++)
        {
            BlockPos newPos = startPosition.relative(direction, i);
            level.addParticle(ParticleTypes.POOF, newPos.getX() + level.random.nextFloat()*0.5f + 0.25f, newPos.getY() + level.random.nextFloat()*0.5f + 0.25f, newPos.getZ() + level.random.nextFloat()*0.5f + 0.25f, direction.getStepX() * speed, direction.getStepY() * speed, direction.getStepZ() * speed);

            // The wind can stoke a fires.
            if (level.getBlockState(newPos).getBlock() == Blocks.FIRE || level.getBlockState(newPos).getBlock() == ModBlocks.STOKED_FIRE.get())
                level.setBlock(newPos, ModBlocks.STOKED_FIRE.get().defaultBlockState().setValue(StokedFireBlock.AIR, 3), 3);
        }
    }
}
