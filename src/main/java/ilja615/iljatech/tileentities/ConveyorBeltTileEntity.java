package ilja615.iljatech.tileentities;

import com.google.common.collect.Maps;
import ilja615.iljatech.blocks.ConveyorBeltBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.util.RotationDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;

public class ConveyorBeltTileEntity  extends TileEntity implements ITickableTileEntity
{
    protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0d, 1.0d, 0.0d, 1.0d, 1.1d, 1.0d);
    protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0d, -0.1d, 0.0d, 1.0d, 0.0d, 1.0d);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(-0.1d, 0.0d, 0.0d, 0.0d, 1.0d, 1.0d);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(1.0d, 0.0d, 0.0d, 1.1d, 1.0d, 1.0d);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0d, 0.0d, -0.1d, 1.0d, 1.0d, 0.0d);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0d, 0.0d, 1.0d, 1.0d, 1.0d, 1.1d);

    public static final Map<Direction, AxisAlignedBB> AABB_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
        p_203421_0_.put(Direction.NORTH, NORTH_AABB);
        p_203421_0_.put(Direction.EAST, EAST_AABB);
        p_203421_0_.put(Direction.SOUTH, SOUTH_AABB);
        p_203421_0_.put(Direction.WEST, WEST_AABB);
        p_203421_0_.put(Direction.UP, UP_AABB);
        p_203421_0_.put(Direction.DOWN, DOWN_AABB);
    });

    public ConveyorBeltTileEntity(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }
    public ConveyorBeltTileEntity() { this(ModTileEntityTypes.CONVEYOR_BELT.get()); }

    @Override
    public void tick()
    {
        World world = this.level;
        BlockPos thisPos = this.getBlockPos();
        BlockState state = world.getBlockState(thisPos);
        final double speed = 0.08d;

        if (((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
        {
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.X && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.CLOCKWISE)
            {
                getAndMoveEntities(world, Direction.UP, new Vector3d(0.0d, 0.0d, speed));
                getAndMoveEntities(world, Direction.DOWN, new Vector3d(0.0d, 0.0d, -speed));
                getAndMoveEntities(world, Direction.NORTH, new Vector3d(0.0d, speed, 0.0d));
                getAndMoveEntities(world, Direction.SOUTH, new Vector3d(0.0d, -speed, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Y && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.CLOCKWISE)
            {
                getAndMoveEntities(world, Direction.EAST, new Vector3d(0.0d, 0.0d, -speed));
                getAndMoveEntities(world, Direction.WEST, new Vector3d(0.0d, 0.0d, speed));
                getAndMoveEntities(world, Direction.NORTH, new Vector3d(-speed, 0.0d, 0.0d));
                getAndMoveEntities(world, Direction.SOUTH, new Vector3d(speed, 0.0d, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Z && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.CLOCKWISE)
            {
                getAndMoveEntities(world, Direction.UP, new Vector3d(-speed, 0.0d, 0.0d));
                getAndMoveEntities(world, Direction.DOWN, new Vector3d(speed, 0.0d, 0.0d));
                getAndMoveEntities(world, Direction.EAST, new Vector3d(0.0d, speed, 0.0d));
                getAndMoveEntities(world, Direction.WEST, new Vector3d(0.0d, -speed, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.X && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.COUNTER_CLOCKWISE)
            {
                getAndMoveEntities(world, Direction.UP, new Vector3d(0.0d, 0.0d, -speed));
                getAndMoveEntities(world, Direction.DOWN, new Vector3d(0.0d, 0.0d, speed));
                getAndMoveEntities(world, Direction.NORTH, new Vector3d(0.0d, -speed, 0.0d));
                getAndMoveEntities(world, Direction.SOUTH, new Vector3d(0.0d, speed, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Y && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.COUNTER_CLOCKWISE)
            {
                getAndMoveEntities(world, Direction.EAST, new Vector3d(0.0d, 0.0d, speed));
                getAndMoveEntities(world, Direction.WEST, new Vector3d(0.0d, 0.0d, -speed));
                getAndMoveEntities(world, Direction.NORTH, new Vector3d(speed, 0.0d, 0.0d));
                getAndMoveEntities(world, Direction.SOUTH, new Vector3d(-speed, 0.0d, 0.0d));
            }
            if (state.getValue(ConveyorBeltBlock.AXIS) == Direction.Axis.Z && state.getValue(ConveyorBeltBlock.ROTATION_DIRECTION) == RotationDirection.COUNTER_CLOCKWISE)
            {
                getAndMoveEntities(world, Direction.UP, new Vector3d(speed, 0.0d, 0.0d));
                getAndMoveEntities(world, Direction.DOWN, new Vector3d(-speed, 0.0d, 0.0d));
                getAndMoveEntities(world, Direction.EAST, new Vector3d(0.0d, -speed, 0.0d));
                getAndMoveEntities(world, Direction.WEST, new Vector3d(0.0d, speed, 0.0d));
            }
        }
    }

    private void getAndMoveEntities(World world, Direction face, Vector3d vec)
    {
        if (!world.getBlockState(this.getBlockPos()).getBlock().equals(ModBlocks.CONVEYOR_BELT.get()))
            return;

        Vector3i roundedVec = new Vector3i(Math.signum(vec.x), Math.signum(vec.y), Math.signum(vec.z));
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
                if (entity instanceof PlayerEntity && entity.isCrouching())
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