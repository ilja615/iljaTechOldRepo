package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.entity.GassEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, IljaTech.MOD_ID);
    public static final EntityType<GassEntity> STEAM_CLOUD_TYPE = EntityType.Builder.<GassEntity>create(GassEntity::new, EntityClassification.MISC).size(0.7F, 1.9F).build("testmod:snowman");
    public static final RegistryObject<EntityType<GassEntity>> STEAM_CLOUD = ENTITY_TYPES.register("steam_cloud",
            () -> STEAM_CLOUD_TYPE);
}
