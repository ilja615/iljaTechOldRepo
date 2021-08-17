package ilja615.iljatech.util;

import net.minecraft.util.StringRepresentable;

public enum RotationDirection implements StringRepresentable
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
