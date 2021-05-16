package ilja615.iljatech.power;

import net.minecraft.util.IStringSerializable;

public enum MechanicalPower  implements IStringSerializable
{
    OFF("off"),
    ALMOST_STOPPING("almost_stopping"),
    SPINNING("spinning");

    private final String name;
    private MechanicalPower(String nameIn) {
        this.name = nameIn;
    }

    @Override
    public String getSerializedName() { return this.name; }
}
