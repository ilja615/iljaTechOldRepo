package ilja615.iljatech.init;

import ilja615.iljatech.util.ModItemGroup;
import net.minecraft.item.Item;
import net.minecraft.state.IntegerProperty;

public class ModProperties
{
    //items properties
    public static final Item.Properties ITEM_PROPERTY = new Item.Properties().group(ModItemGroup.instance);

    //state properties
    public static final IntegerProperty MECHANICAL_POWER = IntegerProperty.create("mechanical_power", 0, 15);

}
