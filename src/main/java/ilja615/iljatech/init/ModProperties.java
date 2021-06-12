package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.util.ModItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModProperties
{
    //items properties
    public static final Item.Properties ITEM_PROPERTY = new Item.Properties().tab(ModItemGroup.instance);
    public static final Item.Properties ITEM_PROPERTY_NOT_STACKABLE = new Item.Properties().tab(ModItemGroup.instance).stacksTo(1);

    //state properties
    public static final EnumProperty MECHANICAL_POWER = EnumProperty.create("mechanical_power", MechanicalPower.class);
}
