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
            if (context.getClickLocation().x - context.getClickedPos().getX() >= 0.5d && context.getClickLocation().z - context.getClickedPos().getZ() >= 0.5d)
                edge = Edge.SOUTHEAST;
            else if (context.getClickLocation().x - context.getClickedPos().getX() >= 0.5d && context.getClickLocation().z - context.getClickedPos().getZ() < 0.5d)
                edge = Edge.NORTHEAST;
            else if (context.getClickLocation().x - context.getClickedPos().getX() < 0.5d && context.getClickLocation().z - context.getClickedPos().getZ() >= 0.5d)
                edge = Edge.SOUTHWEST;
            else if (context.getClickLocation().x - context.getClickedPos().getX() < 0.5d && context.getClickLocation().z - context.getClickedPos().getZ() < 0.5d)
                edge = Edge.NORTHWEST;

        return edge;
    }

    @Override
    public String getSerializedName() { return this.name; }
}