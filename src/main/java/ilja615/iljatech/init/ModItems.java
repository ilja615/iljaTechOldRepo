package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.items.IronHammerItem;
import ilja615.iljatech.items.NailGunItem;
import ilja615.iljatech.util.ModItemGroup;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IljaTech.MOD_ID);

    public static final RegistryObject<Item> ALUMINIUM_INGOT = ITEMS.register("aluminium_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> CHROME_INGOT = ITEMS.register("chrome_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> CONSTANTAN_INGOT = ITEMS.register("constantan_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> NICHROME_INGOT = ITEMS.register("nichrome_ingot", () -> new Item(ModProperties.ITEM_PROPERTY));

    public static final RegistryObject<Item> AZURITE_SHARD = ITEMS.register("azurite_shard", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> CASSITERITE_SHARD = ITEMS.register("cassiterite_shard", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> RUBY_SHARD = ITEMS.register("ruby_shard", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> AZURITE_POWDER = ITEMS.register("azurite_powder", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> CASSITERITE_POWDER = ITEMS.register("cassiterite_powder", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> RUBY_POWDER = ITEMS.register("ruby_powder", () -> new Item(ModProperties.ITEM_PROPERTY));
    public static final RegistryObject<Item> AMETHYST_POWDER = ITEMS.register("amethyst_powder", () -> new Item(ModProperties.ITEM_PROPERTY));

    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new IronHammerItem(ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> NAILGUN = ITEMS.register("nailgun", () -> new NailGunItem(ModProperties.ITEM_PROPERTY_NOT_STACKABLE));

    public static final RegistryObject<Item> BRONZE_GEAR = ITEMS.register("bronze_gear", () -> new Item(ModProperties.ITEM_PROPERTY));

    public static final RegistryObject<Item> BOILED_EGG = ITEMS.register("boiled_egg", () -> new Item(ModProperties.BOILED_EGG_PROPERTY));

    // Armor
    public static final RegistryObject<Item> PETROLYMER_HELMET = ITEMS.register("petrolymer_helmet", () -> new ArmorItem(ModArmorMaterials.PETROLYMER, EquipmentSlot.HEAD, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> PETROLYMER_CHESTPLATE = ITEMS.register("petrolymer_chestplate", () -> new ArmorItem(ModArmorMaterials.PETROLYMER, EquipmentSlot.CHEST, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> PETROLYMER_LEGGINGS = ITEMS.register("petrolymer_leggings", () -> new ArmorItem(ModArmorMaterials.PETROLYMER, EquipmentSlot.LEGS, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));
    public static final RegistryObject<Item> PETROLYMER_BOOTS = ITEMS.register("petrolymer_boots", () -> new ArmorItem(ModArmorMaterials.PETROLYMER, EquipmentSlot.FEET, ModProperties.ITEM_PROPERTY_NOT_STACKABLE));

    public static final RegistryObject<Item> ELECTRIC_FISH_BUCKET = ITEMS.register("electric_fish_bucket", () -> new MobBucketItem(ModEntities.ELECTRIC_FISH_TYPE, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, (new Item.Properties().stacksTo(1).tab(ModItemGroup.instance))));
}
