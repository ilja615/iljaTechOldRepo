package ilja615.iljatech.blocks.wire;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class WireState
{
    private final Level level;
    private final BlockPos pos;
    private final Block block;
    private BlockState state;
    private List<BlockPos> connections = Lists.newArrayList();

    public WireState(Level p_55421_, BlockPos p_55422_, BlockState p_55423_) {
        this.level = p_55421_;
        this.pos = p_55422_;
        this.state = p_55423_;
        this.block = p_55423_.getBlock();
        if (this.block instanceof BaseWireBlock)
        {
            WireShape wireshape = ((BaseWireBlock)this.block).getWireDirection(state, p_55421_, p_55422_);
            this.updateConnections(wireshape);
        }
        if (this.block instanceof ElectricalWireBlock)
        {
            WireShape wireshape = ((ElectricalWireBlock)this.block).getWireDirection(state, p_55421_, p_55422_);
            this.updateConnections(wireshape);
        }
    }

    public List<BlockPos> getConnections() {
        return this.connections;
    }

    private void updateConnections(WireShape p_55428_) {
        this.connections.clear();
        this.connections = getConnectionArray(this.pos, p_55428_);
    }

    public static List<BlockPos> getConnectionArray(BlockPos startPos, WireShape p_55428_) {
        List<BlockPos> list = Lists.newArrayList();
        switch(p_55428_) {
            case NORTH_SOUTH:
                list.add(startPos.north());
                list.add(startPos.south());
                break;
            case EAST_WEST:
                list.add(startPos.west());
                list.add(startPos.east());
                break;
            case ASCENDING_EAST:
                list.add(startPos.west());
                list.add(startPos.east().above());
                break;
            case ASCENDING_WEST:
                list.add(startPos.west().above());
                list.add(startPos.east());
                break;
            case ASCENDING_NORTH:
                list.add(startPos.north().above());
                list.add(startPos.south());
                break;
            case ASCENDING_SOUTH:
                list.add(startPos.north());
                list.add(startPos.south().above());
                break;
            case SOUTH_EAST:
                list.add(startPos.east());
                list.add(startPos.south());
                break;
            case SOUTH_WEST:
                list.add(startPos.west());
                list.add(startPos.south());
                break;
            case NORTH_WEST:
                list.add(startPos.west());
                list.add(startPos.north());
                break;
            case NORTH_EAST:
                list.add(startPos.east());
                list.add(startPos.north());
                break;
            case CROSSROAD:
                list.add(startPos.north());
                list.add(startPos.south());
                list.add(startPos.west());
                list.add(startPos.east());
        }
        return list;
    }

    private void removeSoftConnections() {
        for(int i = 0; i < this.connections.size(); ++i) {
            WireState wirestate = this.getWire(this.connections.get(i));
            if (wirestate != null && wirestate.connectsTo(this)) {
                this.connections.set(i, wirestate.pos);
            } else {
                this.connections.remove(i--);
            }
        }

    }

    private boolean hasWire(BlockPos p_55430_) {
        return BaseWireBlock.isWire(this.level, p_55430_) || BaseWireBlock.isWire(this.level, p_55430_.above()) || BaseWireBlock.isWire(this.level, p_55430_.below());
    }

    @Nullable
    private WireState getWire(BlockPos p_55439_) {
        BlockState blockstate = this.level.getBlockState(p_55439_);
        if (BaseWireBlock.isWire(blockstate)) {
            return new WireState(this.level, p_55439_, blockstate);
        } else {
            BlockPos $$1 = p_55439_.above();
            blockstate = this.level.getBlockState($$1);
            if (BaseWireBlock.isWire(blockstate)) {
                return new WireState(this.level, $$1, blockstate);
            } else {
                $$1 = p_55439_.below();
                blockstate = this.level.getBlockState($$1);
                return BaseWireBlock.isWire(blockstate) ? new WireState(this.level, $$1, blockstate) : null;
            }
        }
    }

    private boolean connectsTo(WireState p_55426_) {
        return this.hasConnection(p_55426_.pos);
    }

    private boolean hasConnection(BlockPos p_55444_) {
        for(int i = 0; i < this.connections.size(); ++i) {
            BlockPos blockpos = this.connections.get(i);
            if (blockpos.getX() == p_55444_.getX() && blockpos.getZ() == p_55444_.getZ()) {
                return true;
            }
        }

        return false;
    }

    protected int countPotentialConnections() {
        int i = 0;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.hasWire(this.pos.relative(direction))) {
                ++i;
            }
        }

        return i;
    }

    private boolean canConnectTo(WireState p_55437_) {
        return this.connectsTo(p_55437_) || this.connections.size() != 2;
    }

    private void connectTo(WireState p_55442_) {
        this.connections.add(p_55442_.pos);
        BlockPos blockpos = this.pos.north();
        BlockPos blockpos1 = this.pos.south();
        BlockPos blockpos2 = this.pos.west();
        BlockPos blockpos3 = this.pos.east();
        boolean flag = this.hasConnection(blockpos);
        boolean flag1 = this.hasConnection(blockpos1);
        boolean flag2 = this.hasConnection(blockpos2);
        boolean flag3 = this.hasConnection(blockpos3);
        WireShape wireshape = null;
        if (flag || flag1) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (flag2 || flag3) {
            wireshape = WireShape.EAST_WEST;
        }

        if (flag1 && flag3 && !flag && !flag2) {
            wireshape = WireShape.SOUTH_EAST;
        }

        if (flag1 && flag2 && !flag && !flag3) {
            wireshape = WireShape.SOUTH_WEST;
        }

        if (flag && flag2 && !flag1 && !flag3) {
            wireshape = WireShape.NORTH_WEST;
        }

        if (flag && flag3 && !flag1 && !flag2) {
            wireshape = WireShape.NORTH_EAST;
        }

        if (countPotentialConnections() > 2) {
            wireshape = WireShape.CROSSROAD;
        }

        if (wireshape == WireShape.NORTH_SOUTH) {
            if (BaseWireBlock.isWire(this.level, blockpos.above())) {
                wireshape = WireShape.ASCENDING_NORTH;
            }

            if (BaseWireBlock.isWire(this.level, blockpos1.above())) {
                wireshape = WireShape.ASCENDING_SOUTH;
            }
        }

        if (wireshape == WireShape.EAST_WEST) {
            if (BaseWireBlock.isWire(this.level, blockpos3.above())) {
                wireshape = WireShape.ASCENDING_EAST;
            }

            if (BaseWireBlock.isWire(this.level, blockpos2.above())) {
                wireshape = WireShape.ASCENDING_WEST;
            }
        }

        if (wireshape == null) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (this.block instanceof BaseWireBlock)
        {
            this.state = this.state.setValue(((BaseWireBlock)this.block).getShapeProperty(), wireshape);
        }
        if (this.block instanceof ElectricalWireBlock)
        {
            this.state = this.state.setValue(((ElectricalWireBlock)this.block).getShapeProperty(), wireshape);
        }
        this.level.setBlock(this.pos, this.state, 3);
    }

    private boolean hasNeighborWire(BlockPos p_55447_) {
        WireState wirestate = this.getWire(p_55447_);
        if (wirestate == null) {
            return false;
        } else {
            wirestate.removeSoftConnections();
            return wirestate.canConnectTo(this);
        }
    }

    public WireState place(boolean p_55432_, boolean p_55433_, WireShape p_55434_) {
        BlockPos northpos = this.pos.north();
        BlockPos southpos = this.pos.south();
        BlockPos westpos = this.pos.west();
        BlockPos eastpos = this.pos.east();
        boolean hasNorthNeighborWire = this.hasNeighborWire(northpos);
        boolean hasSouthNeighborWire = this.hasNeighborWire(southpos);
        boolean hasWestNeighborWire = this.hasNeighborWire(westpos);
        boolean hasEastNeighborWire = this.hasNeighborWire(eastpos);
        WireShape wireshape = null;
        boolean hasNorthOrSouthNeighborWire = hasNorthNeighborWire || hasSouthNeighborWire;
        boolean hasWestOrEastNeighborWire = hasWestNeighborWire || hasEastNeighborWire;
        if (hasNorthOrSouthNeighborWire && !hasWestOrEastNeighborWire) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (hasWestOrEastNeighborWire && !hasNorthOrSouthNeighborWire) {
            wireshape = WireShape.EAST_WEST;
        }

        boolean flag6 = hasSouthNeighborWire && hasEastNeighborWire;
        boolean flag7 = hasSouthNeighborWire && hasWestNeighborWire;
        boolean flag8 = hasNorthNeighborWire && hasEastNeighborWire;
        boolean flag9 = hasNorthNeighborWire && hasWestNeighborWire;
        if (flag6 && !hasNorthNeighborWire && !hasWestNeighborWire) {
            wireshape = WireShape.SOUTH_EAST;
        }

        if (flag7 && !hasNorthNeighborWire && !hasEastNeighborWire) {
            wireshape = WireShape.SOUTH_WEST;
        }

        if (flag9 && !hasSouthNeighborWire && !hasEastNeighborWire) {
            wireshape = WireShape.NORTH_WEST;
        }

        if (flag8 && !hasSouthNeighborWire && !hasWestNeighborWire) {
            wireshape = WireShape.NORTH_EAST;
        }

        if (countPotentialConnections() > 2) {
            wireshape = WireShape.CROSSROAD;
        }

        if (wireshape == null) {
            if (hasNorthOrSouthNeighborWire && hasWestOrEastNeighborWire) {
                wireshape = p_55434_;
            } else if (hasNorthOrSouthNeighborWire) {
                wireshape = WireShape.NORTH_SOUTH;
            } else if (hasWestOrEastNeighborWire) {
                wireshape = WireShape.EAST_WEST;
            }

            if (p_55432_) {
                if (flag6) {
                    wireshape = WireShape.SOUTH_EAST;
                }

                if (flag7) {
                    wireshape = WireShape.SOUTH_WEST;
                }

                if (flag8) {
                    wireshape = WireShape.NORTH_EAST;
                }

                if (flag9) {
                    wireshape = WireShape.NORTH_WEST;
                }
            } else {
                if (flag9) {
                    wireshape = WireShape.NORTH_WEST;
                }

                if (flag8) {
                    wireshape = WireShape.NORTH_EAST;
                }

                if (flag7) {
                    wireshape = WireShape.SOUTH_WEST;
                }

                if (flag6) {
                    wireshape = WireShape.SOUTH_EAST;
                }
            }
        }

        if (wireshape == WireShape.NORTH_SOUTH) {
            if (BaseWireBlock.isWire(this.level, northpos.above())) {
                wireshape = WireShape.ASCENDING_NORTH;
            }

            if (BaseWireBlock.isWire(this.level, southpos.above())) {
                wireshape = WireShape.ASCENDING_SOUTH;
            }
        }

        if (wireshape == WireShape.EAST_WEST) {
            if (BaseWireBlock.isWire(this.level, eastpos.above())) {
                wireshape = WireShape.ASCENDING_EAST;
            }

            if (BaseWireBlock.isWire(this.level, westpos.above())) {
                wireshape = WireShape.ASCENDING_WEST;
            }
        }

        if (wireshape == null) {
            wireshape = p_55434_;
        }

        this.updateConnections(wireshape);
        if (this.block instanceof BaseWireBlock)
        {
            this.state = this.state.setValue(((BaseWireBlock)this.block).getShapeProperty(), wireshape);
        }
        if (this.block instanceof ElectricalWireBlock)
        {
            this.state = this.state.setValue(((ElectricalWireBlock)this.block).getShapeProperty(), wireshape);
        }
        if (p_55433_ || this.level.getBlockState(this.pos) != this.state) {
            this.level.setBlock(this.pos, this.state, 3);

            for(int i = 0; i < this.connections.size(); ++i) {
                WireState wirestate = this.getWire(this.connections.get(i));
                if (wirestate != null) {
                    wirestate.removeSoftConnections();
                    if (wirestate.canConnectTo(this)) {
                        wirestate.connectTo(this);
                    }
                }
            }
        }

        return this;
    }

    public BlockState getState() {
        return this.state;
    }
}
