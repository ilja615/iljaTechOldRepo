package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.tileentities.BurnerTileEntity;
import ilja615.iljatech.tileentities.CrafterMachineTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, IljaTech.MOD_ID);

    public static final RegistryObject<TileEntityType<CrafterMachineTileEntity>> CRAFTER_MACHINE = TILE_ENTITY_TYPES.register("crafter_machine", () -> TileEntityType.Builder.create(CrafterMachineTileEntity::new, ModBlocks.CRAFTER_MACHINE.get()).build(null));
    public static final RegistryObject<TileEntityType<BurnerTileEntity>> BURNER = TILE_ENTITY_TYPES.register("burner", () -> TileEntityType.Builder.create(BurnerTileEntity::new, ModBlocks.BURNER.get()).build(null));

}
