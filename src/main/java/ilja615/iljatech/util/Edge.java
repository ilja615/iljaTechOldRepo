package ilja615.iljatech.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

public enum Edge implements IStringSerializable
{
    NORTHEAST("northeast"),
    NORTHWEST("northwest"),
    SOUTHEAST("southeast"),
    SOUTHWEST("southwest");

    private final String name;
    private Edge(String nameIn) {
        this.name = nameIn;
    }

    public static Edge getEdgeForContext(BlockItemUseContext context)
    {
        Edge edge = Edge.SOUTHEAST;
            if (context.getHitVec().x - context.getPos().getX() >= 0.5d && context.getHitVec().z - context.getPos().getZ() >= 0.5d)
                edge = Edge.SOUTHEAST;
            else if (context.getHitVec().x - context.getPos().getX() >= 0.5d && context.getHitVec().z - context.getPos().getZ() < 0.5d)
                edge = Edge.NORTHEAST;
            else if (context.getHitVec().x - context.getPos().getX() < 0.5d && context.getHitVec().z - context.getPos().getZ() >= 0.5d)
                edge = Edge.SOUTHWEST;
            else if (context.getHitVec().x - context.getPos().getX() < 0.5d && context.getHitVec().z - context.getPos().getZ() < 0.5d)
                edge = Edge.NORTHWEST;

        return edge;
    }

    @Override
    public String getString() { return this.name; }
}