package ilja615.iljatech.tileentities;

import com.google.common.collect.Maps;
import ilja615.iljatech.blocks.ConveyorBeltBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.util.RotationDirection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ConveyorBeltBlockEntity extends BlockEntity
{
    protected static final AABB UP_AABB = new AABB(0.0d, 1.0d, 0.0d, 1.0d, 1.1d, 1.0d);
    protected static final AABB DOWN_AABB = new AABB(0.0d, -0.1d, 0.0d, 1.0d, 0.0d, 1.0d);
    protected static final AABB WEST_AABB = new AABB(-0.1d, 0.0d, 0.0d, 0.0d, 1.0d, 1.0d);
    protected static final AABB EAST_AABB = new AABB(1.0d, 0.0d, 0.0d, 1.1d, 1.0d, 1.0d);
    protected static final AABB NORTH_AABB = new AABB(0.0d, 0.0d, -0.1d, 1.0d, 1.0d, 0.0d);
    protected static final AABB SOUTH_AABB = new AABB(0.0d, 0.0d, 1.0d, 1.0d, 1.0d, 1.1d);

    public static final Map<Direction, AABB> AABB_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
        p_203421_0_.put(Direction.NORTH, NORTH_AABB);
        p_203421_0_.put(Direction.EAST, EAST_AABB);
        p_203421_0_.put(Direction.SOUTH, SOUTH_AABB);
        p_203421_0_.put(Direction.WEST, WEST_AABB);
        p_203421_0_.put(Direction.UP, UP_AABB);
        p_203421_0_.put(Direction.DOWN, DOWN_AABB);
    });

    public ConveyorBeltBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(ModBlockEntityTypes.CONVEYOR_BELT.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ConveyorBeltBlockEntity blockEntity)
    {
        final double speed = 0.08d;

        if (((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
        {
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.X && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.CLOCKWISE)
            {
                blockEntity.getAndMoveEntities(level, Direction.UP, new Vec3(0.0d, 0.0d, speed));
                blockEntity.getAndMoveEntities(level, Direction.DOWN, new Vec3(0.0d, 0.0d, -speed));
                blockEntity.getAndMoveEntities(level, Direction.NORTH, new Vec3(0.0d, speed, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.SOUTH, new Vec3(0.0d, -speed, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Y && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.CLOCKWISE)
            {
                blockEntity.getAndMoveEntities(level, Direction.EAST, new Vec3(0.0d, 0.0d, -speed));
                blockEntity.getAndMoveEntities(level, Direction.WEST, new Vec3(0.0d, 0.0d, speed));
                blockEntity.getAndMoveEntities(level, Direction.NORTH, new Vec3(-speed, 0.0d, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.SOUTH, new Vec3(speed, 0.0d, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Z && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.CLOCKWISE)
            {
                blockEntity.getAndMoveEntities(level, Direction.UP, new Vec3(-speed, 0.0d, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.DOWN, new Vec3(speed, 0.0d, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.EAST, new Vec3(0.0d, speed, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.WEST, new Vec3(0.0d, -speed, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.X && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.COUNTER_CLOCKWISE)
            {
                blockEntity.getAndMoveEntities(level, Direction.UP, new Vec3(0.0d, 0.0d, -speed));
                blockEntity.getAndMoveEntities(level, Direction.DOWN, new Vec3(0.0d, 0.0d, speed));
                blockEntity.getAndMoveEntities(level, Direction.NORTH, new Vec3(0.0d, -speed, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.SOUTH, new Vec3(0.0d, speed, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Y && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.COUNTER_CLOCKWISE)
            {
                blockEntity.getAndMoveEntities(level, Direction.EAST, new Vec3(0.0d, 0.0d, speed));
                blockEntity.getAndMoveEntities(level, Direction.WEST, new Vec3(0.0d, 0.0d, -speed));
                blockEntity.getAndMoveEntities(level, Direction.NORTH, new Vec3(speed, 0.0d, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.SOUTH, new Vec3(-speed, 0.0d, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Z && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.COUNTER_CLOCKWISE)
            {
                blockEntity.getAndMoveEntities(level, Direction.UP, new Vec3(speed, 0.0d, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.DOWN, new Vec3(-speed, 0.0d, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.EAST, new Vec3(0.0d, -speed, 0.0d));
                blockEntity.getAndMoveEntities(level, Direction.WEST, new Vec3(0.0d, speed, 0.0d));
            }
        }
    }

    private void getAndMoveEntities(Level world, Direction face, Vec3 vec)
    {
        if (!world.getBlockState(this.getBlockPos()).getBlock().equals(ModBlocks.CONVEYOR_BELT.get()))
            return;

        Vec3i roundedVec = new Vec3i(Math.signum(vec.x), Math.signum(vec.y), Math.signum(vec.z));
        BlockPos to = this.getBlockPos().relative(face).offset(roundedVec);

//        if (world.getBlockState(to).isFaceSturdy(world, to, Objects.requireNonNull(Direction.fromNormal(roundedVec.getX(), roundedVec.getY(), roundedVec.getZ()))))
//            return;
        if (world.getBlockState(this.getBlockPos().relative(face)).isFaceSturdy(world, this.getBlockPos(), face.getOpposite()))
            return;

        world.getEntitiesOfClass(Entity.class, AABB_BY_DIRECTION.get(face).inflate(0.1d).move(this.getBlockPos())).forEach(
            entity ->
            {
                if (!entity.isAlive())
                    return;
                if (entity instanceof Player && entity.isCrouching())
                    return;

                if (!entity.noPhysics)
                {
                    System.out.println(entity.getDeltaMovement().lengthSqr());
                    entity.setDeltaMovement(vec.add(entity.getDeltaMovement()));
                }
            }
        );
    }
}