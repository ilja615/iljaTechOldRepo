package ilja615.iljatech.util;

import net.minecraft.util.IStringSerializable;

public enum RotationDirection implements IStringSerializable
{
    CLOCKWISE("clockwise"),
    COUNTER_CLOCKWISE("counterclockwise");

    private String name;

    private RotationDirection(String nameIn)
    {
        this.name = nameIn;
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }
}
