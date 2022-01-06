package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.entity.ElectricFishEntity;
import ilja615.iljatech.entity.NailProjectileEntity;
import ilja615.iljatech.entity.SteamEntity;
import ilja615.iljatech.util.ModItemGroup;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, IljaTech.MOD_ID);

    public static final EntityType<AbstractGasEntity> STEAM_CLOUD_TYPE = EntityType.Builder.<AbstractGasEntity>of(SteamEntity::new, MobCategory.MISC).fireImmune().sized(0.4F, 0.4F).build("iljatech:steam_cloud");
    public static final RegistryObject<EntityType<AbstractGasEntity>> STEAM_CLOUD = ENTITY_TYPES.register("steam_cloud",
            () -> STEAM_CLOUD_TYPE);

    public static final EntityType<NailProjectileEntity> IRON_NAILS_PROJECTILE_TYPE = EntityType.Builder.of(NailProjectileEntity::new, MobCategory.MISC).fireImmune().sized(0.25F, 0.25F).build("iljatech:iron_nails_projectile");
    public static final RegistryObject<EntityType<NailProjectileEntity>> IRON_NAILS_PROJECTILE = ENTITY_TYPES.register("iron_nails_projectile",
            () -> IRON_NAILS_PROJECTILE_TYPE);

    public static final EntityType<ElectricFishEntity> ELECTRIC_FISH_TYPE = EntityType.Builder.of(ElectricFishEntity::new, MobCategory.MISC).sized(0.6F, 0.8F).build("iljatech:electric_fish");
    public static final RegistryObject<EntityType<ElectricFishEntity>> ELECTRIC_FISH = ENTITY_TYPES.register("electric_fish",
            () -> ELECTRIC_FISH_TYPE);

    public static void registerEntitySpawnEggs(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                registerEntitySpawnEgg(ELECTRIC_FISH_TYPE, 0x1b6f5b, 0x7b7e6e, "electric_fish_spawn_egg")
        );
    }

    public static Item registerEntitySpawnEgg(EntityType<? extends Mob> type, int color1, int color2, String name)
    {
        SpawnEggItem item = new SpawnEggItem(type, color1, color2, new Item.Properties().tab(ModItemGroup.instance));
        item.setRegistryName(name);
        return item;
    }
}
