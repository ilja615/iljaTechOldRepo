package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.entity.*;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<EntityType<AbstractGasEntity>> STEAM_CLOUD = ENTITY_TYPES.register("steam_cloud",
            () -> EntityType.Builder.<AbstractGasEntity>of(SteamEntity::new, MobCategory.MISC).fireImmune().sized(0.4F, 0.4F).build("iljatech:steam_cloud"));

    public static final RegistryObject<EntityType<NailProjectileEntity>> IRON_NAILS_PROJECTILE = ENTITY_TYPES.register("iron_nails_projectile",
            () -> EntityType.Builder.of(NailProjectileEntity::new, MobCategory.MISC).fireImmune().sized(0.25F, 0.25F).build("iljatech:iron_nails_projectile"));

    public static final RegistryObject<EntityType<ElectricFishEntity>> ELECTRIC_FISH = ENTITY_TYPES.register("electric_fish",
            () -> EntityType.Builder.of(ElectricFishEntity::new, MobCategory.MISC).sized(0.6F, 0.8F).build("iljatech:electric_fish"));

    public static final RegistryObject<EntityType<AluminiumGolemEntity>> ALUMINIUM_GOLEM = ENTITY_TYPES.register("aluminium_golem",
            () -> EntityType.Builder.of(AluminiumGolemEntity::new, MobCategory.MISC).sized(0.6F, 0.8F).build("iljatech:aluminium_golem"));

    public static final RegistryObject<EntityType<SaltGolemEntity>> SALT_GOLEM = ENTITY_TYPES.register("salt_golem",
            () -> EntityType.Builder.of(SaltGolemEntity::new, MobCategory.MISC).sized(0.6F, 0.8F).build("iljatech:salt_golem"));

//    public static void registerEntitySpawnEggs(final RegistryEvent.Register<Item> event)
//    {
//        event.getRegistry().registerAll(
//                registerEntitySpawnEgg(ELECTRIC_FISH_TYPE, 0x1b6f5b, 0x7b7e6e, "electric_fish_spawn_egg")
//        );
//    }
//
//    public static Item registerEntitySpawnEgg(EntityType<? extends Mob> type, int color1, int color2, String name)
//    {
//        SpawnEggItem item = new SpawnEggItem(type, color1, color2, new Item.Properties().tab(ModItemGroup.instance));
//        item.setRegistryName(name);
//        return item;
//    }

    // TODO spawnegg

    // The event is passed in via main class
    public static void CreateEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.ELECTRIC_FISH.get(), ElectricFishEntity.createAttributes().build());
        event.put(ModEntities.ALUMINIUM_GOLEM.get(), AluminiumGolemEntity.createAttributes().build());
        event.put(ModEntities.SALT_GOLEM.get(), SaltGolemEntity.createAttributes().build());
    }
}
