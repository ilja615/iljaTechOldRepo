package ilja615.iljatech.power;

import net.minecraft.util.StringRepresentable;

public enum MechanicalPower  implements StringRepresentable
{
    OFF("off", false),
    ALMOST_STOPPING("almost_stopping", false),
    SPINNING_1("spinning_1", true, 1),
    SPINNING_2("spinning_2", true, 2),
    SPINNING_3("spinning_3", true, 3),
    SPINNING_4("spinning_4", true, 4),
    SPINNING_5("spinning_5", true, 5),
    SPINNING_6("spinning_6", true, 6),
    SPINNING_7("spinning_7", true, 7),
    SPINNING_8("spinning_8", true, 8);

    private final String name;
    private final boolean isSpinning;
    private final int amount;

    private MechanicalPower(String nameIn, boolean isSpinningIn) {
        this.name = nameIn;
        this.isSpinning = isSpinningIn;
        this.amount = 0;
    }

    private MechanicalPower(String nameIn, boolean isSpinningIn, int amountIn) {
        this.name = nameIn;
        this.isSpinning = isSpinningIn;
        this.amount = amountIn;
    }

    @Override
    public String getSerializedName() { return this.name; }

    public boolean isSpinning() { return this.isSpinning; }

    public static MechanicalPower getFromAmount(int amount)
    {
        switch (amount)
        {
            case 1: return SPINNING_1;
            case 2: return SPINNING_2;
            case 3: return SPINNING_3;
            case 4: return SPINNING_4;
            case 5: return SPINNING_5;
            case 6: return SPINNING_6;
            case 7: return SPINNING_7;
            case 8: return SPINNING_8;
            default: return amount > 8 ? SPINNING_8 : OFF;
        }
    }

    public int getInt()
    {
        return this.amount;
    }
}
