package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.entity.SteamEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, IljaTech.MOD_ID);

    public static final EntityType<AbstractGasEntity> STEAM_CLOUD_TYPE = EntityType.Builder.<AbstractGasEntity>of(SteamEntity::new, MobCategory.MISC).fireImmune().sized(0.4F, 0.4F).build("iljatech:steam_cloud");
    public static final RegistryObject<EntityType<AbstractGasEntity>> STEAM_CLOUD = ENTITY_TYPES.register("steam_cloud",
            () -> STEAM_CLOUD_TYPE);
}
