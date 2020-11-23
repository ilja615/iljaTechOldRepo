package ilja615.iljatech.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

public enum Edge implements IStringSerializable {
    BOTTOM_NORTH("bottom_north"),
    BOTTOM_EAST("bottom_east"),
    BOTTOM_SOUTH("bottom_south"),
    BOTTOM_WEST("bottom_west"),
    NORTHEAST("northeast"),
    NORTHWEST("northwest"),
    SOUTHEAST("southeast"),
    SOUTHWEST("southwest"),
    TOP_NORTH("top_north"),
    TOP_EAST("top_east"),
    TOP_SOUTH("top_south"),
    TOP_WEST("top_west");

    private final String name;
    private Edge(String nameIn) {
        this.name = nameIn;
    }

    public static Edge getEdgeBetweenSides(Direction side1, Direction side2)
    {
        if (side1 == Direction.DOWN)
        {
            if (side2 == Direction.NORTH) return BOTTOM_NORTH;
            if (side2 == Direction.EAST) return BOTTOM_EAST;
            if (side2 == Direction.SOUTH) return BOTTOM_SOUTH;
            if (side2 == Direction.WEST) return BOTTOM_WEST;
        }
        if (side1 == Direction.UP)
        {
            if (side2 == Direction.NORTH) return TOP_NORTH;
            if (side2 == Direction.EAST) return TOP_EAST;
            if (side2 == Direction.SOUTH) return TOP_SOUTH;
            if (side2 == Direction.WEST) return TOP_WEST;
        }
        if (side1 == Direction.NORTH)
        {
            if (side2 == Direction.UP) return TOP_NORTH;
            if (side2 == Direction.DOWN) return BOTTOM_NORTH;
            if (side2 == Direction.EAST) return NORTHEAST;
            if (side2 == Direction.WEST) return NORTHWEST;
        }
        if (side1 == Direction.EAST)
        {
            if (side2 == Direction.UP) return TOP_EAST;
            if (side2 == Direction.DOWN) return BOTTOM_EAST;
            if (side2 == Direction.NORTH) return NORTHEAST;
            if (side2 == Direction.SOUTH) return SOUTHEAST;
        }
        if (side1 == Direction.SOUTH)
        {
            if (side2 == Direction.UP) return TOP_SOUTH;
            if (side2 == Direction.DOWN) return BOTTOM_SOUTH;
            if (side2 == Direction.EAST) return SOUTHEAST;
            if (side2 == Direction.WEST) return SOUTHWEST;
        }
        if (side1 == Direction.WEST)
        {
            if (side2 == Direction.UP) return TOP_WEST;
            if (side2 == Direction.DOWN) return BOTTOM_WEST;
            if (side2 == Direction.NORTH) return NORTHWEST;
            if (side2 == Direction.SOUTH) return SOUTHWEST;
        }
        // Default
        return BOTTOM_NORTH;
    }

    public static Edge getEdgeForContext(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        Direction face1 = Direction.DOWN;
        Direction face2 = Direction.NORTH;
        Edge edge = Edge.BOTTOM_NORTH;
        if (context.getFace().getAxis() == Direction.Axis.Y)
        {
            if (context.getHitVec().x - context.getPos().getX() > 0.9375D && context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                edge = Edge.SOUTHEAST;
            else if (context.getHitVec().x - context.getPos().getX() > 0.9375D && context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                edge = Edge.NORTHEAST;
            else if (context.getHitVec().x - context.getPos().getX() < 0.0625D && context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                edge = Edge.SOUTHWEST;
            else if (context.getHitVec().x - context.getPos().getX() < 0.0625D && context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                edge = Edge.NORTHWEST;
            else
            {
                face1 = context.getFace().getOpposite();
                if (Math.abs(context.getHitVec().x - context.getPos().getX() - 0.5D) > Math.abs(context.getHitVec().z - context.getPos().getZ() - 0.5D))
                    face2 = context.getHitVec().x - context.getPos().getX() >= 0.5D ? Direction.EAST : Direction.WEST;
                else
                    face2 = context.getHitVec().z - context.getPos().getZ() >= 0.5D ? Direction.SOUTH : Direction.NORTH;
                edge = Edge.getEdgeBetweenSides(face1, face2);
            }
        }
        if (context.getFace().getAxis() == Direction.Axis.X)
        {
            if (context.getHitVec().y - context.getPos().getY() > 0.9375D && context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                edge = Edge.TOP_SOUTH;
            else if (context.getHitVec().y - context.getPos().getY() > 0.9375D && context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                edge = Edge.TOP_NORTH;
            else if (context.getHitVec().y - context.getPos().getY() < 0.0625D && context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                edge = Edge.BOTTOM_SOUTH;
            else if (context.getHitVec().y - context.getPos().getY() < 0.0625D && context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                edge = Edge.BOTTOM_NORTH;
            else
            {
                face1 = context.getFace().getOpposite();
                if (Math.abs(context.getHitVec().y - context.getPos().getY() - 0.5D) > Math.abs(context.getHitVec().z - context.getPos().getZ() - 0.5D))
                    face2 = context.getHitVec().y - context.getPos().getY() >= 0.5D ? Direction.UP : Direction.DOWN;
                else
                    face2 = context.getHitVec().z - context.getPos().getZ() >= 0.5D ? Direction.SOUTH : Direction.NORTH;
                edge = Edge.getEdgeBetweenSides(face1, face2);
            }
        }
        if (context.getFace().getAxis() == Direction.Axis.Z)
        {
            if (context.getHitVec().x - context.getPos().getX() > 0.9375D && context.getHitVec().y - context.getPos().getY() > 0.9375D)
                edge = Edge.TOP_EAST;
            else if (context.getHitVec().x - context.getPos().getX() > 0.9375D && context.getHitVec().y - context.getPos().getY() < 0.0625D)
                edge = Edge.BOTTOM_EAST;
            else if (context.getHitVec().x - context.getPos().getX() < 0.0625D && context.getHitVec().y - context.getPos().getY() > 0.9375D)
                edge = Edge.TOP_WEST;
            else if (context.getHitVec().x - context.getPos().getX() < 0.0625D && context.getHitVec().y - context.getPos().getY() < 0.0625D)
                edge = Edge.BOTTOM_WEST;
            else
            {
                face1 = context.getFace().getOpposite();
                if (Math.abs(context.getHitVec().x - context.getPos().getX() - 0.5D) > Math.abs(context.getHitVec().y - context.getPos().getY() - 0.5D))
                    face2 = context.getHitVec().x - context.getPos().getX() >= 0.5D ? Direction.EAST : Direction.WEST;
                else
                    face2 = context.getHitVec().y - context.getPos().getY() >= 0.5D ? Direction.UP : Direction.DOWN;
                edge = Edge.getEdgeBetweenSides(face1, face2);
            }
        }
        return edge;
    }

    @Override
    public String getString() { return this.name; }
}