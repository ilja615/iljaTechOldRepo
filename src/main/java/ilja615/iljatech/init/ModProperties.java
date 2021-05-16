package ilja615.iljatech.init;

import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.util.ModItemGroup;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;

public class ModProperties
{
    //items properties
    public static final Item.Properties ITEM_PROPERTY = new Item.Properties().tab(ModItemGroup.instance);

    //state properties
    public static final EnumProperty MECHANICAL_POWER = EnumProperty.create("mechanical_power", MechanicalPower.class);

}
