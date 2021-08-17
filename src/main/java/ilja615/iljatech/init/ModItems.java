package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.items.IronHammerItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IljaTech.MOD_ID);

    public static final RegistryObject<Item> ALUMINIUM_INGOT = ITEMS.register("aluminium_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> CHROME_INGOT = ITEMS.register("chrome_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));

    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new IronHammerItem(ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> NAILGUN = ITEMS.register("nailgun", () -> new Item(ModProperties.ITEM_PROPERTY_NOT_STACKABLE));

    public static final RegistryObject<Item> BRONZE_GEAR = ITEMS.register("bronze_gear", () -> new Item(ModProperties.ITEM_PROPERTY));

    public static final RegistryObject<Item> BOILED_EGG = ITEMS.register("boiled_egg", () -> new Item(ModProperties.BOILED_EGG_PROPERTY));

    // Armor
    public static final RegistryObject<Item> PLASTIC_HELMET = ITEMS.register("plastic_helmet", () -> new ArmorItem(ModArmorMaterials.PLASTIC, EquipmentSlot.HEAD, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> PLASTIC_CHESTPLATE = ITEMS.register("plastic_chestplate", () -> new ArmorItem(ModArmorMaterials.PLASTIC, EquipmentSlot.CHEST, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> PLASTIC_LEGGINGS = ITEMS.register("plastic_leggings", () -> new ArmorItem(ModArmorMaterials.PLASTIC, EquipmentSlot.LEGS, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> PLASTIC_BOOTS = ITEMS.register("plastic_boots", () -> new ArmorItem(ModArmorMaterials.PLASTIC, EquipmentSlot.FEET, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
}
