package ilja615.iljatech.items;

import com.google.common.collect.ImmutableMap;
import ilja615.iljatech.blocks.NailsBlock;
import ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class IronHammerItem extends Item
{
    protected static final Map<Block, Block> BLOCK_NAILS_MAP = (new ImmutableMap.Builder<Block, Block>())
            .put(Blocks.OAK_PLANKS, ModBlocks.NAILED_OAK_PLANKS.get())
            .put(Blocks.SPRUCE_PLANKS, ModBlocks.NAILED_SPRUCE_PLANKS.get())
            .put(Blocks.BIRCH_PLANKS, ModBlocks.NAILED_BIRCH_PLANKS.get())
            .put(Blocks.JUNGLE_PLANKS, ModBlocks.NAILED_JUNGLE_PLANKS.get())
            .put(Blocks.ACACIA_PLANKS, ModBlocks.NAILED_ACACIA_PLANKS.get())
            .put(Blocks.DARK_OAK_PLANKS, ModBlocks.NAILED_DARK_OAK_PLANKS.get())
            .put(Blocks.WARPED_PLANKS, ModBlocks.NAILED_WARPED_PLANKS.get())
            .put(Blocks.CRIMSON_PLANKS, ModBlocks.NAILED_CRIMSON_PLANKS.get()).build();

    protected static final Map<Block, Block> BLOCK_CRACKING_MAP = (new ImmutableMap.Builder<Block, Block>())
            .put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS)
            .put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
            .put(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS)
            .put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
            .put(Blocks.STONE, Blocks.COBBLESTONE)
            .put(Blocks.COBBLESTONE, Blocks.GRAVEL).build();

    public IronHammerItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof NailsBlock && blockstate.hasProperty(NailsBlock.STAGE))
        {
            world.playSound(context.getPlayer(), blockpos, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            int stage = blockstate.get(NailsBlock.STAGE);
            if (stage < 3)
                world.setBlockState(blockpos, blockstate.with(NailsBlock.STAGE, stage + 1));
            else
            {
                Block block = BLOCK_NAILS_MAP.get(world.getBlockState(blockpos.offset(blockstate.get(BlockStateProperties.FACING))).getBlock());
                if (block != null)
                {
                    world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
                    world.setBlockState(blockpos.offset(blockstate.get(BlockStateProperties.FACING)), block.getDefaultState());
                }
            }
            return ActionResultType.SUCCESS;
        } else {
            Block block = BLOCK_CRACKING_MAP.get(world.getBlockState(blockpos).getBlock());
            if (block != null)
            {
                world.playSound(context.getPlayer(), blockpos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockState(blockpos, block.getDefaultState());
                context.getPlayer().getCooldownTracker().setCooldown(this, 20);
                return ActionResultType.SUCCESS;
            }
        }

        return super.onItemUse(context);
    }
}
