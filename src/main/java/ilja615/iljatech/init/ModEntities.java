package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.entity.AirEntity;
import ilja615.iljatech.entity.SteamEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, IljaTech.MOD_ID);

    public static final EntityType<AbstractGasEntity> STEAM_CLOUD_TYPE = EntityType.Builder.<AbstractGasEntity>of(SteamEntity::new, EntityClassification.MISC).fireImmune().sized(0.4F, 0.4F).build("iljatech:steam_cloud");
    public static final RegistryObject<EntityType<AbstractGasEntity>> STEAM_CLOUD = ENTITY_TYPES.register("steam_cloud",
            () -> STEAM_CLOUD_TYPE);

    public static final EntityType<AbstractGasEntity> AIR_CLOUD_TYPE = EntityType.Builder.<AbstractGasEntity>of(AirEntity::new, EntityClassification.MISC).fireImmune().sized(0.4F, 0.4F).build("iljatech:air_cloud");
    public static final RegistryObject<EntityType<AbstractGasEntity>> AIR_CLOUD = ENTITY_TYPES.register("air_cloud",
            () -> AIR_CLOUD_TYPE);
}
