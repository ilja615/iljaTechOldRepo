package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.items.IronHammerItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
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

    public static final RegistryObject<Item> BRONZE_GEAR = ITEMS.register("bronze_gear", () -> new Item(ModProperties.ITEM_PROPERTY));

    // Armor
    public static final RegistryObject<Item> RUBBER_HELMET = ITEMS.register("rubber_helmet", () -> new ArmorItem(ModArmorMaterials.RUBBER, EquipmentSlotType.HEAD, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> RUBBER_CHESTPLATE = ITEMS.register("rubber_chestplate", () -> new ArmorItem(ModArmorMaterials.RUBBER, EquipmentSlotType.CHEST, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> RUBBER_LEGGINGS = ITEMS.register("rubber_leggings", () -> new ArmorItem(ModArmorMaterials.RUBBER, EquipmentSlotType.LEGS, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> RUBBER_BOOTS = ITEMS.register("rubber_boots", () -> new ArmorItem(ModArmorMaterials.RUBBER, EquipmentSlotType.FEET, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
}
