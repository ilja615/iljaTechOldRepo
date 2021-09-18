package ilja615.iljatech.init;

import ilja615.iljatech.mechanicalpower.MechanicalPower;
import ilja615.iljatech.util.ModItemGroup;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ModProperties
{
    //food
    public static final FoodProperties BOILED_EGG = new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).build();

    //items properties
    public static final Item.Properties ITEM_PROPERTY = new Item.Properties().tab(ModItemGroup.instance);
    public static final Item.Properties ITEM_PROPERTY_NOT_STACKABLE = new Item.Properties().tab(ModItemGroup.instance).stacksTo(1);
    public static final Item.Properties BOILED_EGG_PROPERTY = new Item.Properties().tab(ModItemGroup.instance).food(BOILED_EGG);

    //state properties
    public static final EnumProperty MECHANICAL_POWER = EnumProperty.create("mechanical_power", MechanicalPower.class);
}
