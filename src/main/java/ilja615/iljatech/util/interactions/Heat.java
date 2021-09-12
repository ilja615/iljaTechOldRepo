package ilja615.iljatech.util.interactions;

import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.init.ModEntities;
import ilja615.iljatech.init.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Heat
{
    public static void emitHeat(Level level, BlockPos startPosition)
    {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(startPosition.above());
            if (startPosition.getY() < level.getMaxBuildHeight() - 1 && state.getBlock() == Blocks.WATER_CAULDRON) {
                int value = state.getValue(LayeredCauldronBlock.LEVEL) - 1; // The value that the cauldron level would be.
                if (value >= 0) {
                    if (value == 0)
                        level.setBlockAndUpdate(startPosition.above(), Blocks.CAULDRON.defaultBlockState());
                    else
                        level.setBlockAndUpdate(startPosition.above(), state.setValue(LayeredCauldronBlock.LEVEL, value));

                    AbstractGasEntity gasEntity = ModEntities.STEAM_CLOUD.get().create(level);
                    gasEntity.moveTo(startPosition.getX() + 0.5f, startPosition.getY() + 1.8f, startPosition.getZ() + 0.5f, 0.0f, 0.0F);
                    gasEntity.setDeltaMovement(0.0d, 0.05d, 0.0d);
                    level.addFreshEntity(gasEntity);

                    // Code for thing such as boil egg, thx basti
                    List<ItemEntity> itemEntityList= level.getEntitiesOfClass(ItemEntity.class, AABB.unitCubeFromLowerCorner(new Vec3(startPosition.getX(), startPosition.getY() + 1, startPosition.getZ())));
                    for (ItemEntity itemEntity : itemEntityList)
                    {
                        if (itemEntity.getItem().getItem() == Items.EGG)
                        {
                            itemEntity.getItem().shrink(1);
                            level.addFreshEntity(new ItemEntity(level, startPosition.getX() + 0.5d, startPosition.getY() + 1.5d, startPosition.getZ() + 0.5d, new ItemStack(ModItems.BOILED_EGG.get(), 1)));
                            break;
                        }
                    }
                }
            }
        }
    }
}
